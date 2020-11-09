package it.extrasys.marche.regione.fatturapa.enti.bridge;


import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.EsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
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
import javax.mail.MessagingException;
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

/**
 * Created by agosteeno on 14/04/15.
 */

public class PecNotificaEsitoCommittente {

    private static final Logger LOG = LoggerFactory.getLogger(PecNotificaEsitoCommittente.class);

    private static final String SENDER_ADDRESS_RETURN_PATH_HEADER = "Return-Path";
    private static final String SENDER_ADDRESS_HEADER_REPLY_TO = "Reply-To";
    private static final String EMAIL_DA_IGNORARE = "emailDaIgnorare";
    private static final String SUBJECT_HEADER = "subject";
    private static final String SUBJECT_ACCETTAZIONE = "accettazione";
    private static final String SUBJECT_CONSEGNA = "consegna";
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

    private String providerEmarche;
    private String mailHost;
    private String mailTransportProtocol;

    private EnteManager enteManager;

    private DatiFatturaManager datiFatturaManager;

    private NotificaFromEntiManager notificaFromEntiManager;

    public void estraiMessaggioAccettazioneRifiuto(Exchange exchange) throws MessagingException, IOException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException, JAXBException, FatturaPAFatturaNonTrovataException {

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

        if(replyTo != null && !"".equals(replyTo)){
            headerEmailMittente = replyTo;
        } else if(returnPath != null && !"".equals(returnPath)){
            headerEmailMittente = returnPath;
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

        subjectPec = subjectPec.trim().toLowerCase();

        if(subjectPec.contains(SUBJECT_ACCETTAZIONE) || subjectPec.contains(SUBJECT_CONSEGNA)){

            /*
            email da ignorare_ e' una di quelle di servizio dei provider PEC
             */

            msg.setHeader(EMAIL_DA_IGNORARE, true);
            return;
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
                                //file non conforme, potrebbe essere un allegato in piu' messo dal provider di posta; semplicemente ignoriamo
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

            msg.setHeader(ERRORE_NOME_ALLEGATO, ERRORE_NOME_ALLEGATO);

        } else {

            InputStream is = fileNotifica.getInputStream();
            byte[] fileNotificaByteArray= IOUtils.toByteArray(is);

            NotificaEsitoCommittenteType notificaEsitoCommittenteType = JaxBUtils.getNotificaEsitoCommittenteType(fileNotificaByteArray);

            BigInteger identificativoSdi = notificaEsitoCommittenteType.getIdentificativoSdI();
            msg.setHeader(IDENTIFICATIVO_SDI_HEADER, identificativoSdi.toString());
            String tipoNotifica = notificaEsitoCommittenteType.getEsito().value();

            msg.setHeader(TIPO_MESSAGGIO, tipoNotifica);

            //regma112 oltre al controllo dell'ente vedo anche se la fattura e' di tipo interno
            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdi);

            if(!checkEnte(indirizzoEmailMittente, datiFatturaEntityList, identificativoSdi)){
                throw new FatturaPAEnteNonTrovatoException("Ente non trovato per indirizzo " + indirizzoEmailMittente);
            }

            final InputStream in = fileNotifica.getInputStream();
            byte[] fileNotificaOriginale = IOUtils.toByteArray(in);

            /*msg.setHeader(ID_ENTE_MITTENTE_HEADER, enteEntity.getIdEnte());
            msg.setHeader(CODICE_UFFICIO_HEADER, enteEntity.getCodiceUfficio());*/

            msg.setHeader(NOME_FILE_NOTIFICA_ESITO_HEADER, nomeFileNotifica);

            String messaggioEncoded = new String(Base64.encodeBase64(fileNotificaOriginale));
            msg.setBody(messaggioEncoded);


            if(datiFatturaEntityList.get(0).getFatturazioneInterna()){
                msg.setHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER, CHECK_FLUSSO_SEMPLIFICATO_HEADER);
                msg.setHeader(NOME_FILE_FATTURA_HEADER, datiFatturaEntityList.get(0).getNomeFile());
            }

        }
    }

    private boolean checkEnte(String indirizzoEmailMittente, List<DatiFatturaEntity> datiFatturaEntityList, BigInteger identificativoSdi) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAFatturaNonTrovataException {
        //List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdi);

        String codiceUfficioPerFattura = "";

        if(datiFatturaEntityList != null && datiFatturaEntityList.size() >= 1){

            codiceUfficioPerFattura = datiFatturaEntityList.get(0).getCodiceDestinatario();

        } else {
            LOG.info("PecNotificaEsitoCommittente - estraiMessaggioAccettazioneRifiuto: Fattura non trovate per l'identificativoSdI: " + identificativoSdi);
            throw new FatturaPAFatturaNonTrovataException("Fattura non trovate per l'identificativoSdI: " + identificativoSdi);
        }

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficioPerFattura);

        String listaEmailPec = enteEntity.getEmailPec();

        if(!checkEmailPec(listaEmailPec, indirizzoEmailMittente)){
            LOG.info("PecNotificaEsitoCommittente - estraiMessaggioAccettazioneRifiuto: Indirizzo mittente non autorizzato: " + indirizzoEmailMittente);
            return false;
        }
        return true;
    }

    private boolean checkEmailPec(String listaEmailPec, String indirizzoEmailMittente) {

        if (listaEmailPec == null || "".equals(listaEmailPec) || indirizzoEmailMittente == null || "".equals(indirizzoEmailMittente)){
            return false;
        }

        List<String> emails = new ArrayList<String>(Arrays.asList(listaEmailPec.split(",")));

        boolean found = false;
        for(String s : emails) {
            if (s.trim().toLowerCase().equals(indirizzoEmailMittente.trim().toLowerCase())) {
                found =  true;
                break;
            }

        }
        return found;
    }

   /* metodo per provare al volo un dao
   public void testEnteManagerPecAddress(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        String emailDaTestare = "extratest@pec.it";

        List<EnteEntity> enteEntityList = enteManager.getEnteByPecAddress(emailDaTestare);

        int size = enteEntityList.size();
        LOG.info("Risultato: enteEntity size: " + size);
        if(size != 0){
            LOG.info("Risultato: enteEntity codiceUfficio: " + enteEntityList.get(0).getCodiceUfficio());
        }
    }*/

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

        if(replyTo != null && !"".equals(replyTo)){
            headerEmailMittente = replyTo;
        } else if(returnPath != null && !"".equals(returnPath)){
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

        for(String s : msg.getAttachmentNames()){

            String tmp = new String(s);
            setNames.add(tmp);
        }

        for(String s : setNames) {
            if(!s.equals(nomeFileAllegato)){
                msg.removeAttachment(s);
            }
        }

        msg.getAttachments().clear();

        for (Map.Entry<String, DataHandler> entry : attachmentsMap.entrySet()) {

            DataHandler dh = entry.getValue();
            final InputStream in = dh.getInputStream();
            byte[] byteArray=org.apache.commons.io.IOUtils.toByteArray(in);

            String contentType = dh.getContentType();

            String estensioneFile = FileUtils.getFileExtension(entry.getKey()).toLowerCase();

            DataSource ds = new ByteArrayDataSource(byteArray, contentType);
            DataHandler newDh = new DataHandler(ds);

            msg.getAttachments().put("originalAttachment-"+entry.getKey(), newDh);

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
        //String nomeFileNotificaEsito = (String) msg.getHeader(NOME_FILE_NOTIFICA_ESITO_HEADER);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        String statoFattura;

        if(tipoMessaggio.equals(EsitoCommittenteType.EC_01.value())){
            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_ACCETTAZIONE.getValue();
        } else if(tipoMessaggio.equals(EsitoCommittenteType.EC_02.value())){
            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTO_RIFIUTO.getValue();
        } else {
            throw new FatturaPAException("PecManager - aggiornaFattura: Tipo messaggio non riconosciuto");
        }

        for(DatiFatturaEntity dfe : datiFatturaEntityList){
            datiFatturaManager.aggiornaStatoFatturaEsito(dfe.getIdDatiFattura(), statoFattura);
        }
    }

    public void salvaNotificaAccettazioneRifiuto(Exchange msgExchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException {

        Message msg = msgExchange.getIn();

        String nomeFileNotificaEsito = (String) msg.getHeader(NOME_FILE_NOTIFICA_ESITO_HEADER);

        //Salvo la notifica di accettazione/rifiuto
        notificaFromEntiManager.salvaNotificaECPec(msg.getBody().toString(), nomeFileNotificaEsito);
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
