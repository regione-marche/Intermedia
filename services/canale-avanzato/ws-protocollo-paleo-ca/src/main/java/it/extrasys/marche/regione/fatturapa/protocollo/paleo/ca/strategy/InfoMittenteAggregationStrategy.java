package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.strategy;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class InfoMittenteAggregationStrategy implements AggregationStrategy {

    private final String MITTENTE_DENOMINAZIONE_FATT_HEADER = "mittenteDenominazione";
    private final String MITTENTE_COGNOME_FATT_HEADER = "mittenteCognome";
    private final String MITTENTE_NOME_FATT_HEADER = "mittenteNome";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        String mittente;

        String mittenteDenominazione = (String) newExchange.getIn().getHeader(MITTENTE_DENOMINAZIONE_FATT_HEADER);
        String mittenteCognome = (String) newExchange.getIn().getHeader(MITTENTE_COGNOME_FATT_HEADER);
        String mittenteNome = (String) newExchange.getIn().getHeader(MITTENTE_NOME_FATT_HEADER);

        if(mittenteDenominazione == null || "".equals(mittenteDenominazione.trim())){
            mittente = mittenteCognome + " " + mittenteNome;
        }else{
            mittente = mittenteDenominazione;
        }

        oldExchange.getIn().getBody(FatturaElettronicaWrapper.class).setMittente(mittente);

        return oldExchange;
    }
}