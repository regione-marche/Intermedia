package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 05/03/15.
 */
public class EstraiCodiceStrutturaProcessor implements Processor {


   // String pattern = ".*\\|GRM\\|(.*?)\\|.*";
    
    private String pattern;
    
    
    @Override
    public void process(Exchange exchange) throws Exception {

        String idDocumento = exchange.getIn().getHeader("idDocumento", String.class);
        String codiceStruttura ="";

        if(idDocumento != null && !idDocumento.trim().isEmpty()) {

            String[] idDocumentoSplitted = idDocumento.split("\\|");



           /* if(idDocumentoSplitted.length > 4) {
                codiceStruttura = idDocumentoSplitted[4];
            }*/

            //TODO modifica, da attivare, per REGMA-104. Per attivarlo commentare la parte di sopra
           if(idDocumentoSplitted.length > 1) {
                codiceStruttura = idDocumentoSplitted[1];
            }

        }
        exchange.getIn().setHeader("codiceStruttura", codiceStruttura);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}