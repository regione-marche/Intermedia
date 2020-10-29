package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.marche.regione.paleo.services.ArrayOfOperatorePaleo;
import it.marche.regione.paleo.services.BEListOfOperatorePaleoZA0HwLp5;
import it.marche.regione.paleo.services.OperatorePaleo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */
public class RispostaFindOperatoriProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(RispostaFindOperatoriProcessor.class);

    private String ruoliOperatoreDaFiltrareStringCSV;

    private Set<String> ruoliOperatoreDaFiltrareSet;

    public RispostaFindOperatoriProcessor(String ruoliOperatoreDaFiltrareStringCSV) {

        this.ruoliOperatoreDaFiltrareStringCSV = ruoliOperatoreDaFiltrareStringCSV;

        ruoliOperatoreDaFiltrareSet = new HashSet<String>();

        if (ruoliOperatoreDaFiltrareStringCSV != null && !ruoliOperatoreDaFiltrareStringCSV.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(ruoliOperatoreDaFiltrareStringCSV, ",");

            while (st.hasMoreTokens()) {
                ruoliOperatoreDaFiltrareSet.add(st.nextToken());
            }
        }
    }

    public void process(Exchange exchange) throws Exception {

        LOG.info("GIUNTA: Numero Ruoli Da Filtrare: #: " + ruoliOperatoreDaFiltrareSet.size());

        LOG.info("GIUNTA: Lista Ruoli da Filtrare:" + ruoliOperatoreDaFiltrareStringCSV);

        String piuRuoliRup = "false";

        String codiceStruttura = exchange.getIn().getHeader("codiceStruttura", String.class);

        MessageContentsList listaMessaggi = exchange.getIn().getBody(MessageContentsList.class);

        BEListOfOperatorePaleoZA0HwLp5 beListOfOperatorePaleoZA0HwLp5 = (BEListOfOperatorePaleoZA0HwLp5) listaMessaggi.get(0);

        ArrayOfOperatorePaleo arrayOfOperatorePaleo = beListOfOperatorePaleoZA0HwLp5.getLista().getValue();

        List<OperatorePaleo> operatorePaleos = arrayOfOperatorePaleo.getOperatorePaleo();

        operatorePaleos = filtraOperatoriPaleo(operatorePaleos);

        if (operatorePaleos.size() == 1){
            OperatorePaleo op = operatorePaleos.get(0);
            LOG.info("GIUNTA: TROVATO OPERATORE:"+op.getNome().getValue()+" "+op.getCognome()+" ruolo: "+op.getRuolo());
            exchange.getIn().setBody(op);

            exchange.getIn().setHeader("piuRuoliRup", piuRuoliRup);

            return;
        }

        //aggiunto per REGMA136-137
        if(operatorePaleos.size() > 1) {

            if(codiceStruttura != null && !codiceStruttura.trim().isEmpty()) {
                LOG.info("GIUNTA: ATTENZIONE: PIU' OPERATORI TROVATI");

                int countRuoli = 0;

                for (OperatorePaleo op : operatorePaleos) {

                    if (codiceStruttura.equalsIgnoreCase(op.getCodiceUO())) {
                        LOG.info("GIUNTA: TROVATO OPERATORE:" + op.getNome().getValue() + " " + op.getCognome() + " ruolo: " + op.getRuolo());
                        exchange.getIn().setBody(op);
                        countRuoli++;
                    }
                }

                if (countRuoli != 1) {

                    LOG.info("GIUNTA: ATTENZIONE: PIU' RUOLI DISPONIBILI PER L'OPERATORE NELLA STESSA STRUTTURA: IMPOSSIBILE STABILIRE IL RUOLO");

                    //questa descrizione deve essere aggiunta nel campo note del protocollo. Bisogna distinguere il caso di RUP non trovato perche' non presente da quello di
                    //RUP non identificato a causa di piu' ruoli disponibili
                    piuRuoliRup = "true";
                    //exchange.getIn().setBody(null);
                } else {
                    LOG.info("GIUNTA: ATTENZIONE: PIU' RUOLI DISPONIBILI PER L'OPERATORE MA IN DIVERSE STRUTTURE: SELEZIONATO RUOLO " + ((OperatorePaleo) exchange.getIn().getBody()).getRuolo());

                    //questa descrizione deve essere aggiunta nel campo note del protocollo. Bisogna distinguere il caso di RUP non trovato perche' non presente da quello di
                    //RUP non identificato a causa di piu' ruoli disponibili
                    piuRuoliRup = "false";
                }
            } else {
                LOG.info("GIUNTA: ATTENZIONE: PIU' RUOLI DISPONIBILI PER L'OPERATORE MA NON E' SPECIFICATO IL CODICE STRUTTURA");

                //in questo caso non si puo' identificare il ruolo perche' manca il codice struttura, ma comunque ne sono stati diversi
                exchange.getIn().setBody(operatorePaleos.get(0));
                piuRuoliRup = "true";
            }

            exchange.getIn().setHeader("piuRuoliRup", piuRuoliRup);
            return;

        }

        exchange.getIn().setHeader("piuRuoliRup", piuRuoliRup);

        LOG.info("GIUNTA: ATTENZIONE: NESSUN OPERATORE TROVATO");
        exchange.getIn().setBody(null);
    }


    private List<OperatorePaleo> filtraOperatoriPaleo(List<OperatorePaleo> listaOperatoriDaFiltrare) {

        List<OperatorePaleo> listaOperatoriFiltrata = new ArrayList<OperatorePaleo>(listaOperatoriDaFiltrare.size());

        for (OperatorePaleo op : listaOperatoriDaFiltrare) {

            LOG.info("GIUNTA: operatore:"+op.getNome().getValue()+" "+op.getCognome()+" ruolo: "+op.getRuolo());

            if (op != null && ruoliOperatoreDaFiltrareSet.contains(op.getRuolo().toUpperCase())) {
                LOG.info("GIUNTA: operatore:"+op.getNome().getValue()+" "+op.getCognome()+" ruolo: "+op.getRuolo()+" RIMOSSO!");
            }else{
                LOG.info("GIUNTA: operatore:"+op.getNome().getValue()+" "+op.getCognome()+" ruolo: "+op.getRuolo()+" AGGIUNTO ALLA LISTA!");
                listaOperatoriFiltrata.add(op);
            }
        }
        return listaOperatoriFiltrata;
    }

}
