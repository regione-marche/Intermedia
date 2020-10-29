package it.extrasys.marche.regione.fatturapa.services.invioFatturazioneAttiva;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.RispostaSdIRiceviFileType;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class EsitoInvioFatturazioneAttivaProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(EsitoInvioFatturazioneAttivaProcessor.class);

	FatturaAttivaManagerImpl fatturaAttivaManager;
	
	@Override
	public void process(Exchange exchange) throws Exception {

		String idFattura = (String) exchange.getIn().getHeader("idFatturaAttiva");

		MessageContentsList contentsList = exchange.getIn().getBody(MessageContentsList.class);

		RispostaSdIRiceviFileType rispostaSdINotificaEsito = (RispostaSdIRiceviFileType) contentsList.get(0);
		BigInteger identificativoSdI = rispostaSdINotificaEsito.getIdentificativoSdI();

		LOG.info("EsitoInvioFatturazioneAttivaProcessor - salvo la fattura con Identificativo SdI " + identificativoSdI + " e idFatturaFattiva " + idFattura + ";");

		// Savla SdI e statoFattura
		fatturaAttivaManager.salvaIdentificativoSdIAttiva(new BigInteger(idFattura), identificativoSdI);
		LOG.info("EsitoInvioFatturazioneAttivaProcessor - Identificativo SdI " + identificativoSdI + " e idFatturaFattiva " + idFattura + ": fattura salvata");

		//cancello il body perché dà fastidio ad activemq
		exchange.getIn().setBody(null);
	}

	public FatturaAttivaManagerImpl getFatturaAttivaManager() {
		return fatturaAttivaManager;
	}

	public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
		this.fatturaAttivaManager = fatturaAttivaManager;
	}
	
	

	

}
