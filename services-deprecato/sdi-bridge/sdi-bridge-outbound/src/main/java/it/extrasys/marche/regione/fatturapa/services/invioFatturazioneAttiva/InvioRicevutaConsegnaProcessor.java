package it.extrasys.marche.regione.fatturapa.services.invioFatturazioneAttiva;

import it.extrasys.marche.regione.fatturapa.contracts.trasmissione.fatture.sdi.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.contracts.trasmissione.fatture.sdi.beans.ObjectFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class InvioRicevutaConsegnaProcessor implements Processor {

    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileRC";

    @SuppressWarnings("restriction")
    @Override
    public void process(Exchange exchange) throws Exception {

        String ricevutaConsegna = (String) exchange.getIn().getBody();
        String nomeFile = (String) exchange.getIn().getHeader(NOME_FILE_FATTURA_HEADER);
        BigInteger identificativoSdi = (BigInteger) exchange.getIn().getHeader("identificativoSdI");

        ObjectFactory objectFactory = new ObjectFactory();
        FileSdIType ricevuta = objectFactory.createFileSdIType();

        byte[] ricevutaConsegnaBytesArray = ricevutaConsegna.getBytes(Charset.forName("UTF-8")); //Base64.decode(ricevutaConsegna);

        DataSource fatturapa = new ByteArrayDataSource(ricevutaConsegnaBytesArray, "application/octet-stream");

        ricevuta.setFile(new DataHandler(fatturapa));
        ricevuta.setNomeFile(nomeFile);
        ricevuta.setIdentificativoSdI(identificativoSdi);

        FileSdIType ricevutaConsegna1 = objectFactory.createRicevutaConsegna(ricevuta).getValue();
        exchange.getIn().setBody(ricevuta);
    }
}
