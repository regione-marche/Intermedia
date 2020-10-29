package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ProtocollazioneCANotificaECScartataStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        //Prendo sempre il vecchio Exchange in quanto devo ancora protocollare la notifica scarto esito committente
        return oldExchange;

    }
}