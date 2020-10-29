package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */
public class XsltAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String fatturaHtml = newExchange.getIn().getBody(String.class);
        FatturaElettronicaWrapper fatturaElettronicaWrapper = oldExchange.getIn().getBody(FatturaElettronicaWrapper.class);

        fatturaElettronicaWrapper.setFatturaElettronicaHTML(fatturaHtml);
        oldExchange.getIn().setBody(fatturaElettronicaWrapper, FatturaElettronicaWrapper.class);
        return oldExchange;
    }
}
