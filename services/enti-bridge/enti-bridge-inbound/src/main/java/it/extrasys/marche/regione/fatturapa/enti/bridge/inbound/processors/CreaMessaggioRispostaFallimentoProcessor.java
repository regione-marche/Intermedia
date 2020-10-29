package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageResponse;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoNotificaToEntiResponseErrorCodeType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/02/15.
 */
public class CreaMessaggioRispostaFallimentoProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String errorCode = exchange.getIn().getHeader("ErrorCode", String.class);

        String errorDescription = exchange.getIn().getHeader("ErrorDescription", String.class);
        
        EsitoFatturaMessageResponse response = new EsitoFatturaMessageResponse();

        if(errorCode == null || "".compareTo(errorCode.trim())==0){
            response.setErrorCode(EsitoNotificaToEntiResponseErrorCodeType.EN_03);
        }else {
            response.setErrorCode(EsitoNotificaToEntiResponseErrorCodeType.fromValue(errorCode));
        }

        if(errorDescription == null || "".compareTo(errorDescription.trim())==0){
            response.setErrorDescription("SERVIZIO NON DISPONIBILE");
        }else {
            response.setErrorDescription(errorDescription);
        }

        response.setIdComunicazione(exchange.getIn().getHeader("idComunicazione", String.class));
        
        exchange.getIn().setBody(response);
        
    }
}
