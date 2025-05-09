package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */
public class ProtocollazioneAggregationStrategy implements AggregationStrategy {

    private final String PALEO_ERROR_HEADER = "PALEO_ERROR_MESSAGE";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String paleoErrorMessage =  newExchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);

        if(paleoErrorMessage != null &&  !paleoErrorMessage.trim().isEmpty()){
            oldExchange.getIn().setHeader(PALEO_ERROR_HEADER, paleoErrorMessage);
            return oldExchange;
        }else{
            oldExchange.getIn().setHeader(PALEO_ERROR_HEADER, paleoErrorMessage);
        }

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = oldExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        String segnaturaProtocollo = newExchange.getIn().getBody(String.class);

        notificaEsitoCommittenteWrapper.setSegnaturaProtocolloNotifica(segnaturaProtocollo);

        //String paleoErrorMessage = newExchange.getIn().getHeader(PALEO_ERROR_HEADER, String.class);

        oldExchange.getIn().setHeader(PALEO_ERROR_HEADER, paleoErrorMessage);
        oldExchange.getIn().setBody(notificaEsitoCommittenteWrapper);

        return oldExchange;
    }
}