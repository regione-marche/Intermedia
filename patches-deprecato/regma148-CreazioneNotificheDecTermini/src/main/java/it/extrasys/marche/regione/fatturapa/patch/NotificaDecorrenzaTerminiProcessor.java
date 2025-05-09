package it.extrasys.marche.regione.fatturapa.patch;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by gianfranco on 04/11/2016.
 */
public class NotificaDecorrenzaTerminiProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(NotificaDecorrenzaTerminiProcessor.class);

    private FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager;

    @Override
    public void process(Exchange exchange){

        String esito = "ok";
        String errore = "";
        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = null;

        try {

            String decTermini = exchange.getIn().getBody(String.class);

            byte[] notificaDecorrenzaBytesArray = decTermini.getBytes();

            String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);

            BigInteger identificativoSDI = new BigInteger(exchange.getIn().getHeader("identificativoSdi", String.class));

            notificaDecorrenzaTerminiEntity = fatturazionePassivaNotificaDecorrenzaTerminiManager
                    .salvaNotificaDecorrenzaTermini(nomeFile, identificativoSDI, notificaDecorrenzaBytesArray);

        }catch (Exception e){
            log.error("Errore scrittura DB. Msg = " + e.getMessage());
            e.printStackTrace();
            esito = "ko";
            errore = e.getMessage();

        }finally {
            exchange.getIn().setHeader("esito", esito);
            exchange.getIn().setHeader("errore", errore);

            if("ok".equals(esito) && notificaDecorrenzaTerminiEntity != null){
                exchange.getIn().setHeader("notificaDecorrenzaTerminiEntity", notificaDecorrenzaTerminiEntity.toString());
                log.debug("***** Notifica Dec Creata = " + notificaDecorrenzaTerminiEntity.toString() + " *****");
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("identificativoSdi", exchange.getIn().getHeader("identificativoSdi", String.class));
            body.put("nomeFile", exchange.getIn().getHeader("nomeFile", String.class));
            body.put("esito",  exchange.getIn().getHeader("esito", String.class));
            body.put("errore", exchange.getIn().getHeader("errore", String.class));

            exchange.getIn().setBody(body);
        }
    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getFatturazionePassivaNotificaDecorrenzaTerminiManager() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManager = fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }
}