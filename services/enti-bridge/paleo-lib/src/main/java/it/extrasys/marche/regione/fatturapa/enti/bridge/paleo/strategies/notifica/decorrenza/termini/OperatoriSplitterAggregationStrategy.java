package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Operatore;
import it.marche.regione.paleo.services.OperatorePaleo;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class OperatoriSplitterAggregationStrategy implements AggregationStrategy {


    /*
    FIXME sbagliato! ora si aspetta che sia valorizzato l'intero array di operatorePaleo, invece ce ne sara'
    solo uno per volta che dovra' essere aggiunto all'array del nuovo oggetto Operatore!!!
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        List<Operatore> operatoreList;

        //controlla il body
        if(oldExchange == null) {

            operatoreList = new ArrayList<>();

            oldExchange = newExchange;
        } else {
            operatoreList = (List) oldExchange.getIn().getBody();
        }

        OperatorePaleo operatorePaleo = newExchange.getIn().getBody(OperatorePaleo.class);

        Operatore operatore = new Operatore();

        if (operatorePaleo != null) {

            operatore.setRuolo(operatorePaleo.getRuolo());

            if (operatorePaleo.getNome() != null) {
                operatore.setNome(operatorePaleo.getNome().getValue());
            }

            operatore.setCognome(operatorePaleo.getCognome());

            operatore.setUo(operatorePaleo.getCodiceUO());

            if (operatorePaleo.getCodiceFiscale() != null) {
                operatore.setCodiceFiscale(operatorePaleo.getCodiceFiscale().getValue());
            }

            operatoreList.add(operatore);

        }

        oldExchange.getIn().setBody(operatoreList);

        return oldExchange;
    }
}
