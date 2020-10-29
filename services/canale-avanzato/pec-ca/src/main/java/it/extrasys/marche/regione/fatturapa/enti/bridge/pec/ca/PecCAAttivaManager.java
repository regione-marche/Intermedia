package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.CodInvioCAType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.DescInvioCAType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeFattura;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class PecCAAttivaManager {

    private static final Logger LOG = LoggerFactory.getLogger(PecCAAttivaManager.class);

    private static final String SENDER_ADDRESS_RETURN_PATH_HEADER = "Return-Path";
    private static final String SENDER_ADDRESS_HEADER_REPLY_TO = "Reply-To";
    private static final String ID_ENTE_MITTENTE_HEADER = "idEnteMittente";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFile";
    private static final String ID_FISCALE_COMMITTENTE_HEADER = "idFiscaleCommittente";
    private static final String CODICE_UFFICIO_HEADER = "codiceUfficio";
    private static final String ID_FATTURA_ATTIVA_HEADER = "idFatturaAttiva";
    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String SUBJECT_ACCETTAZIONE = "accettazione";
    private static final String SUBJECT_CONSEGNA = "consegna";
    private static final String SUBJECT_POSTA_CERTIFICATA = "posta certificata:";

    private static final String ID_NOTIFICA_HEADER = "idNotifica";

    private static final String ID_ENTE_DESTINATARIO = "idEnteDestinatario";

    private static final String TO_HEADER = "to";
    private static final String NOME_ENTE_HEADER = "nomeEnte";
    private static final String SUBJECT_HEADER = "subject";
    private static final String SUBJECT_ORIGINAL_HEADER = "subjectOriginal";
    private static final String TIPO_ERRORE_HEADER = "tipoErrore";
    private static final String EMAIL_DA_IGNORARE = "emailDaIgnorare";

    private static final String NOME_FILE_ESITO_OPERAZIONE = "nomeFileEsitoOperazione";
    private static final String DATA_RICEZIONE_ESITO_OPERAZIONE = "dataRicezione";
    private static final String COD_ESITO_ESITO_OPERAZIONE = "codiceEsito";
    private static final String DESCRIZIONE_ESITO_ESITO_OPERAZIONE = "descrizioneEsito";
    private static final String NOME_FILE_FATTURA_ESITO_OPERAZIONE = "nomeFileFattura";
    private static final String RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE = "ricevutaComunicazione";
    private static final String FILE_ESITO_OPERAZIONE = "fileEsitoOperazione";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private String providerEmarche;

    private String mailHost;
    private String mailTransportProtocol;

    private FatturaAttivaManagerImpl fatturaAttivaFromEntiManager;

    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;

    private EnteManager enteManager;

    public void salvaFatturaDb(Exchange exchange) throws Exception {

        LOG.info("PecCAAttivaManager - salvaFattura STARTED");

        Message msg = exchange.getIn();

        Map<String, DataHandler> attachmentsMap = msg.getAttachments();

        int fatturaTrovata = 0;
        String nomeFileFattura = "";
        DataHandler fileFattura = null;
        String fileNameDecoded = "";

        String headerEmailMittente = "";

        String replyTo = (String) msg.getHeader(SENDER_ADDRESS_HEADER_REPLY_TO);
        String returnPath = (String) msg.getHeader(SENDER_ADDRESS_RETURN_PATH_HEADER);
        if(replyTo != null && !"".equals(replyTo)){

            headerEmailMittente = replyTo;
            LOG.info("PEC CA ATTIVA MANAGER: indirizzoEmailMittente: case reply to " + headerEmailMittente);
        } else if(returnPath != null && !"".equals(returnPath)){
            headerEmailMittente = returnPath;
            LOG.info("PEC CA ATTIVA MANAGER: indirizzoEmailMittente: case return path " + headerEmailMittente);
        }

        InternetAddress internetAddressSender = new InternetAddress(headerEmailMittente);

        String indirizzoEmailMittente = internetAddressSender.getAddress();
        msg.setHeader(SENDER_ADDRESS_RETURN_PATH_HEADER, indirizzoEmailMittente);

        if(indirizzoEmailMittente.equals(providerEmarche)){
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

        if(subjectPec.toLowerCase().contains(SUBJECT_ACCETTAZIONE) || subjectPec.toLowerCase().contains(SUBJECT_CONSEGNA)){

            /*
            email da ignorare_ e' una di quelle di servizio dei provider PEC
             */

            msg.setHeader(EMAIL_DA_IGNORARE, true);
            return;
        }

        if(subjectPec.toLowerCase().contains(SUBJECT_POSTA_CERTIFICATA)){
            subjectPec = subjectPec.replaceAll("POSTA CERTIFICATA:", "").trim();
        }

        msg.setHeader(SUBJECT_HEADER, subjectPec);

        if(!ValidatoreNomeFattura.validate(subjectPec)){
            throw new FatturaPAValidazioneOggettoFallitaException();
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

                            if (!ValidatoreNomeFattura.validate(fileNameDecoded)) {
                                //file non conforme, potrebbe essere un allegato in piu' messo dal provider di posta

                                if(fileNameDecoded.contains(".xml")){
                                    fatturaTrovata++;
                                }

                            } else {
                                fatturaTrovata++;
                                nomeFileFattura = fileNameDecoded;
                                fileFattura = fileAllegato;
                            }
                        }
                    }
                }
            }
        }

        //String ricevutaComunicazione = createRicevutaComunicazione();
        String ricevutaComunicazione = CommonUtils.createRicevutaComunicazione();
        msg.setHeader(RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE, ricevutaComunicazione);

        //EnteEntity enteEntity = mailInToEntiAttivaManager.getEnteFromIndirizzoEmail(indirizzoEmailMittente);
        EnteEntity enteEntity = enteManager.getEnteByEndpointAttivaCA(indirizzoEmailMittente);

        //Controllo se è un ente di TEST
        Boolean isTest=Boolean.FALSE;
        if("STAGING".equalsIgnoreCase(enteEntity.getAmbienteCicloAttivo())) {
            isTest=Boolean.TRUE;
        }
        exchange.getIn().setHeader("fatturazioneTest", isTest);

        if (fatturaTrovata != 1) {
            throw new FatturaPAAllegatoAttivaNonTrovatoException();
        } else {

            if(!ValidatoreNomeFattura.validate(nomeFileFattura)){
                throw new FatturaPANomeFileErratoException();
            }

            final InputStream in = fileFattura.getInputStream();
            byte[] fileFattureOriginale = IOUtils.toByteArray(in);

            String formatoTrasmissione = null;
            String codiceUfficioInternoFattura  = null;
            String pecDestinatario = null;

            try {

                String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFileFattura, fileFattureOriginale);

                //TODO REVO-3
                FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);
                formatoTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getFormatoTrasmissione().value();
                codiceUfficioInternoFattura = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario();
                pecDestinatario = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getPECDestinatario();

                /**
                 * NOTA in questo punto si deve aggiungere un controllo sulla dimensione del file, che non superi una dimensione particolare
                 */
            }catch (Exception ex){
                throw new FatturaPAValidazioneFallitaException();
            }

            msg.setHeader(ID_ENTE_MITTENTE_HEADER, enteEntity.getIdEnte());
            msg.setHeader(ID_FISCALE_COMMITTENTE_HEADER, enteEntity.getIdFiscaleCommittente());
            msg.setHeader(CODICE_UFFICIO_HEADER, enteEntity.getCodiceUfficio());

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaFromEntiManager.salvaFatturaAttiva(fileFattureOriginale, nomeFileFattura, enteEntity, formatoTrasmissione, codiceUfficioInternoFattura, pecDestinatario, ricevutaComunicazione, isTest);

            String idFatturaAttiva = fatturaAttivaEntity.getIdFatturaAttiva().toString();
            msg.setHeader(ID_FATTURA_ATTIVA_HEADER, idFatturaAttiva);

            msg.setHeader(NOME_FILE_FATTURA_HEADER, nomeFileFattura);

            String messaggioEncoded = new String(Base64.encode(fileFattureOriginale));
            msg.setBody(messaggioEncoded);
        }
    }

    public void inviaNotificaEnte(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        Message msg = exchange.getIn();

        BigInteger idEnte = new BigInteger((String)msg.getHeader(ID_ENTE_DESTINATARIO));

        EnteEntity enteEntity = enteManager.getEnteById(idEnte);

        String nomeFile = (String) msg.getHeader(NOME_FILE_FATTURA_HEADER);

        if(enteEntity.getEndpointNotificheAttivaCa() == null) {
            throw new FatturaPAException("EndpointNotificheAttivaCa non trovato per Ente con Codice Ufficio = " + enteEntity.getCodiceUfficio());
        }

        //La lista degli indirizzi deve essere saparata da ","
        String listaEmailInvio = enteEntity.getEndpointNotificheAttivaCa().getEndpoint();

        msg.removeHeader("To");

        msg.setHeader(TO_HEADER, listaEmailInvio);
        msg.setHeader(NOME_ENTE_HEADER, enteEntity.getNome());

        try {

            byte[] messaggioRicevuto = Base64.decode((String) msg.getBody());

            DataSource dataSource = new ByteArrayDataSource(messaggioRicevuto, "application/xml");
            DataHandler dh = new DataHandler(dataSource);

            msg.addAttachment(nomeFile, dh);

            msg.setBody(XAdESUnwrapper.unwrap(messaggioRicevuto));

        } catch (ParserConfigurationException e) {
            throw new FatturaPAException(e.getMessage() + e.getCause(), e);
        } catch (IOException e) {
            throw new FatturaPAException(e.getMessage() + e.getCause(), e);
        } catch (SAXException e) {
            throw new FatturaPAException(e.getMessage() + e.getCause(), e);
        } catch (XPathExpressionException e) {
            throw new FatturaPAException(e.getMessage() + e.getCause(), e);
        } catch (TransformerException e) {
            throw new FatturaPAException(e.getMessage() + e.getCause(), e);
        }
    }

    public void aggiornaStatoNotifica(Exchange exchange) throws FatturaPAException {

        LOG.info("PecCAAttivaManager - aggiornaStatoNotifica STARTED");

        Message msg = exchange.getIn();
        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO_HEADER);

        String statoInviata = CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue();

        String headerIdNotifica = (String) msg.getHeader(ID_NOTIFICA_HEADER);
        BigInteger idNotifica = new BigInteger(headerIdNotifica);

        if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoRiceviConsegna(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoMancataConsegna(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoNotificaScarto(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoNotificaEsito(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoNotificaDecorrenzaTermini(statoInviata, idNotifica);

        } else if(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.getValue().equals(tipoMessaggio)){

            fatturaAttivaNotificheManager.aggiornaStatoAttestazioneTrasmissioneFattura(statoInviata, idNotifica);
        }
    }

    public void setHeadersEsitoOperazione(Exchange exchange) {

        LOG.info("PecCAAttivaManager - setHeadersEsitoOperazione STARTED");

        Message msg = exchange.getIn();

        String nomeFileEsitoOperazione = "IM_FE_CA_001.xml";
        String codiceEsito = (String) msg.getHeader(COD_ESITO_ESITO_OPERAZIONE);
        String descrizioneEsito = (String) msg.getHeader(DESCRIZIONE_ESITO_ESITO_OPERAZIONE);
        String subject = (String) msg.getHeader(SUBJECT_HEADER);
        String ricevutaComunicazione = (String) msg.getHeader(RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE);

        if(codiceEsito == null || "".equals(codiceEsito)){
            codiceEsito = CodInvioCAType.FA_00.value();
            descrizioneEsito = DescInvioCAType.RICEZIONE_AVVENUTA_CON_SUCCESSO.value();
        }

        String nomeFileFattura = (String) msg.getHeader(NOME_FILE_FATTURA_HEADER);

        if(nomeFileFattura == null || "".equals(nomeFileFattura)){

            nomeFileFattura = subject;

        }

        msg.setHeader(DATA_RICEZIONE_ESITO_OPERAZIONE, sdf.format(new Date()));
        msg.setHeader(NOME_FILE_ESITO_OPERAZIONE, nomeFileEsitoOperazione);
        msg.setHeader(COD_ESITO_ESITO_OPERAZIONE, codiceEsito);
        msg.setHeader(DESCRIZIONE_ESITO_ESITO_OPERAZIONE, descrizioneEsito);
        msg.setHeader(NOME_FILE_FATTURA_ESITO_OPERAZIONE, nomeFileFattura);
        msg.setHeader(RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE, ricevutaComunicazione);
    }

    public void setHeadersEsitoOperazioneErrore(Exchange exchange) {

        LOG.info("PecCAAttivaManager - setHeadersEsitoOperazioneErrore STARTED");

        Message msg = exchange.getIn();

        String subject = (String) msg.getHeader(SUBJECT_HEADER);
        subject = subject.trim();

        if(subject.toLowerCase().contains(SUBJECT_POSTA_CERTIFICATA)){
            subject = subject.replaceAll("POSTA CERTIFICATA:", "").trim();
        }

        //String ricevutaComunicazione = createRicevutaComunicazione();
        String ricevutaComunicazione = CommonUtils.createRicevutaComunicazione();

        String nomeFileFattura = "";

        try {
            String fileNameDecoded = "";

            Map<String, DataHandler> attachmentsMap = msg.getAttachments();
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

                                if (!ValidatoreNomeFattura.validate(fileNameDecoded)) {
                                    //file non conforme, potrebbe essere un allegato in piu' messo dal provider di posta
                                } else {
                                    nomeFileFattura = fileNameDecoded;
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            nomeFileFattura = "";
        }

        msg.setHeader(SUBJECT_HEADER, subject);
        msg.setHeader(RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE, ricevutaComunicazione);
        msg.setHeader(NOME_FILE_FATTURA_HEADER, nomeFileFattura);
    }

    /*
    private String createRicevutaComunicazione(){

        String prefisso = "IntermediaMarche";
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String dateTime = sdf.format(new Date());

        return prefisso + "_" + uuid + "_" + dateTime;
    }
    */

    public void preparaMailEsitoOperazione(Exchange exchange) throws AddressException {

        LOG.info("PecCAAttivaManager - preparaMailEsitoOperazione STARTED");

        Message msg = exchange.getIn();

        String headerEmailMittente = "";
        String replyTo = (String) msg.getHeader(SENDER_ADDRESS_HEADER_REPLY_TO);
        String returnPath = (String) msg.getHeader(SENDER_ADDRESS_RETURN_PATH_HEADER);
        String tipoErrore = (String) msg.getHeader(TIPO_ERRORE_HEADER);

        if(replyTo != null && !"".equals(replyTo)){

            headerEmailMittente = replyTo;
        } else if(returnPath != null && !"".equals(returnPath)){
            headerEmailMittente = returnPath;
        }

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

        //Per Dopo:
        String codiceUfficio = (String) msg.getHeader(CODICE_UFFICIO_HEADER);
        String idEnteMittente = (String) msg.getHeader(ID_ENTE_MITTENTE_HEADER);
        String idFatturaAttiva = (String) msg.getHeader(ID_FATTURA_ATTIVA_HEADER);
        String idFiscaleCommittente = (String) msg.getHeader(ID_FISCALE_COMMITTENTE_HEADER);
        String nomeFile = (String) msg.getHeader(NOME_FILE_FATTURA_HEADER);
        //String tipoErrore = (String) msg.getHeader(TIPO_ERRORE_HEADER);

        msg.removeHeaders("*");
        msg.getAttachments().clear();

        ByteArrayDataSource ds = new ByteArrayDataSource(fileEsitoOperazione.getBytes(), "text/xml");
        DataHandler dh = new DataHandler(ds);

        msg.getAttachments().put(nomeFileEsitoOperazione, dh);

        //Per Email
        msg.setHeader(SUBJECT_HEADER, subject);
        msg.setHeader(SENDER_ADDRESS_RETURN_PATH_HEADER, indirizzoDestinatario);
        msg.setHeader(TIPO_ERRORE_HEADER, tipoErrore);

        //Per Fatt. Att.
        msg.setHeader(CODICE_UFFICIO_HEADER, codiceUfficio);
        msg.setHeader(ID_ENTE_MITTENTE_HEADER, idEnteMittente);
        msg.setHeader(ID_FATTURA_ATTIVA_HEADER, idFatturaAttiva);
        msg.setHeader(ID_FISCALE_COMMITTENTE_HEADER, idFiscaleCommittente);
        msg.setHeader(NOME_FILE_FATTURA_HEADER, nomeFile);
    }

    public void preparaFatturaPerValidazione(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        byte[] fatturaElettronicaBytesArray = org.apache.commons.codec.binary.Base64.decodeBase64(msg.getBody(String.class));

        String nomeFile = msg.getHeader(NOME_FILE_FATTURA_HEADER, String.class);

        String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFile, fatturaElettronicaBytesArray);

        FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);

        String fileFatturaString = JaxBUtils.getFatturaElettronicaAsString(fatturaElettronicaType);

        msg.setBody(fileFatturaString);
    }

    public FatturaAttivaManagerImpl getFatturaAttivaFromEntiManager() {
        return fatturaAttivaFromEntiManager;
    }

    public void setFatturaAttivaFromEntiManager(FatturaAttivaManagerImpl fatturaAttivaFromEntiManager) {
        this.fatturaAttivaFromEntiManager = fatturaAttivaFromEntiManager;
    }

    public String getMailHost () {
        return mailHost;
    }

    public void setMailHost (String mailHost){
        this.mailHost = mailHost;
    }

    public String getMailTransportProtocol () {
        return mailTransportProtocol;
    }

    public void setMailTransportProtocol (String mailTransportProtocol){
        this.mailTransportProtocol = mailTransportProtocol;
    }

    public FatturaAttivaNotificheManager getFatturaAttivaNotificheManager() {
        return fatturaAttivaNotificheManager;
    }

    public void setFatturaAttivaNotificheManager(FatturaAttivaNotificheManager fatturaAttivaNotificheManager) {
        this.fatturaAttivaNotificheManager = fatturaAttivaNotificheManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public String getProviderEmarche() {
        return providerEmarche;
    }

    public void setProviderEmarche(String providerEmarche) {
        this.providerEmarche = providerEmarche;
    }
}