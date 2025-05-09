package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.processors;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.EsitoRicezioneType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.RispostaRiceviFattureType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/02/15.
 */
public class CreaRispostaNotificaDecorrenzaTerminiProcessor implements Processor {

    /**
     * Creo una risposta affermativa per il servizio  RiceviFatture.
     */
	@Override
	public void process(Exchange exchange) throws Exception {
		//TODO
	}
 
}
