package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorRielaborazioniEntity;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class MonitorRielaborazioniDao extends GenericDao<MonitorRielaborazioniEntity, BigInteger> {

    public List<MonitorRielaborazioniEntity> getMonitorRielaborazioniByReportAndDate(String nomeReport, Date data, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT mr FROM MonitorRielaborazioniEntity mr ";

            if (StringUtils.isNotEmpty(nomeReport)) {
                queryString += " WHERE mr.nomeReport = :nomeReport ";
            }

            if (data != null && StringUtils.isNotEmpty(nomeReport)) {
                queryString += " AND mr.dataInizioRielaborazione < :data ";
            } else if (data != null && StringUtils.isNotEmpty(nomeReport)) {
                queryString += " WHERE mr.dataInizioRielaborazione < :data ";
            }

            TypedQuery<MonitorRielaborazioniEntity> query = entityManager.createQuery(queryString, MonitorRielaborazioniEntity.class);

            query.setParameter("nomeReport", nomeReport);
            query.setParameter("data", data);

            List<MonitorRielaborazioniEntity> resultList = query.getResultList();

            return resultList;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }


    public List<MonitorRielaborazioniEntity> getMonitorRielaborazioniByNomeReport(String nomeReport, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT mr FROM MonitorRielaborazioniEntity mr WHERE mr.nomeReport = :nomeReport";


            TypedQuery<MonitorRielaborazioniEntity> query = entityManager.createQuery(queryString, MonitorRielaborazioniEntity.class);

            query.setParameter("nomeReport", nomeReport);

            List<MonitorRielaborazioniEntity> resultList = query.getResultList();

            return resultList;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }

    public List<MonitorRielaborazioniEntity> getMonitorRielaborazioniByNomeReportAndSdi(String nomeReport, BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT mr FROM MonitorRielaborazioniEntity mr WHERE mr.nomeReport = :nomeReport AND mr.identificativoSdi = :identificativoSdi";


            TypedQuery<MonitorRielaborazioniEntity> query = entityManager.createQuery(queryString, MonitorRielaborazioniEntity.class);

            query.setParameter("nomeReport", nomeReport);
            query.setParameter("identificativoSdi", identificativoSdi);

            List<MonitorRielaborazioniEntity> resultList = query.getResultList();

            return resultList;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_RIELABORAZIONI");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public int deleteMonitorRielaborazioniBeforeDate(Timestamp date, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM MonitorRielaborazioniEntity mr WHERE mr.dataInizioRielaborazione <= :date";
            Query query = entityManager.createQuery(queryString);

            query.setParameter("date", date);

            int i = query.executeUpdate();

            return i;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore Ripulitura MONITOR_RIELABORAZIONI");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public List<String> getMonitorRielaborazioniBeforeDate(Timestamp date, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT mr.nomeReport FROM MonitorRielaborazioniEntity mr WHERE mr.dataInizioRielaborazione <= :date";
            TypedQuery<String> query = entityManager.createQuery(queryString, String.class);

            query.setParameter("date", date);

            List<String> result = query.getResultList();

            return result;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_RIELABORAZIONI");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
