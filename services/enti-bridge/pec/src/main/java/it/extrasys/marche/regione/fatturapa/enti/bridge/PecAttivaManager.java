package it.extrasys.marche.regione.fatturapa.enti.bridge;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAAllegatoAttivaNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeFattura;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.MailInToEntiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaNotificheManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MailInToEntiAttivaManager;
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
import java.util.*;

/**
 * Created by agosteeno on 14/03/15.
 */
public class PecAttivaManager {

    private static final Logger LOG = LoggerFactory.getLogger(PecAttivaManager.class);

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

    private static final String ID_NOTIFICA_HEADER = "idNotifica";

    private static final String ID_ENTE_DESTINATARIO = "idEnteDestinatario";

    private static final String TO_HEADER = "to";
    private static final String NOME_ENTE_HEADER = "nomeEnte";
    private static final String SUBJECT_HEADER = "subject";
    private static final String SUBJECT_ORIGINAL_HEADER = "subjectOriginal";
    private static final java.lang.String TIPO_ERRORE_HEADER = "tipoErrore";
    private static final String EMAIL_DA_IGNORARE = "emailDaIgnorare";

    private String providerEmarche;

    private String mailHost;
    private String mailTransportProtocol;

    private MailInToEntiAttivaManager mailInToEntiAttivaManager;

    private FatturaAttivaManagerImpl fatturaAttivaFromEntiManager;

    private FatturaAttivaNotificheManager fatturaAttivaNotificheManager;

    private EnteManager enteManager;

    public void salvaFatturaDb(Exchange exchange) throws Exception {
        LOG.info("PecAttivaManager - salvaFattura STARTED");

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
            LOG.info("PEC ATTIVA MANAGER: indirizzoEmailMittente: case reply to " + headerEmailMittente);
        } else if(returnPath != null && !"".equals(returnPath)){
            headerEmailMittente = returnPath;
            LOG.info("PEC ATTIVA MANAGER: indirizzoEmailMittente: case return path " + headerEmailMittente);
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

                            if (!ValidatoreNomeFattura.validate(fileNameDecoded)) {
                                //file non conforme, potrebbe essere un allegato in piu' messo dal provider di posta
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

        EnteEntity enteEntity = mailInToEntiAttivaManager.getEnteFromIndirizzoEmail(indirizzoEmailMittente);

        //Controllo se è un ente di TEST
        Boolean isTest=Boolean.FALSE;
        if("STAGING".equalsIgnoreCase(enteEntity.getAmbienteCicloAttivo())) {
            isTest=Boolean.TRUE;
        }
        exchange.getIn().setHeader("fatturazioneTest", isTest);

        if (fatturaTrovata != 1) {

            throw new FatturaPAAllegatoAttivaNonTrovatoException();
        } else {

            final InputStream in = fileFattura.getInputStream();
            byte[] fileFattureOriginale = IOUtils.toByteArray(in);

            String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFileFattura, fileFattureOriginale);

            //TODO REVO-3
            FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);
            String formatoTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getFormatoTrasmissione().value();
            String codiceUfficioInternoFattura = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario();
            String pecDestinatario = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getPECDestinatario();

            /**
             * NOTA in questo punto si deve aggiungere un controllo sulla dimensione del file, che non superi una dimensione particolare
             */

            msg.setHeader(ID_ENTE_MITTENTE_HEADER, enteEntity.getIdEnte());
            msg.setHeader(ID_FISCALE_COMMITTENTE_HEADER, enteEntity.getIdFiscaleCommittente());
            msg.setHeader(CODICE_UFFICIO_HEADER, enteEntity.getCodiceUfficio());

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaFromEntiManager.salvaFatturaAttiva(fileFattureOriginale, nomeFileFattura, enteEntity, formatoTrasmissione, codiceUfficioInternoFattura, pecDestinatario, null, isTest);

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

        List<MailInToEntiAttivaEntity> mailInToEntiAttivaEntities = mailInToEntiAttivaManager.getMailInToEntiAttivaEntitiesByEnte(enteEntity);

        String listaEmailInvio = getListaEmailFromMailInToEntiAttivaEntities(mailInToEntiAttivaEntities);

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

        LOG.info("PecAttivaManager - aggiornaStatoNotifica STARTED");

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

    private String getListaEmailFromMailInToEntiAttivaEntities(List<MailInToEntiAttivaEntity> mailInToEntiAttivaEntities) {

        String listaNomi = "";

        int count = 1;
        int size = mailInToEntiAttivaEntities.size();

        for(MailInToEntiAttivaEntity meae : mailInToEntiAttivaEntities){
            listaNomi = listaNomi + meae.getEmail();
            if(count < size){
                listaNomi = listaNomi + ",";
            }
            count++;
        }

        return listaNomi;
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
        String subject = (String) msg.getHeader(SUBJECT_HEADER);
        String tipoErrore = (String) msg.getHeader(TIPO_ERRORE_HEADER);

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
            nomeFile = nomeFile + s + ", ";
            msg.removeAttachment(s);
        }

        msg.getAttachments().clear();

        for (Map.Entry<String, DataHandler> entry : attachmentsMap.entrySet()) {

            DataHandler dh = entry.getValue();
            final InputStream in = dh.getInputStream();
            byte[] byteArray=org.apache.commons.io.IOUtils.toByteArray(in);

            String contentType = dh.getContentType();

            ByteArrayDataSource ds = new ByteArrayDataSource(byteArray, contentType);
            DataHandler newDh = new DataHandler(ds);

            msg.getAttachments().put("originalAttachment-"+entry.getKey(), newDh);

        }

        msg.setHeader(SENDER_ADDRESS_RETURN_PATH_HEADER, indirizzoDestinatario);
        msg.setHeader(SUBJECT_ORIGINAL_HEADER, subject);
        msg.setHeader(NOME_FILE_FATTURA_HEADER, nomeFile);
        msg.setHeader(TIPO_ERRORE_HEADER, tipoErrore);
    }

    public MailInToEntiAttivaManager getMailInToEntiAttivaManager () {
        return mailInToEntiAttivaManager;
    }

    public void setMailInToEntiAttivaManager (MailInToEntiAttivaManager mailInToEntiAttivaManager){
        this.mailInToEntiAttivaManager = mailInToEntiAttivaManager;
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