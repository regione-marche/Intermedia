package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaTestCicloNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.EnteDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.TestCicloAttivoDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.TestCicloPassivoDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.UtentiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.TestCicloAttivoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.TestCicloPassivoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class TestManager {

    private static final Logger LOG = LoggerFactory.getLogger(InvoicesManager.class);

    private EntityManagerFactory entityManagerFactory;

    private TestCicloPassivoDao testCicloPassivoDao;

    private TestCicloAttivoDao testCicloAttivoDao;

    private UtentiDao utentiDao;

    private EnteDao enteDao;


    public List<TestCicloPassivoEntity> getTestCicloPassivoByEnte(EnteEntity enteEntity) throws FatturaPaTestCicloNonTrovatoException {
        LOG.info("********** TestManager: getTestCicloPassivoByEnte **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return testCicloPassivoDao.getTestCicloPassivoByEnte(enteEntity, entityManager);
        } catch (FatturaPaTestCicloNonTrovatoException e) {
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

    public List<TestCicloAttivoEntity> getTestCicloAttivoByEnte(EnteEntity enteEntity) throws FatturaPaTestCicloNonTrovatoException {
        LOG.info("********** TestManager: getTestCicloAttivoByEnte **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return testCicloAttivoDao.getTestCicloAttivoByEnte(enteEntity, entityManager);
        } catch (FatturaPaTestCicloNonTrovatoException e) {
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

    public void salvaTestCicloPassivo(String nomeFile, BigInteger identificativoSdi, String codiceUfficio, String username) throws FatturaPAUtenteNonTrovatoException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPaTestCicloNonTrovatoException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            TestCicloPassivoEntity testCicloPassivoEntity = new TestCicloPassivoEntity();
            testCicloPassivoEntity.setNomeFile(nomeFile);
            testCicloPassivoEntity.setIdentificativoSdi(identificativoSdi);

            EnteEntity ente = enteDao.getEnteByCodiceUfficio(codiceUfficio, entityManager);
            testCicloPassivoEntity.setEnte(ente);
            testCicloPassivoEntity.setDataTest(new Timestamp(System.currentTimeMillis()));

            UtenteEntity utente = utentiDao.getUtenteByUsername(username, entityManager);
            testCicloPassivoEntity.setUtente(utente);

            testCicloPassivoDao.create(testCicloPassivoEntity, entityManager);

            entityManager.getTransaction().commit();

        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }


    public void salvaTestCicloAttivo(String nomeFile, BigInteger identificativoSdi, String codiceUfficio, String ricevutaComunicazione) throws FatturaPAUtenteNonTrovatoException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            TestCicloAttivoEntity testCicloAttivoEntity = new TestCicloAttivoEntity();
            testCicloAttivoEntity.setNomeFile(nomeFile);
            testCicloAttivoEntity.setIdentificativoSdi(identificativoSdi);

            EnteEntity ente = enteDao.getEnteByCodiceUfficio(codiceUfficio, entityManager);
            testCicloAttivoEntity.setEnte(ente);
            testCicloAttivoEntity.setDataTest(new Timestamp(System.currentTimeMillis()));
            testCicloAttivoEntity.setRicevutaComunicazione(ricevutaComunicazione);
            testCicloAttivoDao.create(testCicloAttivoEntity, entityManager);

            entityManager.getTransaction().commit();

        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }


    public int ripulisciFatturePassiveTestBeforeDate(Timestamp date) throws FatturaPaPersistenceException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            int deleted = testCicloPassivoDao.deleteBeforeDate(date, entityManager);
            entityManager.getTransaction().commit();
            return deleted;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }


    public int ripulisciFattureAttiveTestBeforeDate(Timestamp date) throws FatturaPaPersistenceException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            int deleted = testCicloAttivoDao.deleteBeforeDate(date, entityManager);
            entityManager.getTransaction().commit();
            return deleted;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    public TestCicloPassivoDao getTestCicloPassivoDao() {
        return testCicloPassivoDao;
    }

    public void setTestCicloPassivoDao(TestCicloPassivoDao testCicloPassivoDao) {
        this.testCicloPassivoDao = testCicloPassivoDao;
    }

    public TestCicloAttivoDao getTestCicloAttivoDao() {
        return testCicloAttivoDao;
    }

    public void setTestCicloAttivoDao(TestCicloAttivoDao testCicloAttivoDao) {
        this.testCicloAttivoDao = testCicloAttivoDao;
    }

    public UtentiDao getUtentiDao() {
        return utentiDao;
    }

    public void setUtentiDao(UtentiDao utentiDao) {
        this.utentiDao = utentiDao;
    }

    public EnteDao getEnteDao() {
        return enteDao;
    }

    public void setEnteDao(EnteDao enteDao) {
        this.enteDao = enteDao;
    }
}
