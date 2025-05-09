package it.extrasys.marche.regione.fatturapa.services.invioFatturazioneAttiva;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 20/09/16.
 */
public class AggiornaStatoFatturazioneAttiva implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoFatturazioneAttiva.class);

    FatturaAttivaManagerImpl fatturaAttivaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String idFattura = (String) exchange.getIn().getHeader("idFatturaAttiva");

        LOG.info("AggiornaStatoFatturazioneAttiva - aggiorno stato fattura: idFattura " + idFattura + ";");

        fatturaAttivaManager.aggiornaStatoFatturaAttivaEsito(new BigInteger(idFattura), CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.INVIATA.getValue());

        LOG.info("AggiornaStatoFatturazioneAttiva - idFattura " + idFattura + ": stato aggiornato");
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }
}
