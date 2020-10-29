package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreaMessaggioRispostaFallimentoProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaMessaggioRispostaFallimentoProcessor.class);

    private static final String CODICE_ERRORE_GENERICO = "EN99";
    private static final String DESC_ERRORE_GENERICO = "ERRORE GENERICO";

    @Override
    public void process(Exchange exchange) throws EsitoFatturaResponseFault {

        Message inMessage = exchange.getIn();

        String operationName = inMessage.getHeader(CxfConstants.OPERATION_NAME, String.class);

        LOG.info("operationName = " + operationName);

        String codErrore = inMessage.getHeader("ErrorCode", String.class);
        String descErrore = inMessage.getHeader("ErrorDescription", String.class);

        if(codErrore == null || "".equals(codErrore) || CODICE_ERRORE_GENERICO.equals(codErrore)){

            FaultDetailType faultDetailType = new FaultDetailType();
            faultDetailType.setCodice(CODICE_ERRORE_GENERICO);
            faultDetailType.setDescrizione(DESC_ERRORE_GENERICO);

            //Genero un SoapFault
            throw new EsitoFatturaResponseFault("Errore nell'elaborazione del messaggio", faultDetailType);

        }else {

            EsitoNotificaToEntiResponseCodeType errorCode = EsitoNotificaToEntiResponseCodeType.fromValue(codErrore);
            EsitoNotificaToEntiResponseDescriptionType errorDescription = EsitoNotificaToEntiResponseDescriptionType.fromValue(descErrore);

            EsitoFatturaMessageResponse response = new EsitoFatturaMessageResponse();
            response.setCode(errorCode);
            response.setDescription(errorDescription);

            inMessage.setBody(response);

            /*
            **** ?????????????? ****

            if(errorCode == null || "".compareTo(errorCode.value()) == 0){
                response.setCode(EsitoNotificaToEntiResponseCodeType.EN_03);
            }else {
                response.setCode(errorCode);
            }

            if(errorDescription == null || "".compareTo(errorDescription.value()) == 0){
                response.setDescription(EsitoNotificaToEntiResponseDescriptionType.ERRORE_GENERICO);
            }else {
                response.setDescription(errorDescription);
            }

            inMessage.setBody(response);
            */
        }
    }
}