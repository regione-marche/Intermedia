package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca;


import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoNotificaToEntiResponseCodeType;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoNotificaToEntiResponseDescriptionType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.EsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreSubjectNotificaEsitoCommittente;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromEntiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

public class PecCANotificaEsitoCommittente {

    private static final Logger LOG = LoggerFactory.getLogger(PecCANotificaEsitoCommittente.class);

    private static final String SENDER_ADDRESS_RETURN_PATH_HEADER = "Return-Path";
    private static final String SENDER_ADDRESS_HEADER_REPLY_TO = "Reply-To";
    private static final String EMAIL_DA_IGNORARE = "emailDaIgnorare";
    private static final String SUBJECT_HEADER = "subject";
    private static final String SUBJECT_ACCETTAZIONE = "accettazione";
    private static final String SUBJECT_CONSEGNA = "consegna";
    private static final String SUBJECT_POSTA_CERTIFICATA = "posta certificata:";
    private static final String NOME_FILE_NOTIFICA_ESITO_HEADER = "nomeFileNotificaEsito";
    private static final String NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER = "nomeFileScartoEsito";
    private static final String TIPO_MESSAGGIO = "tipoMessaggio";
    private static final String ESITO_RISPOSTA_HEADER = "esitoRisposta";

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private static final String ERRORE_NOME_ALLEGATO = "erroreNomeAllegato";
    private static final String SUBJECT_ORIGINAL_HEADER = "subjectOriginal";
    private static final String OLD_SUBJECT = "oldSubject";

    private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileFattura";

    private static final String NOME_FILE_ESITO_OPERAZIONE = "nomeFileEsitoOperazione";
    private static final String COD_ESITO_ESITO_OPERAZIONE = "codiceEsito";
    private static final String DESCRIZIONE_ESITO_ESITO_OPERAZIONE = "descrizioneEsito";
    private static final String NOME_FILE_FATTURA_ESITO_OPERAZIONE = "nomeFileFattura";
    private static final String NUMERO_FATTURA_ESITO_OPERAZIONE = "numeroFattura";
    private static final String FILE_ESITO_OPERAZIONE = "fileEsitoOperazione";

    private static final String TIPO_ERRORE_HEADER = "esitoRisposta";

    private static final String TIPO_OPERAZIONE_HEADER = "tipoOperazione";

    private static final String INFO_TIPO_INVIO_FATTURA_CA_HEADER = "infoTipoInvioFatturaCA";

    private static final String COMMITTENTE_CODICE_IVA = "committenteCodiceIva";
    private static final String CODICE_UFFICIO = "codiceUfficio";
    private static final String IDENTIFICATIVO_SDI = "identificativoSdi";

    private static final String MESSAGGIO_ESITO_COMMITENTE = "NotificaEsitoCommittente";
    private static final String MESSAGGIO_SCARTO_ESITO_COMMITENTE = "NotificaScartoEsito";

    private String providerEmarche;
    private String mailHost;
    private String mailTransportProtocol;

    private EnteManager enteManager;

    private DatiFatturaManager datiFatturaManager;

    private NotificaFromEntiManager notificaFromEntiManager;

