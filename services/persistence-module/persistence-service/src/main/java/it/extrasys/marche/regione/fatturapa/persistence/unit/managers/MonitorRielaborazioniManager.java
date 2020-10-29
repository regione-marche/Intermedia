package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodeDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.MonitorRielaborazioniDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.UtentiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodeEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorRielaborazioniEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MonitorRielaborazioniManager {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorRielaborazioniManager.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private EntityManagerFactory entityManagerFactory;
    private MonitorRielaborazioniDao monitorRielaborazioniDao;
    private UtentiDao utentiDao;
    private CodeDao codeDao;

    public MonitorRielaborazioniEntity salvaRielaborazione(int sizeCoda, String username, String nomeCoda, String nomeReport, BigInteger identificativoSdi) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            MonitorRielaborazioniEntity monitor = new MonitorRielaborazioniEntity();
            //La prendo dal nome report per essere coerente
            String dateString = nomeReport.split("_")[1].replace(".csv", "");
            monitor.setDataInizioRielaborazione(new Timestamp(((Date) sdf.parse(dateString)).getTime()));
            UtenteEntity utente = utentiDao.getUtenteByUsername(username, entityManager);
            monitor.setIdUtente(utente);

            CodeEntity coda = codeDao.getCodaByNome(nomeCoda, entityManager);
            monitor.setIdCoda(coda);
            monitor.setNumeroRielaborazioni(sizeCoda);
            monitor.setNomeReport(nomeReport);
            monitor.setIdentificativoSdi(identificativoSdi);
            monitor.setNumeroTentativi(0);
            MonitorRielaborazioniEntity monitorRielaborazioniEntity = monitorRielaborazioniDao.create(monitor, entityManager);
            entityManager.getTransaction().commit();

            return monitorRielaborazioniEntity;
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


    public List<MonitorRielaborazioniEntity> getMonitorRielaborazioniByReportAndDate(String nomeReport, Date data) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            List<MonitorRielaborazioniEntity> result = monitorRielaborazioniDao.getMonitorRielaborazioniByReportAndDate(nomeReport, data, entityManager);

            return result;
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


    public List<MonitorRielaborazioniEntity> getMonitorRielaborazioniByNomeReport(String nomeReport) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            List<MonitorRielaborazioniEntity> result = monitorRielaborazioniDao.getMonitorRielaborazioniByNomeReport(nomeReport, entityManager);

            return result;
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

    public void updateMonitorRielaborazioniByIdentificativoSdiAndNomeReport(BigInteger identificativoSdi, String nomeReport, String stacktrace) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            List<MonitorRielaborazioniEntity> monitorRielaborazioni = monitorRielaborazioniDao.getMonitorRielaborazioniByNomeReportAndSdi(nomeReport, identificativoSdi, entityManager);

            if (monitorRielaborazioni != null && monitorRielaborazioni.size() > 0) {
                MonitorRielaborazioniEntity monitorRielaborazione = monitorRielaborazioni.get(0);
                if (StringUtils.isNotEmpty(stacktrace)) {
                    monitorRielaborazione.setStacktrace(stacktrace.getBytes(Charset.forName("UTF-8")));
                }
                monitorRielaborazione.setNumeroTentativi(monitorRielaborazioni.get(0).getNumeroTentativi() + 1);
                entityManager.getTransaction().begin();
                MonitorRielaborazioniEntity update = monitorRielaborazioniDao.update(monitorRielaborazione, entityManager);
                entityManager.getTransaction().commit();
            }
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


    public List<String> deleteMonitorRielaborazioniBeforeDate(Date date) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            //Recupero tutti i nomi del fileReport dei record che andrò ad eliminare in modo da cancellare da filesystem il file.
            List<String> monitorRielaborazioniBeforeDate = monitorRielaborazioniDao.getMonitorRielaborazioniBeforeDate(new Timestamp(date.getTime()), entityManager);

            entityManager.getTransaction().begin();
            int i = monitorRielaborazioniDao.deleteMonitorRielaborazioniBeforeDate(new Timestamp(date.getTime()), entityManager);

            entityManager.getTransaction().commit();
            return monitorRielaborazioniBeforeDate;
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

    public MonitorRielaborazioniDao getMonitorRielaborazioniDao() {
        return monitorRielaborazioniDao;
    }

    public void setMonitorRielaborazioniDao(MonitorRielaborazioniDao monitorRielaborazioniDao) {
        this.monitorRielaborazioniDao = monitorRielaborazioniDao;
    }

    public UtentiDao getUtentiDao() {
        return utentiDao;
    }

    public void setUtentiDao(UtentiDao utentiDao) {
        this.utentiDao = utentiDao;
    }

    public CodeDao getCodeDao() {
        return codeDao;
    }

    public void setCodeDao(CodeDao codeDao) {
        this.codeDao = codeDao;
    }
}
