package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MetadatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PecCAManager {

    private static final Logger LOG = LoggerFactory.getLogger(PecCAManager.class);

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

    private static final String INFO_TIPO_INVIO_CA_HEADER = "infoTipoInvioFatturaCA";

    private EnteManager enteManager;

    private DatiFatturaManager datiFatturaManager;

    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    private MetadatiFatturaManager metadatiFatturaManager;

    public void preparaMessaggioNotifica(Exchange exchange) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        Message message = exchange.getIn();

        String body = message.getBody(String.class);

        byte[] notificaByteArray = body.getBytes();

        if (Base64Utils.isBase64(notificaByteArray)) {
            notificaByteArray = Base64.decodeBase64(notificaByteArray);
        }

        DataSource dataSource = new ByteArrayDataSource(notificaByteArray, "application/xml");
        DataHandler dh = new DataHandler(dataSource);

        String nomeFileNotifica = (String) message.getHeader(NOME_FILE);

        message.addAttachment(nomeFileNotifica, dh);

        String notifica = XAdESUnwrapper.unwrap(notificaByteArray);

        message.setBody(notifica);
    }

    public void preparaMessaggioDecTermini(Exchange msgExchange) throws FatturaPAException {

        Message msg = msgExchange.getIn();

        String nomeFile = (String) msg.getHeader(NOME_FILE);

        DataHandler dh = new DataHandler(msg.getBody(), "text/plain");
        msg.addAttachment(nomeFile, dh);
    }

    public void preparaMessaggio(Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {

        Message msg = msgExchange.getIn();

        String codiceUfficio = (String) msg.getHeader(CODICE_UFFICIO);
        String nomeFile = (String) msg.getHeader(NOME_FILE);

        String identificativoSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);

        /*
        al posto di prendere il body e farlo diventare un datahandler devo prendere dal db
         */

        //aggiungo la fattura come allegato
        FileFatturaEntity fileFatturaEntity = fatturazionePassivaFatturaManager.getFileFatturaEntityByIdentificativiSdI(identificativoSdi);

        byte[] fileOriginale = fileFatturaEntity.getContenutoFile();
        String nomeFilePresoDaFileFatturaEntity = fileFatturaEntity.getNomeFileFattura();

        Date dataRicezione = fileFatturaEntity.getDataRicezione();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dataRicezioneSdI = sdf.format(dataRicezione);

        msg.setHeader(DATA_RICEZIONE_SDI_SUBJECT, dataRicezioneSdI);

        String estensioneFile = FileUtils.getFileExtension(nomeFilePresoDaFileFatturaEntity);

        LOG.info("PecCAManager - estraiMessaggio: nomeFileFattura " + nomeFilePresoDaFileFatturaEntity + "; estensioneFile " + estensioneFile);

        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);

        //if (MESSAGGIO_FATTURA.equals(tipoMessaggio)) {

        DataSource dataSource = null;
        DataHandler dataHandler = null;

        if (P7M.equalsIgnoreCase(estensioneFile)) {

            LOG.info("PecCAManager - estraiMessaggio: estensioneFile = P7M");
            dataSource = new ByteArrayDataSource(fileOriginale, "application/x-pkcs7-mime");

            LOG.info("PecCAManager - estraiMessaggio: dataSource " + dataSource);

        } else if (XML.equalsIgnoreCase(estensioneFile)) {
            LOG.info("PecCAManager - estraiMessaggio: estensioneFile = XML");
            dataSource = new ByteArrayDataSource(fileOriginale, "application/xml");
            LOG.info("PecCAManager - estraiMessaggio: dataSource " + dataSource);

        } else {

            LOG.info("PecCAManager - estraiMessaggio: ESTENSIONE ERRATA");
            throw new FatturaPAException("PecCAManager - estraiMessaggio: Estensione File errata");

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

        //}
    }

    public void aggiornaFattura(Exchange msgExchange) throws FatturaPAException, FatturaPaPersistenceException {

        Message msg = msgExchange.getIn();

        String identificativoSdI = (String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);
        String infoTipoInvioFatturaCA = (String) msg.getHeader(INFO_TIPO_INVIO_CA_HEADER);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        String statoFattura = null;

        if(tipoMessaggio == null || "".equals(tipoMessaggio))
            throw new FatturaPAException("PecCAManager - aggiornaFattura: Tipo messaggio non valido");

        switch (tipoMessaggio) {

            case MESSAGGIO_FATTURA:

                if (infoTipoInvioFatturaCA != null && !"".equals(infoTipoInvioFatturaCA)) {
                    switch (infoTipoInvioFatturaCA) {

                        case "InvioSingolo":
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_INVIO_UNICO.getValue();
                            break;
                        case "Protocollazione":
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_INOLTRATA_PROTOCOLLO.getValue();
                            break;
                        case "Registrazione":
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_INOLTRATA_REGISTRAZIONE.getValue();
                            break;
                    }
                } else {
                    throw new FatturaPAException("PecCAManager - aggiornaFattura: Info Stato 'FATTURA' non valido");
                }

                break;

            case MESSAGGIO_NOTIFICA_DECORRENZA:

                if (infoTipoInvioFatturaCA != null && !"".equals(infoTipoInvioFatturaCA)) {

                    switch (infoTipoInvioFatturaCA) {

                        case "InvioSingolo":
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_INVIO_UNICO.getValue();
                            break;
                        case "Protocollazione":
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_INOLTRATA_PROTOCOLLO.getValue();
                            break;
                        case "Registrazione":
                            statoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_INOLTRATA_REGISTRAZIONE.getValue();
                            break;
                    }
                } else {
                    throw new FatturaPAException("PecCAManager - aggiornaFattura: Info Stato 'NOTIFICA DEC. TERMINI' non valido");
                }

                break;

            default:
                throw new FatturaPAException("PecCAManager - aggiornaFattura: Tipo messaggio non riconosciuto");
        }

        for (DatiFatturaEntity dfe : datiFatturaEntityList) {
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