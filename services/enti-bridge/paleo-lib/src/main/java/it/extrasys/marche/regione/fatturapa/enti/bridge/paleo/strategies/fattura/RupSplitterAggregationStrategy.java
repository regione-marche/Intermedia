package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.strategies.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaMetadatiPaleo;
import it.marche.regione.paleo.services.OperatorePaleo;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/03/15.
 */
public class RupSplitterAggregationStrategy implements AggregationStrategy {

    Map<String, FatturaElettronicaMetadatiPaleo> map;

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if (oldExchange == null) {
            map = new HashMap<String, FatturaElettronicaMetadatiPaleo>();
            oldExchange = newExchange;

        } else {
            map = oldExchange.getIn().getBody(Map.class);
        }

        String numeroFattura = newExchange.getIn().getHeader("numeroFattura", String.class);
        String idDocumento = newExchange.getIn().getHeader("idDocumento", String.class);
        String codFascicolo = newExchange.getIn().getHeader("codiceFascicolo", String.class);
        String codStruttura = newExchange.getIn().getHeader("codiceStruttura", String.class);
        String descrizione = newExchange.getIn().getHeader("descrizione", String.class);

        String canaleAvanzato = newExchange.getIn().getHeader("canaleAvanzato", String.class);
        String fascicoloResult = newExchange.getIn().getHeader("fascicoloResult", String.class);
        String protocolloEntrataNote = newExchange.getIn().getHeader("protocolloEntrataNote", String.class);

        FatturaElettronicaMetadatiPaleo metadatiPaleo = new FatturaElettronicaMetadatiPaleo();

        metadatiPaleo.setIdDocumento(idDocumento);
        metadatiPaleo.setCodiceFascicolo(codFascicolo);
        metadatiPaleo.setCodiceStruttura(codStruttura);
        metadatiPaleo.setNumeroFattura(numeroFattura);
        metadatiPaleo.setDescrizione(descrizione);

        metadatiPaleo.setCanaleAvanzato(false);
        metadatiPaleo.setFascicoloResult(fascicoloResult);
        metadatiPaleo.setProtocolloEntrataNote(protocolloEntrataNote);

        if(canaleAvanzato == null || "".equals(canaleAvanzato)) {

            OperatorePaleo operatorePaleo = newExchange.getIn().getBody(OperatorePaleo.class);

        /*
        regma 136, se e' definifinto l'header "piuRuoliRup" significa che l'operatore ha piu' ruoli e va' settato correttamente il valore nei metadatipaleo.
        Cancello anche l'operatore paleo che non ndeve essere inserito in questo caso
         */
            String piuRuoliRup = newExchange.getIn().getHeader("piuRuoliRup", String.class);

            if ("true".equals(piuRuoliRup)) {
                metadatiPaleo.setRuoloUnico(false);

                if (operatorePaleo != null) {
                    if (operatorePaleo.getNome() != null) {
                        metadatiPaleo.setNomeRUP(operatorePaleo.getNome().getValue());
                    }

                    metadatiPaleo.setCognomeRUP(operatorePaleo.getCognome());

                    if (operatorePaleo.getCodiceFiscale() != null) {
                        metadatiPaleo.setCodiceFiscaleRUP(operatorePaleo.getCodiceFiscale().getValue());
                    }

                    operatorePaleo = null;
                }

            } else {
                metadatiPaleo.setRuoloUnico(true);
            }

            if (operatorePaleo != null) {
                newExchange.getIn().setHeader("rupAssegnato", true);

                metadatiPaleo.setRuoloRUP(operatorePaleo.getRuolo());

                if (operatorePaleo.getNome() != null) {
                    metadatiPaleo.setNomeRUP(operatorePaleo.getNome().getValue());
                }

                metadatiPaleo.setCognomeRUP(operatorePaleo.getCognome());

                metadatiPaleo.setCodiceUORUP(operatorePaleo.getCodiceUO());

                if (operatorePaleo.getCodiceFiscale() != null) {
                    metadatiPaleo.setCodiceFiscaleRUP(operatorePaleo.getCodiceFiscale().getValue());
                }

            } else {
                newExchange.getIn().setHeader("rupAssegnato", false);
            }
        }else{
            metadatiPaleo.setCanaleAvanzato(true);
        }

        map.put(numeroFattura, metadatiPaleo);

        oldExchange.getIn().setBody(map);

        return oldExchange;

    }
}
