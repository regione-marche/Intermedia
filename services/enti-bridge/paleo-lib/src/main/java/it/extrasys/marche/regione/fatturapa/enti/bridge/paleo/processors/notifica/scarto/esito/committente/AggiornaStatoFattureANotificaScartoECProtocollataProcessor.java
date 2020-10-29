package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.scarto.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggiornaStatoFattureANotificaScartoECProtocollataProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoFattureANotificaScartoECProtocollataProcessor.class);

    private DatiFatturaManager fatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        LOG.info("AggiornaStatoFattureANotificaScartoECProtocollataProcessor: Protocollo " + notificaScartoEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());

        String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_PROTOCOLLATA.getValue();

        fatturaManager.protocollaNotifica(identificativoSdI, codificaStatoEntity);

        //fatturaManager.protocollaNotifica(identificativoSdI);
    }

	public DatiFatturaManager getFatturaManager() {
		return fatturaManager;
	}

	public void setFatturaManager(DatiFatturaManager fatturaManager) {
		this.fatturaManager = fatturaManager;
	}

}