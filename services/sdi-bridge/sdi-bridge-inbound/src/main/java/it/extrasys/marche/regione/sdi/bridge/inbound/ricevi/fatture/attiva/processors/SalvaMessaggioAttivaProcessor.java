package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.attiva.processors;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificheAttivaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by agosteeno on 21/03/15.
 */
public class SalvaMessaggioAttivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(SalvaMessaggioAttivaProcessor.class);

    private NotificheAttivaFromSdiManager notificheAttivaFromSdiManager;

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdi";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";

    @Override
    public void process(Exchange exchange) throws FatturaPAException {

        Message msg = exchange.getIn();

        //NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = new NotificheAttivaFromSdiEntity();

        String identificativoSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String nomeFile = (String) msg.getHeader(NOME_FILE_HEADER);
        String tipoMessaggio = (String)msg.getHeader(TIPO_MESSAGGIO_HEADER);
        String messaggioDecodificato = (String) msg.getBody();

        //riprendo il dataHandler
        byte[] messaggioOriginaleByteArray = Base64.decode(messaggioDecodificato);

        String messaggioOriginale = new String(messaggioOriginaleByteArray);

        notificheAttivaFromSdiManager.salvaNotificaFromSdi(new BigInteger(identificativoSdi), nomeFile, tipoMessaggio, messaggioOriginale);

    }

    public NotificheAttivaFromSdiManager getNotificheAttivaFromSdiManager() {
        return notificheAttivaFromSdiManager;
    }

    public void setNotificheAttivaFromSdiManager(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager) {
        this.notificheAttivaFromSdiManager = notificheAttivaFromSdiManager;
    }
}