    public void estraiMessaggioAccettazioneRifiuto(Exchange exchange) throws Exception {

        //estrarre l'allegato e verificare che ci sia quanto aspettato

        Message msg = exchange.getIn();

        Map<String, DataHandler> attachmentsMap = msg.getAttachments();

        int notificaTrovata = 0;
        String nomeFileNotifica = "";
        DataHandler fileNotifica = null;
        String fileNameDecoded = "";

        String headerEmailMittente = "";

        String replyTo = (String) msg.getHeader(SENDER_ADDRESS_HEADER_REPLY_TO);
        String returnPath = (String) msg.getHeader(SENDER_ADDRESS_RETURN_PATH_HEADER);

        if (replyTo != null && !"".equals(replyTo)) {
            headerEmailMittente = replyTo;
        } else if (returnPath != null && !"".equals(returnPath)) {
            headerEmailMittente = returnPath;
        }

        InternetAddress internetAddressSender = new InternetAddress(headerEmailMittente);

        String indirizzoEmailMittente = internetAddressSender.getAddress();
        msg.setHeader(SENDER_ADDRESS_RETURN_PATH_HEADER, indirizzoEmailMittente);

        if (indirizzoEmailMittente.equals(providerEmarche)) {
            /*
                EMAIL da ignorare perche' si tratta di una email di servizio della pec (accettazione o consegna)
            */
            msg.setHeader(EMAIL_DA_IGNORARE, true);

            return;
        } else {
            msg.setHeader(EMAIL_DA_IGNORARE, false);
        }
        String subjectPec = (String) msg.getHeader(SUBJECT_HEADER);

        //subjectPec = subjectPec.trim().toLowerCase();
        subjectPec = subjectPec.trim();

//FIXME questo controllo non ha più senso, visto che viene fatto all'inizio nel processor "PecCAScansioneCasellaCheckSubjectProcessor.java"
        if (subjectPec.toLowerCase().contains(SUBJECT_ACCETTAZIONE) || subjectPec.toLowerCase().contains(SUBJECT_CONSEGNA)) {

            /*
            email da ignorare_ e' una di quelle di servizio dei provider PEC
             */

            msg.setHeader(EMAIL_DA_IGNORARE, true);
            return;
        }

        if (subjectPec.toLowerCase().contains(SUBJECT_POSTA_CERTIFICATA)) {
            subjectPec = subjectPec.replaceAll("POSTA CERTIFICATA:", "").trim();
        }

        msg.setHeader(SUBJECT_HEADER, subjectPec);

/**
 * FIXME questa dovrebbe essere eseguita prima, in uno dei rami degli if quando si scansiona la casella pec: per quale motivo inviare un messaggio nella coda dell'esito committente
 * se già il subject è errato?
 */
        if (!ValidatoreSubjectNotificaEsitoCommittente.validate(subjectPec)) {
            throw new FatturaPANomeFileErratoException();
        }

        for (Map.Entry<String, DataHandler> entry : attachmentsMap.entrySet()) {

            if (entry.getKey().contains(".eml")) {
                Properties props = System.getProperties();
                props.put("mail.host", mailHost);
                props.put("mail.transport.protocol", mailTransportProtocol);
                Session mailSession = Session.getDefaultInstance(props, null);
                InputStream source = entry.getValue().getInputStream();
                MimeMessage message = new MimeMessage(mailSession, source);

                Object content = message.getContent();

                if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;

                    int count = multipart.getCount();

                    for (int i = 0; i < count; i++) {

                        BodyPart bodyPart = multipart.getBodyPart(i);

                        String fileName = bodyPart.getFileName();
                        DataHandler fileAllegato = bodyPart.getDataHandler();

                        if (fileName == null || "".equals(fileName)) {

                        } else {

                            fileNameDecoded = MimeUtility.decodeText(fileName);

                            if (!ValidatoreNomeNotificaEsitoCommittente.validate(fileNameDecoded)) {
                                if (fileNameDecoded.contains(".xml")) {
                                    notificaTrovata++;
                                }
                            } else {
                                notificaTrovata++;
                                nomeFileNotifica = fileNameDecoded;
                                fileNotifica = fileAllegato;
                            }
                        }
                    }
                }
            }
        }

