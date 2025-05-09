package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaMancataConsegnaEntity;
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
public class FatturaAttivaNotificaMancataConsegnaDao extends GenericDao<FatturaAttivaNotificaMancataConsegnaEntity, BigInteger> {

    public List<BigInteger> getIdFatturaAttivaNotificaMancataConsegnaUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<BigInteger> list = null;

        try {
            String queryString = "SELECT fandt.fatturaAttiva.idFatturaAttiva FROM StatoAttivaNotificaMancataConsegnaEntity sandt JOIN sandt.notificaMancataConsegnaEntity fandt " +
                    " WHERE sandt.stato.codStato = '001' AND  fandt.fatturaAttiva.idFatturaAttiva IN ( :idFattureAttive ) " +
                    " AND sandt.data = ( SELECT MAX(sandt1.data) FROM StatoAttivaNotificaMancataConsegnaEntity sandt1 JOIN sandt1.notificaMancataConsegnaEntity fandt1 " +
                    " WHERE fandt1.idNotificaMancataConsegna = fandt.idNotificaMancataConsegna)";

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


    public FatturaAttivaNotificaMancataConsegnaEntity getFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        FatturaAttivaNotificaMancataConsegnaEntity result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaMancataConsegnaEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaMancataConsegnaEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = (FatturaAttivaNotificaMancataConsegnaEntity) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public List<FatturaAttivaNotificaMancataConsegnaEntity> getListFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<FatturaAttivaNotificaMancataConsegnaEntity> result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaMancataConsegnaEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ORDER BY rc.data DESC";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaMancataConsegnaEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = query.getResultList();
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public int deleteMancataConsegnaByIdMancataConsegna(BigInteger idMancataConsegna, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM fattura_attiva_notifica_mancata_consegna WHERE id_notifica_mancata_consegna = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idMancataConsegna);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id  " + idMancataConsegna);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }
}
