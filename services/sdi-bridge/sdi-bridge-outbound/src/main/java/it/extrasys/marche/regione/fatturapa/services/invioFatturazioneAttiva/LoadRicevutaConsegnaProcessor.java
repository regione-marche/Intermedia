package it.extrasys.marche.regione.fatturapa.services.invioFatturazioneAttiva;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoadRicevutaConsegnaProcessor implements Processor{
    private String ricevutaConsegnaPath;

    @Override
    public void process(Exchange exchange) throws Exception {
        if (ricevutaConsegnaPath == null) {
            throw new NullArgumentException("fatturaPath");
        }
        URL url = new URL(ricevutaConsegnaPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String fileString = IOUtils.toString(br);

      /*  String numeroSequenza = generaNumeroSequenza((Long) exchange.getIn().getHeader("identificativoSdI"));
        String nomeFile = Constants.NOME_FILE_FATTURA.replace(Constants.NUMERO_SEQUENZA, numeroSequenza);

        exchange.getIn().setHeader("nomeFile", nomeFile);*/
        exchange.getIn().setBody(fileString);
    }

    public String getRicevutaConsegnaPath() {
        return ricevutaConsegnaPath;
    }

    public void setRicevutaConsegnaPath(String ricevutaConsegnaPath) {
        this.ricevutaConsegnaPath = ricevutaConsegnaPath;
    }
}
