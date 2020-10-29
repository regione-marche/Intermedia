package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.scarto.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class SegnaturaProtocolloFatturaAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        String segnaturaProtocolloFattura = newExchange.getIn().getBody(String.class);

        notificaScartoEsitoCommittenteWrapper.setSegnaturaProtocolloFattura(segnaturaProtocolloFattura);

        oldExchange.getIn().setBody(notificaScartoEsitoCommittenteWrapper, NotificaScartoEsitoCommittenteWrapper.class);

        return oldExchange;
    }
}