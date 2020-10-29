package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;


import it.extrasys.marche.regione.fatturapa.api.rest.model.FileFattura;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaBodyType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.MetadatiInvioFileType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.converters.FatturaElettronicaToStatoFatturaEntityConverter;
import it.extrasys.marche.regione.fatturapa.persistence.unit.converters.MetadatiToEntityConverter;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.MessaggiSDIEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/02/15.
 */
public class FatturazionePassivaFatturaManagerXAImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaFatturaManagerXAImpl.class);

    private EntityManagerFactory entityManagerFactory;

    private FatturaElettronicaToStatoFatturaEntityConverter statoFatturaConverter;

    private MetadatiToEntityConverter metadatiConverter;

    private MetadatiFatturaDao metadatiFatturaDao;

    private CodificaStatiDao codificaStatiDao;

    private DatiFatturaDao datiFatturaDao;

    private StatoFatturaDao statoFatturaDao;

    private MessaggiSDIDao messaggiSDIDao;

    private FileFatturaDao fileFatturaDao;


    public void salvaFatture(String nomeFile,
                             String nomeFileMetadati,
                             BigInteger identificativoSdI,
                             FatturaElettronicaType fatturaElettronica,
                             MetadatiInvioFileType metadati,
                             byte[] fileFattura,
                             byte[] fileMetadati,
                             String originalSoapMessage, boolean isFatturaInterna, Date dataRicezioneSdI, Boolean isTest) throws FatturaPAException {

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            CodificaStatiEntity codiceStato = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA, entityManager);

            // SALVATAGGIO FILE FATTURE

            FileFatturaEntity fileFatturaEntity = new FileFatturaEntity();

            fileFatturaEntity.setContenutoFile(fileFattura);

            fileFatturaEntity.setNomeFileFattura(nomeFile);

            fileFatturaEntity.setIdentificativoSdI(identificativoSdI);

            fileFatturaEntity.setDataRicezione(new Timestamp(dataRicezioneSdI.getTime()));

            if (isTest) {
                fileFatturaEntity.setFatturazioneTest(Boolean.TRUE);
            } else {
                fileFatturaEntity.setFatturazioneTest(Boolean.FALSE);
            }

            fileFatturaDao.create(fileFatturaEntity, entityManager);

            entityManager.flush();
            entityManager.clear();

            // FINE SALVATAGGIO FILE FATTURE


            //SALVATAGGIO FATTURE

            StatoFatturaEntity statoFatturaEntity;

            int countPosizioneFattura = 1;

            for (FatturaElettronicaBodyType body : fatturaElettronica.getFatturaElettronicaBody()) {
                statoFatturaEntity = statoFatturaConverter.convert(identificativoSdI, nomeFile, countPosizioneFattura, fatturaElettronica.getFatturaElettronicaHeader(), body, isFatturaInterna, isTest);
                statoFatturaEntity.getDatiFattura().setFileFatturaEntity(fileFatturaEntity);
                statoFatturaEntity.setStato(codiceStato);

                statoFatturaEntity.getDatiFattura().setDataCreazione(dataRicezioneSdI);

                long giorni15 = 1296000000L;

                long dataCreazioneFatturaLong = dataRicezioneSdI.getTime();

                long dataDecorrenzaTermini = dataCreazioneFatturaLong+giorni15;

                statoFatturaEntity.getDatiFattura().setDataDecorrenzaTerminiPrevista(new Date(dataDecorrenzaTermini));

                datiFatturaDao.create(statoFatturaEntity.getDatiFattura(), entityManager);
                statoFatturaDao.create(statoFatturaEntity, entityManager);
                countPosizioneFattura++;
            }

            entityManager.flush();
            entityManager.clear();

            //FINE SALVATAGGIO FATTURE

            //SALVATAGGIO METADATI

            MetadatiFatturaEntity metadatiFatturaEntity = metadatiConverter.convert(metadati, nomeFileMetadati, fileMetadati);

            metadatiFatturaEntity.setDataRicezioneSdi(new Timestamp(dataRicezioneSdI.getTime()));
            if (isTest) {
                metadatiFatturaEntity.setFatturazioneTest(Boolean.TRUE);
            } else {
                metadatiFatturaEntity.setFatturazioneTest(Boolean.FALSE);
            }


            metadatiFatturaDao.create(metadatiFatturaEntity, entityManager);


            // SALVO IL MESSAGGIO ORIGINALE SE DIVERSO DA NULL

            if (originalSoapMessage != null && !originalSoapMessage.trim().isEmpty()) {

                MessaggiSDIEntity messaggiSDIEntity = new MessaggiSDIEntity();
                messaggiSDIEntity.setIdentificativoSdI(identificativoSdI);
                messaggiSDIEntity.setMessaggioSdI(originalSoapMessage);

                messaggiSDIDao.create(messaggiSDIEntity, entityManager);

                entityManager.flush();
                entityManager.clear();
            }

        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {

            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public FileFatturaEntity getFileFatturaFromIdentificativoSdi(BigInteger identificativoSdi){
        EntityManager entityManager = null;

        try{
            entityManager = entityManagerFactory.createEntityManager();

            FileFatturaEntity fileFattura = fileFatturaDao.getFileFatturaByIdentificativiSdI(identificativoSdi, entityManager);
            entityManager.flush();
            entityManager.clear();
            return fileFattura;

        }catch(NoResultException e){
            return null;
        }finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FatturaElettronicaToStatoFatturaEntityConverter getStatoFatturaConverter() {
        return statoFatturaConverter;
    }

    public void setStatoFatturaConverter(FatturaElettronicaToStatoFatturaEntityConverter statoFatturaConverter) {
        this.statoFatturaConverter = statoFatturaConverter;
    }

    public MetadatiToEntityConverter getMetadatiConverter() {
        return metadatiConverter;
    }

    public void setMetadatiConverter(MetadatiToEntityConverter metadatiConverter) {
        this.metadatiConverter = metadatiConverter;
    }

    public MetadatiFatturaDao getMetadatiFatturaDao() {
        return metadatiFatturaDao;
    }

    public void setMetadatiFatturaDao(MetadatiFatturaDao metadatiFatturaDao) {
        this.metadatiFatturaDao = metadatiFatturaDao;
    }

    public CodificaStatiDao getCodificaStatiDao() {
        return codificaStatiDao;
    }

    public void setCodificaStatiDao(CodificaStatiDao codificaStatiDao) {
        this.codificaStatiDao = codificaStatiDao;
    }

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }

    public StatoFatturaDao getStatoFatturaDao() {
        return statoFatturaDao;
    }

    public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
        this.statoFatturaDao = statoFatturaDao;
    }

    public MessaggiSDIDao getMessaggiSDIDao() {
        return messaggiSDIDao;
    }

    public void setMessaggiSDIDao(MessaggiSDIDao messaggiSDIDao) {
        this.messaggiSDIDao = messaggiSDIDao;
    }

    public FileFatturaDao getFileFatturaDao() {
        return fileFatturaDao;
    }

    public void setFileFatturaDao(FileFatturaDao fileFatturaDao) {
        this.fileFatturaDao = fileFatturaDao;
    }
}
