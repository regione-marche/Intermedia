package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */
public class ProtocollazioneAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

       String paleoErrorMessage =  newExchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);

        if(paleoErrorMessage != null &&  !paleoErrorMessage.trim().isEmpty()){
            oldExchange.getIn().setHeader("PALEO_ERROR_MESSAGE",paleoErrorMessage);
            return oldExchange;
        }else{
            oldExchange.getIn().setHeader("PALEO_ERROR_MESSAGE",paleoErrorMessage);
        }

        FatturaElettronicaWrapper fatturaElettronicaWrapper = oldExchange.getIn().getBody(FatturaElettronicaWrapper.class);

        String segnaturaProtocollo = newExchange.getIn().getBody(String.class);

        fatturaElettronicaWrapper.setSegnaturaProtocollo(segnaturaProtocollo);

        oldExchange.getIn().setBody(fatturaElettronicaWrapper,FatturaElettronicaWrapper.class);

        Date dataProtocollazione =  newExchange.getIn().getHeader("dataProtocollazione", Date.class);

        if(dataProtocollazione == null){
            dataProtocollazione = new Date();
        }
        oldExchange.getIn().setHeader("dataProtocollazione", dataProtocollazione);

        return oldExchange;
    }
}