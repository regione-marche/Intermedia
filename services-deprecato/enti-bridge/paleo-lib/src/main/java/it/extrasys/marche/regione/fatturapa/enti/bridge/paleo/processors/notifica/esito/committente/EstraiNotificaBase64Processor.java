package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.xml.security.utils.Base64;

/**
 * Created by agosteeno on 10/08/15.
 */
public class EstraiNotificaBase64Processor implements Processor {

    /*
    FIXME identico al processor EstraiNotificaBase64 di areasLib, rifattorizzare e mettere a comune nel core
     */
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn();

        byte[] messaggioRicevuto = Base64.decode((String) msg.getBody());

        String fileNotifica = new String(messaggioRicevuto);

        msg.setBody(fileNotifica);
    }
}
