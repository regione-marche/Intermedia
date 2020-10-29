package it.extrasys.marche.regione.fatturapa.enti.bridge;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MetadatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 24/02/15.
 */
public class PecManager {

    private static final Logger LOG = LoggerFactory.getLogger(PecManager.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String ID_FISCALE_COMMITTENTE_HEADER = "idFiscaleCommittente";
    private static final String NOME_FILE = "nomeFile";
    private static final String TIPO_MESSAGGIO = "tipoMessaggio";

    private static final String MESSAGGIO_NOTIFICA_DECORRENZA = "NotificaDecorrenzaTermini";
    private static final String MESSAGGIO_FATTURA = "FatturaElettronica";

    private static final String TO_HEADER = "to";
    private static final String NOME_ENTE_HEADER = "nomeEnte";
    private static final String CODICE_UFFICIO = "codiceUfficio";

    private static final String XML = "xml";
    private static final String P7M = "p7m";
    private static final String DATA_RICEZIONE_SDI_SUBJECT = "dataRicezioneSdISubject";

    private EnteManager enteManager;

    private DatiFatturaManager datiFatturaManager;

    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    private MetadatiFatturaManager metadatiFatturaManager;

    public void preparaMessaggio (Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {

        Message msg = msgExchange.getIn();

        String idFiscaleCommittente = (String) msg.getHeader(ID_FISCALE_COMMITTENTE_HEADER);
        String codiceUfficio = (String) msg.getHeader(CODICE_UFFICIO);
        String nomeFile = (String) msg.getHeader(NOME_FILE);

        String identificativoSdi = (String)msg.getHeader(IDENTIFICATIVO_SDI_HEADER);

        //questa era la vecchia ricerca per idFiscaleCommittente e codiceUffico, sostituita dalla ricerca per solo codiceUfficio
        //EnteEntity entePec = enteManager.getEnteByIdFiscaleCommittenteAndCodiceUfficio(idFiscaleCommittente, codiceUfficio);
        EnteEntity entePec = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        //aggiungo la fattura come allegato

        msg.setHeader(TO_HEADER, entePec.getEmailPec());
        msg.setHeader(NOME_ENTE_HEADER, entePec.getNome());

        //DataHandler dh = new DataHandler(msg.getBody(), "text/plain");

        /*
        al posto di prendere il body e farlo diventare un datahandler devo prendere dal db
         */

        FileFatturaEntity fileFatturaEntity = fatturazionePassivaFatturaManager.getFileFatturaEntityByIdentificativiSdI(identificativoSdi);

        byte[] fileOriginale = fileFatturaEntity.getContenutoFile();
        String nomeFilePresoDaFileFatturaEntity = fileFatturaEntity.getNomeFileFattura();

        Date dataRicezione = fileFatturaEntity.getDataRicezione();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dataRicezioneSdI = sdf.format(dataRicezione);

        msg.setHeader(DATA_RICEZIONE_SDI_SUBJECT, dataRicezioneSdI);

        String estensioneFile = FileUtils.getFileExtension(nomeFilePresoDaFileFatturaEntity);

        LOG.info("PecManager - estraiMessaggio: nomeFileFattura " + nomeFilePresoDaFileFatturaEntity + "; estensioneFile " + estensioneFile);

        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);

        if(MESSAGGIO_FATTURA.equals(tipoMessaggio)) {

            DataSource dataSource = null;
            DataHandler dataHandler = null;

            if (P7M.equalsIgnoreCase(estensioneFile)) {

                LOG.info("PecManager - estraiMessaggio: estensioneFile = P7M");
                dataSource = new ByteArrayDataSource(fileOriginale, "application/x-pkcs7-mime");

                LOG.info("PecManager - estraiMessaggio: dataSource " + dataSource);

            } else if (XML.equalsIgnoreCase(estensioneFile)) {
                LOG.info("PecManager - estraiMessaggio: estensioneFile = XML");
                dataSource = new ByteArrayDataSource(fileOriginale, "application/xml");
                LOG.info("PecManager - estraiMessaggio: dataSource " + dataSource);

            } else {

                LOG.info("PecManager - estraiMessaggio: ESTENSIONE ERRATA");
                throw new FatturaPAException("PecManager - estraiMessaggio: Estensione File errata");

            }

            dataHandler = new DataHandler(dataSource);

            msg.addAttachment(nomeFile, dataHandler);

            //metadati
            DataSource dataSourceMetadati = null;
            DataHandler dataHandlerMetadati = null;

            BigInteger numeroSdi = new BigInteger(identificativoSdi);

            MetadatiFatturaEntity metadatiFatturaEntity = metadatiFatturaManager.getMetadatiByIdentificativoSdi(numeroSdi);

            byte[] fileMetadati = metadatiFatturaEntity.getContenutoFile();
            String nomeFileMetadati = metadatiFatturaEntity.getNomeFileMetadati();
            dataSourceMetadati = new ByteArrayDataSource(fileMetadati, "application/xml");

            dataHandlerMetadati = new DataHandler(dataSourceMetadati);
            msg.addAttachment(nomeFileMetadati, dataHandlerMetadati);

        } else {
            DataHandler dh = new DataHandler(msg.getBody(), "text/plain");
            msg.addAttachment(nomeFile, dh);
        }


    }

    public void aggiornaFattura(Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException {

        Message msg = msgExchange.getIn();

        String identificativoSdI = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        String statoFattura;

        if(tipoMessaggio.equals(MESSAGGIO_NOTIFICA_DECORRENZA)){
            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI.getValue();
        } else if(tipoMessaggio.equals(MESSAGGIO_FATTURA)){
            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.INOLTRATA_MAIL.getValue();
        } else {
            throw new FatturaPAException("PecManager - aggiornaFattura: Tipo messaggio non riconosciuto");
        }

        for(DatiFatturaEntity dfe : datiFatturaEntityList){
            datiFatturaManager.aggiornaStatoFatturaEsito(dfe.getIdDatiFattura(), statoFattura);
        }

    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }

    public MetadatiFatturaManager getMetadatiFatturaManager() {
        return metadatiFatturaManager;
    }

    public void setMetadatiFatturaManager(MetadatiFatturaManager metadatiFatturaManager) {
        this.metadatiFatturaManager = metadatiFatturaManager;
    }
}
