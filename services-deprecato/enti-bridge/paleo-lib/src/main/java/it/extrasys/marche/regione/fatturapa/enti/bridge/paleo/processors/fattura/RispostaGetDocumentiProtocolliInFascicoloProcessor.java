package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.marche.regione.paleo.services.ArrayOfrespDocProtocolliInfo;
import it.marche.regione.paleo.services.BEListOfrespDocProtocolliInfoZA0HwLp5;
import it.marche.regione.paleo.services.MessaggioRisultato;
import it.marche.regione.paleo.services.TipoRisultato;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/04/15.
 */
public class RispostaGetDocumentiProtocolliInFascicoloProcessor implements Processor {


    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList listaMessaggi = exchange.getIn().getBody(MessageContentsList.class);

        BEListOfrespDocProtocolliInfoZA0HwLp5 listOfrespDocProtocolliInfoZA0HwLp5 = (BEListOfrespDocProtocolliInfoZA0HwLp5) listaMessaggi.get(0);

        ArrayOfrespDocProtocolliInfo arrayOfrespDocProtocolliInfo = listOfrespDocProtocolliInfoZA0HwLp5.getLista().getValue();

        MessaggioRisultato messaggioRisultato = listOfrespDocProtocolliInfoZA0HwLp5.getMessaggioRisultato().getValue();

        if (messaggioRisultato.getTipoRisultato().value().compareTo(TipoRisultato.ERROR.value()) == 0) {
            if ("Fascicolo inesistente.".compareTo(messaggioRisultato.getDescrizione().getValue()) == 0 || messaggioRisultato.getDescrizione().getValue().toLowerCase().contains("non ha i diritti di visibilit")) {
                exchange.getIn().setBody("KO");
            } else {
                throw new FatturaPAException("ERRORE DURANTE LA VALIDAZIONE DEL CODICE FASCICOLO: " + messaggioRisultato.getDescrizione().getValue());
            }
        } else {
            exchange.getIn().setBody("OK");
        }
    }
}
