package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.attiva.processors;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FormatoTrasmissioneType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaDecorrenzaTerminiType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.RicevutaConsegnaType;
import it.extrasys.marche.regione.fatturapa.contracts.trasmissione.fatture.sdi.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.TipoCanaleEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 21/03/15.
 */
public class EstraiMessaggioAttivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EstraiMessaggioAttivaProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdi";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";

    private static final String RICEVUTA_CONSEGNA_SOAP_ACTION = "http://www.fatturapa.it/TrasmissioneFatture/RicevutaConsegna";
    private static final String NOTIFICA_MANCANTA_CONSEGNA_SOAP_ACTION = "http://www.fatturapa.it/TrasmissioneFatture/NotificaMancataConsegna";
    private static final String NOTIFICA_SCARTO_SOAP_ACTION = "http://www.fatturapa.it/TrasmissioneFatture/NotificaScarto";
    private static final String NOTIFICA_ESITO_SOAP_ACTION = "http://www.fatturapa.it/TrasmissioneFatture/NotificaEsito";
    private static final String NOTIFICA_DECORRENZA_TERMINI_SOAP_ACTION = "http://www.fatturapa.it/TrasmissioneFatture/NotificaDecorrenzaTermini";
    private static final String ATTESTAZIONE_TRASMISSIONE_FATTURA_SOAP_ACTION = "http://www.fatturapa.it/TrasmissioneFatture/AttestazioneTrasmissioneFattura";

    private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";
    private static final String CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER = "codiceEnteDestinatarioFlussoSemplificato";
    private static final String MESSAGE_ID_FLUSSO_SEMPLIFICATO_HEADER = "messageIdFlussoSemplificato";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileFattura";
    //REVO-15 formato trasmissione della fattura attiva
    private static final String FORMATO_TRASMISSIONE_HEADER = "formatoTrasmissioneFatturaAttiva";
    private static final String TIPO_CANALE_ENTE = "tipoCanaleEnte";


    private EntityManagerFactory entityManagerFactory;
    private DatiFatturaDao datiFatturaDao;
    private FatturaAttivaManagerImpl fatturaAttivaManager;

    @Override
    public void process(Exchange exchange) throws IOException, JAXBException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException, FatturaPAException {

        LOG.info("SDI INBOUND: EstraiMessaggioAttivaProcessor - process: STARTED");

        MessageContentsList body = exchange.getIn().getBody(MessageContentsList.class);

        Message cxfMessage = exchange.getIn().getHeader(CxfConstants.CAMEL_CXF_MESSAGE, Message.class);

        String soapAction = (String) cxfMessage.get("SOAPAction");

        String tipoMessaggio = "";

        boolean checkFlussoSemplificatoRicevutaConsegna = false;
        boolean checkFlussoSemplificatoDecorrenzaTermini = false;


        if (RICEVUTA_CONSEGNA_SOAP_ACTION.equals(soapAction)) {

            tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue();

            checkFlussoSemplificatoRicevutaConsegna = true;

        } else if (NOTIFICA_MANCANTA_CONSEGNA_SOAP_ACTION.equals(soapAction)) {

            tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.getValue();

        } else if (NOTIFICA_SCARTO_SOAP_ACTION.equals(soapAction)) {

            tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.getValue();

        } else if (NOTIFICA_ESITO_SOAP_ACTION.equals(soapAction)) {

            tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue();

        } else if (NOTIFICA_DECORRENZA_TERMINI_SOAP_ACTION.equals(soapAction)) {

            tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue();

            checkFlussoSemplificatoDecorrenzaTermini = true;

        } else if (ATTESTAZIONE_TRASMISSIONE_FATTURA_SOAP_ACTION.equals(soapAction)) {

            tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.getValue();
        }

        String originalSoapMessage = null;

        if (cxfMessage != null && cxfMessage.getExchange() != null) {
            originalSoapMessage = (String) cxfMessage.getExchange().get("originalSoapMessage");

            //il messaggio originale non viene salvato
        }

        if (body != null && body.size() > 0) {

            FileSdIType fileSdIType = (FileSdIType) body.get(0);
            byte[] fileRicevuto = getMessaggioAsBytesArray(fileSdIType.getFile());
            String fileRicevutoEncoded = new String(Base64.encode(fileRicevuto));

            String identificativoSdI = fileSdIType.getIdentificativoSdI().toString();
            exchange.getIn().setHeader(IDENTIFICATIVO_SDI_HEADER, fileSdIType.getIdentificativoSdI().toString());
            exchange.getIn().setHeader(NOME_FILE_HEADER, fileSdIType.getNomeFile());
            exchange.getIn().setBody(fileRicevutoEncoded);
            exchange.getIn().setHeader(TIPO_MESSAGGIO_HEADER, tipoMessaggio);

            LOG.info("SDI INBOUND: EstraiMessaggioAttivaProcessor - process: IDENTIFICATIVO SDI " + identificativoSdI + ";");

            /**
             * REVO-15 prendo la fattura attiva da db e vedo che formato trasmissione ha: se è per pa allora faccio i check
             * del flusso semplificato, altrimenti no
             */
            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI));
            String formatoTrasmissione = fatturaAttivaEntity.getFormatoTrasmissione();

            exchange.getIn().setHeader(FORMATO_TRASMISSIONE_HEADER, formatoTrasmissione);

            LOG.info("SDI INBOUND: EstraiMessaggioAttivaProcessor - process:  ID SDI [ " + identificativoSdI + "] - Formato Trasmissione rilevato [" + formatoTrasmissione + "]");
            boolean isFtp = false;

            //Per gestire i vecchi enti che sul DB il tipo canale è stato censito in maniera errata: Es: "1" invece di "001" in questo modo JPA non riesce a fare la fetch
            if(fatturaAttivaEntity.getEnte().getTipoCanale() != null) {

                //Verifico se l'ente è FTP
                if (TipoCanaleEntity.TIPO_CANALE.CA.getValue().equals(fatturaAttivaEntity.getEnte().getTipoCanale().getCodTipoCanale()) &&
                        TipoCanaleEntity.TIPO_CANALE.FTP.getValue().equals(fatturaAttivaEntity.getEnte().getEndpointProtocolloCa() != null ? fatturaAttivaEntity.getEnte().getEndpointProtocolloCa().getCanaleCa().getCodCanale() : null)) {
                    isFtp = true;
                    exchange.getIn().setHeader(TIPO_CANALE_ENTE, "FTP");
                }
            }

            if (!isFtp) {

                if (checkFlussoSemplificatoRicevutaConsegna && formatoTrasmissione.equals(FormatoTrasmissioneType.FPA_12.value())) {

            /*
              necessario estrarre il messaggio per verificare se l'ente e' o meno aderente al servizio IntermediaMarche:
              in questo caso infatti va' gestito con il flusso semplificato
             */
                    RicevutaConsegnaType ricevutaConsegna = JaxBUtils.getRicevutaConsegna(fileSdIType.getFile());

                    String codiceEnteDestinatario = ricevutaConsegna.getDestinatario().getCodice();
                    exchange.getIn().setHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER, CHECK_FLUSSO_SEMPLIFICATO_HEADER);
                    exchange.getIn().setHeader(CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER, codiceEnteDestinatario);

                    exchange.getIn().setHeader(MESSAGE_ID_FLUSSO_SEMPLIFICATO_HEADER, ricevutaConsegna.getMessageId());
                    exchange.getIn().setHeader(NOME_FILE_FATTURA_HEADER, ricevutaConsegna.getNomeFile());

                    //REGMA 126, aggiungo la data di ricezione dallo SdI che verra' utilizzata per settare la dataCreazione/dataRicezione della fattura
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    String dataRicezioneSdI = sdf.format(new Date());
                    exchange.getIn().setHeader("dataRicezioneSdI", dataRicezioneSdI);
                }

                if (checkFlussoSemplificatoDecorrenzaTermini && formatoTrasmissione.equals(FormatoTrasmissioneType.FPA_12.value())) {

            /*
              necessario estrarre il messaggio per verificare se l'ente e' o meno aderente al servizio IntermediaMarche:
              in questo caso infatti va' gestito con il flusso semplificato
             */
                    NotificaDecorrenzaTerminiType notificaDecorrenza = JaxBUtils.getNotificaDecorrenza(fileSdIType.getFile());

                    //Ricavo il codice ente destinatario
                    BigInteger idSdI = notificaDecorrenza.getIdentificativoSdI();

                    String codiceEnteDestinatario = getCodiceEnteDestinatario(idSdI);

                    exchange.getIn().setHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER, CHECK_FLUSSO_SEMPLIFICATO_HEADER);
                    exchange.getIn().setHeader(CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER, codiceEnteDestinatario);

                    exchange.getIn().setHeader(MESSAGE_ID_FLUSSO_SEMPLIFICATO_HEADER, notificaDecorrenza.getMessageId());
                    exchange.getIn().setHeader(NOME_FILE_FATTURA_HEADER, notificaDecorrenza.getNomeFile());
                }
            }
        }
    }

    private static byte[] getMessaggioAsBytesArray(DataHandler dh) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] messaggioBytes;
        try {
            dh.writeTo(outputStream);
            messaggioBytes = outputStream.toByteArray();
            return messaggioBytes;
        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }

    private String getCodiceEnteDestinatario(BigInteger idSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        String codiceEnteDestinatario = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            List<DatiFatturaEntity> lista = datiFatturaDao.getFattureByIdentificativoSdi(idSdI, entityManager);

            if (lista != null && !lista.isEmpty()) {
                DatiFatturaEntity datiFatturaEntity = lista.get(0);
                codiceEnteDestinatario = datiFatturaEntity.getCodiceDestinatario();
            } else {
                LOG.error("SDI INBOUND: EstraiMessaggioAttivaProcessor - process: CODICE ENTE DESTINATARIO NON TROVATO!!!");
                throw new FatturaPAException("SDI INBOUND: EstraiMessaggioAttivaProcessor - process: CODICE ENTE DESTINATARIO NON TROVATO!!!");
            }

        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {

            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return codiceEnteDestinatario;
    }

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }
}