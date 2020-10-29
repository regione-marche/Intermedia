package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageResponse;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoNotificaToEntiResponseCodeType;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoNotificaToEntiResponseDescriptionType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreaMessaggioRispostaSuccessoProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaMessaggioRispostaSuccessoProcessor.class);
    
    @Override
    public void process(Exchange exchange) {

        Message inMessage = exchange.getIn();

        String operationName = inMessage.getHeader(CxfConstants.OPERATION_NAME, String.class);

        LOG.info("operationName = " + operationName);

        EsitoFatturaMessageResponse response = new EsitoFatturaMessageResponse();

        response.setCode(EsitoNotificaToEntiResponseCodeType.fromValue("EN00"));

        response.setDescription(EsitoNotificaToEntiResponseDescriptionType.NOTIFICA_PRESA_IN_CARICO);

        inMessage.setBody(response);
    }
}