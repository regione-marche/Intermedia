package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
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

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = oldExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        String segnaturaProtocollo = newExchange.getIn().getBody(String.class);

        notificaDecorrenzaTerminiWrapper.setSegnaturaProtocolloNotifica(segnaturaProtocollo);

        oldExchange.getIn().setBody(notificaDecorrenzaTerminiWrapper);

        return oldExchange;
    }
}