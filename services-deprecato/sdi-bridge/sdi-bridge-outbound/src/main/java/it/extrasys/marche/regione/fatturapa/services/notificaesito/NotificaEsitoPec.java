package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.EsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.ScartoEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.ScartoType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.RispostaSdINotificaEsitoType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaSDIServizioNonDisponibileException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.services.common.SdiBridgeOutboundCommon;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 15/04/15.
 */
public class NotificaEsitoPec {

    private static final Logger LOG = LoggerFactory.getLogger(NotificaEsitoPec.class);
    private static final String NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER = "nomeFileScartoEsito";
    private static final String ESITO_RISPOSTA_HEADER = "esitoRisposta";
    private static final String IS_SCARTO_EN02_HEADER = "isScartoEN02";
    private static final String NOME_FILE_NOTIFICA_ESITO_HEADER = "nomeFileNotificaEsito";
    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String STATO_RISPOSTA_HEADER = "STATO_RISPOSTA";
    private static final String TIPO_MESSAGGIO = "tipoMessaggio";
    private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";
    private static final String NOTIFICA_ESITO_COMMITTENTE_ORIGINALE = "notificaEsitoCommittenteOriginale";
    private DatiFatturaManager datiFatturaManager;

    private static final String TIPO_NOTIFICA = "tipoNotifica";
    private static final String IS_NOTIFICA_SCARTATA = "notificaScarto";
    private static final String IS_NOTIFICA_ACCETTATA = "notificaOk";

