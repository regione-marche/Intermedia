package it.extrasys.marche.regione.fatturapa.persistence.camel.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecuperaListaIdentificativiSdiAttivaProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(RecuperaListaIdentificativiSdiAttivaProcessor.class);

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String giorni = (String) exchange.getIn().getHeader("giorniHeader");

        DateTime now = new DateTime();
        now.minusDays(Integer.parseInt(giorni));

        List<Object[]> listaIdentificativiSdi = fatturaAttivaManager.getFattureAttiveListBeforeDate(now.toDate());

        LOG.info("STORICIZZAZIONE FATTURE ATTIVA - Trovati " + listaIdentificativiSdi.size() + " identificativi SDI");

        exchange.getIn().setBody(listaIdentificativiSdi);

    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }
}
