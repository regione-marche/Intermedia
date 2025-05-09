package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class FascicoloAggregationStrategy implements AggregationStrategy {

    private final String PROTOCOLLAZIONE_ENTRATA_NOTE_HEADER = "protocolloEntrataNote";
    private final String FASCICOLO_RESULT_HEADER = "fascicoloResult";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String result = newExchange.getIn().getBody(String.class);
        String protocolloEntrataNote = (String) newExchange.getIn().getHeader(PROTOCOLLAZIONE_ENTRATA_NOTE_HEADER);
        
        if(result != null && "KO".compareTo(result.trim().toUpperCase()) == 0){
            oldExchange.getIn().setHeader("codiceFascicolo","");
        }

        oldExchange.getIn().setHeader(FASCICOLO_RESULT_HEADER, result);
        oldExchange.getIn().setHeader(PROTOCOLLAZIONE_ENTRATA_NOTE_HEADER, protocolloEntrataNote);

        return oldExchange;
    }
}