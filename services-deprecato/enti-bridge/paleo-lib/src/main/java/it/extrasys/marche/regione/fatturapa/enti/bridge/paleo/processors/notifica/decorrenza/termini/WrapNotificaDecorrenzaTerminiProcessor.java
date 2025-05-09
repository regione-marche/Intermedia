package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class WrapNotificaDecorrenzaTerminiProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = new NotificaDecorrenzaTerminiWrapper();

        notificaDecorrenzaTerminiWrapper.setNotificaDecorrenzaTermini(exchange.getIn().getBody(String.class));

        exchange.getIn().setBody(notificaDecorrenzaTerminiWrapper, NotificaDecorrenzaTerminiWrapper.class);

    }
}
