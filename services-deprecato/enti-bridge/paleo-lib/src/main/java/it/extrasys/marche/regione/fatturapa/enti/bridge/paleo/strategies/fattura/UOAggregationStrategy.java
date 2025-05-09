package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Set;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/03/15.
 */
public class UOAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        FatturaElettronicaWrapper fatturaElettronicaWrapper = oldExchange.getIn().getBody(FatturaElettronicaWrapper.class);

        Set<String> UOset = newExchange.getIn().getBody(Set.class);

        fatturaElettronicaWrapper.setUOset(UOset);

        oldExchange.getIn().setBody(fatturaElettronicaWrapper,FatturaElettronicaWrapper.class);

        return oldExchange;
    }
}
