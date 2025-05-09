package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by agosteeno on 13/04/15.
 */
public class FatturaAttivaValidationAggregationStrategy implements AggregationStrategy {

    private static final String VALIDATION_HEADER = "validation";

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange){

        String validationHeader = (String) newExchange.getIn().getHeader(VALIDATION_HEADER);

        oldExchange.getIn().setHeader(VALIDATION_HEADER, validationHeader);

        /*
        l'aggregazione serve solo per far passare il messaggio alla validazione, mentre nel body ci deve stare il
        messaggio gia' presente nel body dell'old exchange
         */
        return oldExchange;

    }
}
