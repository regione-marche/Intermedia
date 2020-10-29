package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.scarto.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ProtocollazioneAggregationStrategy implements AggregationStrategy {

    private final String PALEO_ERROR_HEADER = "PALEO_ERROR_MESSAGE";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        String segnaturaProtocollo = newExchange.getIn().getBody(String.class);

        notificaScartoEsitoCommittenteWrapper.setSegnaturaProtocolloNotifica(segnaturaProtocollo);

        String paleoErrorMessage = newExchange.getIn().getHeader(PALEO_ERROR_HEADER, String.class);

        oldExchange.getIn().setHeader(PALEO_ERROR_HEADER, paleoErrorMessage);
        oldExchange.getIn().setBody(notificaScartoEsitoCommittenteWrapper);

        return oldExchange;
    }
}
