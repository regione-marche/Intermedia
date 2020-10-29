package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.api.rest.model.MonitoraggioReportResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Base64;

public class MonitoraggioReportResponseProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        MonitoraggioReportResponse mrr = new MonitoraggioReportResponse();
        mrr.setContenuto(Base64.getEncoder().encodeToString(((String) exchange.getIn().getBody()).getBytes()));
        mrr.setNomeReport((String) exchange.getIn().getHeader("MONITORAGGIO.nomeReport"));

        exchange.getIn().setBody(mrr);
    }
}

