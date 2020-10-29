package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/03/15.
 */
public class CreaRichiestaFindOperatoriProcessor implements Processor {


    public void process(Exchange exchange) throws Exception {

        String cognome = null;

        String codiceFiscale = exchange.getIn().getHeader("RUP_CF", String.class);

        List<Object> params = new ArrayList<Object>();
        params.add(cognome);
        params.add(codiceFiscale);

        exchange.getIn().setBody(params);

    }
}
