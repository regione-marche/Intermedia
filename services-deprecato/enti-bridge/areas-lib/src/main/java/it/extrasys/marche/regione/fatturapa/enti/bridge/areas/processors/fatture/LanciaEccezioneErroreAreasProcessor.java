package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.fatture;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/03/15.
 */
public class LanciaEccezioneErroreAreasProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(LanciaEccezioneErroreAreasProcessor.class);

    private final static String CODICE_NOTIFICA_GIA_INSERITA = "EN001";
    private final static String CODICE_FATTURA_GIA_INSERITA = "EF001";

    private static Pattern patternFatturaGiaInserita = Pattern.compile(".*" + CODICE_FATTURA_GIA_INSERITA + ".*", Pattern.CASE_INSENSITIVE);

    private static Pattern patternNotificaGiaInserita = Pattern.compile(".*" + CODICE_NOTIFICA_GIA_INSERITA + ".*", Pattern.CASE_INSENSITIVE);


    public void process(Exchange exchange) throws Exception {

        String errorDescription = exchange.getIn().getHeader("error-description",String.class);

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        LOG.info("LanciaEccezioneErroreAreasProcessor: CONTROLLO MESSAGGIO DI ERRORE DI AREAS START - IDENTIFICATIVO SDI "+identificativoSdI);

        Matcher matcherFattura = patternFatturaGiaInserita.matcher(errorDescription);

        Matcher matcherNotifica = patternNotificaGiaInserita.matcher(errorDescription);

        if (!matcherFattura.matches()) {

            if (!matcherNotifica.matches()) {
                LOG.info("LanciaEccezioneErroreAreasProcessor: RICEVUTO MESSAGGIO DI ERRORE DA AREAS  DURANTE LA REGISTRAZIONE DELLA FATTURA/NOTIFICA '" + errorDescription + "' - IDENTIFICATIVO SDI " + identificativoSdI);
                throw new FatturaPAException(errorDescription);
            } else {
                LOG.info("LanciaEccezioneErroreAreasProcessor: IGNORO IL MESSAGGIO DI ERRORE DI AREAS 'DOCUMENTO GIA INSERITO' DURANTE LA REGISTRAZIONE DELLA NOTIFICA DECORRENZA - IDENTIFICATIVO SDI " + identificativoSdI);
            }
        } else {
            LOG.info("LanciaEccezioneErroreAreasProcessor: IGNORO IL MESSAGGIO DI ERRORE DI AREAS 'DOCUMENTO GIA INSERITO' DURANTE LA REGISTRAZIONE DELLA FATTURA - IDENTIFICATIVO SDI " + identificativoSdI);
        }

        LOG.info("LanciaEccezioneErroreAreasProcessor: CONTROLLO MESSAGGIO DI ERRORE DI AREAS END - IDENTIFICATIVO SDI "+identificativoSdI);

    }
}
