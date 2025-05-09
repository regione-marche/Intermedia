package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.notifiche;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitiType;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Locale;

/**
 * Created by agosteeno on 11/03/15.
 */
public class NotificaRifiutoProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(NotificaRifiutoProcessor.class);

	public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);


		LOG.info("NotificaRifiutoProcessor - PROCESS: NOTIFICA ESITO COMMITTENTE CON ESITO DI RIFIUTO - IDENTIFICATIVO SDI "+identificativoSdI);

		Message msg = exchange.getIn();

		// il tipo restituito dovrebbe essere un EsitoFatturaMessageRequest

		EsitoFatturaMessageType esitoFatturaMessageType = new EsitoFatturaMessageType();
		esitoFatturaMessageType.setEsito(EsitiType.fromValue("EC02"));
		esitoFatturaMessageType.setIdFiscaleCommittente(msg.getHeader("committenteCodiceIva", String.class));
		esitoFatturaMessageType.setCodUfficio(msg.getHeader("codiceUfficio", String.class));
		Date dataFattura = DateUtils.parseDate(msg.getHeader("dataDocumento", String.class), "yyyy-MM-dd", Locale.ITALIAN);
		esitoFatturaMessageType.setDataFattura(DateUtils.DateToXMLGregorianCalendar(dataFattura));
		esitoFatturaMessageType.setDescrizione(msg.getHeader("validationErrorMessage", String.class));
		exchange.getIn().setBody(esitoFatturaMessageType, EsitoFatturaMessageType.class);

	}
}