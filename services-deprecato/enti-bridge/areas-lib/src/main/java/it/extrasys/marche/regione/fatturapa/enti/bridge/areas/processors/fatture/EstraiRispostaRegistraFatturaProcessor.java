package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.fatture;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 13/04/15.
 */
public class EstraiRispostaRegistraFatturaProcessor implements Processor {

    private String regexpEstraiRispostaRegistraFatturaProcessor = "^.*<.*?:*callReturn\\s*?.*?>(.*)<\\/.*?:*callReturn>.*";
    private Pattern patternEstraiRispostaRegistraFatturaProcessor;// = Pattern.compile(regexpEstraiRispostaRegistraFatturaProcessor, Pattern.DOTALL);

    private static final Logger LOG = LoggerFactory.getLogger(EstraiRispostaRegistraFatturaProcessor.class);


    public EstraiRispostaRegistraFatturaProcessor(){
        patternEstraiRispostaRegistraFatturaProcessor = Pattern.compile(regexpEstraiRispostaRegistraFatturaProcessor, Pattern.DOTALL);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        String rispostaAREAS = exchange.getIn().getBody(String.class);

        LOG.debug("EstraiRispostaRegistraFatturaProcessor: rispostaAREAS string:\n" + rispostaAREAS);

        Matcher matcher = patternEstraiRispostaRegistraFatturaProcessor.matcher(rispostaAREAS);
        String sanitizedXmlString = "";
        while (matcher.find()) {
            sanitizedXmlString = matcher.group(1);
        }
        if (sanitizedXmlString == null || sanitizedXmlString.trim().isEmpty()) {
            exchange.getIn().setBody(rispostaAREAS, String.class);
            LOG.warn("EstraiRispostaRegistraFatturaProcessor: sanitized string:\n"+rispostaAREAS);
            return;
        }

        exchange.getIn().setBody(sanitizedXmlString,String.class);
        LOG.debug("EstraiRispostaRegistraFatturaProcessor: sanitized string:\n" + sanitizedXmlString);
        return ;
    }
}

