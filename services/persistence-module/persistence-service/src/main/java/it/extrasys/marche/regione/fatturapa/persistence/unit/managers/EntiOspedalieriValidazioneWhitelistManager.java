package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.EntiOspedalieriValidazioneWhitelistDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntiOspedalieriValidazioneWhitelistEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Created by agosteeno on 24/11/15.
 */
public class EntiOspedalieriValidazioneWhitelistManager {

    private static final Logger LOG = LoggerFactory.getLogger(EntiOspedalieriValidazioneWhitelistManager.class);

    private EntityManagerFactory entityManagerFactory;

    private EntiOspedalieriValidazioneWhitelistDao entiOspedalieriValidazioneWhitelistDao;

    public EntiOspedalieriValidazioneWhitelistEntity getEnteByIdFiscaleCedente(String idFiscaleCedente) throws FatturaPAException, FatturaPaPersistenceException {

        EntityManager entityManager = null;

        EntiOspedalieriValidazioneWhitelistEntity entiOspedalieriValidazioneWhitelistEntity;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entiOspedalieriValidazioneWhitelistEntity = entiOspedalieriValidazioneWhitelistDao.getEnteByIdFiscaleCedente(idFiscaleCedente, entityManager);

        } catch (FatturaPaPersistenceException e){
            throw e;
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return entiOspedalieriValidazioneWhitelistEntity;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntiOspedalieriValidazioneWhitelistDao getEntiOspedalieriValidazioneWhitelistDao() {
        return entiOspedalieriValidazioneWhitelistDao;
    }

    public void setEntiOspedalieriValidazioneWhitelistDao(EntiOspedalieriValidazioneWhitelistDao entiOspedalieriValidazioneWhitelistDao) {
        this.entiOspedalieriValidazioneWhitelistDao = entiOspedalieriValidazioneWhitelistDao;
    }
}
