package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.api.rest.impl.Autenticazione;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPATokenNonValidoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonAutorizzatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.UtentiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutenticazioneProcess implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(AutenticazioneProcess.class);
    private UtentiManager utentiManager;
    private String durataToken;

    @Override
    public void process(Exchange exchange) throws Exception {
        String token = (String) exchange.getIn().getHeader("token");
        String username = "";
        try {
            username = Autenticazione.checkToken(token, utentiManager, durataToken);

        } catch (FatturaPAEnteNonTrovatoException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
        //username=indirizzo email
        exchange.getIn().setHeader("username", username);
    }

    public UtentiManager getUtentiManager() {
        return utentiManager;
    }

    public void setUtentiManager(UtentiManager utentiManager) {
        this.utentiManager = utentiManager;
    }

    public String getDurataToken() {
        return durataToken;
    }

    public void setDurataToken(String durataToken) {
        this.durataToken = durataToken;
    }
}
