package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.api.rest.models.ReportBindy;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorRielaborazioniEntity;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MonitorRielaborazioniProcessor implements Processor {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public void process(Exchange exchange) throws Exception {
        List<MonitorRielaborazioniEntity> monitorRielaborazioni = (List<MonitorRielaborazioniEntity>) exchange.getIn().getBody();
        List<ReportBindy> reportBindies = new ArrayList<>();

        for (MonitorRielaborazioniEntity mr : monitorRielaborazioni) {
            ReportBindy rb = new ReportBindy();
            rb.setDataRielaborazione(sdf.format(mr.getDataInizioRielaborazione()));
            rb.setUtente(mr.getIdUtente().getUsername());
            rb.setNomeCoda(mr.getIdCoda().getNome());
            rb.setIdentificativoSdi(mr.getIdentificativoSdi() + "");
            rb.setNumeroRielaborazioni(mr.getNumeroRielaborazioni() + "");
            if (mr.getStacktrace() != null) {
                String s = new String(mr.getStacktrace(), "UTF-8");
                String s1 = s.replaceAll("\"", "'");
                String s2 = "\"" + s1 + "\"";
                rb.setStackTrace(s2);
            }
            reportBindies.add(rb);
        }

        exchange.getIn().setHeader("MONITORAGGIO.nomeReport", monitorRielaborazioni.get(0).getNomeReport());
        exchange.getIn().setHeader("MONITORAGGIO.username", monitorRielaborazioni.get(0).getIdUtente().getUsername());
        exchange.getIn().setBody(reportBindies);
    }
}
