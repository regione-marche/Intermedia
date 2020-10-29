package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class SdiOutboundAggregationStrategy implements AggregationStrategy {

    private final String TIPO_NOTIFICA_HEADER = "tipoNotifica";
    private final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String tipoNotifica = newExchange.getIn().getHeader(TIPO_NOTIFICA_HEADER, String.class);

        switch (tipoNotifica) {

            case "notificaOk":

                oldExchange.getIn().setHeader(TIPO_NOTIFICA_HEADER, tipoNotifica);
                oldExchange.getIn().setHeader(TIPO_MESSAGGIO_HEADER, "NotificaEsitoCommittente");

                return oldExchange;

            case "notificaScarto":

                newExchange.getIn().setHeader(TIPO_MESSAGGIO_HEADER, "NotificaScartoEsito");

                return newExchange;
        }

        return oldExchange;
    }
}