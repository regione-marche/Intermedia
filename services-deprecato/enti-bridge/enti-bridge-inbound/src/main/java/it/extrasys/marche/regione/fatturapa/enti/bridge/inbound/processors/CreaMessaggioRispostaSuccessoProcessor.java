package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageResponse;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoNotificaToEntiResponseErrorCodeType;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/02/15.
 */

public class CreaMessaggioRispostaSuccessoProcessor implements Processor {
    
    @Override
    public void process(Exchange exchange) throws Exception {

        EsitoFatturaMessageResponse response = new EsitoFatturaMessageResponse();

        NotificaFromEntiEntity notificaFromEntiEntity = exchange.getIn().getBody(NotificaFromEntiEntity.class);

        response.setErrorCode(EsitoNotificaToEntiResponseErrorCodeType.fromValue("EN00"));

        response.setErrorDescription("Notifiche Fatture Accettate");

        response.setIdComunicazione(exchange.getIn().getHeader("idComunicazione", String.class));

        exchange.getIn().setBody(response);
    }
}