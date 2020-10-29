package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 17/03/15.
 */
public class ClassificazioniAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        List<String> classificazioniList = newExchange.getIn().getBody(List.class);

        if (classificazioniList != null && classificazioniList.size() > 0) {
            notificaEsitoCommittenteWrapper.setClassificazioniList(classificazioniList);
        }

        oldExchange.getIn().setBody(notificaEsitoCommittenteWrapper);

        return oldExchange;
    }
}