        if (notificaTrovata != 1) {

            throw new FatturaPAAllegatoNotificaEsitoCommNonTrovatoException();

        } else {

            if (!ValidatoreNomeNotificaEsitoCommittente.validate(nomeFileNotifica)) {
                throw new FatturaPANomeFileErratoException();
            }

            InputStream is = fileNotifica.getInputStream();
            byte[] fileNotificaByteArray = IOUtils.toByteArray(is);

            BigInteger identificativoSdi = null;
            String tipoNotifica = null;

            try {

                NotificaEsitoCommittenteType notificaEsitoCommittenteType = JaxBUtils.getNotificaEsitoCommittenteType(fileNotificaByteArray);

                if (notificaEsitoCommittenteType.getIdentificativoSdI() == null || "".equals(notificaEsitoCommittenteType.getIdentificativoSdI())) {
                    throw new FatturaPAValidazioneFallitaException("Identificativo SdI non presente!");
                }

                if (notificaEsitoCommittenteType.getEsito() == null || "".equals(notificaEsitoCommittenteType.getEsito().value())) {
                    throw new FatturaPAValidazioneFallitaException("Esito non presente!");
                }

                identificativoSdi = notificaEsitoCommittenteType.getIdentificativoSdI();
                tipoNotifica = notificaEsitoCommittenteType.getEsito().value();

            } catch (Exception ex) {
                throw new FatturaPAValidazioneFallitaException();
            }

            msg.setHeader(IDENTIFICATIVO_SDI_HEADER, identificativoSdi.toString());
            msg.setHeader(TIPO_MESSAGGIO, tipoNotifica);

            //regma112 oltre al controllo dell'ente vedo anche se la fattura e' di tipo interno
            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdi);

            if (!checkEnte(indirizzoEmailMittente, datiFatturaEntityList, identificativoSdi, msg)) {
                throw new FatturaPAEnteNonTrovatoException("Ente non trovato per indirizzo " + indirizzoEmailMittente);
            }

            final InputStream in = fileNotifica.getInputStream();
            byte[] fileNotificaOriginale = IOUtils.toByteArray(in);

            msg.setHeader(NOME_FILE_NOTIFICA_ESITO_HEADER, nomeFileNotifica);

            String messaggioEncoded = new String(Base64.encodeBase64(fileNotificaOriginale));
            msg.setBody(messaggioEncoded);

