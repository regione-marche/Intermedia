package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.marche.regione.paleo.services.ObjectFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/03/15.
 */
public class CreaRichiestaGetUOProcessor implements Processor {

    private String codiceRegistro;

    @Override
    public void process(Exchange exchange) throws Exception {

        ObjectFactory objectFactory = new ObjectFactory();

        exchange.getIn().setBody(codiceRegistro,String.class);
    }

    public String getCodiceRegistro() {
        return codiceRegistro;
    }

    public void setCodiceRegistro(String codiceRegistro) {
        this.codiceRegistro = codiceRegistro;
    }
}
