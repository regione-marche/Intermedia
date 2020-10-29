package it.extrasys.marche.regione.fatturapa.patch.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.FileFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MetadatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 20/08/15.
 */
public class FromByteArrayToStringProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(FromByteArrayToStringProcessor.class);

    private static final String IDENTIFICATIVO_SDI = "identificativoSdI";

    private MetadatiFatturaManager metadatiFatturaManager;
    private FileFatturaDao fileFatturaDao;
    private EntityManagerFactory entityManagerFactory;
    private DatiFatturaManager datiFatturaManager;


    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdiString = (String) message.getHeader(IDENTIFICATIVO_SDI);

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        if(identificativoSdiString != null && !"".equals(identificativoSdiString)) {

            BigInteger identificativoSdi = new BigInteger(identificativoSdiString);

            FileFatturaEntity fileFatturaEntity = fileFatturaDao.getFileFatturaByIdentificativiSdI(identificativoSdi, entityManager);

            String nomeFileFattura = fileFatturaEntity.getNomeFileFattura();

            MetadatiFatturaEntity metadatiFatturaEntity = metadatiFatturaManager.getMetadatiByIdentificativoSdi(identificativoSdi);

            DataSource dataSourceMetadati = null;
            DataHandler dataHandlerMetadati = null;

            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdi);

            DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);

            String committenteCodiceIva = datiFatturaEntity.getCommittenteIdFiscaleIVA();

            byte[] fileMetadati = metadatiFatturaEntity.getContenutoFile();
            String nomeFileMetadati = metadatiFatturaEntity.getNomeFileMetadati();
            String codiceUfficio = metadatiFatturaEntity.getCodiceDestinatario();

            dataSourceMetadati = new ByteArrayDataSource(fileMetadati, "application/xml");

            dataHandlerMetadati = new DataHandler(dataSourceMetadati);

            String metadati = getMetadati(dataHandlerMetadati);

            message.setHeader("nomeFile", nomeFileFattura);
            message.setHeader("nomeFileMetadati", nomeFileMetadati);
            message.setHeader("metadati", metadati);
            message.setHeader("committenteCodiceIva", committenteCodiceIva);
            message.setHeader("idFiscaleCommittente", committenteCodiceIva);
            message.setHeader("codiceUfficio", codiceUfficio);
            message.setHeader("tipoMessaggio", "FatturaElettronica");
            message.setHeader("SOAPAction", "http://www.fatturapa.it/RicezioneFatture/RiceviFattureSdI");

            message.setHeader("isAsurValidationActive", "true");
            message.setHeader("performValidation", "false");

            message.setHeader("dataDocumento", datiFatturaEntity.getDataFattura());
            message.setHeader("dataProtocollazione", datiFatturaEntity.getDataCreazione());
            message.setHeader("dataRicezione", datiFatturaEntity.getDataCreazione());
            message.setHeader("numeroProtocollo", datiFatturaEntity.getNumeroProtocollo());

            LOG.info("REGMA 124 REGISTRAZIONE SU AREAS - FromByteArrayToStringProcessor: headers " + message.getHeaders());

            byte[] fatturaElettronicaBytesArray = fileFatturaEntity.getContenutoFile();

            //REVO-17
            String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFileFattura, fatturaElettronicaBytesArray);

            message.setBody(fatturaElettronica, String.class);
        } else {
            LOG.error("Regma124FattureNonRegistrateSuAreas: identificativoSdI vuoto o NULL");
        }
    }

    private static String getMetadati(DataHandler dh) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] metadati;
        try {
            dh.writeTo(outputStream);
            metadati = outputStream.toByteArray();
            return new String(metadati);
        } finally {
            outputStream.reset();
            outputStream.close();
        }

        // InputStream is = dh.getInputStream();
        // byte[] bytes = IOUtils.toByteArray(is);
        // return new String(bytes);
    }

    public MetadatiFatturaManager getMetadatiFatturaManager() {
        return metadatiFatturaManager;
    }

    public void setMetadatiFatturaManager(MetadatiFatturaManager metadatiFatturaManager) {
        this.metadatiFatturaManager = metadatiFatturaManager;
    }

    public FileFatturaDao getFileFatturaDao() {
        return fileFatturaDao;
    }

    public void setFileFatturaDao(FileFatturaDao fileFatturaDao) {
        this.fileFatturaDao = fileFatturaDao;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}