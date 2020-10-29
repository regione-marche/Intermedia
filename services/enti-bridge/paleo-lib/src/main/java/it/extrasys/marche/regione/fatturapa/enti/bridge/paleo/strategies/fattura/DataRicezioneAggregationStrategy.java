package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/03/15.
 */
public class DataRicezioneAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Date dataRicezioneFattura = newExchange.getIn().getBody(Date.class);
        oldExchange.getIn().setHeader("dataRicezione", dataRicezioneFattura);
        return oldExchange;
    }
}
