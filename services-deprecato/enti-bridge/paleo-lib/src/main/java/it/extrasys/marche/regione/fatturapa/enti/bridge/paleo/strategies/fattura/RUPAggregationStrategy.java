package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Map;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 24/02/15.
 */
public class RUPAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        FatturaElettronicaWrapper wrapper = oldExchange.getIn().getBody(FatturaElettronicaWrapper.class);
        wrapper.setFatturaMetadatiMap(newExchange.getIn().getBody(Map.class));
        oldExchange.getIn().setBody(wrapper);
        return oldExchange;

    }
}
