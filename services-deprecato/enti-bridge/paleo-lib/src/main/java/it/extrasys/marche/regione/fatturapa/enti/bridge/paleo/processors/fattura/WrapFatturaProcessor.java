package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 24/02/15.
 */
public class WrapFatturaProcessor implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {
 
        FatturaElettronicaWrapper fatturaElettronicaWrapper = new FatturaElettronicaWrapper();
        
        fatturaElettronicaWrapper.setFatturaElettronica(exchange.getIn().getBody(String.class));

        exchange.getIn().setBody(fatturaElettronicaWrapper);
    }
}