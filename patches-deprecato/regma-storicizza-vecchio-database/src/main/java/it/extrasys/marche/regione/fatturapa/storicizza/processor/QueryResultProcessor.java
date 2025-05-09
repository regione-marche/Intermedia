package it.extrasys.marche.regione.fatturapa.storicizza.processor;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class QueryResultProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(QueryResultProcessor.class);


    public void getIdentificativoSDI(Exchange exchange) throws Exception {
        HashMap<String, Object> body = (HashMap<String, Object>) exchange.getIn().getBody();

        exchange.getIn().setHeader("identificativoSdI", body.get("identificativo_sdi"));
    }

    public void getIdDatiFattura(Exchange exchange) throws Exception {
        HashMap<String, Object> body = (HashMap<String, Object>) exchange.getIn().getBody();

        exchange.getIn().setHeader("idDatiFattura", body.get("id_dati_fattura"));
    }
}
