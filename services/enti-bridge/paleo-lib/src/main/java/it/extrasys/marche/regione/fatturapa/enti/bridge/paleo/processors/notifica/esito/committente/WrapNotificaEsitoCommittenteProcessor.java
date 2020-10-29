package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class WrapNotificaEsitoCommittenteProcessor implements Processor {


    /**
     * EC01 = FATTURA ACCETTATA
     * EC02 = FATTURA RIFIUTATA
     */


    @Override
    public void process(Exchange exchange) throws Exception {

        String esitoString = exchange.getIn().getHeader("esitoCommittente", String.class);

        String numeroProtocollo = exchange.getIn().getHeader("numeroProtocollo", String.class);

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = new NotificaEsitoCommittenteWrapper();

        String notificaEsitoXml = exchange.getIn().getBody(String.class);

        notificaEsitoCommittenteWrapper.setSegnaturaProtocolloFattura(numeroProtocollo);

        notificaEsitoCommittenteWrapper.setNotificaEsitoCommittente(notificaEsitoXml);

        if (esitoString != null && !esitoString.trim().isEmpty()) {
            notificaEsitoCommittenteWrapper.setAccettata(esitoString.equals("EC01") ? true : false);
        }

        exchange.getIn().setBody(notificaEsitoCommittenteWrapper, NotificaEsitoCommittenteWrapper.class);

    }
}
