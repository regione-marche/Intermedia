package it.extrasys.marche.regione.fatturapa.patch.processor;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
}