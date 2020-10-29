package it.extrasys.marche.regione.fatturapa.enti.bridge.giunta.processors.fatto;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Created by agosteeno on 18/04/16.
 */
public class FormattaDataCreazioneProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(FormattaDataCreazioneProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String dataRicezioneFromSdI = (String) message.getHeader("dataRicezioneSdI");

        LOG.info("FormattaDataCreazioneProcessor - data ricezione from sdi: " + dataRicezioneFromSdI);

        if(dataRicezioneFromSdI != null && !"".equals(dataRicezioneFromSdI)) {

            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

            String dataRicezioneFormattata = newFormat.format(oldFormat.parse(dataRicezioneFromSdI));

            message.setHeader("dataRicezioneSdIFormattata", dataRicezioneFormattata);
        } else {
            message.setHeader("dataRicezioneSdIFormattata", "");
        }

    }
}
