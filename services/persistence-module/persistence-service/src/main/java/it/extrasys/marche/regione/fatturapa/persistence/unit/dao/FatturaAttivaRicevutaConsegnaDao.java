package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaRicevutaConsegnaEntity;
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
public class FatturaAttivaRicevutaConsegnaDao extends GenericDao<FatturaAttivaRicevutaConsegnaEntity, BigInteger> {

    public List<BigInteger> getIdFatturaAttivaRicevutaConsegnaUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<BigInteger> list = null;

        try {
            String queryString = "SELECT fandt.fatturaAttiva.idFatturaAttiva FROM StatoAttivaRicevutaConsegnaEntity sandt JOIN sandt.ricevutaConsegnaEntity fandt " +
                    " WHERE sandt.stato.codStato = '001' AND  fandt.fatturaAttiva.idFatturaAttiva IN ( :idFattureAttive ) " +
                    " AND sandt.data = ( SELECT MAX(sandt1.data) FROM StatoAttivaRicevutaConsegnaEntity sandt1 JOIN sandt1.ricevutaConsegnaEntity fandt1 " +
                    " WHERE fandt1.idRicevutaConsegna = fandt.idRicevutaConsegna)";

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


    public FatturaAttivaRicevutaConsegnaEntity getFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        FatturaAttivaRicevutaConsegnaEntity result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaRicevutaConsegnaEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaRicevutaConsegnaEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = (FatturaAttivaRicevutaConsegnaEntity) query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public FatturaAttivaRicevutaConsegnaEntity getFatturaAttivaRicevutaConsegnaFatturaAttiva(FatturaAttivaEntity fatturaAttivaEntity, EntityManager entityManager) throws FatturaPaPersistenceException {
        FatturaAttivaRicevutaConsegnaEntity result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaRicevutaConsegnaEntity rc WHERE rc.fatturaAttiva = :fatturaAttiva ";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaRicevutaConsegnaEntity.class));
            query.setParameter("fatturaAttiva", fatturaAttivaEntity);

            result = (FatturaAttivaRicevutaConsegnaEntity) query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + fatturaAttivaEntity.getIdFatturaAttiva());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public List<FatturaAttivaRicevutaConsegnaEntity> getListFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<FatturaAttivaRicevutaConsegnaEntity> result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaRicevutaConsegnaEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ORDER BY rc.data DESC";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaRicevutaConsegnaEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = query.getResultList();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public int deleteRicevutaConsegnaById(BigInteger idRicevutaConsegna, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM fattura_attiva_ricevuta_consegna WHERE id_ricevuta_consegna = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idRicevutaConsegna);
            return query.executeUpdate();
        }catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id " + idRicevutaConsegna);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
