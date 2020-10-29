package it.extrasys.marche.regione.fatturapa.elaborazione.module.moduloftp;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MetadatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 06/09/17.
 */
public class PrelevaFatturaPassivaDaDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(PrelevaFatturaPassivaDaDatabase.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String NOME_FILE_HEADER = "CamelFileName";

    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    private MetadatiFatturaManager metadatiFatturaManager;

    public void prelevaFatturaPassiva(Exchange exchange) throws Exception {

        Message message = exchange.getIn();
        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        FileFatturaEntity fileFatturaEntity  = fatturazionePassivaFatturaManager.getFileFatturaEntityByIdentificativiSdI(identificativoSdI);
        byte[] fatturaByteArray = fileFatturaEntity.getContenutoFile();

        String fattura = new String(fatturaByteArray);

        message.setHeader(NOME_FILE_HEADER, fileFatturaEntity.getNomeFileFattura());
        message.setBody(fattura);
    }

    public void prelevaMetaDati(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        MetadatiFatturaEntity metadatiFatturaEntity = metadatiFatturaManager.getMetadatiByIdentificativoSdi(new BigInteger(identificativoSdI));

        byte[] metadDatiByteArray = metadatiFatturaEntity.getContenutoFile();

        String metadati = new String(metadDatiByteArray);

        message.setHeader(NOME_FILE_HEADER, metadatiFatturaEntity.getNomeFileMetadati());
        message.setBody(metadati);

    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }

    public MetadatiFatturaManager getMetadatiFatturaManager() {
        return metadatiFatturaManager;
    }

    public void setMetadatiFatturaManager(MetadatiFatturaManager metadatiFatturaManager) {
        this.metadatiFatturaManager = metadatiFatturaManager;
    }
}
