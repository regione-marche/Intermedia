package it.extrasys.marche.regione.fatturapa.elaborazione.module.moduloftp;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 06/09/17.
 */
public class PrelevaFatturaAttivaDaDatabaseProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PrelevaFatturaAttivaDaDatabaseProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String NOME_FILE_HEADER = "CamelFileName";

    private FatturaAttivaManagerImpl fatturaAttivaManagerImpl;

    @Override
    public void process(Exchange exchange) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManagerImpl.getFatturaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI));

        byte[] fatturaByteArray = fatturaAttivaEntity.getFileFatturaOriginale();

        String fattura = new String(fatturaByteArray);

        message.setHeader(NOME_FILE_HEADER, fatturaAttivaEntity.getNomeFile());
        message.setBody(fattura);
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManagerImpl() {
        return fatturaAttivaManagerImpl;
    }

    public void setFatturaAttivaManagerImpl(FatturaAttivaManagerImpl fatturaAttivaManagerImpl) {
        this.fatturaAttivaManagerImpl = fatturaAttivaManagerImpl;
    }
}
