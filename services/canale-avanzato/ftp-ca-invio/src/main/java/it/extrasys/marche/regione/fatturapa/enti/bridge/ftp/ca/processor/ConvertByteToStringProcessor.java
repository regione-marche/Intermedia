package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;

public class ConvertByteToStringProcessor implements Processor{
    @Override
    public void process(Exchange exchange) throws Exception {
        byte[] fatturaBytesArray = (byte[]) exchange.getIn().getBody();
        String fatturaString = new String(Base64.encodeBase64(fatturaBytesArray));
        exchange.getIn().setBody(fatturaString);
    }
}
