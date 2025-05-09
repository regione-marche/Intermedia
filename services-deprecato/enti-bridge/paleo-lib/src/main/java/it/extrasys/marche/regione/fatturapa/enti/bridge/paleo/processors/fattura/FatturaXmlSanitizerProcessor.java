package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 02/04/15.
 */
public class FatturaXmlSanitizerProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(FatturaXmlSanitizerProcessor.class);


    @Override
    public void process(Exchange exchange) throws Exception {

        String body  = exchange.getIn().getBody(String.class);

        // rimuovo i caratteri sporchi prima dell'apertura del tag

        if(body.indexOf("<")>=1)
            body = body.substring(body.indexOf("<"), body.length());

        if(body.lastIndexOf(">")<body.length()-1)
            body = body.substring(0, body.lastIndexOf(">")+1);


        //xmlFatturaPASanitized = removeXmlStringNamespaceAndPreamble(xmlFatturaPASanitized);


        LOG.debug("FatturaXmlSanitizerProcessor: fattura dopo sanitize: \""+body+"\"");



        exchange.getIn().setBody(body,String.class);
    }


    public static String removeXmlStringNamespaceAndPreamble(String xmlString) {


        return xmlString.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
                replaceAll("xmlns.*?(\"|\').*?(\"|\')", "") /* remove xmlns declaration */
                .replaceAll("(<)(\\w+:)(.*?>)", "$1$3") /* remove opening tag prefix */
                .replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
    }

}
