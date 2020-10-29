package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.attiva.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.xml.security.utils.Base64;

/**
 * Created by agosteeno on 15/07/15.
 */
public class EstraiNotificaBase64 implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        byte[] messaggioRicevuto = Base64.decode((String) msg.getBody());

        String fileRicevutaConsegna = new String(messaggioRicevuto);

        msg.setBody(fileRicevutaConsegna);
    }
}