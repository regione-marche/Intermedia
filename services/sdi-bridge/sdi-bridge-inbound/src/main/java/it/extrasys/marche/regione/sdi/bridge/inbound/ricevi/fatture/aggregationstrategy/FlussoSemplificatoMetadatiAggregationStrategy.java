package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.aggregationstrategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by agosteeno on 12/08/15.
 */
public class FlussoSemplificatoMetadatiAggregationStrategy  implements AggregationStrategy {

    private static final String NOME_FILE_METADATI = "nomeFileMetadati";
    private static final String METADATI = "metadati";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String metadati = (String) newExchange.getIn().getBody();
        String nomeFileMetadati = (String) newExchange.getIn().getHeader(NOME_FILE_METADATI);

        oldExchange.getIn().setHeader(METADATI, metadati);
        oldExchange.getIn().setHeader(NOME_FILE_METADATI, nomeFileMetadati);

        return oldExchange;
    }
}
