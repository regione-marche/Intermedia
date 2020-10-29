package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Cedente;
import it.marche.regione.paleo.services.BEListOfRubricaZA0HwLp5;
import it.marche.regione.paleo.services.Rubrica;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/03/15.
 */
public class RispostaFindRubricaExtProcessor implements Processor {


    public void process(Exchange exchange) throws Exception {

        BEListOfRubricaZA0HwLp5 list = (BEListOfRubricaZA0HwLp5) exchange.getIn().getBody(MessageContentsList.class).get(0);
        //list.

        Cedente cedente = new Cedente();


        // Se non trovo niente nella rubrica uso i dati del cedente che sono nella testata della fattura;

        String prolog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

        String cedenteString = exchange.getIn().getHeader("FatturaCedentePrestatore", String.class);

        cedenteString = prolog + cedenteString;

        CedentePrestatoreType cedentePrestatoreType = JaxBUtils.getCedentePrestatoreType(cedenteString.getBytes());

        cedente.setCedentePrestatoreType(cedentePrestatoreType);

        List<Rubrica> arrayOfRubrica = list.getLista().getValue().getRubrica();

        if (arrayOfRubrica.size() > 0) {
            String codiceRubrica = list.getLista().getValue().getRubrica().get(0).getCodice();
            cedente.setCodiceRubrica(codiceRubrica);
        }
        exchange.getIn().setBody(cedente);
    }
}
