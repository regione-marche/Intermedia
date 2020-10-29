package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaScartoEntity;
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
public class FatturaAttivaNotificaScartoDao extends GenericDao<FatturaAttivaNotificaScartoEntity, BigInteger> {


    public List<BigInteger> getIdFatturaAttivaNotificaScartoUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<BigInteger> list = null;

        try {
            String queryString = "SELECT fandt.fatturaAttiva.idFatturaAttiva FROM StatoAttivaNotificaScartoEntity sandt JOIN sandt.notificaScartoEntity fandt " +
                    " WHERE sandt.stato.codStato = '001' AND  fandt.fatturaAttiva.idFatturaAttiva IN ( :idFattureAttive ) " +
                    " AND sandt.data = ( SELECT MAX(sandt1.data) FROM StatoAttivaNotificaScartoEntity sandt1 JOIN sandt1.notificaScartoEntity fandt1 " +
                    " WHERE fandt1.idNotificaScarto = fandt.idNotificaScarto)";

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


    public FatturaAttivaNotificaScartoEntity getFatturaAttivaNotificaScartoByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        FatturaAttivaNotificaScartoEntity result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaScartoEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaScartoEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = (FatturaAttivaNotificaScartoEntity) query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public List<FatturaAttivaNotificaScartoEntity> getListFatturaAttivaNotificaScartoByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<FatturaAttivaNotificaScartoEntity> result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaScartoEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ORDER BY rc.data DESC";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaScartoEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = query.getResultList();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public int deleteNotificaScartoById(BigInteger idNotificaScarto, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM fattura_attiva_notifica_scarto WHERE id_notifica_scarto = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idNotificaScarto);
            return query.executeUpdate();
        }catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id " + idNotificaScarto);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
