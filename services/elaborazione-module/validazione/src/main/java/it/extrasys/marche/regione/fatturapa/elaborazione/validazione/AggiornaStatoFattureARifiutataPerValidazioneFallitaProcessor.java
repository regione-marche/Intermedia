package it.extrasys.marche.regione.fatturapa.elaborazione.validazione;

import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/04/15.
 */
public class AggiornaStatoFattureARifiutataPerValidazioneFallitaProcessor implements Processor {

    DatiFatturaManager datiFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);

        datiFatturaManager.rifiutaFatturaPerValidazioneFallita(identificativoSdI);

    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}
