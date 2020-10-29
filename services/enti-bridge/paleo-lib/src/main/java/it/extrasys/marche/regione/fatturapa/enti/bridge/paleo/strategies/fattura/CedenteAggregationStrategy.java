package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Cedente;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 06/03/15.
 */
public class CedenteAggregationStrategy implements AggregationStrategy {


    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Cedente cedente = newExchange.getIn().getBody(Cedente.class);
        oldExchange.getIn().getBody(FatturaElettronicaWrapper.class).setCedente(cedente);
        return oldExchange;
    }

}
