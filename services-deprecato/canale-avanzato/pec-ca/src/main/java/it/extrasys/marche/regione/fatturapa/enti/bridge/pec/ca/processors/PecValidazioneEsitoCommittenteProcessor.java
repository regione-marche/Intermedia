package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;

public class PecValidazioneEsitoCommittenteProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        Message msg = exchange.getIn();

        byte[] notificaBytesArray = Base64.decodeBase64(msg.getBody(String.class));

        String xml = new String(notificaBytesArray);

        msg.setBody(xml);
    }
}