package it.extrasys.marche.regione.fatturapa.services.invioFatturazioneAttiva;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.FileSdIBaseType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.ObjectFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.util.encoders.Base64;

public class InvioFatturazioneAttivaProcessor implements Processor {

	private static final String NOME_FILE_FATTURA_HEADER = "nomeFile";

	@SuppressWarnings("restriction")
	@Override
	public void process(Exchange exchange) throws Exception {

		String fattura = (String) exchange.getIn().getBody();
		String nomeFile = (String) exchange.getIn().getHeader(NOME_FILE_FATTURA_HEADER);

		ObjectFactory objectFactory = new ObjectFactory();
		FileSdIBaseType richiesta = objectFactory.createFileSdIBaseType();

		byte[] fatturaElettronicaBytesArray = Base64.decode(fattura);

		DataSource fatturapa = new ByteArrayDataSource(fatturaElettronicaBytesArray ,"application/octet-stream");

		richiesta.setFile(new DataHandler(fatturapa));
		richiesta.setNomeFile(nomeFile);

		exchange.getIn().setBody(richiesta);
	}
}
