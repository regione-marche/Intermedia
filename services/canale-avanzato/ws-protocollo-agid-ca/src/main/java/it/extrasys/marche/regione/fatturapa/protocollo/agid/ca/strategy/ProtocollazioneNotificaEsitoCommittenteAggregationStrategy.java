package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.strategy;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.utils.AgidConstant;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ProtocollazioneNotificaEsitoCommittenteAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        String segnaturaProtocollo = newExchange.getIn().getBody(String.class);

        notificaEsitoCommittenteWrapper.setSegnaturaProtocolloNotifica(segnaturaProtocollo);

        String paleoErrorMessage = newExchange.getIn().getHeader(AgidConstant.AGID_ERROR_HEADER, String.class);

        oldExchange.getIn().setHeader(AgidConstant.AGID_ERROR_HEADER, paleoErrorMessage);
        oldExchange.getIn().setBody(notificaEsitoCommittenteWrapper);

        return oldExchange;
    }
}
