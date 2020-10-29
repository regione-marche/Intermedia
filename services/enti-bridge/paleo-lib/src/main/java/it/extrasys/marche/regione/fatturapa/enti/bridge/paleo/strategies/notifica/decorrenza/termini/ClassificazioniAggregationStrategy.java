package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class ClassificazioniAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = oldExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        List<String> classificazioniList = newExchange.getIn().getBody(List.class);

        if (classificazioniList != null && classificazioniList.size() > 0) {
            notificaDecorrenzaTerminiWrapper.setClassificazioniList(classificazioniList);
        }

        oldExchange.getIn().setBody(notificaDecorrenzaTerminiWrapper);

        return oldExchange;
    }
}
