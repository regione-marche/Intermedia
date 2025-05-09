package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class ValidazioneEsitoCommittenteProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList message = exchange.getIn().getBody(MessageContentsList.class);

        EsitoFatturaMessageRequest esitoFatturaMessageRequest = (EsitoFatturaMessageRequest) message.get(0);

        String xml = CommonUtils.getXmlFromDataHandler(esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile());

        exchange.getIn().setBody(xml);
    }
}
