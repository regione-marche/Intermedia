package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.RispostaSdINotificaEsitoType;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Created by agosteeno on 02/03/15.
 */
public class NotificaOkProcessor implements Processor{

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String ID_COMUNICAZIONE = "idComunicazione";
    private static final String ORIGINAL_MESSAGE_FROM_SDI = "originalMessageFromSdi";

    private static final String NOTIFICA_EC_PROTOCOLLAZIONE = "notificaECProtocollazione";

    private NotificaFromSdiManager notificaFromSdiManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String identificativoSdI = msg.getHeader(IDENTIFICATIVO_SDI_HEADER, String.class);

        if(msg.getHeader(NOTIFICA_EC_PROTOCOLLAZIONE) == null || "".equals(msg.getHeader(NOTIFICA_EC_PROTOCOLLAZIONE))){

            RispostaSdINotificaEsitoType rispostaSdINotificaEsito = (RispostaSdINotificaEsitoType) msg.getBody();

            String idComunicazione = (String) msg.getHeader(ID_COMUNICAZIONE);

            String originalMessage = (String) msg.getHeader(ORIGINAL_MESSAGE_FROM_SDI);

            //notificaFromSdiManager.salvaNotificaOk(idComunicazione, originalMessage);
            notificaFromSdiManager.salvaNotificaFromSdi(idComunicazione, true, null, originalMessage, identificativoSdI, null);

            //non e' necessario fare altro perche' la notifica con esito positivo non deve essere inoltrata all'ente

            //TODO REGMA 112 verificare se il flag flusso semplificato e' settato: in questo caso bisogna creare una notifica di esito da inviare all'ente mittente

        }else{

            //Serve per la protocollazione in caso delle Notifica Esito Committente originale in caso di scarto dallo sdi
            notificaFromSdiManager.salvaNotificaECScartataFromSdi(identificativoSdI);

        }
    }

    public NotificaFromSdiManager getNotificaFromSdiManager() {
        return notificaFromSdiManager;
    }

    public void setNotificaFromSdiManager(NotificaFromSdiManager notificaFromSdiManager) {
        this.notificaFromSdiManager = notificaFromSdiManager;
    }

}
