package it.extrasys.marche.regione.fatturapa.patch.processor;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaDecorrenzaTerminiType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.RicevutaConsegnaType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.NotificheAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificheAttivaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FlussoSemplificatoProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FlussoSemplificatoProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String TIPO_NOTIFICA_HEADER = "tipoNotifica";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String NOME_FILE_FATTURA_ATTIVA_HEADER = "nomeFileFatturaAttiva";
    private static final String NOME_FILE_FATTURA_HEADER = "nomeFileFattura";
    private static final String NOME_FILE_METADATI_HEADER = "nomeFileMetadati";
    private static final String CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER = "codiceEnteDestinatarioFlussoSemplificato";
    private static final String MESSAGE_ID_FLUSSO_SEMPLIFICATO_HEADER = "messageIdFlussoSemplificato";

    private FatturaAttivaManagerImpl fatturaAttivaManager;
    private DatiFatturaManager datiFatturaManager;
    private NotificheAttivaFromSdiManager notificheAttivaFromSdiManager;

    public void imporstaFlagFatturazioneInterna(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        fatturaAttivaManager.impostaFlagFatturazioneInterna(new BigInteger(identificativoSdI), true);
    }

    public void prelevaFatturaAttiva(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoNotifica = (String) message.getHeader(TIPO_NOTIFICA_HEADER);

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI));

        String fatturaOriginale = new String(Base64.encodeBase64(fatturaAttivaEntity.getFileFatturaOriginale()));

        LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, fattura originale [" + fatturaOriginale + "]");

        message.setBody(fatturaOriginale);

        settaHeaderDaNotificaAttiva(identificativoSdI, tipoNotifica, message);

        //Questo Header mi serve per creare il file dei metadati
        message.setHeader(NOME_FILE_FATTURA_ATTIVA_HEADER, fatturaAttivaEntity.getNomeFile());
    }

    public void creaNomeFileMetadati(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String nomeFile = (String) message.getHeader(NOME_FILE_HEADER);

        String nomeFileFattura = (String) message.getHeader(NOME_FILE_FATTURA_ATTIVA_HEADER);

        String nomeFileMetadati = FileUtils.getNomeMetadatiFromNomeFattura(nomeFileFattura);

        LOG.info("FlussoSemplificatoProcessor - creaNomeFileMetadati: nome file fattura " + nomeFileFattura + ", nome file metadati " + nomeFileMetadati);

        message.setHeader(NOME_FILE_FATTURA_HEADER, nomeFileFattura);
        message.setHeader(NOME_FILE_METADATI_HEADER, nomeFileMetadati);
    }

    private NotificheAttivaFromSdiEntity getNotificaAttivaFromSdi(String identificativoSdI, String tipoNotifica) throws FatturaPAException, FatturaPaPersistenceException {

        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = null;

        if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue().equals(tipoNotifica)) {
            notificheAttivaFromSdiEntity = notificheAttivaFromSdiManager.getNotificaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI), TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA);
            return notificheAttivaFromSdiEntity;
        } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue().equals(tipoNotifica)){
            notificheAttivaFromSdiEntity = notificheAttivaFromSdiManager.getNotificaAttivaFromIdentificativSdi(new BigInteger(identificativoSdI), TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI);
            return notificheAttivaFromSdiEntity;
        }

        throw new FatturaPAException("Errore Tipo Notifica per Flusso Semplificato!\nValore inserito [" + tipoNotifica + "]\nValori ammessi ['001' per Ricevuta Consegna] - ['005' per Decorrenza Termini]");
    }

    private void settaHeaderDaNotificaAttiva(String identificativoSdI, String tipoNotifica, Message message) throws FatturaPAException, FatturaPaPersistenceException, IOException, JAXBException {

        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = getNotificaAttivaFromSdi(identificativoSdI, tipoNotifica);

        switch (notificheAttivaFromSdiEntity.getTipoNotificaAttivaFromSdiEntity().getCodTipoNotificaFromSdi()) {

            case RICEVUTA_CONSEGNA:

                RicevutaConsegnaType ricevutaConsegnaType = JaxBUtils.getRicevutaConsegna(notificheAttivaFromSdiEntity.getOriginalMessage());
                message.setHeader(CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER, ricevutaConsegnaType.getDestinatario().getCodice());
                message.setHeader(MESSAGE_ID_FLUSSO_SEMPLIFICATO_HEADER, ricevutaConsegnaType.getMessageId());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String dataRicezioneSdI = sdf.format(new Date());
                message.setHeader("dataRicezioneSdI", dataRicezioneSdI);

                LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, Tipo Notifica [" + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.getValue() + "]");
                LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, codice destinatario [" + ricevutaConsegnaType.getDestinatario().getCodice() + "]");
                LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, message id [" + ricevutaConsegnaType.getMessageId() + "]");

                break;

            case NOTIFICA_DECORRENZA_TERMINI:

                NotificaDecorrenzaTerminiType decorrenzaTerminiType = JaxBUtils.getDecorrenzaTermini(notificheAttivaFromSdiEntity.getOriginalMessage());
                message.setHeader(CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER, getCodiceEnteDestinatario(new BigInteger(identificativoSdI)));
                message.setHeader(MESSAGE_ID_FLUSSO_SEMPLIFICATO_HEADER, decorrenzaTerminiType.getMessageId());
                LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, Tipo Notifica [" + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.getValue() + "]");
                LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, codice destinatario [" + message.getHeader(CODICE_ENTE_DESTINATARIO_FLUSSO_SEMPLIFICATO_HEADER, String.class) + "]");
                LOG.info("****************************************** FlussoSemplificatoProcessor - prelevaFatturaAttiva, message id [" + decorrenzaTerminiType.getMessageId() + "]");

                break;
        }
    }

    private String getCodiceEnteDestinatario(BigInteger idSdI) throws FatturaPAException, FatturaPaPersistenceException {

        List<DatiFatturaEntity> lista = datiFatturaManager.getFatturaByIdentificativoSDI(idSdI);

        if(lista != null && !lista.isEmpty()){
            DatiFatturaEntity datiFatturaEntity = lista.get(0);
            String codiceEnteDestinatario = datiFatturaEntity.getCodiceDestinatario();
            return codiceEnteDestinatario;
        }

        LOG.error("FlussoSemplificatoProcessor - prelevaFatturaAttiva: CODICE ENTE DESTINATARIO NON TROVATO!!!");
        throw new FatturaPAException("FlussoSemplificatoProcessor - prelevaFatturaAttiva: CODICE ENTE DESTINATARIO NON TROVATO!!!");
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }

    public NotificheAttivaFromSdiManager getNotificheAttivaFromSdiManager() {
        return notificheAttivaFromSdiManager;
    }

    public void setNotificheAttivaFromSdiManager(NotificheAttivaFromSdiManager notificheAttivaFromSdiManager) {
        this.notificheAttivaFromSdiManager = notificheAttivaFromSdiManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}