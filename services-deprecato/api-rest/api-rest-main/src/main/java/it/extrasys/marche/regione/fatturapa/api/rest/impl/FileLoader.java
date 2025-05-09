package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.utils.Constants;
import org.apache.camel.Exchange;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileLoader {

    private static final Logger log = LoggerFactory.getLogger(FileLoader.class);

    private String fatturaPath;
    private String metadatiPath;


    public void loadFattura(Exchange exchange) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
        if (fatturaPath == null) {
            throw new NullArgumentException("fatturaPath");
        }
        URL url = new URL(fatturaPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String fileString = IOUtils.toString(br);

       /* String numeroSequenza = generaNumeroSequenza((Long) exchange.getIn().getHeader("identificativoSdI"));
        String nomeFile = Constants.NOME_FILE_FATTURA.replace(Constants.NUMERO_SEQUENZA, numeroSequenza);

        exchange.getIn().setHeader("nomeFile", nomeFile);*/
        exchange.getIn().setBody(fileString);

    }


    public void loadMetadati(Exchange exchange) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException {
        if (metadatiPath == null) {
            throw new NullArgumentException("metadatiPath");
        }

        URL url = new URL(metadatiPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String fileString = IOUtils.toString(br);

        /*String numeroSequenza = generaNumeroSequenza((Long) exchange.getIn().getHeader("identificativoSdI"));
        String nomeFile = Constants.NOME_FILE_METADATI.replace(Constants.NUMERO_SEQUENZA, numeroSequenza);

        exchange.getIn().setHeader("nomeFileMetadati", nomeFile);*/
        exchange.getIn().setBody(fileString);
    }





    public void encodeBase64(Exchange exchange) {
        String body = (String) exchange.getIn().getBody();
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String bytesFile = new String(Base64.encodeBase64(bytes));

        exchange.getIn().setBody(bytesFile);
    }

    public String getFatturaPath() {
        return fatturaPath;
    }

    public void setFatturaPath(String fatturaPath) {
        this.fatturaPath = fatturaPath;
    }

    public String getMetadatiPath() {
        return metadatiPath;
    }

    public void setMetadatiPath(String metadatiPath) {
        this.metadatiPath = metadatiPath;
    }
}
