package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/04/15.
 */
public class FascicoloAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String result = newExchange.getIn().getBody(String.class);
        
        if(result != null && "KO".compareTo(result.trim().toUpperCase()) == 0){
            oldExchange.getIn().setHeader("codiceFascicolo","");
        }
        return oldExchange;
    }
}
