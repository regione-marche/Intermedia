package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.*;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreaRispostaRicezioneFatturaAttivaProcessor implements Processor {

	private static final String CODICE_ERRORE_GENERICO = "FA99";
	private static final String DESC_ERRORE_GENERICO = "ERRORE GENERICO";
	private static final String ESITO_RISPOSTA_HEADER = "esitoRisposta";
	private static final String RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE = "ricevutaComunicazione";

	@Override
	public void process(Exchange exchange) throws Exception {

		Message msg = exchange.getIn();

		String esitoRisposta = (String) msg.getHeader(ESITO_RISPOSTA_HEADER);

		if(esitoRisposta == null || "".equals(esitoRisposta) || CODICE_ERRORE_GENERICO.equals(esitoRisposta)){

			FaultDetailType faultDetailType = new FaultDetailType();
			faultDetailType.setCodice(CODICE_ERRORE_GENERICO);
			faultDetailType.setDescrizione(DESC_ERRORE_GENERICO);

			//Genero un SoapFault
			throw new RispostaRiceviFileCAMsgFault("Errore nell'elaborazione del messaggio", faultDetailType);

		}else {

			CodInvioCAType codInvioCAType = CodInvioCAType.fromValue(esitoRisposta);

			RispostaRiceviFileCAType rispostaRiceviFileCAType = new RispostaRiceviFileCAType();

			GregorianCalendar c = new GregorianCalendar();
			c.setTime(new Date());
			XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

			rispostaRiceviFileCAType.setDataOraRicezione(date);

			switch (codInvioCAType) {

				case FA_00:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_00);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.RICEZIONE_AVVENUTA_CON_SUCCESSO);

					break;

				case FA_01:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_01);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.CREDENZIALI_NON_VALIDE);

					break;

				case FA_02:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_02);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.FILE_FATTURA_RIFIUTATO_ERRORE_VALIDAZIONE_FATTURA);

					break;

				case FA_03:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_03);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.ENTE_NON_ABILITATO_A_INTERMEDIAMARCHE);

					break;

				case FA_04:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_04);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.NOME_FILE_FATTURA_NON_VALIDO);

					break;

				case FA_05:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_05);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.ALLEGATO_NON_TROVATO);

					break;

				case FA_06:

					rispostaRiceviFileCAType.setCodice(CodInvioCAType.FA_06);
					rispostaRiceviFileCAType.setDescrizione(DescInvioCAType.MESSAGGIO_RIFIUTATO_CAMPI_OBBLIGATORI_NON_VALORIZZATI);

					break;
			}

			String ricevutaComunicazione = msg.getHeader(RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE, String.class);

			if (ricevutaComunicazione != null && !"".equals(ricevutaComunicazione)) {
				rispostaRiceviFileCAType.setRicevutaComunicazione(ricevutaComunicazione);
			} else {
				rispostaRiceviFileCAType.setRicevutaComunicazione(CommonUtils.createRicevutaComunicazione());
			}
			exchange.getIn().setBody(rispostaRiceviFileCAType);
		}
	}
}