package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;
import it.marche.regione.paleo.services.RespProtocolloArrivoExt;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class RispostaCercaDocumentoProtocolloEsitoCommittenteProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);

        if (messageContentsList != null && messageContentsList.size() > 0) {

            RespProtocolloArrivoExt responsePaleo = (RespProtocolloArrivoExt) messageContentsList.get(0);
            JAXBElement<ArrayOfstring> jaxbClassificazioni = responsePaleo.getClassificazioni();
            ArrayOfstring classificazioni = jaxbClassificazioni.getValue();
            List<String> classificazioniList = classificazioni.getString();
            exchange.getIn().setBody(classificazioniList, List.class);
            return;
        }

        exchange.getIn().setBody(null, List.class);
    }
}
