package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 13/03/15.
 */
public class CreaIdComunicazioneProcessor implements Processor {


    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader("idComunicazione", java.util.UUID.randomUUID().toString());
    }
}
