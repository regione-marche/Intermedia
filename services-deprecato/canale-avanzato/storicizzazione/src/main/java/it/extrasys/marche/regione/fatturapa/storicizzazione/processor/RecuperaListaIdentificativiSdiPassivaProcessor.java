package it.extrasys.marche.regione.fatturapa.storicizzazione.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecuperaListaIdentificativiSdiPassivaProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(RecuperaListaIdentificativiSdiPassivaProcessor.class);
    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String giorni = (String) exchange.getIn().getHeader("giorniHeader");

        DateTime now = new DateTime();
        now.minusDays(Integer.parseInt(giorni));
        List<String> listaIdentificativiSdi = fatturazionePassivaFatturaManager.getIdentificativoSdiListBeforeDate(now.toDate());

        LOG.info("STORICIZZAZIONE FATTURE PASSIVA - Trovati " + listaIdentificativiSdi.size() + " identificativi SDI");

        exchange.getIn().setBody(listaIdentificativiSdi);

    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }
}
