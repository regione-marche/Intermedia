package it.extrasys.marche.regione.fatturapa.api.rest.utils;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utility {
    private static final Logger LOG = LoggerFactory.getLogger(Utility.class);

    /*
    Elimina identificativiSdi doppi
     */
    public void eliminaSDIdoppi(Exchange exchange) {
        List<String> listaSDI = (List<String>) exchange.getIn().getBody();

        if (listaSDI != null && !listaSDI.isEmpty()) {

            //Cancello i duplicati
            Set<String> mySet = new HashSet<String>(listaSDI);
            listaSDI.clear();
            listaSDI.addAll(mySet);
            exchange.getIn().setBody(listaSDI);
        } else {
            LOG.debug("Fine Pulizia Lista Identificativi SDI: Nessuna pulizia effettuata in quanto lista vuota o nulla");
            return;
        }

    }
}
