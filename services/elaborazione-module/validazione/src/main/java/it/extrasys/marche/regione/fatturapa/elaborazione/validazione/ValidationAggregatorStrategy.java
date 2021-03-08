package it.extrasys.marche.regione.fatturapa.elaborazione.validazione;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationAggregatorStrategy implements AggregationStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(ValidationAggregatorStrategy.class);

	//private String messaggioErrore = "fattura rifiutata per mancanza riferimenti all’ordine nei campi 2.1.2.2 (numero ordine) e 2.1.2.3 (data ordine). Se ordine non esistente inserire &#226;NO ORDINE&#226; sul campo 2.1.2.2 e &#226;01/01/2015&#226;  sul campo 2.1.2.3.";
	private String messaggioErrore = "Fattura rifiutata per mancanza riferimenti all&#226; ordine nel campi 2.1.2.2 (numero ordine). Se ordine non esistente inserire \"NO ORDINE\"; sul campo 2.1.2.2.";

	private String messaggioErroreNotaCredito = "Nota credito rifiutata per mancanza riferimenti alla fattura da stornare nel campo 2.1.6.2 (Fatture Collegate)";

	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		// put order together in old exchange by adding the order from new
		// exchange

		String tipoDoc = (String) newExchange.getIn().getHeader("tipoDocumento");

		boolean validationResult = false;

		if (oldExchange == null) {

			//Commentato controllo per richiesta di Stefano Barbabietolini ed autorizzato da Pacetti 12/01/2021
			//validationResult = checkHeaders(newExchange);
			validationResult = true;

			if(validationResult == false){

				if(tipoDoc == null || "".equals(tipoDoc.trim())){
					newExchange.getIn().setHeader("validationErrorMessage", "Errore Identificazione Tipo Documento");
				}else{
					if ("TD04".equalsIgnoreCase(tipoDoc)) {
						newExchange.getIn().setHeader("validationErrorMessage", messaggioErroreNotaCredito);
					}else{
						newExchange.getIn().setHeader("validationErrorMessage", messaggioErrore);
					}
				}
			}

			newExchange.getIn().setHeader("validationResult", validationResult);

			
			if ((Boolean) newExchange.getProperty("CamelSplitComplete")){
				setPreSplittingBody(newExchange);
			}
			
			return newExchange;
		}

		//Commentato controllo per richiesta di Stefano Barbabietolini ed autorizzato da Pacetti 12/01/2021
		//validationResult = checkHeaders(newExchange);
		validationResult = true;

		Boolean oldResult = (Boolean) oldExchange.getIn().getHeader("validationResult");

		if (oldResult == null){
			oldResult = true;
		}

		validationResult = validationResult && oldResult;

		if(validationResult == false){

			if(tipoDoc == null || "".equals(tipoDoc.trim())){
				oldExchange.getIn().setHeader("validationErrorMessage", "Errore Identificazione Tipo Documento");
			}else{
				if ("TD04".equalsIgnoreCase(tipoDoc)) {
					oldExchange.getIn().setHeader("validationErrorMessage", messaggioErroreNotaCredito);
				}else{
					oldExchange.getIn().setHeader("validationErrorMessage", messaggioErrore);
				}
			}
		}

		oldExchange.getIn().setHeader("validationResult", validationResult);

		if ((Boolean) newExchange.getProperty("CamelSplitComplete")){
			setPreSplittingBody(oldExchange);
		}

		return oldExchange;
	}

	private void setPreSplittingBody(Exchange exchange) {

		exchange.getIn().setBody(exchange.getIn().getHeader("preSplittingBody"));

	}

	//Controllo non più utilizzato per richiesta di Stefano Barbabietolini ed autorizzato da Pacetti 12/01/2021
	public static boolean checkHeaders(Exchange newExchange) {

		String tipoDoc = (String) newExchange.getIn().getHeader("tipoDocumento");

		LOG.debug("VALIDAZIONE: TIPO DOCUMENTO " + tipoDoc);

		//il seguente blocco non funziona correttamente, per ora non verifico mai se la nota di credito deve essere validata o no, semplicemente non le considero
		//if(tipoDoc != null && ("TD01".equalsIgnoreCase(tipoDoc) || "TD04".equalsIgnoreCase(tipoDoc))){
		if(tipoDoc != null && "TD01".equalsIgnoreCase(tipoDoc)){

			String idDocumento = (String) newExchange.getIn().getHeader("idDocumento");

			if (idDocumento == null || idDocumento.trim().isEmpty() ){
				return false;
			} else {
				return true;
			}
		}else{
			return true;
		}
	}
}