package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.flussosemplificato;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XmlStringSanitizer;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 12/08/15.
 */
public class FlussoSemplificatoProcessor  {

    private static final Logger LOG = LoggerFactory.getLogger(FlussoSemplificatoProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String NOME_FILE_FATTURA_ATTIVA_HEADER = "nomeFileFatturaAttiva";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileFattura";
    private static final String NOME_FILE_METADATI_HEADER = "nomeFileMetadati";

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    private XmlStringSanitizer xmlStringSanitizer;

    public void imporstaFlagFatturazioneInterna(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        fatturaAttivaManager.impostaFlagFatturazioneInterna(new BigInteger(identificativoSdI), true);
    }

    public void prelevaFatturaAttiva(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI));

        String fatturaOriginale = new String(Base64.encodeBase64(fatturaAttivaEntity.getFileFatturaOriginale()));

        LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, fattura originale[ " + fatturaOriginale + "]");

        message.setBody(fatturaOriginale);

        //Questo Header mi serve per creare il file dei metadati
        message.setHeader(NOME_FILE_FATTURA_ATTIVA_HEADER, fatturaAttivaEntity.getNomeFile());
    }

    public void creaNomeFileMetadati(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String nomeFile = (String)message.getHeader(NOME_FILE_HEADER);


        //String nomeFileFattura = FileUtils.getNomeFileFatturaFromNomeFileNotificaRicevutaConsegna(nomeFile);
        String nomeFileFattura = (String)message.getHeader(NOME_FILE_FATTURA_ATTIVA_HEADER);

        String nomeFileMetadati = FileUtils.getNomeMetadatiFromNomeFattura(nomeFileFattura);

        LOG.info("FlussoSemplificatoProcessor - creaNomeFileMetadati: nome file fattura " + nomeFileFattura + ", nome file metadati " + nomeFileMetadati);

        message.setHeader(NOME_FILE_FATTURA_HEADER, nomeFileFattura);
        message.setHeader(NOME_FILE_METADATI_HEADER, nomeFileMetadati);
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }

    public XmlStringSanitizer getXmlStringSanitizer() {
        return xmlStringSanitizer;
    }

    public void setXmlStringSanitizer(XmlStringSanitizer xmlStringSanitizer) {
        this.xmlStringSanitizer = xmlStringSanitizer;
    }
}