package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.processor;

import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.EsitoType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RispostaEsitoConsegnaProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        EsitoType consegna = (EsitoType) exchange.getIn().getBody();

    }
}
