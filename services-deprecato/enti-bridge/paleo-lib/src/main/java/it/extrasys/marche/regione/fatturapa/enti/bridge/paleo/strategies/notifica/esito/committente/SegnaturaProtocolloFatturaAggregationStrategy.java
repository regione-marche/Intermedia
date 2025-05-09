package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class SegnaturaProtocolloFatturaAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        String segnaturaProtocolloFattura = newExchange.getIn().getBody(String.class);

        notificaEsitoCommittenteWrapper.setSegnaturaProtocolloFattura(segnaturaProtocolloFattura);

        oldExchange.getIn().setBody(notificaEsitoCommittenteWrapper, NotificaEsitoCommittenteWrapper.class);

        return oldExchange;
    }
}
