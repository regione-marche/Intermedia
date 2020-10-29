package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACampoOpzionaleNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CampoOpzionaleFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.EnteCampoOpzionaleAssociazioneDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CampoOpzionaleFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteCampoOpzionaleAssociazioneEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;


public class CampoOpzionaleFatturaManager {

    private static final Logger LOG = LoggerFactory.getLogger(CampoOpzionaleFatturaManager.class);

    private EntityManagerFactory entityManagerFactory;
    private CampoOpzionaleFatturaDao campoOpzionaleFatturaDao;
    private EnteCampoOpzionaleAssociazioneDao enteCampoOpzionaleAssociazioneDao;

    public List<CampoOpzionaleFatturaEntity> getAll() throws FatturaPAException {

        LOG.info("********** CampoOpzionaleFatturaManager: getAll **********");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return campoOpzionaleFatturaDao.getAll(entityManager);
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
    }

    public CampoOpzionaleFatturaEntity getCampoOpzionaleFromDescCampo(String desc) throws FatturaPaPersistenceException, FatturaPACampoOpzionaleNonTrovatoException {

        LOG.info("********** CampoOpzionaleFatturaManager: getCampoOpzionaleFromDescCampo **********");

        EntityManager entityManager = null;

            entityManager = entityManagerFactory.createEntityManager();
        try {
            return campoOpzionaleFatturaDao.getCampoOpzionaleFromDescCampo(desc, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPACampoOpzionaleNonTrovatoException e) {
            throw e;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public void createEnteCampoOpzionaleAssociazione(EnteCampoOpzionaleAssociazioneEntity enteCampoOpzionaleAssociazioneEntity) throws FatturaPAException {

        LOG.info("********** CampoOpzionaleFatturaManager: createEnteCampoOpzionaleAssociazione **********");

        EntityManager entityManager = null;

        try{
            entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            enteCampoOpzionaleAssociazioneDao.create(enteCampoOpzionaleAssociazioneEntity, entityManager);

            entityTransaction.commit();


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
    }

    public List<EnteCampoOpzionaleAssociazioneEntity> getAssociazioniFromEnte(EnteEntity enteEntity) throws FatturaPAException {

        LOG.info("********** CampoOpzionaleFatturaManager: getAssociazioniFromEnte **********");

        EntityManager entityManager = null;

        try{
            entityManager = entityManagerFactory.createEntityManager();

            return enteCampoOpzionaleAssociazioneDao.getAssociazioniFromEnte(enteEntity, entityManager);

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
    }

    public void deleteAllEnteCampoOpzionaleAssociazioni(EnteEntity enteEntity) throws FatturaPAException {

        LOG.info("********** CampoOpzionaleFatturaManager: deleteAllEnteCampoOpzionaleAssociazioni **********");

        EntityManager entityManager = null;

        try{
            entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            List<EnteCampoOpzionaleAssociazioneEntity> list = enteCampoOpzionaleAssociazioneDao.getAssociazioniFromEnte(enteEntity, entityManager);

            if(list != null){
                for(EnteCampoOpzionaleAssociazioneEntity enteCampoOpzionaleAssociazioneEntity : list){
                    enteCampoOpzionaleAssociazioneDao.delete(enteCampoOpzionaleAssociazioneEntity, entityManager);
                }
            }

            entityTransaction.commit();


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
    }




    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public CampoOpzionaleFatturaDao getCampoOpzionaleFatturaDao() {
        return campoOpzionaleFatturaDao;
    }

    public void setCampoOpzionaleFatturaDao(CampoOpzionaleFatturaDao campoOpzionaleFatturaDao) {
        this.campoOpzionaleFatturaDao = campoOpzionaleFatturaDao;
    }

    public EnteCampoOpzionaleAssociazioneDao getEnteCampoOpzionaleAssociazioneDao() {
        return enteCampoOpzionaleAssociazioneDao;
    }

    public void setEnteCampoOpzionaleAssociazioneDao(EnteCampoOpzionaleAssociazioneDao enteCampoOpzionaleAssociazioneDao) {
        this.enteCampoOpzionaleAssociazioneDao = enteCampoOpzionaleAssociazioneDao;
    }
}


