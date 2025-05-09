package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.scarto.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class WrapNotificaScartoEsitoCommittenteProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String scartoString = exchange.getIn().getHeader("scartoEsitoCommittente", String.class);

        String numeroProtocollo = exchange.getIn().getHeader("numeroProtocollo", String.class);

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = new NotificaScartoEsitoCommittenteWrapper();

        String notificaScartoEsitoXml = exchange.getIn().getBody(String.class);

        notificaScartoEsitoCommittenteWrapper.setSegnaturaProtocolloFattura(numeroProtocollo);

        notificaScartoEsitoCommittenteWrapper.setNotificaScartoEsitoCommittente(notificaScartoEsitoXml);

        exchange.getIn().setBody(notificaScartoEsitoCommittenteWrapper, NotificaScartoEsitoCommittenteWrapper.class);
    }
}