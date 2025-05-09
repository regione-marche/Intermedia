package it.extrasys.marche.regione.fatturapa.api.rest.utils;

import it.extrasys.marche.regione.fatturapa.api.rest.model.ReinviaFatturaRequest;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by gianfranco on 07/02/2017.
 */
public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public void controllaListaSDI(Exchange exchange){

        String listaSDI = (String) exchange.getIn().getHeader("listaIdentificativiSdI");
        listaSDI = listaSDI.replaceAll(" ","");

        String[] splittata = listaSDI.split(",");

        LOG.debug("Regma 122 - Start Pulizia Lista Identificativi SDI: Size [" + splittata.length + "]  Elementi [" + listaSDI + "]");

        String listaPulita = "";
        int sizeSet = 0;

        if(listaSDI != null && !"".equals(listaSDI)){

            //Cancello i duplicati
            Set<String> mySet = new HashSet<String>(Arrays.asList(splittata));
            sizeSet = mySet.size();

            listaPulita = mySet.toString().replaceAll("\\[","");
            listaPulita = listaPulita.replaceAll("\\]","");
            listaPulita = listaPulita.replaceAll(" ","");

            exchange.getIn().setHeader("listaIdentificativiSdI", listaPulita);
        }else{
            LOG.debug("Regma 122 - Fine Pulizia Lista Identificativi SDI: Nessuna pulizia effettuata in quanto lista vuota o nulla");
            return;
        }

        LOG.debug("Regma 122 - Fine Pulizia Lista Identificativi SDI: Size [" + sizeSet + "]  Elementi [" + listaPulita + "]");
    }

    public void prepareExchange(Exchange exchange) {

        String idSdi = (String) exchange.getIn().getBody();

        ReinviaFatturaRequest reinviaFatturaRequest = new ReinviaFatturaRequest();

        List<String> idSdiList = new ArrayList<>();
        idSdiList.add(idSdi);

        reinviaFatturaRequest.setIdentificativoSdi(idSdiList);
        reinviaFatturaRequest.setOnlyRegistrazione(false);

        List<Object> body = new ArrayList<>();
        body.add(reinviaFatturaRequest);
        body.add(reinviaFatturaRequest);

        //String body = "{\"identificativo_sdi\":[\"" + idSdi + "\"],\"only_registrazione\":false}";

        exchange.getIn().setBody(body);
    }

}