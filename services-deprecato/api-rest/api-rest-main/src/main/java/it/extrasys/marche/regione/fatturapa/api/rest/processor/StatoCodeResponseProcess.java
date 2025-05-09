package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.api.rest.model.MonitoraggioResponse;
import it.extrasys.marche.regione.fatturapa.api.rest.model.MonitoraggioResponseList;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodeEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.CodeManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jolokia.client.request.J4pResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatoCodeResponseProcess implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(StatoCodeResponseProcess.class);
    private CodeManager codeManager;
    private static final String DLQ = "DLQ.";

    @Override
    public void process(Exchange exchange) throws Exception {
        String ciclo = (String) exchange.getIn().getHeader("ciclo");
        Map<String, Object> value = (Map<String, Object>) ((J4pResponse) exchange.getIn().getBody()).getValue();
        List<MonitoraggioResponse> responseList = new ArrayList<>();

        //1) Costruisco una mappa del tipo <nomeCoda, size>, filtrando quelle con size != 0
        //2) Cerca sul DB la coda in basa al nome a e al tipo di Ciclo
        //3) Costruisce la risposta pe ril FE
        value.entrySet().stream()
                .filter(f -> (Long) ((Map<String, Object>) f.getValue()).get("QueueSize") != 0)
                .collect(Collectors.toMap(x -> splitNomeCode(x.getKey()), x -> ((Map<String, Object>) x.getValue()).get("QueueSize")))
                .entrySet().forEach(c -> {
            CodeEntity coda = null;
            MonitoraggioResponse mr = new MonitoraggioResponse();
            try {
                coda = codeManager.getCodaByNome(c.getKey().replace(DLQ, ""));
                if (coda != null) {
                    mr.setLabelCoda(coda.getLabel());
                    mr.setTipoCanale(coda.getTipoCanale());
                    mr.setNomeCoda(c.getKey());
                    mr.setNumeroMessaggi(((Long) c.getValue()).intValue());
                    if (ciclo.equalsIgnoreCase(coda.getCiclo())) {
                        responseList.add(mr);
                    }
                } else {
                    LOG.info("La coda " + c.getKey() + " non è presente sul database");
                }
            } catch (FatturaPAException e) {
                LOG.info("La coda " + c.getKey() + " non è presente sul database");
            }
        });
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
        MonitoraggioResponseList monitoraggioResponseList = new MonitoraggioResponseList();
        monitoraggioResponseList.setMonitoraggioResponseList(responseList);
        exchange.getIn().setBody(monitoraggioResponseList);
    }


    private static String splitNomeCode(String nomeCoda) {
        String destinationName = nomeCoda.split(",")[1];
        return destinationName.split("=")[1];
    }

    public CodeManager getCodeManager() {
        return codeManager;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }
}


