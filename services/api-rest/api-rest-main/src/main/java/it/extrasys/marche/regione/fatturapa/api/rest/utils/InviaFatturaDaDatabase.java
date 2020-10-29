package it.extrasys.marche.regione.fatturapa.api.rest.utils;


import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.FileFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MetadatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.bouncycastle.cms.CMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by agosteeno on 10/07/15.
 */
public class InviaFatturaDaDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(InviaFatturaDaDatabase.class);
    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private MetadatiFatturaManager metadatiFatturaManager;
    private FileFatturaDao fileFatturaDao;
    private EntityManagerFactory entityManagerFactory;
    private DatiFatturaManager datiFatturaManager;

    public void prelevaFatturaDalDatabase(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPaPersistenceException, IOException, JAXBException, CMSException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        LOG.info("InviaFatturaDaDatabase - prelevaFatturaDalDatabase STARTED");

        Message message = exchange.getIn();

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String identificativoSdiString = (String) message.getBody();

        LOG.info("InviaFatturaDaDatabase - prelevaFatturaDalDatabase: identificativoSdI = " + identificativoSdiString);

        if(identificativoSdiString != null && !"".equals(identificativoSdiString)) {

            message.setHeader(IDENTIFICATIVO_SDI_HEADER, identificativoSdiString.trim());

            BigInteger identificativoSdi = new BigInteger(identificativoSdiString.trim());

            FileFatturaEntity fileFatturaEntity = fileFatturaDao.getFileFatturaByIdentificativiSdI(identificativoSdi, entityManager);

            String nomeFileFattura = fileFatturaEntity.getNomeFileFattura();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dataRicezioneSdI = sdf.format(fileFatturaEntity.getDataRicezione());

            MetadatiFatturaEntity metadatiFatturaEntity = metadatiFatturaManager.getMetadatiByIdentificativoSdi(identificativoSdi);

            DataSource dataSourceMetadati = null;
            DataHandler dataHandlerMetadati = null;

            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdi);

            String cedenteCodiceIva = datiFatturaEntityList.get(0).getCedenteIdFiscaleIVA();
            String committenteCodiceIva = datiFatturaEntityList.get(0).getCommittenteIdFiscaleIVA();

            byte[] fileMetadati = metadatiFatturaEntity.getContenutoFile();
            String nomeFileMetadati = metadatiFatturaEntity.getNomeFileMetadati();
            String codiceUfficio = metadatiFatturaEntity.getCodiceDestinatario();

            dataSourceMetadati = new ByteArrayDataSource(fileMetadati, "application/xml");

            dataHandlerMetadati = new DataHandler(dataSourceMetadati);

            String metadati = getMetadati(dataHandlerMetadati);

            message.setHeader("nomeFile", nomeFileFattura);
            message.setHeader("nomeFileMetadati", nomeFileMetadati);
            message.setHeader("metadati", metadati);
            message.setHeader("cedenteCodiceIva", cedenteCodiceIva);
            message.setHeader("committenteCodiceIva", committenteCodiceIva);
            message.setHeader("codiceUfficio", codiceUfficio);
            message.setHeader("tipoMessaggio", "FatturaElettronica");
            message.setHeader("dataRicezioneSdI", dataRicezioneSdI);

            byte[] fatturaElettronicaBytesArray = fileFatturaEntity.getContenutoFile();

            //REVO-17
            String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFileFattura, fatturaElettronicaBytesArray);

            message.setBody(fatturaElettronica, String.class);
        } else {
            LOG.error("InviaFatturaDaDatabase - prelevaFatturaDalDatabase: identificativoSdI vuoto o NULL");
        }
    }

    private static String getMetadati(DataHandler dh) throws IOException, JAXBException {
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

    public MetadatiFatturaManager getMetadatiFatturaManager() {
        return metadatiFatturaManager;
    }

    public void setMetadatiFatturaManager(MetadatiFatturaManager metadatiFatturaManager) {
        this.metadatiFatturaManager = metadatiFatturaManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}