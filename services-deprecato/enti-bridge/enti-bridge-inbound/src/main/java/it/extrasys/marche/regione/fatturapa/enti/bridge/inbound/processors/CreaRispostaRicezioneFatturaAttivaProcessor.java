package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.processors;

import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.beans.CodErroreInvioType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.beans.RispostaRiceviFileType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreaRispostaRicezioneFatturaAttivaProcessor implements Processor {

	private static final String ESITO_RISPOSTA_HEADER = "esitoRisposta";

	private static final String ESITO_OK = "FA01";
	private static final String ESITO_ERRORE_VALIDAZIONE = "FA02";
	private static final String ESITO_SERVICE_UNVAILABLE = "FA03";

	/**
     * Creo una risposta affermativa per il servizio  RiceviFatture.
     */
	@Override
	public void process(Exchange exchange) throws Exception {

		Message msg = exchange.getIn();

		String esitoRisposta = (String) msg.getHeader(ESITO_RISPOSTA_HEADER);

		RispostaRiceviFileType rispostaRiceviFattureType = new RispostaRiceviFileType();

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		rispostaRiceviFattureType.setDataOraRicezione(date);

		if(ESITO_OK.equals(esitoRisposta)){

			rispostaRiceviFattureType.setCodErrore(CodErroreInvioType.FA_01);

		} else if(ESITO_ERRORE_VALIDAZIONE.equals(esitoRisposta)){

			rispostaRiceviFattureType.setCodErrore(CodErroreInvioType.FA_02);
		} else {

			rispostaRiceviFattureType.setCodErrore(CodErroreInvioType.FA_03);
		}

		exchange.getIn().setBody(rispostaRiceviFattureType);
	}
 
}
