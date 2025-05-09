package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.processor;

import it.marche.regione.paleo.services.ArrayOfrespDocProtocolliInfo;
import it.marche.regione.paleo.services.BEListOfrespDocProtocolliInfoZA0HwLp5;
import it.marche.regione.paleo.services.MessaggioRisultato;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class RispostaGetDocumentiProtocolliInFascicoloProcessor implements Processor {

    private final String CODICE_FASCICOLO_HEADER = "codiceFascicolo";
    private final String PROTOCOLLAZIONE_ENTRATA_NOTE_HEADER = "protocolloEntrataNote";

    private final String FASCICOLO_INESISTENTE_ERROR = "fascicolo inesistente";
    private final String FASCICOLO_NO_DIRITTI_ERROR = "operatore non ha i diritti di visibilit";
    //private final String ALTRO_ERROR = "altro";
    private final String FASCICOLO_CHIUSO_INFO = "fascicolo chiuso";

    @Override
    public void process(Exchange exchange) {

        Message msg = exchange.getIn();

        String codiceFascicolo = (String) msg.getHeader(CODICE_FASCICOLO_HEADER);

        MessageContentsList listaMessaggi = msg.getBody(MessageContentsList.class);

        BEListOfrespDocProtocolliInfoZA0HwLp5 listOfrespDocProtocolliInfoZA0HwLp5 = (BEListOfrespDocProtocolliInfoZA0HwLp5) listaMessaggi.get(0);

        ArrayOfrespDocProtocolliInfo arrayOfrespDocProtocolliInfo = listOfrespDocProtocolliInfoZA0HwLp5.getLista().getValue();

        MessaggioRisultato messaggioRisultato = listOfrespDocProtocolliInfoZA0HwLp5.getMessaggioRisultato().getValue();

        boolean esito = true;
        String protocolloEntrataNote = "";

        switch (messaggioRisultato.getTipoRisultato()) {

            case INFO:

                if (messaggioRisultato.getDescrizione().getValue().toLowerCase().contains(FASCICOLO_CHIUSO_INFO)) {

                    esito = false;
                    protocolloEntrataNote = "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo + " perché chiuso";

                }

                break;

            case WARNING:
                    esito = true;
                break;

            case ERROR:

                if (messaggioRisultato.getDescrizione().getValue().toLowerCase().contains(FASCICOLO_INESISTENTE_ERROR)) {

                    esito = false;
                    protocolloEntrataNote = "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo + " perché inesistente";

                } else if (messaggioRisultato.getDescrizione().getValue().toLowerCase().contains(FASCICOLO_NO_DIRITTI_ERROR)) {

                    esito = false;
                    protocolloEntrataNote = "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo + " perché non visibile al protocollista virtuale";

                /*
                } else if (messaggioRisultato.getDescrizione().getValue().toLowerCase().contains(ALTRO_ERROR)) {

                    esito = false;
                    protocolloEntrataNote = "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo;
                }
                */
                } else {

                    esito = false;
                    protocolloEntrataNote = "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo;

                }

                break;
        }

        msg.setHeader(PROTOCOLLAZIONE_ENTRATA_NOTE_HEADER, protocolloEntrataNote);

        if(esito){
            msg.setBody("OK");
        }else{
            msg.setBody("KO");
        }
        //throw new FatturaPAException("ERRORE DURANTE LA VALIDAZIONE DEL CODICE FASCICOLO: " + messaggioRisultato.getDescrizione().getValue());
    }
}