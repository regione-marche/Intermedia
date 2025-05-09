package it.extrasys.marche.regione.fatturapa.elaborazione.validazione;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntiOspedalieriValidazioneWhitelistEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EntiOspedalieriValidazioneWhitelistManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agosteeno on 25/11/15.
 */
public class EntiOspedalieriValidationWhitelistCheckProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EntiOspedalieriValidationWhitelistCheckProcessor.class);

    private static final String TIPO_DOCUMENTO_HEADER = "tipoDocumento";
    private static final String ID_FISCALE_CEDENTE_HEADER = "cedenteCodiceIva";
    private static final String IS_ENTE_MITTENTE_WHITELIST = "isEnteMittenteWhitelist";

    private EntiOspedalieriValidazioneWhitelistManager entiOspedalieriValidazioneWhitelistManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String tipoDocumento = (String) message.getHeader(TIPO_DOCUMENTO_HEADER);

        String idFiscaleCedente = (String) message.getHeader(ID_FISCALE_CEDENTE_HEADER);

        /*
        EntiOspedalieriValidazioneWhitelistEntity entiOspedalieriValidazioneWhitelistEntity = entiOspedalieriValidazioneWhitelistManager.getEnteByIdFiscaleCedente(idFiscaleCedente);

        if (entiOspedalieriValidazioneWhitelistEntity != null){
            message.setHeader(IS_ENTE_MITTENTE_WHITELIST, "true");
        } else {
            //ente non presente nella tabella whitelist, dunque si deve applicare la validazione
        }
        */

        EntiOspedalieriValidazioneWhitelistEntity entiOspedalieriValidazioneWhitelistEntity = null;

        if(tipoDocumento != null && "TD01".equals(tipoDocumento)){
            entiOspedalieriValidazioneWhitelistEntity = entiOspedalieriValidazioneWhitelistManager.getEnteByIdFiscaleCedente(idFiscaleCedente);
        }

        if (entiOspedalieriValidazioneWhitelistEntity != null){
            message.setHeader(IS_ENTE_MITTENTE_WHITELIST, "true");
        } else {
            //ente non presente nella tabella whitelist, dunque si deve applicare la validazione
        }
    }

    public EntiOspedalieriValidazioneWhitelistManager getEntiOspedalieriValidazioneWhitelistManager() {
        return entiOspedalieriValidazioneWhitelistManager;
    }

    public void setEntiOspedalieriValidazioneWhitelistManager(EntiOspedalieriValidazioneWhitelistManager entiOspedalieriValidazioneWhitelistManager) {
        this.entiOspedalieriValidazioneWhitelistManager = entiOspedalieriValidazioneWhitelistManager;
    }
}
