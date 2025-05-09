package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente;

import it.marche.regione.paleo.services.RespProtocolloArrivo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 17/03/15.
 */
public class RispostaProtocollazioneNotificaEsitoCommittenteProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);

        if (messageContentsList != null && messageContentsList.size() > 0) {

            RespProtocolloArrivo responseRespProtocolloArrivo = (RespProtocolloArrivo) messageContentsList.get(0);

            if (responseRespProtocolloArrivo.getSegnatura() != null) {
                exchange.getIn().setBody(responseRespProtocolloArrivo.getSegnatura(), String.class);
                return;
            }

            exchange.getIn().setBody("");

        }
    }
}
