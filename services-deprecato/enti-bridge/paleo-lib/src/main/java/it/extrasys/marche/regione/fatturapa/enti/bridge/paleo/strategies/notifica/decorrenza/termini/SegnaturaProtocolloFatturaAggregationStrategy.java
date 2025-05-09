package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class SegnaturaProtocolloFatturaAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = oldExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        String segnaturaProtocolloFattura = newExchange.getIn().getBody(String.class);

        notificaDecorrenzaTerminiWrapper.setSegnaturaProtocolloFattura(segnaturaProtocolloFattura);

        oldExchange.getIn().setBody(notificaDecorrenzaTerminiWrapper, NotificaDecorrenzaTerminiWrapper.class);

        return oldExchange;
    }
}
