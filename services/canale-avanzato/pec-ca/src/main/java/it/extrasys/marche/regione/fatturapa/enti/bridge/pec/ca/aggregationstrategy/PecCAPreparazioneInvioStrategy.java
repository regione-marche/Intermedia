package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.aggregationstrategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class PecCAPreparazioneInvioStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        return oldExchange;
    }
}