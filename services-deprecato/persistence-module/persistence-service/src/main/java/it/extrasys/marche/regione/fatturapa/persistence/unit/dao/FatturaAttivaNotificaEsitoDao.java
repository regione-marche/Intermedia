package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaEsitoEntity;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 23/03/15.
 */
public class FatturaAttivaNotificaEsitoDao extends GenericDao<FatturaAttivaNotificaEsitoEntity, BigInteger> {
    public List<BigInteger> getIdFatturaAttivaNotificaEsitoUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<BigInteger> list = null;

        try {
            String queryString = "SELECT fandt.fatturaAttiva.idFatturaAttiva FROM StatoAttivaNotificaEsitoEntity sandt JOIN sandt.notificaEsitoEntity fandt " +
                    " WHERE sandt.stato.codStato = '001' AND  fandt.fatturaAttiva.idFatturaAttiva IN ( :idFattureAttive ) " +
                    " AND sandt.data = ( SELECT MAX(sandt1.data) FROM StatoAttivaNotificaEsitoEntity sandt1 JOIN sandt1.notificaEsitoEntity fandt1 " +
                    " WHERE fandt1.idNotificaEsito = fandt.idNotificaEsito)";

            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, BigInteger.class));
            query.setParameter("idFattureAttive", idFattureAttive);
            list = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFattureAttive);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return list;
    }


    public FatturaAttivaNotificaEsitoEntity getFatturaAttivaNotificaEsitoByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        FatturaAttivaNotificaEsitoEntity result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaEsitoEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaEsitoEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = (FatturaAttivaNotificaEsitoEntity) query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public List<FatturaAttivaNotificaEsitoEntity> getListFatturaAttivaNotificaEsitoByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<FatturaAttivaNotificaEsitoEntity> result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaEsitoEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ORDER BY rc.data DESC";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaEsitoEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = query.getResultList();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }


    public int deleteNotificaEsitoById(BigInteger idNotificaEsito, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM fattura_attiva_notifica_esito WHERE id_notifica_esito = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idNotificaEsito);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id  " + idNotificaEsito);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
