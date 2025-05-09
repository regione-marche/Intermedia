package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.fatture;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/03/15.
 */
public class DecodificaRispostaAREASProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {

        String escapedXml = exchange.getIn().getBody(String.class);
        String unescapedXml = StringEscapeUtils.unescapeXml(escapedXml);
        exchange.getIn().setBody(unescapedXml,String.class);

    }
}
