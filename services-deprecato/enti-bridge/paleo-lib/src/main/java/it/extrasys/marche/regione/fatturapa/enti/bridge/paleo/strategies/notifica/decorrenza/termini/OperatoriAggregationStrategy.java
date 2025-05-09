package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Operatore;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.List;

/**
 * Created by agosteeno on 15/04/16.
 */
public class OperatoriAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = oldExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        List<Operatore> operatoriList = newExchange.getIn().getBody(List.class);

        if (operatoriList != null && operatoriList.size() > 0) {
            notificaDecorrenzaTerminiWrapper.setOperatoreList(operatoriList);
        }

        oldExchange.getIn().setBody(notificaDecorrenzaTerminiWrapper);

        return oldExchange;
    }
}
