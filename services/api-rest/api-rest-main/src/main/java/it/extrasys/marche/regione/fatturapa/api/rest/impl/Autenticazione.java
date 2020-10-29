package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.UtentiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class Autenticazione {

    private static final Logger LOG = LoggerFactory.getLogger(Autenticazione.class);

    public static String checkToken(String token, UtentiManager utentiManager, String tokenTime) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAEnteNonTrovatoException {

        LOG.info("********** checkToken **********");

        byte[] byteArray = Base64.getDecoder().decode(token);
        String tokenJson = new String(byteArray);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> oggetto = objectMapper.readValue(tokenJson, new TypeReference<Map<String,Object>>(){});
            String username = (String) oggetto.get("username");
            String creationTimeString = (String) oggetto.get("timestamp");
            long creationTime = Long.parseLong(creationTimeString);

            long now = new Date().getTime();

            UtenteEntity utenteEntity = utentiManager.getUtenteByUsername(username);

            if(!token.equals(utenteEntity.getTokenAuth())){
                throw new FatturaPATokenNonValidoException("Token non coincidente");
            }

            if(!utenteEntity.getRuolo().equalsIgnoreCase("superadmin")) {

                long tokenTime2 = Long.valueOf(tokenTime);

                if (now - creationTime >= tokenTime2) {
                    throw new FatturaPATokenNonValidoException("Token scaduto");
                } else {
                    return username;
                }
            }
            else {
                return username;
            }

        } catch (JsonParseException e) {
            throw new FatturaPAException("Error parsing JSON");
        } catch (JsonMappingException e) {
            throw new FatturaPAException("JsonMappingException");
        } catch (IOException e) {
            throw new FatturaPAException("IOException");
        }
    }

}
