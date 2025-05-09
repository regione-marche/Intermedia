package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.fatture;

import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/03/15.
 */
public class AggiornaStatoFattureAdInviatoScartoEsitoProcessor implements Processor {

    private DatiFatturaManager datiFatturaManager;

    public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);

        datiFatturaManager.notificaFatturePerNotificaEsitoScartata(identificativoSdI);
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}
