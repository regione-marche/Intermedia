package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaFlagWarningDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.MonitorFatturaPassivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorFatturaPassivaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class MonitorFatturaPassivaManager {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorFatturaPassivaManager.class);

    private EntityManagerFactory entityManagerFactory;
    private MonitorFatturaPassivaDao monitorFatturaPassivaDao;
    private CodificaFlagWarningDao codificaFlagWarningDao;

    public void salvaMonitorFatturaPassiva(List<MonitorFatturaPassivaEntity> monitorFatturaPassivaEntityList) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            monitorFatturaPassivaDao.deleteAll(entityManager);
            LOG.info("MONITOR FATTURA PASSIVA - Cancellate le vecchie fatture");

            for (MonitorFatturaPassivaEntity monitor : monitorFatturaPassivaEntityList) {
                CodificaFlagWarningEntity flag = codificaFlagWarningDao.read(monitor.getFlag(), entityManager);
                monitor.setFlagWarning(flag);
                monitorFatturaPassivaDao.create(monitor, entityManager);
            }

            entityManager.getTransaction().commit();
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


    public List<MonitorFatturaPassivaEntity> getMonitorFatturePassive(String orderBy, String ordering, Integer numberOfElements, Integer pageNumber,String flag) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<MonitorFatturaPassivaEntity> monitorFatturePassive = monitorFatturaPassivaDao.getMonitorFatturePassive(orderBy, ordering, numberOfElements, pageNumber,flag, entityManager);
            return monitorFatturePassive;
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

    public Long getCountMonitorFatturePassive(String flag) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Long monitorFatturePassive = monitorFatturaPassivaDao.getCountMonitorFatturePassive(flag, entityManager);
            return monitorFatturePassive;
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

    public MonitorFatturaPassivaDao getMonitorFatturaPassivaDao() {
        return monitorFatturaPassivaDao;
    }

    public void setMonitorFatturaPassivaDao(MonitorFatturaPassivaDao monitorFatturaPassivaDao) {
        this.monitorFatturaPassivaDao = monitorFatturaPassivaDao;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public CodificaFlagWarningDao getCodificaFlagWarningDao() {
        return codificaFlagWarningDao;
    }

    public void setCodificaFlagWarningDao(CodificaFlagWarningDao codificaFlagWarningDao) {
        this.codificaFlagWarningDao = codificaFlagWarningDao;
    }
}
