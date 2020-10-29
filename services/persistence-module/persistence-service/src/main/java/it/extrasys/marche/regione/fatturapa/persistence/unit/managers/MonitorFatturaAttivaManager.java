package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaFlagWarningDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.MonitorFatturaAttivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorFatturaAttivaEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class MonitorFatturaAttivaManager {

    private EntityManagerFactory entityManagerFactory;
    private MonitorFatturaAttivaDao monitorFatturaAttivaDao;
    private CodificaFlagWarningDao codificaFlagWarningDao;


    public void aggiornaMonitorFatturaAttiva(List<MonitorFatturaAttivaEntity> monitorFatturaAttivaList) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            monitorFatturaAttivaDao.deleteAll(entityManager);

            for (MonitorFatturaAttivaEntity monitorFatturaAttiva : monitorFatturaAttivaList) {
                CodificaFlagWarningEntity flag = codificaFlagWarningDao.read(monitorFatturaAttiva.getFlag(), entityManager);
                monitorFatturaAttiva.setFlagWarning(flag);
                monitorFatturaAttivaDao.create(monitorFatturaAttiva, entityManager);
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


    public int deleteAll() throws FatturaPAException {
        EntityManager entityManager = null;
        int deleted;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            deleted = monitorFatturaAttivaDao.deleteAll(entityManager);

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
        return deleted;
    }


    public List<MonitorFatturaAttivaEntity> getMonitorFattureAttiveWarning(String orderBy, String ordering, Integer numberOfElements, Integer pageNumber, String flag) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<MonitorFatturaAttivaEntity> monitorFattureAttive = monitorFatturaAttivaDao.getMonitorFattureAttive(orderBy, ordering, numberOfElements, pageNumber, flag, entityManager);
            return monitorFattureAttive;
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

    public Long getCountMonitorFattureAttiveWarning(String flag) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Long monitorFattureAttive = monitorFatturaAttivaDao.getCountMonitorFattureAttive(flag, entityManager);
            return monitorFattureAttive;
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


    public List<MonitorFatturaAttivaEntity> getMonitorFattureAttive(String orderBy, String ordering, Integer numberOfElements, Integer pageNumber, String flag) throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<MonitorFatturaAttivaEntity> monitorFattureAttive = monitorFatturaAttivaDao.getMonitorFattureAttive(orderBy, ordering, numberOfElements, pageNumber,flag, entityManager);
            return monitorFattureAttive;
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

    public Long getCountMonitorFattureAttive() throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Long monitorFattureAttive = monitorFatturaAttivaDao.getCountMonitorFattureAttive(entityManager);
            return monitorFattureAttive;
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

    public MonitorFatturaAttivaDao getMonitorFatturaAttivaDao() {
        return monitorFatturaAttivaDao;
    }

    public void setMonitorFatturaAttivaDao(MonitorFatturaAttivaDao monitorFatturaAttivaDao) {
        this.monitorFatturaAttivaDao = monitorFatturaAttivaDao;
    }

    public CodificaFlagWarningDao getCodificaFlagWarningDao() {
        return codificaFlagWarningDao;
    }

    public void setCodificaFlagWarningDao(CodificaFlagWarningDao codificaFlagWarningDao) {
        this.codificaFlagWarningDao = codificaFlagWarningDao;
    }
}
