package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.api.rest.model.RielaboraMessaggiResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RielaboraMessaggiResponseProcess implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        RielaboraMessaggiResponse response = new RielaboraMessaggiResponse();
        response.setEsito("OK");
        response.setMessaggio("La rielaborazione dei messaggi è stata presa in consegna");
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
        exchange.getIn().setBody(response);
    }

}
