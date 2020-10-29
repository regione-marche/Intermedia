package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.strategies.fatture;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/03/15.
 */
public class AreasRegistrazioneAggregationStrategy implements AggregationStrategy{

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String error_description = newExchange.getIn().getHeader("error-description", String.class);

        if(error_description != null && !error_description.trim().isEmpty()){
            oldExchange.getIn().setHeader("error-description", error_description);
            return oldExchange;
        }
        return oldExchange;

    }
}
