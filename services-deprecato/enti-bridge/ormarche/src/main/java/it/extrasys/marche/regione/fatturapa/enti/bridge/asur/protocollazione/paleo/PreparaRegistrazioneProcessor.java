package it.extrasys.marche.regione.fatturapa.enti.bridge.asur.protocollazione.paleo;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 20/03/15.
 */
public class PreparaRegistrazioneProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {

        FatturaElettronicaWrapper fatturaElettronicaWrapper = exchange.getIn().getBody(FatturaElettronicaWrapper.class);

        String fatturaString= fatturaElettronicaWrapper.getFatturaElettronica();

        String numeroProtocollo = fatturaElettronicaWrapper.getSegnaturaProtocollo();

        exchange.getIn().setHeader("numeroProtocollo",numeroProtocollo);

        exchange.getIn().setBody(fatturaString,String.class);
    }
}
