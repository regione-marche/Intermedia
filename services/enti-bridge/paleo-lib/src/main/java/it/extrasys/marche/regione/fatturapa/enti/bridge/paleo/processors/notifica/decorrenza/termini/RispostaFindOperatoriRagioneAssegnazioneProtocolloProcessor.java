package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini;

import it.marche.regione.paleo.services.ArrayOfOperatorePaleo;
import it.marche.regione.paleo.services.BEListOfOperatorePaleoZA0HwLp5;
import it.marche.regione.paleo.services.OperatorePaleo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agosteeno on 15/04/16.
 */
public class RispostaFindOperatoriRagioneAssegnazioneProtocolloProcessor implements Processor {


    private static final Logger LOG = LoggerFactory.getLogger(RispostaFindOperatoriRagioneAssegnazioneProtocolloProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList listaMessaggi = exchange.getIn().getBody(MessageContentsList.class);

        BEListOfOperatorePaleoZA0HwLp5 beListOfOperatorePaleoZA0HwLp5 = (BEListOfOperatorePaleoZA0HwLp5) listaMessaggi.get(0);

        ArrayOfOperatorePaleo arrayOfOperatorePaleo = beListOfOperatorePaleoZA0HwLp5.getLista().getValue();

        List<OperatorePaleo> operatorePaleos = arrayOfOperatorePaleo.getOperatorePaleo();

        OperatorePaleo operatorePaleo = filtraProtocollista(operatorePaleos);

        exchange.getIn().setBody(operatorePaleo);
    }

    private OperatorePaleo filtraProtocollista(List<OperatorePaleo> listaOperatoriDaFiltrare) {

        List<OperatorePaleo> listaOperatoriFiltrata = new ArrayList<OperatorePaleo>(listaOperatoriDaFiltrare.size());

        for (OperatorePaleo op : listaOperatoriDaFiltrare) {

            LOG.info("GIUNTA: operatore:"+op.getNome().getValue()+" "+op.getCognome()+" ruolo: "+op.getRuolo());

            if (op != null && "PROTOCOLLISTA".equals(op.getRuolo().toUpperCase())) {
                LOG.info("GIUNTA: operatore:"+op.getNome().getValue()+" "+op.getCognome()+" ruolo: "+op.getRuolo()+" TROVATO!");
                return op;
           }
        }
        return null;
    }

}