            if (datiFatturaEntityList.get(0).getFatturazioneInterna()) {
                msg.setHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER, CHECK_FLUSSO_SEMPLIFICATO_HEADER);
                msg.setHeader(NOME_FILE_FATTURA_HEADER, datiFatturaEntityList.get(0).getNomeFile());
                msg.setHeader(NUMERO_FATTURA_ESITO_OPERAZIONE, datiFatturaEntityList.get(0).getNumeroFattura());
            }
        }
    }

    private boolean checkEnte(String indirizzoEmailMittente, List<DatiFatturaEntity> datiFatturaEntityList, BigInteger identificativoSdi, Message msg) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAFatturaNonTrovataException {

        String codiceUfficioPerFattura = "";

        if (datiFatturaEntityList != null && datiFatturaEntityList.size() >= 1) {

            codiceUfficioPerFattura = datiFatturaEntityList.get(0).getCodiceDestinatario();

        } else {
            LOG.info("PecCANotificaEsitoCommittente - estraiMessaggioAccettazioneRifiuto: Fattura non trovata per l'identificativoSdI: " + identificativoSdi);
            throw new FatturaPAFatturaNonTrovataException("Fattura non trovate per l'identificativoSdI: " + identificativoSdi);
        }

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficioPerFattura);

        String listaEmailPec = null;

        if (enteEntity.getTipoCanale().getCodStato().getValue().equals(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {

            if (enteEntity.getEndpointEsitoCommittenteCa().getCanaleCa().getCodCanale().equals(CanaleCaEntity.CANALE_CA.PEC.getValue())) {
                listaEmailPec = enteEntity.getEndpointEsitoCommittenteCa().getEndpoint();
            }

        } else {
            listaEmailPec = enteEntity.getEmailPec();
        }

        if (!checkEmailPec(listaEmailPec, indirizzoEmailMittente)) {
            LOG.info("PecCANotificaEsitoCommittente - estraiMessaggioAccettazioneRifiuto: Indirizzo mittente non autorizzato: " + indirizzoEmailMittente);
            return false;
        }

        //Salvo in degli headers delle info utili sull'ente
        msg.setHeader(COMMITTENTE_CODICE_IVA, enteEntity.getIdFiscaleCommittente());
        msg.setHeader(CODICE_UFFICIO, enteEntity.getCodiceUfficio());
        msg.setHeader(IDENTIFICATIVO_SDI, identificativoSdi);

        return true;
    }

    private boolean checkEmailPec(String listaEmailPec, String indirizzoEmailMittente) {

        if (listaEmailPec == null || "".equals(listaEmailPec) || indirizzoEmailMittente == null || "".equals(indirizzoEmailMittente)) {
            return false;
        }

        List<String> emails = new ArrayList<String>(Arrays.asList(listaEmailPec.split(",")));

        boolean found = false;
        for (String s : emails) {
            if (s.trim().toLowerCase().equals(indirizzoEmailMittente.trim().toLowerCase())) {
                found = true;
                break;
            }

        }
        return found;
    }

    public void preparaMessaggioErrore(Exchange exchange) throws IOException, JAXBException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        Message message = exchange.getIn();

        byte[] scartoEsitoCommittenteByteArray = Base64.decodeBase64((String) message.getBody());

        DataSource dataSource = new ByteArrayDataSource(scartoEsitoCommittenteByteArray, "application/xml");
        DataHandler dh = new DataHandler(dataSource);

        String nomeFileScartoEsito = (String) message.getHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER);

        message.addAttachment(nomeFileScartoEsito, dh);

        String scartoEsitoCommittente = XAdESUnwrapper.unwrap(scartoEsitoCommittenteByteArray);

        message.setBody(scartoEsitoCommittente);

    }

    public void prepareMailError(Exchange exchange) throws IOException, AddressException {
        Message msg = exchange.getIn();

        String headerEmailMittente = "";

        String replyTo = (String) msg.getHeader(SENDER_ADDRESS_HEADER_REPLY_TO);
        String returnPath = (String) msg.getHeader(SENDER_ADDRESS_RETURN_PATH_HEADER);

        if (replyTo != null && !"".equals(replyTo)) {
            headerEmailMittente = replyTo;
        } else if (returnPath != null && !"".equals(returnPath)) {
            headerEmailMittente = returnPath;
        }

        InternetAddress internetAddressSender = new InternetAddress(headerEmailMittente);

        String indirizzoDestinatario = internetAddressSender.getAddress();

        String oldSubject = (String) msg.getHeader(OLD_SUBJECT);
        String subject = (String) msg.getHeader(SUBJECT_HEADER);
        String identificativoSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String nomeFileAllegato = (String) msg.getHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER);
        String esitoRisposta = (String) msg.getHeader(ESITO_RISPOSTA_HEADER);

        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);

        String nomeFile = "";

        Map<String, DataHandler> attachmentsMap = new HashMap<>();
        attachmentsMap.putAll(msg.getAttachments());

        msg.removeHeaders("*");
        Set<String> setNames = new HashSet<>();

        for (String s : msg.getAttachmentNames()) {

            String tmp = new String(s);
            setNames.add(tmp);
        }

        for (String s : setNames) {
            if (!s.equals(nomeFileAllegato)) {
                msg.removeAttachment(s);
            }
        }

        msg.getAttachments().clear();

        for (Map.Entry<String, DataHandler> entry : attachmentsMap.entrySet()) {

            DataHandler dh = entry.getValue();
            final InputStream in = dh.getInputStream();
            byte[] byteArray = IOUtils.toByteArray(in);

            String contentType = dh.getContentType();

            String estensioneFile = FileUtils.getFileExtension(entry.getKey()).toLowerCase();

            DataSource ds = new ByteArrayDataSource(byteArray, contentType);
            DataHandler newDh = new DataHandler(ds);

            msg.getAttachments().put("originalAttachment-" + entry.getKey(), newDh);

        }

        msg.setHeader(SENDER_ADDRESS_RETURN_PATH_HEADER, indirizzoDestinatario);
        msg.setHeader(SUBJECT_ORIGINAL_HEADER, subject);
        msg.setHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER, nomeFileAllegato);
        msg.setHeader(IDENTIFICATIVO_SDI_HEADER, identificativoSdi);
        msg.setHeader(TIPO_MESSAGGIO, tipoMessaggio);
        msg.setHeader(ESITO_RISPOSTA_HEADER, esitoRisposta);
        msg.setHeader(OLD_SUBJECT, oldSubject);
    }

    public void aggiornaFattura(Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        Message msg = msgExchange.getIn();

        String identificativoSdI = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);
        String tipoOperazione = (String) msg.getHeader(TIPO_OPERAZIONE_HEADER);
        String infoTipoInvioFatturaCA = (String) msg.getHeader(INFO_TIPO_INVIO_FATTURA_CA_HEADER);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        String statoFattura = null;

        switch (tipoOperazione) {

            case "ricezioneNotifica":

                if (tipoMessaggio == null || "".equals(tipoMessaggio))
                    throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - ricezioneNotifica: Tipo messaggio non valido");

                if (tipoMessaggio.equals(EsitoCommittenteType.EC_01.value())) {
                    statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_RICEVUTA_ACCETTAZIONE.getValue();
                } else if (tipoMessaggio.equals(EsitoCommittenteType.EC_02.value())) {
                    statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_RICEVUTO_RIFIUTO.getValue();
                } else {
                    throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - ricezioneNotifica: Tipo messaggio non riconosciuto");
                }

                break;

            case "protocollazioneNotifica":

                if (tipoMessaggio == null || "".equals(tipoMessaggio))
                    throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - protocollazioneNotifica: Tipo messaggio non valido");

                switch (tipoMessaggio) {

                    case MESSAGGIO_ESITO_COMMITENTE:

                        statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_INOLTRATA_PROTOCOLLO.getValue();

                        break;

                    case MESSAGGIO_SCARTO_ESITO_COMMITENTE:

                        if ("Protocollazione".equals(infoTipoInvioFatturaCA))
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_INOLTRATA_PROTOCOLLO.getValue();
                        else
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_INVIO_UNICO.getValue();

                        break;

                    default:
                        throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - protocollazioneNotifica: Tipo messaggio non riconosciuto");
                }

                //statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_PROTOCOLLATA.getValue();

                break;

            case "registrazioneNotifica":

                if (tipoMessaggio == null || "".equals(tipoMessaggio))
                    throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - registrazioneNotifica: Tipo messaggio non valido");

                switch (tipoMessaggio) {

                    case MESSAGGIO_ESITO_COMMITENTE:
                        throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - registrazioneNotifica: Tentativo di inoltro a registrazione di una notifica esito committente");

                    case MESSAGGIO_SCARTO_ESITO_COMMITENTE:
                        statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_INOLTRATA_REGISTRAZIONE.getValue();
                        break;

                    default:
                        throw new FatturaPAException("PecCANotificaEsitoCommittente - aggiornaFattura - protocollazioneNotifica: Tipo messaggio non riconosciuto");
                }

                //statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_REGISTRATA.getValue();

                break;
        }

        for (DatiFatturaEntity dfe : datiFatturaEntityList) {
            datiFatturaManager.aggiornaStatoFatturaEsito(dfe.getIdDatiFattura(), statoFattura);
        }
    }

    public void salvaNotificaAccettazioneRifiuto(Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        Message msg = msgExchange.getIn();

        String tipoOperazione = (String) msg.getHeader(TIPO_OPERAZIONE_HEADER);
        String nomeFileNotificaEsito = (String) msg.getHeader(NOME_FILE_NOTIFICA_ESITO_HEADER);

        if ("ricezioneNotifica".equals(tipoOperazione)) {
            //Salvo la notifica di accettazione/rifiuto
            notificaFromEntiManager.salvaNotificaECPec(msg.getBody().toString(), nomeFileNotificaEsito);
        }
    }

    public void preparaMailEsitoOperazione(Exchange exchange) throws AddressException {

        LOG.info("PecCANotificaEsitoCommittente - preparaMailEsitoOperazione STARTED");

        Message msg = exchange.getIn();

        String headerEmailMittente = "";
        String replyTo = (String) msg.getHeader(SENDER_ADDRESS_HEADER_REPLY_TO);
        String returnPath = (String) msg.getHeader(SENDER_ADDRESS_RETURN_PATH_HEADER);

        if (replyTo != null && !"".equals(replyTo)) {

            headerEmailMittente = replyTo;
        } else if (returnPath != null && !"".equals(returnPath)) {
            headerEmailMittente = returnPath;
        }

        //Per Dopo:
        String nomeFile = (String) msg.getHeader(NOME_FILE_FATTURA_HEADER);
        String tipoErrore = (String) msg.getHeader(TIPO_ERRORE_HEADER);

        InternetAddress internetAddressSender = new InternetAddress(headerEmailMittente);
        String indirizzoDestinatario = internetAddressSender.getAddress();
        String originalSubject = (String) msg.getHeader(SUBJECT_HEADER);

        String subject = "";

        if (tipoErrore != null && !"".equals(tipoErrore)) {
            subject = "Errore - " + originalSubject;
        } else {
            subject = "Presa in carico IntermediaMarche - " + originalSubject;
        }

        String fileEsitoOperazione = (String) msg.getHeader(FILE_ESITO_OPERAZIONE);
        String nomeFileEsitoOperazione = (String) msg.getHeader(NOME_FILE_ESITO_OPERAZIONE);

        msg.removeHeaders("*");
        msg.getAttachments().clear();

        ByteArrayDataSource ds = new ByteArrayDataSource(fileEsitoOperazione.getBytes(), "text/xml");
        DataHandler dh = new DataHandler(ds);

        msg.getAttachments().put(nomeFileEsitoOperazione, dh);

        //Per Email
        msg.setHeader(SUBJECT_HEADER, subject);
        msg.setHeader(SENDER_ADDRESS_RETURN_PATH_HEADER, indirizzoDestinatario);
        msg.setHeader(TIPO_ERRORE_HEADER, tipoErrore);

        //Per Fatt
        msg.setHeader(NOME_FILE_FATTURA_HEADER, nomeFile);
    }

    public void setHeadersEsitoOperazione(Exchange exchange) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        LOG.info("PecCANotificaEsitoCommittente - setHeadersEsitoOperazione STARTED");

        Message msg = exchange.getIn();

        String nomeFileEsitoOperazione = "IM_FE_CP_001.xml";
        String codiceEsito = (String) msg.getHeader(COD_ESITO_ESITO_OPERAZIONE);
        String descrizioneEsito = (String) msg.getHeader(DESCRIZIONE_ESITO_ESITO_OPERAZIONE);
        String subject = (String) msg.getHeader(SUBJECT_HEADER);
        String numeroFatturaEsito = (String) msg.getHeader(NUMERO_FATTURA_ESITO_OPERAZIONE);

        if (codiceEsito == null || "".equals(codiceEsito)) {
            codiceEsito = EsitoNotificaToEntiResponseCodeType.EN_00.value();
            descrizioneEsito = EsitoNotificaToEntiResponseDescriptionType.NOTIFICA_PRESA_IN_CARICO.value();
        }

        String nomeFileFattura = (String) msg.getHeader(NOME_FILE_FATTURA_HEADER);

        if (nomeFileFattura == null || "".equals(nomeFileFattura)) {

            String idSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);

            if (idSdi != null && !"".equals(idSdi)) {

                List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(idSdi));

                if (datiFatturaEntityList == null || datiFatturaEntityList.isEmpty()) {

                    throw new FatturaPAFatturaNonTrovataException();

                } else {

                    nomeFileFattura = datiFatturaEntityList.get(0).getNomeFile();
                    numeroFatturaEsito = datiFatturaEntityList.get(0).getNumeroFattura();
                }
            }
        }

        msg.setHeader(NOME_FILE_ESITO_OPERAZIONE, nomeFileEsitoOperazione);
        msg.setHeader(COD_ESITO_ESITO_OPERAZIONE, codiceEsito);
        msg.setHeader(DESCRIZIONE_ESITO_ESITO_OPERAZIONE, descrizioneEsito);
        msg.setHeader(NOME_FILE_FATTURA_ESITO_OPERAZIONE, nomeFileFattura);
        msg.setHeader(NUMERO_FATTURA_ESITO_OPERAZIONE, numeroFatturaEsito);
    }

    public void setHeadersEsitoOperazioneErrore(Exchange exchange) throws Exception {

        LOG.info("PecCANotificaEsitoCommittente - setHeadersEsitoOperazioneErrore STARTED");

        Message msg = exchange.getIn();

        String subject = (String) msg.getHeader(SUBJECT_HEADER);
        subject = subject.trim();

        if (subject.toLowerCase().contains(SUBJECT_POSTA_CERTIFICATA)) {
            subject = subject.replaceAll("POSTA CERTIFICATA:", "").trim();
        }

//      String esitoRisposta = (String) msg.getHeader(ESITO_RISPOSTA_HEADER);
        String nomeFileFattura = (String) msg.getHeader(NOME_FILE_FATTURA_HEADER);
        String numeroFattura = (String) msg.getHeader(NUMERO_FATTURA_ESITO_OPERAZIONE);

        if (nomeFileFattura == null || "".equals(nomeFileFattura)) {

            String idSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);

            if (idSdi == null || "".equals(idSdi)) {

                //Provo a ricavare l'idSdi dal Subject
                String[] split = subject.split("-");
                idSdi = split[0].trim();

                //Verifico l'identificativo sdi
                try {
                    BigInteger sdiBigInteger = new BigInteger(idSdi);
                } catch (Exception e) {
                    idSdi = null;
                }
            }

            if (idSdi != null && !"".equals(idSdi)) {

                List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(idSdi));

                if (datiFatturaEntityList == null || datiFatturaEntityList.isEmpty()) {
                    nomeFileFattura = "";
                    numeroFattura = "";
                } else {
                    nomeFileFattura = datiFatturaEntityList.get(0).getNomeFile();
                    numeroFattura = datiFatturaEntityList.get(0).getNumeroFattura();
                }
            } else {
                nomeFileFattura = "";
                numeroFattura = "";
            }
        }

        msg.setHeader(SUBJECT_HEADER, subject);
        msg.setHeader(NUMERO_FATTURA_ESITO_OPERAZIONE, numeroFattura);
        msg.setHeader(NOME_FILE_FATTURA_HEADER, nomeFileFattura);
    }

    public String getProviderEmarche() {
        return providerEmarche;
    }

    public void setProviderEmarche(String providerEmarche) {
        this.providerEmarche = providerEmarche;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getMailTransportProtocol() {
        return mailTransportProtocol;
    }

    public void setMailTransportProtocol(String mailTransportProtocol) {
        this.mailTransportProtocol = mailTransportProtocol;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public NotificaFromEntiManager getNotificaFromEntiManager() {
        return notificaFromEntiManager;
    }

    public void setNotificaFromEntiManager(NotificaFromEntiManager notificaFromEntiManager) {
        this.notificaFromEntiManager = notificaFromEntiManager;
    }
}