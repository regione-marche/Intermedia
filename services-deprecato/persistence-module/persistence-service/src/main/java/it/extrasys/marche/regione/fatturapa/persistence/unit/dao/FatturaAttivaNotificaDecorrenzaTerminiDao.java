package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoFatturaAttivaEntity;
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
public class FatturaAttivaNotificaDecorrenzaTerminiDao extends GenericDao<FatturaAttivaNotificaDecorrenzaTerminiEntity, BigInteger> {

    public List<BigInteger> getIdFatturaAttivaNotificaDecorrenzaTerminiUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<BigInteger> list = null;

        try {
            String queryString = "SELECT fandt.fatturaAttiva.idFatturaAttiva FROM StatoAttivaNotificaDecorrenzaTerminiEntity sandt JOIN sandt.notificaDecorrenzaTerminiEntity fandt " +
                    " WHERE sandt.stato.codStato = '001' AND  fandt.fatturaAttiva.idFatturaAttiva IN ( :idFattureAttive ) " +
                    " AND sandt.data = ( SELECT MAX(sandt1.data) FROM StatoAttivaNotificaDecorrenzaTerminiEntity sandt1 JOIN sandt1.notificaDecorrenzaTerminiEntity fandt1 " +
                    " WHERE fandt1.idNotificaDecorrenzaTermini = fandt.idNotificaDecorrenzaTermini)";

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


    public FatturaAttivaNotificaDecorrenzaTerminiEntity getFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        FatturaAttivaNotificaDecorrenzaTerminiEntity result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaDecorrenzaTerminiEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaDecorrenzaTerminiEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = (FatturaAttivaNotificaDecorrenzaTerminiEntity) query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }

    public List<FatturaAttivaNotificaDecorrenzaTerminiEntity> getListFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<FatturaAttivaNotificaDecorrenzaTerminiEntity> result = null;
        try {
            String queryString = "SELECT rc FROM FatturaAttivaNotificaDecorrenzaTerminiEntity rc JOIN rc.fatturaAttiva fa WHERE fa.idFatturaAttiva = :idFatturaAttiva ORDER BY rc.data DESC";
            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, FatturaAttivaNotificaDecorrenzaTerminiEntity.class));
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            result = query.getResultList();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFatturaAttiva);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }

        return result;
    }



    public List<StatoAttivaNotificaDecorrenzaTerminiEntity> getStatoFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(BigInteger idFattureAttive, EntityManager entityManager) throws FatturaPaPersistenceException {
        List<StatoAttivaNotificaDecorrenzaTerminiEntity> list = null;

        try {
            String queryString = "SELECT sandt FROM StatoAttivaNotificaDecorrenzaTerminiEntity sandt JOIN sandt.notificaDecorrenzaTerminiEntity fandt " +
                    " WHERE fandt.fatturaAttiva.idFatturaAttiva = ( :idFattureAttive )";

            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, StatoAttivaNotificaDecorrenzaTerminiEntity.class));
            query.setParameter("idFattureAttive", idFattureAttive);
            list = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idFattureAttive);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return list;
    }


    public int deleteNotificaDecTerminiById(BigInteger idDecTermini, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM fattura_attiva_notifica_decorrenza_termini WHERE id_notifica_decorrenza_termini = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idDecTermini);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id  " + idDecTermini);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }
}
