package it.extrasys.marche.regione.fatturapa.patch.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RicevutaConsegnaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(RicevutaConsegnaProcessor.class);

    @Override
    public void process(Exchange exchange) {

        Message message = exchange.getIn();

        byte[] body = (byte[]) message.getBody();
        String bodyEncoded = new String(Base64.encode(body));

        message.setBody(bodyEncoded);
    }
}
