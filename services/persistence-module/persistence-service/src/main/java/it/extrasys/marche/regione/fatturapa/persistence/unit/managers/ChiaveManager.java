package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.ChiaveDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ChiaveManager {
    private static final Logger LOG = LoggerFactory.getLogger(ChiaveManager.class);

    private EntityManagerFactory entityManagerFactory;
    private ChiaveDao chiaveDao;


    public String getChiave() throws FatturaPAException {

        EntityManager entityManager = null;
        String chiave = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            chiave = chiaveDao.getChiave(entityManager);
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        }
        return chiave;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public ChiaveDao getChiaveDao() {
        return chiaveDao;
    }

    public void setChiaveDao(ChiaveDao chiaveDao) {
        this.chiaveDao = chiaveDao;
    }
}
