package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.attiva.processors;

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
 * Created by agosteeno on 11/08/15.
 */
public class FlussoSemplificatoProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(FlussoSemplificatoProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdi";

    private static final String IS_FLUSSO_SEMPLIFICATO_HEADER = "isFlussoSemplificato";

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    @Override
    public void process(Exchange exchange) throws Exception{

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        try {

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI));

            LOG.info("FlussoSemplificatoProcessor - ENTE DESTINATARIO FATTURA " + fatturaAttivaEntity.getCodiceDestinatario() + " FlussoSemplificato = "
                    + fatturaAttivaEntity.getFatturazioneInterna() + " per la fattura attiva con identificativoSdI " + identificativoSdI);

            message.setHeader(IS_FLUSSO_SEMPLIFICATO_HEADER, fatturaAttivaEntity.getFatturazioneInterna());

        } catch (FatturaPAException e) {
            throw e;
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (FatturaPAFatturaNonTrovataException e){
            throw e;
        }
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }
}