package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.scarto.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.List;


public class ClassificazioniAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        List<String> classificazioniList = newExchange.getIn().getBody(List.class);

        if (classificazioniList != null && classificazioniList.size() > 0) {
            notificaScartoEsitoCommittenteWrapper.setClassificazioniList(classificazioniList);
        }

        oldExchange.getIn().setBody(notificaScartoEsitoCommittenteWrapper);

        return oldExchange;
    }
}
