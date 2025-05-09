package it.extrasys.marche.regione.fatturapa.elaborazione.validazione;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/04/15.
 */

public class PerformValidationCheckProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(PerformValidationCheckProcessor.class);

    private String codiciUfficioCSV;

    private Set<String> codiciUfficioSet;

    public PerformValidationCheckProcessor(String csv) {
        codiciUfficioCSV = csv;

        codiciUfficioSet = new HashSet<String>();

        if (codiciUfficioCSV != null && !codiciUfficioCSV.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(codiciUfficioCSV, ",");

            while (st.hasMoreTokens()) {
                codiciUfficioSet.add(st.nextToken().trim().toUpperCase());
            }
        }
    }

   @Override
    public void process(Exchange exchange) throws Exception {

       LOG.debug("VALIDAZIONE: NUMERO CODICI UFFICIO PER CUI E' ATTIVA LA VALIDAZIONE: #: "+codiciUfficioSet.size() );

       LOG.debug("VALIDAZIONE: LISTA  CODICI UFFICIO PER CUI E' ATTIVA LA VALIDAZIONE:"+codiciUfficioCSV);

       String codiceUfficio = exchange.getIn().getHeader("codiceUfficio",String.class);

       if(codiceUfficio != null && codiciUfficioSet.contains(codiceUfficio.trim().toUpperCase())){
           exchange.getIn().setHeader("performValidation",true);
       }else{
           exchange.getIn().setHeader("performValidation",false);
       }
    }
}
