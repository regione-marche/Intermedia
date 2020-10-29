package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.processors;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FormatoTrasmissioneType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.beans.FileType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAValidazioneFallitaException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
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
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Created by agosteeno on 18/03/15.
 */
public class FatturazioneAttivaRiceviManager {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazioneAttivaRiceviManager.class);

    private static final String ID_FISCALE_COMMITTENTE_HEADER = "idFiscaleCommittente";
    private static final String CODICE_UFFICIO_HEADER = "codiceUfficio";
    private static final String ID_ENTE_MITTENTE_HEADER = "idEnteMittente";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String ID_COMUNICAZIONE_HEADER = "idComunicazione";
    private static final String ID_FATTURA_ATTIVA_HEADER = "idFatturaAttiva";
    private static final String NAMESPACE_FATTURA_ELETTRONICA = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2";
    //identifica il caso in cui un destinatario "privato" non abbia un canale accreditato presso SDI
    private static final String CODICE_UFFICIO_PRIVATI_TIPO_PEC = "0000000";

    private FatturaAttivaManagerImpl fatturaAttivaFromEntiManager;
    private EnteManager enteManager;

    public void salvaFattura(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        MessageContentsList messageContentsList = (MessageContentsList) msg.getBody();

        if(messageContentsList != null && messageContentsList.size() > 0 ){

            FileType fileType = (FileType) messageContentsList.get(0);

            String idFiscaleCommittente = fileType.getIdFiscaleMittente();
            String codiceUfficioMittente = fileType.getCodiceUfficio();
            String nomeFile = fileType.getNomeFile();
            BigInteger idComunicazione = fileType.getIdComunicazione();
            DataHandler dataHandler = fileType.getFile();

            LOG.info("ENTI-BRIDGE-INBOUND: RICEZIONE FATTURA ATTIVA WS salvaFattura: nomeFile " + nomeFile + "; file " + dataHandler.toString() + ";  idFiscaleCommittente " + idFiscaleCommittente
                    + "; codiceUfficio " + codiceUfficioMittente + "; idComunicazione " + idComunicazione);


            if(idFiscaleCommittente == null || "".equals(idFiscaleCommittente) || codiceUfficioMittente == null || "".equals(codiceUfficioMittente)
                    || nomeFile == null || "".equals(nomeFile) || idComunicazione == null || dataHandler == null || dataHandler.getContent() == null){
                LOG.error("ENTI-BRIDGE-INBOUND: RICEZIONE FATTURA ATTIVA WS salvaFattura: campi obbligatori non compilati per intero");
                throw new FatturaPAException();
            }


            //questa era la vecchia ricerca per idFiscaleCommittente e codiceUffico, sostituita dalla ricerca per solo codiceUfficio
            //EnteEntity enteEntity = enteManager.getEnteByIdFiscaleCommittenteAndCodiceUfficio(idFiscaleCommittente, codiceUfficio);

            EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficioMittente);

            //Controllo se è un ente di TEST
            Boolean isTest=Boolean.FALSE;
            if("STAGING".equalsIgnoreCase(enteEntity.getAmbienteCicloAttivo())) {
                isTest=Boolean.TRUE;
            }
            exchange.getIn().setHeader("fatturazioneTest", isTest);

            msg.setHeader(ID_FISCALE_COMMITTENTE_HEADER, idFiscaleCommittente);
            msg.setHeader(CODICE_UFFICIO_HEADER, codiceUfficioMittente);
            msg.setHeader(ID_ENTE_MITTENTE_HEADER, enteEntity.getIdEnte());
            msg.setHeader(NOME_FILE_HEADER, nomeFile);
            msg.setHeader(ID_COMUNICAZIONE_HEADER, nomeFile);

            final InputStream in = dataHandler.getInputStream();
            byte[] fileFattureOriginale = IOUtils.toByteArray(in);

            String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFile, fileFattureOriginale);

            //TODO REVO-3
            FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);
            String formatoTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getFormatoTrasmissione().value();
            String codiceUfficioDestinatario = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario();
            String pecDestinatario = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getPECDestinatario();


            LOG.info("ENTI-BRIDGE-INBOUND: RICEZIONE FATTURA ATTIVA WS salvaFattura: nomeFile " + nomeFile + "; file " + fileFattureOriginale +" Fattura size=" + fileFattureOriginale.length +
            "; formatoTrasmissione = " + formatoTrasmissione + "; codiceUfficioDestinatario " + codiceUfficioDestinatario + "; pecDestinatario [" + pecDestinatario + "]");


            if(FormatoTrasmissioneType.FPA_12.equals(FormatoTrasmissioneType.fromValue(formatoTrasmissione))){
                //caso fatturazione verso PA, verificare che il codice ufficio sia di 6 caratteri e procedere come fin'ora

                //check dimensione codiceUfficio da intestazione. Sappiamo essere uguale a quello interno, se no sarebbe schiantato prima
                if(codiceUfficioDestinatario.length() != 6){
                    //codice ufficio deve essere lungo 6 caratteri
                    LOG.error("ENTI-BRIDGE-INBOUND: RICEZIONE FATTURA ATTIVA WS salvaFattura: caso FPA_12,  codiceUfficioDestinatario NON è lungo 6 caratteri: [" + codiceUfficioDestinatario + "]");
                    throw new FatturaPAValidazioneFallitaException();
                }

                //non ho altri controlli fa effettuare

            } else {
                //caso FPR12, cioé fatturazione verso privati: fare i nuovi controlli previsti, cioè la dimensione del codice ufficio e
                // la verifica degli altri campi prima del salvataggio sul database

                if(codiceUfficioDestinatario.length() != 7){
                    //codice ufficio deve essere lungo 7 caratteri
                    LOG.error("ENTI-BRIDGE-INBOUND: RICEZIONE FATTURA ATTIVA WS salvaFattura: caso FPR_12,  codiceUfficioDestinatario in intestazione NON è lungo 7 caratteri: [" + codiceUfficioDestinatario + "]");
                    throw new FatturaPAValidazioneFallitaException();
                }

                //Anche nel caso di privato non bisogna controllare la pec
                /*
                //check sul fatto che sia PEC o meno in base al valore
                if(CODICE_UFFICIO_PRIVATI_TIPO_PEC.equals(codiceUfficioDestinatario)){
                    //verifico che sia valorizzato il campo PEC Destinatario
                    if(pecDestinatario == null | "".equals(pecDestinatario)){
                        //in questo caso il campo deve essere valorizzato
                        LOG.error("ENTI-BRIDGE-INBOUND: RICEZIONE FATTURA ATTIVA WS salvaFattura: caso FPR_12,  campo PEC destinatario non valorizzatos");
                        throw new FatturaPAValidazioneFallitaException();
                    }
                }
                */
            }

            /**
             * NOTA in questo punto si deve aggiungere un controllo sulla dimensione del file, che non superi una dimensione particolare
             */

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaFromEntiManager.salvaFatturaAttiva(fileFattureOriginale, nomeFile, enteEntity, formatoTrasmissione, codiceUfficioDestinatario, pecDestinatario, null, isTest);

            String idFatturaAttiva = fatturaAttivaEntity.getIdFatturaAttiva().toString();
            msg.setHeader(ID_FATTURA_ATTIVA_HEADER, idFatturaAttiva);

            msg.setBody(fileFattureOriginale);

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

}
