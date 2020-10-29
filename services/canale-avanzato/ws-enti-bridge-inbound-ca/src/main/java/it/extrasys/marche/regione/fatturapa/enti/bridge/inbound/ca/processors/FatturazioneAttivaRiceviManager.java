package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FormatoTrasmissioneType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.FileCAType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeFattura;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.TipoCanaleEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.ChiaveManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_BASIC;

public class FatturazioneAttivaRiceviManager {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazioneAttivaRiceviManager.class);

    private static final String ID_FISCALE_COMMITTENTE_HEADER = "idFiscaleCommittente";
    private static final String CODICE_UFFICIO_HEADER = "codiceUfficio";
    private static final String ID_ENTE_MITTENTE_HEADER = "idEnteMittente";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String ID_FATTURA_ATTIVA_HEADER = "idFatturaAttiva";
    private static final String RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE = "ricevutaComunicazione";

    private final String REQ_CAMPI_EX_HEADER = "reqCampiEX";
    private final String REQ_NOME_FILE_EX_HEADER = "reqNomeFileEX";
    private final String REQ_FILE_EX_HEADER = "reqFileEX";
    private final String ENTE_EX_HEADER = "enteEX";
    private final String ENTE_CREDENZIALE_EX_HEADER = "enteCredenzialiEX";

    private FatturaAttivaManagerImpl fatturaAttivaFromEntiManager;
    private EnteManager enteManager;
    private ChiaveManager chiaveManager;

    public void salvaFattura(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        msg.setHeader(REQ_CAMPI_EX_HEADER, false);
        msg.setHeader(REQ_NOME_FILE_EX_HEADER, false);
        msg.setHeader(REQ_FILE_EX_HEADER, false);
        msg.setHeader(ENTE_EX_HEADER, false);
        msg.setHeader(ENTE_CREDENZIALE_EX_HEADER, false);

        MessageContentsList messageContentsList = (MessageContentsList) msg.getBody();

        if(messageContentsList != null && messageContentsList.size() > 0 ){

            FileCAType fileType = (FileCAType) messageContentsList.get(0);

            String idFiscaleCommittente = fileType.getIdFiscaleMittente();
            String codiceUfficioMittente = fileType.getCodiceUfficio();
            String nomeFile = fileType.getNomeFile();
            DataHandler dataHandler = fileType.getFile();

            LOG.info("ENTI-BRIDGE-INBOUND-CA: RICEZIONE FATTURA ATTIVA WS salvaFattura: nomeFile " + nomeFile + "; file "
                    + dataHandler.toString() + ";  idFiscaleCommittente " + idFiscaleCommittente + "; codiceUfficio " + codiceUfficioMittente);

            if(idFiscaleCommittente == null || "".equals(idFiscaleCommittente) ||
                    codiceUfficioMittente == null || "".equals(codiceUfficioMittente) ||
                    nomeFile == null || "".equals(nomeFile)) {

                LOG.error("ENTI-BRIDGE-INBOUND-CA: RICEZIONE FATTURA ATTIVA WS salvaFattura: campi obbligatori non compilati per intero");
                msg.setHeader(REQ_CAMPI_EX_HEADER, true);
                throw new FatturaPACampiObbligatoriNonValorizzatiException();
            }

            //Verifico nome file nella req
            if(!ValidatoreNomeFattura.validate(nomeFile)){
                msg.setHeader(REQ_NOME_FILE_EX_HEADER, true);
                throw new FatturaPANomeFileErratoException();
            }

            //Verifico presenza allegato
            if(dataHandler == null || dataHandler.getContent() == null){
                msg.setHeader(REQ_FILE_EX_HEADER, true);
                throw new FatturaPAAllegatoAttivaNonTrovatoException();
            }
            ByteArrayInputStream bais = (ByteArrayInputStream) dataHandler.getContent();
            if(bais.available() == 0){
                msg.setHeader(REQ_FILE_EX_HEADER, true);
                throw new FatturaPAAllegatoAttivaNonTrovatoException();
            }

            EnteEntity enteEntity = null;

            try{
                enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficioMittente);
            }catch (Exception e){
                //Ente non trovato
            }

            //Inizio verifica ente
            if(enteEntity == null) {
                msg.setHeader(ENTE_EX_HEADER, true);
                throw new FatturaPAEnteNonTrovatoException();
            }

            if(!enteEntity.getTipoCanale().getCodStato().equals(TipoCanaleEntity.TIPO_CANALE.CA)) {
                msg.setHeader(ENTE_EX_HEADER, true);
                throw new FatturaPAEnteNonTrovatoException();
            }

            //Controllo se è un ente di TEST
            Boolean isTest=Boolean.FALSE;
            if("STAGING".equalsIgnoreCase(enteEntity.getAmbienteCicloAttivo())) {
                isTest=Boolean.TRUE;
            }
            exchange.getIn().setHeader("fatturazioneTest", isTest);

            String authorizationHeader = msg.getHeader(AUTHORIZATION, String.class);

            if(authorizationHeader == null || "".equals(authorizationHeader)) {
                msg.setHeader(ENTE_CREDENZIALE_EX_HEADER, true);
                throw new FatturaPACredenzialiNonValideException();
            }

            if(!checkEnte(enteEntity, authorizationHeader)) {
                msg.setHeader(ENTE_CREDENZIALE_EX_HEADER, true);
                throw new FatturaPACredenzialiNonValideException();
            }
            //Fine verifica ente

            msg.setHeader(ID_FISCALE_COMMITTENTE_HEADER, idFiscaleCommittente);
            msg.setHeader(CODICE_UFFICIO_HEADER, codiceUfficioMittente);
            msg.setHeader(ID_ENTE_MITTENTE_HEADER, enteEntity.getIdEnte());
            msg.setHeader(NOME_FILE_HEADER, nomeFile);

            final InputStream in = dataHandler.getInputStream();
            byte[] fileFattureOriginale = IOUtils.toByteArray(in);

            String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFile, fileFattureOriginale);

            //TODO REVO-3
            FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);
            String formatoTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getFormatoTrasmissione().value();
            String codiceUfficioDestinatario = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario();
            String pecDestinatario = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getPECDestinatario();

            LOG.info("ENTI-BRIDGE-INBOUND-CA: RICEZIONE FATTURA ATTIVA WS salvaFattura: nomeFile " + nomeFile + "; file " + fileFattureOriginale +" Fattura size=" + fileFattureOriginale.length +
            "; formatoTrasmissione = " + formatoTrasmissione + "; codiceUfficioDestinatario " + codiceUfficioDestinatario + "; pecDestinatario [" + pecDestinatario + "]");

            if(FormatoTrasmissioneType.FPA_12.equals(FormatoTrasmissioneType.fromValue(formatoTrasmissione))){
                //caso fatturazione verso PA, verificare che il codice ufficio sia di 6 caratteri e procedere come fin'ora

                //check dimensione codiceUfficio da intestazione. Sappiamo essere uguale a quello interno, se no sarebbe schiantato prima
                if(codiceUfficioDestinatario.length() != 6){
                    //codice ufficio deve essere lungo 6 caratteri
                    LOG.error("ENTI-BRIDGE-INBOUND-CA: RICEZIONE FATTURA ATTIVA WS salvaFattura: caso FPA_12,  codiceUfficioDestinatario NON è lungo 6 caratteri: [" + codiceUfficioDestinatario + "]");
                    throw new FatturaPAValidazioneFallitaException();
                }

                //non ho altri controlli fa effettuare

            } else {
                //caso FPR12, cioé fatturazione verso privati: fare i nuovi controlli previsti, cioè la dimensione del codice ufficio e
                // la verifica degli altri campi prima del salvataggio sul database

                if(codiceUfficioDestinatario.length() != 7){
                    //codice ufficio deve essere lungo 7 caratteri
                    LOG.error("ENTI-BRIDGE-INBOUND-CA: RICEZIONE FATTURA ATTIVA WS salvaFattura: caso FPR_12,  codiceUfficioDestinatario in intestazione NON è lungo 7 caratteri: [" + codiceUfficioDestinatario + "]");
                    throw new FatturaPAValidazioneFallitaException();
                }
            }

            /**
             * NOTA in questo punto si deve aggiungere un controllo sulla dimensione del file, che non superi una dimensione particolare
             */

            String ricevutaComunicazione = CommonUtils.createRicevutaComunicazione();
            msg.setHeader(RICEVUTA_COMUNICAZIONE_ESITO_OPERAZIONE, ricevutaComunicazione);

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaFromEntiManager.salvaFatturaAttiva(fileFattureOriginale, nomeFile, enteEntity, formatoTrasmissione, codiceUfficioDestinatario, pecDestinatario, ricevutaComunicazione, isTest);

            String idFatturaAttiva = fatturaAttivaEntity.getIdFatturaAttiva().toString();
            msg.setHeader(ID_FATTURA_ATTIVA_HEADER, idFatturaAttiva);

            String messaggioEncoded = new String(Base64.encodeBase64(fileFattureOriginale));
            msg.setBody(messaggioEncoded);

        } else {
            throw new FatturaPAException();
        }
    }

    public void preparaFatturaPerValidazione(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        byte[] fatturaElettronicaBytesArray = Base64.decodeBase64(msg.getBody(String.class));

        String nomeFile = msg.getHeader(NOME_FILE_HEADER, String.class);

        String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFile, fatturaElettronicaBytesArray);

        FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);

        String fileFatturaString = JaxBUtils.getFatturaElettronicaAsString(fatturaElettronicaType);

        msg.setBody(fileFatturaString);

    }

    private boolean checkEnte(EnteEntity enteEntity, String authorizationHeader){

        boolean checkEnte = false;

        try{

            String chiaveDiDecodifica = chiaveManager.getChiave();
            String pswdEnte = CommonUtils.decryptPassword(enteEntity.getEndpointFattureAttivaCa().getPassword(), chiaveDiDecodifica);

            String authorization = AUTH_TYPE_BASIC + " " + org.apache.cxf.common.util.Base64Utility.encode((enteEntity.getEndpointFattureAttivaCa().getUsername() + ":" + pswdEnte).getBytes());

            if(authorization.equals(authorizationHeader)) {
                checkEnte = true;
            }

        }catch (Exception e){
            checkEnte = false;
        }

        return checkEnte;
    }

    public FatturaAttivaManagerImpl getFatturaAttivaFromEntiManager() {
        return fatturaAttivaFromEntiManager;
    }

    public void setFatturaAttivaFromEntiManager(FatturaAttivaManagerImpl fatturaAttivaFromEntiManager) {
        this.fatturaAttivaFromEntiManager = fatturaAttivaFromEntiManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public ChiaveManager getChiaveManager() {
        return chiaveManager;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }
}