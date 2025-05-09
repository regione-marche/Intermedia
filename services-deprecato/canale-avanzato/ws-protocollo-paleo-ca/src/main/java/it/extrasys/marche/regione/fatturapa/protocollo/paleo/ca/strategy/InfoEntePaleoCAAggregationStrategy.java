package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.strategy;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.*;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class InfoEntePaleoCAAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        EntePaleoCA entePaleoCA = newExchange.getIn().getBody(EntePaleoCA.class);

        if(oldExchange.getIn().getBody() instanceof FatturaElettronicaWrapper){

            //FatturaElettronicaWrapper
            oldExchange.getIn().getBody(FatturaElettronicaWrapper.class).setEntePaleoCA(entePaleoCA);

        }else if(oldExchange.getIn().getBody() instanceof NotificaDecorrenzaTerminiWrapper){

            //NotificaDecorrenzaTerminiWrapper
            oldExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class).setEntePaleoCA(entePaleoCA);

        }else if(oldExchange.getIn().getBody() instanceof NotificaEsitoCommittenteWrapper){

            //NotificaEsitoCommittenteWrapper
            oldExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class).setEntePaleoCA(entePaleoCA);

        }else if(oldExchange.getIn().getBody() instanceof NotificaScartoEsitoCommittenteWrapper){

            //NotificaScartoEsitoCommittenteWrapper
            oldExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class).setEntePaleoCA(entePaleoCA);

        }

        return oldExchange;
    }
}