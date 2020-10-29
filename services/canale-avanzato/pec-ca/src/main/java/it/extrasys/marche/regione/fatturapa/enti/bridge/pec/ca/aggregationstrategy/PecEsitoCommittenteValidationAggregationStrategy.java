package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.aggregationstrategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class PecEsitoCommittenteValidationAggregationStrategy implements AggregationStrategy {

    private static final String VALIDATION_HEADER = "validation";

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange){

        String validationHeader = (String) newExchange.getIn().getHeader(VALIDATION_HEADER);

        oldExchange.getIn().setHeader(VALIDATION_HEADER, validationHeader);

        return oldExchange;
    }
}