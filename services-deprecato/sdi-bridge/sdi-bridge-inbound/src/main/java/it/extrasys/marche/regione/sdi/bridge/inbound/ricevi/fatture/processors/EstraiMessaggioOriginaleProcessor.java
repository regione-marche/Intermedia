package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.processors;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CessionarioCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.FileSdIConMetadatiType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.FileSdIType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 02/03/15.
 */
public class EstraiMessaggioOriginaleProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EstraiMessaggioOriginaleProcessor.class);

    private String identificativoSdi;

    /**
     * @param dh
     * @return
     * @throws java.io.IOException
     */
    private  byte[] getFatturaElettronicaBytesArray(DataHandler dh) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] fatturaElettronicaBytes;
        try {
            dh.writeTo(outputStream);
            fatturaElettronicaBytes = outputStream.toByteArray();
            LOG.info("*** SDI INBOUND: ESTRAI MESSAGGIO Fattura Elettronica Byte Array LENGTH" + fatturaElettronicaBytes.length +" - IDENTIFICATIVO SDI: "+identificativoSdi);
            return fatturaElettronicaBytes;
        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }

    /**
     * @param dh
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    private static byte[] getNotificaDecorrenzaTerminiBytesArray(DataHandler dh) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] notificaDecorrenzaTerminiBytes;
        try {
            dh.writeTo(outputStream);
            notificaDecorrenzaTerminiBytes = outputStream.toByteArray();
            return notificaDecorrenzaTerminiBytes;
        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }

    /**
     * @param dh
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    private static String getMetadati(DataHandler dh) throws IOException, JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] metadati;
        try {
            dh.writeTo(outputStream);
            metadati = outputStream.toByteArray();
            return new String(metadati);
        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }

    /**
     * @param actor CessionarioCommittenteType oppure CedentePrestatoreType di cui si vuole l'id fiscale
     * @return l'id fiscale dell'attore passato come parametro
     */
    private static String cfOrPiva(Object actor) {

        if (actor instanceof CessionarioCommittenteType) {
            CessionarioCommittenteType committente = (CessionarioCommittenteType) actor;
            return committente.getDatiAnagrafici().getIdFiscaleIVA() != null ? committente.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() + committente.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() : committente.getDatiAnagrafici().getCodiceFiscale();
        }
        if (actor instanceof CedentePrestatoreType) {
            CedentePrestatoreType prestatore = (CedentePrestatoreType) actor;
            return prestatore.getDatiAnagrafici().getIdFiscaleIVA() != null ? prestatore.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() + prestatore.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() : prestatore.getDatiAnagrafici().getCodiceFiscale();
        }
        return null;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList body = exchange.getIn().getBody(MessageContentsList.class);

        Message cxfMessage = exchange.getIn().getHeader(CxfConstants.CAMEL_CXF_MESSAGE, Message.class);

        String originalSoapMessage = null;

        if (cxfMessage != null && cxfMessage.getExchange() != null) {
            originalSoapMessage = (String) cxfMessage.getExchange().get("originalSoapMessage");
        }

        exchange.getIn().setHeader("originalSoapMessage", originalSoapMessage);

        if (body.get(0) instanceof FileSdIConMetadatiType) {
            exchange.getIn().getHeaders().put("tipoMessaggio", "FatturaElettronica");

            FileSdIConMetadatiType fileSdIConMetadatiType = (FileSdIConMetadatiType) body.get(0);

            identificativoSdi = fileSdIConMetadatiType.getIdentificativoSdI()+"";

            //  FatturaElettronicaType fatturaElettronicaType = getFatturaElettronica(fileSdIConMetadatiType.getFile());
            byte[] fatturaElettronicaBytesArray = getFatturaElettronicaBytesArray(fileSdIConMetadatiType.getFile());
            //exchange.getIn().setHeader("bodyFatturaLength",fatturaElettronicaBytesArray.length);
            String metadatiInvioFileType = getMetadati(fileSdIConMetadatiType.getMetadati());

            //exchange.getIn().setHeader("originalSoapMessage",originalSoapMessage);

            exchange.getIn().setHeader("nomeFile", fileSdIConMetadatiType.getNomeFile());
            exchange.getIn().setHeader("identificativoSdI", fileSdIConMetadatiType.getIdentificativoSdI() + "");
            exchange.getIn().setHeader("nomeFileMetadati", fileSdIConMetadatiType.getNomeFileMetadati());
            exchange.getIn().setHeader("metadati", metadatiInvioFileType);

            //REGMA 126, aggiungo la data di ricezione dallo SdI che verra' utilizzata per settare la dataCreazione/dataRicezione della fattura
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dataRicezioneSdI = sdf.format(new Date());
            exchange.getIn().setHeader("dataRicezioneSdI", dataRicezioneSdI);

            //aggiunto perche' deve essere utilizzato, insiemo al committenteCodiceIva, per definire un particolare ufficio all'interno di un ente
            // exchange.getIn().setHeader("codiceUfficio", fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceUfficio());

            String stringFatturaElettronica = new String(Base64.encodeBase64(fatturaElettronicaBytesArray));
            exchange.getIn().setBody(stringFatturaElettronica,String.class);

        } else {

            // if(body instanceof NotificaDecorrenzaTerminiType){
            exchange.getIn().getHeaders().put("tipoMessaggio", "NotificaDecorrenzaTermini");

            FileSdIType fileSdIType = (FileSdIType) body.get(0);

            byte[] notificaDecorrenzaTerminiBytesArray = getNotificaDecorrenzaTerminiBytesArray(fileSdIType.getFile());

            exchange.getIn().setHeader("nomeFile", fileSdIType.getNomeFile());
            exchange.getIn().setHeader("identificativoSdI", fileSdIType.getIdentificativoSdI() + "");
            exchange.getIn().setHeader("cedenteCodiceIva", "");
            exchange.getIn().setHeader("committenteCodiceIva", "");

            String stringNotificaDecorrenzaTermini = new String(Base64.encodeBase64(notificaDecorrenzaTerminiBytesArray));
            exchange.getIn().setBody(stringNotificaDecorrenzaTermini);
        }
    }
}