    public void preparaMessaggioNotifica(Exchange exchange) throws ParserConfigurationException, TransformerException, SAXException, XPathExpressionException, IOException, JAXBException {

        Message message = exchange.getIn();
        String nomeFileNotificaEsito = (String) message.getHeader(NOME_FILE_NOTIFICA_ESITO_HEADER);
        String notificaEsitoBase64 = (String) message.getBody();
        byte[] notificaEsitoByteArray = Base64.decodeBase64(notificaEsitoBase64);
        String notificaEsito = XAdESUnwrapper.unwrap(notificaEsitoByteArray);
        String identificativoSdi = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);
        FileSdIType fileSdIType = new FileSdIType();
        fileSdIType.setIdentificativoSdI(new BigInteger(identificativoSdi));
        fileSdIType.setNomeFile(nomeFileNotificaEsito);
        DataSource notificaEsitoDataSource = new ByteArrayDataSource(notificaEsitoByteArray ,"application/octet-stream");
        DataHandler fileNotificaEsitoDataHandler = new DataHandler(notificaEsitoDataSource);
        fileSdIType.setFile(fileNotificaEsitoDataHandler);
        message.setBody(fileSdIType);
        //TODO regma 112 siccome il messaggio dovra' essere utilizzato per costruire la notifica di esito in caso di flusso semplificato, la metto anche in un header
        String flussoSemplificato = (String) message.getHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER);
        if(flussoSemplificato != null && CHECK_FLUSSO_SEMPLIFICATO_HEADER.equals(flussoSemplificato)){
            byte[] fileRicevuto = SdiBridgeOutboundCommon.getMessaggioAsBytesArray(fileSdIType.getFile());
            String fileRicevutoEncoded = new String(org.bouncycastle.util.encoders.Base64.encode(fileRicevuto));
            message.setHeader(NOTIFICA_ESITO_COMMITTENTE_ORIGINALE, fileRicevutoEncoded);
        }
    }
    public void estraiRispostaFromSdi(Exchange exchange) throws IOException, FatturaPAException, FatturaPaPersistenceException,FatturaPaSDIServizioNonDisponibileException {

        Message msg = exchange.getIn();
        org.apache.cxf.message.Message cxfMessage = msg.getHeader(CxfConstants.CAMEL_CXF_MESSAGE, org.apache.cxf.message.Message.class);
        String originalSoapMessage = (String) cxfMessage.getExchange().get("originalSoapMessage");
        RispostaSdINotificaEsitoType rispostaSdINotificaEsito = null;
        MessageContentsList contentsList = msg.getBody(MessageContentsList.class);

        if (contentsList != null && contentsList.size() != 0) {
            rispostaSdINotificaEsito = (RispostaSdINotificaEsitoType) contentsList.get(0);
            String stato;
            // trasformo l'oggetto in byte array e lo metto nel body del messaggio
            if (rispostaSdINotificaEsito.getScartoEsito() != null) {
                // se si tratta di uno scarto mi serve prendere l'oggetto
                byte[] fileSdiBaseTypeByteArray = SdiBridgeOutboundCommon.getFileSdiTypeBytesArray(rispostaSdINotificaEsito.getScartoEsito().getFile());
                String stringRispostaNotifica = new String(Base64.encodeBase64(fileSdiBaseTypeByteArray));
                msg.setBody(stringRispostaNotifica);
                msg.setHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER, rispostaSdINotificaEsito.getScartoEsito().getNomeFile());
            } else {
                msg.setBody(null);
            }
            String esitoRisposta = rispostaSdINotificaEsito.getEsito().value();
            msg.setHeader(ESITO_RISPOSTA_HEADER, esitoRisposta);
            if (EsitoNotificaType.ES_00.value().equals(esitoRisposta)) {

                ScartoType scartoType = null;
                String noteScartoType = null;

                msg.setHeader(IS_SCARTO_EN02_HEADER, "no");

                try{
                    ScartoEsitoCommittenteType scartoEsitoCommittenteType = JaxBUtils.getScartoEsito(rispostaSdINotificaEsito.getScartoEsito().getFile());
                    scartoType = scartoEsitoCommittenteType.getScarto();
                    noteScartoType = scartoEsitoCommittenteType.getNote();
                }catch (Exception e){
                    LOG.error("NotificaEsitoPec - aggiornaFattura: Errore Estrazione Tipo Scarto Esito. MSG: " + e.getMessage());
                }

                if ((scartoType != null && "EN01".equals(scartoType.value())) && (noteScartoType != null && noteScartoType.contains("EN02"))){
                    msg.setBody(null);
                    msg.setHeader(IS_SCARTO_EN02_HEADER, "si");

                    // NOTIFICA ACCETTATA
                    stato = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA_PER_REINVIO.getValue();
                    msg.setHeader(TIPO_NOTIFICA, IS_NOTIFICA_ACCETTATA);

                }else{
                    // NOTIFICA SCARTATA
                    stato = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA.getValue();
                    msg.setHeader(TIPO_NOTIFICA, IS_NOTIFICA_SCARTATA);
                }
            } else if (EsitoNotificaType.ES_01.value().equals(esitoRisposta)) {
                // NOTIFICA ACCETTATA
                stato = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_ACCETTATA.getValue();
                msg.setHeader(TIPO_NOTIFICA, IS_NOTIFICA_ACCETTATA);
            } else if (EsitoNotificaType.ES_02.value().equals(esitoRisposta)) {
                // SERVIZIO NON DISPONIBILE
                /*
                in questo caso si deve effettuare una politica di redelivery: se comunque non riesce si deve mettere il messaggio in una dlq
                 */
                throw new FatturaPaSDIServizioNonDisponibileException("NotificaEsitoPec - aggiornaFattura: SDI Esito: SERVIZIO NON DISPONIBILE");
            } else {
                // CASO NON CENSITO
                throw new FatturaPAException("NotificaEsitoPec - aggiornaFattura: Esito risposta non censito");
            }

            msg.setHeader(STATO_RISPOSTA_HEADER, stato);

        } else {
            throw new FatturaPAException("NotificaEsitoPec - aggiornaFattura: Risposta dallo SDI non valida");
        }
    }

    public void preparaMessaggioPerProtocollazione(Exchange exchange) throws FatturaPAException, IOException {

        Message msg = exchange.getIn();

        String tipoNotifica = msg.getHeader(TIPO_NOTIFICA, String.class);

        switch (tipoNotifica) {

            case "notificaOk":

                /*
                it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest esitoFatturaMessageRequest = null;

                try {
                    esitoFatturaMessageRequest = JaxBUtils.getEsitoFatturaCA(msg.getBody(String.class));
                } catch (Exception e) {
                    throw new FatturaPAException();
                }

                msg.setHeader("nomeFile", esitoFatturaMessageRequest.getEsitoFatturaMessage().getNomeFile());

                String notifica = new String(SdiBridgeOutboundCommon.getMessaggioAsBytesArray(esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile()));

                String notificaXml = getXml(notifica);

                msg.setBody(notificaXml);
                */

                //NotificaEsitoCommittenteType notificaEsitoCommittenteType = null;

                String esito = msg.getBody(String.class);

                byte[] esitoBytes = esito.getBytes();

                if(Base64Utils.isBase64(esitoBytes))
                    esitoBytes = Base64.decodeBase64(esitoBytes);

                String notificaXml = getXml(esitoBytes);

                msg.setHeader("nomeFile", msg.getHeader(NOME_FILE_NOTIFICA_ESITO_HEADER));
                msg.setBody(notificaXml);

                break;

            case "notificaScarto":

                msg.setHeader("nomeFile", msg.getHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER));

                String notificaScartoXml = getXml(msg.getBody());

                msg.setBody(notificaScartoXml);

                break;
        }
    }

    public void aggiornaFatturaInvioSdi(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {

        Message message = exchange.getIn();
        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoMessaggio = (String)message.getHeader(TIPO_MESSAGGIO);
        String statoFattura = "";

        if(tipoMessaggio.equals(EsitoCommittenteType.EC_01.value())){
            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE.getValue();
        } else if(tipoMessaggio.equals(EsitoCommittenteType.EC_02.value())){
            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATO_RIFIUTO.getValue();
        } else {
            throw new FatturaPAException("NotificaEsitoPec - aggiornaFattura: Tipo messaggio non riconosciuto");
        }

        aggiornaFattura(identificativoSdI, statoFattura);
    }
    public void aggiornaFatturaRispostaSdi(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {

        Message message = exchange.getIn();
        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String statoRisposta = (String) message.getHeader(STATO_RISPOSTA_HEADER);
        aggiornaFattura(identificativoSdI, statoRisposta);
    }

    private void aggiornaFattura(String identificativoSdI, String statoRisposta) throws FatturaPaPersistenceException, FatturaPAException {

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));
        for(DatiFatturaEntity dfe : datiFatturaEntityList){
            datiFatturaManager.aggiornaStatoFatturaEsito(dfe.getIdDatiFattura(), statoRisposta);
        }
    }

    private String getXml(Object body) throws FatturaPAException {

        String xml = "";

        if (body instanceof String) {

            xml = (String) body;

        } else if (body instanceof byte[]) {

            byte[] bytes = (byte[]) body;
            xml = new String(bytes);
        }

        try {

            if (Base64Utils.isBase64(xml.getBytes())) {

                byte[] bytesDecod = org.apache.commons.codec.binary.Base64.decodeBase64(xml);
                xml = new String(bytesDecod);

            }

        } catch (Exception ex) {
            throw new FatturaPAException();
        }

        return xml;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}