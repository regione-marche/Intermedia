package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorFatturaPassivaEntity;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class MonitorFatturaPassivaDao extends GenericDao<MonitorFatturaPassivaEntity, BigInteger> {

    private static final String ORDER_DEFAULT = " ORDER BY fa.identificativoSdi ASC ";

    public int deleteAll(EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM MonitorFatturaPassivaEntity";

            Query query = entityManager.createQuery(queryString);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Impossibile svuotare la tabella MONITOR_FATTURA_PASSIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public List<MonitorFatturaPassivaEntity> getMonitorFatturePassive(String orderBy, String ordering, Integer numberOfElements, Integer pageNumber, String flag, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT fa FROM MonitorFatturaPassivaEntity fa ";

            if (StringUtils.isNotEmpty(flag)) {
                queryString += " WHERE fa.flagWarning.codiceFlagWarning <> :flag ";
            }

            if (StringUtils.isNotEmpty(orderBy)) {
                queryString += " ORDER BY fa." + orderBy + " " + ordering;
            } else {
                queryString += ORDER_DEFAULT;
            }

            TypedQuery<MonitorFatturaPassivaEntity> query = entityManager.createQuery(queryString, MonitorFatturaPassivaEntity.class);

            if (StringUtils.isNotEmpty(flag)) {
                query.setParameter("flag", flag);
            }

            if (numberOfElements != null) {
                query.setMaxResults(numberOfElements);
            }
            if (pageNumber != null) {
                query.setFirstResult(pageNumber);
            }
            List<MonitorFatturaPassivaEntity> result = query.getResultList();

            return result;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_PASSIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public Long getCountMonitorFatturePassive(String flag, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT COUNT(fa) FROM MonitorFatturaPassivaEntity fa ";

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
            throw new FatturaPaPersistenceException("Errore MONITOR_FATTURA_PASSIVA");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
