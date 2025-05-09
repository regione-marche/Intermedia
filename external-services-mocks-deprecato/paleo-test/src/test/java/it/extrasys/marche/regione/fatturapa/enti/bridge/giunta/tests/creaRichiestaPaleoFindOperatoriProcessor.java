package it.extrasys.marche.regione.fatturapa.enti.bridge.giunta.tests;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 25/02/15.
 */
public class creaRichiestaPaleoFindOperatoriProcessor implements Processor {


    @Override
    public void process(Exchange exchange) throws Exception {
        String cognome = "";
        String codiceFiscale = "AAALGU80P18A944X";

        List<Object> params = new ArrayList<Object>();
        params.add(cognome);
        params.add(codiceFiscale);

        exchange.getIn().setHeader("SOAPAction", "http://paleo.regione.marche.it/services/IPaleoService/FindOperatori");
        
        /*FindOperatori findOperatori = new FindOperatori();
        JAXBElement<String> codiceFiscale = new ObjectFactory().createFindOperatoriCodiceFiscale("DMSLGU80P18A944X");
        findOperatori.setCodiceFiscale(codiceFiscale);
        JAXBElement<String> cognome = new ObjectFactory().createFindOperatoriCognome("");
        findOperatori.setCognome(cognome); */
        exchange.getIn().setBody(params);
    }
}
