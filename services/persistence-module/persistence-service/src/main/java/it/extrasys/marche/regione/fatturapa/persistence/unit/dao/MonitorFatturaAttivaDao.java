package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorFatturaAttivaEntity;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class MonitorFatturaAttivaDao extends GenericDao<MonitorFatturaAttivaEntity, BigInteger> {

    private static final String ORDER_DEFAULT = " ORDER BY fa.identificativoSdi ASC";

    public int deleteAll(EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM MonitorFatturaAttivaEntity";

            Query query = entityManager.createQuery(queryString);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Impossibile svuotare la tabella MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }


    public List<MonitorFatturaAttivaEntity> getMonitorFattureAttive(String orderBy, String ordering, Integer numberOfElements, Integer pageNumber, String flag, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT fa FROM MonitorFatturaAttivaEntity fa ";

            if (StringUtils.isNotEmpty(flag)) {
                queryString += " WHERE fa.flagWarning.codiceFlagWarning <> :flag ";
            }
            if (StringUtils.isNotEmpty(orderBy)) {
                queryString += " ORDER BY fa." + orderBy + " " + ordering;
            } else {
                queryString += ORDER_DEFAULT;
            }

            TypedQuery<MonitorFatturaAttivaEntity> query = entityManager.createQuery(queryString, MonitorFatturaAttivaEntity.class);

            if (StringUtils.isNotEmpty(flag)) {
                query.setParameter("flag", flag);
            }

            if (numberOfElements != null) {
                query.setMaxResults(numberOfElements);
            }
            if (pageNumber != null) {
                query.setFirstResult(pageNumber);
            }
            List<MonitorFatturaAttivaEntity> result = query.getResultList();

            return result;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public Long getCountMonitorFattureAttive(String flag, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT COUNT(fa) FROM MonitorFatturaAttivaEntity fa ";

            if (StringUtils.isNotEmpty(flag)) {
                queryString += " WHERE fa.flagWarning.codiceFlagWarning <> :flag ";
            }

            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);
            if (StringUtils.isNotEmpty(flag)) {
                query.setParameter("flag", flag);
            }

            Long count = query.getSingleResult().longValue();

            return count;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }


    public List<MonitorFatturaAttivaEntity> getMonitorFattureAttive(String orderBy, String ordering, Integer numberOfElements, Integer pageNumber, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT fa FROM MonitorFatturaAttivaEntity fa";

            if (StringUtils.isNotEmpty(orderBy)) {
                queryString += "ORDER BY fa. " + orderBy + " " + ordering;
            } else {
                queryString += ORDER_DEFAULT;
            }

            TypedQuery<MonitorFatturaAttivaEntity> query = entityManager.createQuery(queryString, MonitorFatturaAttivaEntity.class);

            if (numberOfElements != null) {
                query.setMaxResults(numberOfElements);
            }
            if (pageNumber != null) {
                query.setFirstResult(pageNumber);
            }
            List<MonitorFatturaAttivaEntity> result = query.getResultList();

            return result;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public Long getCountMonitorFattureAttive(EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT COUNT(fa) FROM MonitorFatturaAttivaEntity";

            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);

            Long count = query.getSingleResult().longValue();

            return count;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_ATTIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
