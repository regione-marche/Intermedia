package it.extrasys.marche.regione.fatturapa.storicizzazione.processor;

import it.extrasys.marche.regione.fatturapa.persistence.camel.model.ReportFatturazioneStorico;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaStoricizzataManager;

import it.extrasys.marche.regione.fatturapa.storicizzazione.utils.StoricoConstant;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SalvaCancellaFatturaPassivaStoricizzataProcessor implements Processor {
	private static final Logger LOG = LoggerFactory.getLogger(SalvaCancellaFatturaPassivaStoricizzataProcessor.class);

	//private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

	private FatturazionePassivaStoricizzataManager fatturazionePassivaStoricizzataManager;

	@Override
	public void process(Exchange exchange) throws Exception {

		String identificativoSdI = (String) exchange.getIn().getBody();
		//recupero le informazioni da salvare
		List<ReportFatturazioneStorico> reportFatturazionePassivaStoricoList = fatturazionePassivaStoricizzataManager.storicizzaFatturaPassivaPerIdentificativoSdi(identificativoSdI);

		exchange.setProperty(StoricoConstant.REPORT_LIST_PROP, reportFatturazionePassivaStoricoList);

	}

	public FatturazionePassivaStoricizzataManager getFatturazionePassivaStoricizzataManager() {
		return fatturazionePassivaStoricizzataManager;
	}

	public void setFatturazionePassivaStoricizzataManager(FatturazionePassivaStoricizzataManager fatturazionePassivaStoricizzataManager) {
		this.fatturazionePassivaStoricizzataManager = fatturazionePassivaStoricizzataManager;
	}
}