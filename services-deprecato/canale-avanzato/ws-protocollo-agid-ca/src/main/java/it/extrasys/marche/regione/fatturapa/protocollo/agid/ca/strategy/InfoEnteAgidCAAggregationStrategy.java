package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.strategy;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class InfoEnteAgidCAAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        EnteEntity enteAgidCA = newExchange.getIn().getBody(EnteEntity.class);

        if (oldExchange.getIn().getBody() instanceof FatturaElettronicaWrapper) {

            //FatturaElettronicaWrapper
            oldExchange.getIn().getBody(FatturaElettronicaWrapper.class).setEnteEntity(enteAgidCA);

        } else if (oldExchange.getIn().getBody() instanceof NotificaDecorrenzaTerminiWrapper) {

            //NotificaDecorrenzaTerminiWrapper
            oldExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class).setEnteEntity(enteAgidCA);

        } else if (oldExchange.getIn().getBody() instanceof NotificaEsitoCommittenteWrapper) {

            //NotificaEsitoCommittenteWrapper
            oldExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class).setEnteEntity(enteAgidCA);

        } else if (oldExchange.getIn().getBody() instanceof NotificaScartoEsitoCommittenteWrapper) {

            //NotificaScartoEsitoCommittenteWrapper
            oldExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class).setEnteEntity(enteAgidCA);

        }

        return oldExchange;
    }
}