package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Map;

public class PollEnrichAggregator implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        Map<String, Object> headersOld = oldExchange.getIn().getHeaders();
        newExchange.getIn().getHeaders().putAll(headersOld);
        return newExchange;
    }
}
