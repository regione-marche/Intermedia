package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agosteeno on 02/03/15.
 */
public class NotificaScartoProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(NotificaScartoProcessor.class);

    private final static String ID_COMUNICAZIONE = "idComunicazione";
    private static final String ORIGINAL_MESSAGE_FROM_SDI = "originalMessageFromSdi";
    private static final String COMMITTENTE_CODICE_IVA = "committenteCodiceIva";
    private static final String CODICE_UFFICIO = "codiceUfficio";
    private static final String NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER = "nomeFileScartoEsito";
    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String CONTENUTO_FILE_SCARTO = "contenutoFileScarto";

    private NotificaFromSdiManager notificaFromSdiManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String identificativoSdI = msg.getHeader(IDENTIFICATIVO_SDI_HEADER, String.class);

        String nomeFile = (String) msg.getHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER);

        String idComunicazione = (String) msg.getHeader(ID_COMUNICAZIONE);

        String originalMessage = (String) msg.getHeader(ORIGINAL_MESSAGE_FROM_SDI);

        String contenutoFileScarto = (String) msg.getHeader(CONTENUTO_FILE_SCARTO);

        //NotificaFromSdiEntity notificaFromSdiEntity = notificaFromSdiManager.salvaNotificaScarto(idComunicazione, nomeFile, originalMessage);
        NotificaFromSdiEntity notificaFromSdiEntity = notificaFromSdiManager.salvaNotificaFromSdi(idComunicazione, false, nomeFile, originalMessage, identificativoSdI,contenutoFileScarto);

        //gli header restano uguali a parte il nome file, che adesso deve essere aggiornato con quello della notifica di scarto
        msg.setHeader("nomeFile", nomeFile);

        byte[] notificaScartoByteArray = Base64.decode(exchange.getIn().getBody(String.class));

        //Tutti i messaggi prodotti ed inviati dal Sistema di Interscambio, a
        //eccezione del file dei metadati, vengono firmati elettronicamente mediante una firma elettronica di tipo XAdES-Bes.
        // Tolgo la firma XAdES-Bes dal file di notifica Decorrenza Termini
        String notificaScarto = XAdESUnwrapper.unwrap(notificaScartoByteArray);

        exchange.getIn().setHeader("dataRicezioneSdI", notificaFromSdiEntity.getDataRicezioneRispostaSDI());
        exchange.getIn().setBody(notificaScarto);
    }

    public NotificaFromSdiManager getNotificaFromSdiManager() {
        return notificaFromSdiManager;
    }

    public void setNotificaFromSdiManager(NotificaFromSdiManager notificaFromSdiManager) {
        this.notificaFromSdiManager = notificaFromSdiManager;
    }
}