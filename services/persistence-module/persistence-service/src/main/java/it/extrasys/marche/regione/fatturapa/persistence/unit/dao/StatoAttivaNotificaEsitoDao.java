package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaEsitoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaEsitoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaEsitoPK;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class StatoAttivaNotificaEsitoDao extends GenericDao<StatoAttivaNotificaEsitoEntity,StatoAttivaNotificaEsitoPK> {

    public int deleteStatoEsitoByIdNotificaEsito(BigInteger idNotificaEsito, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM stato_attiva_notifica_esito WHERE id_notifica_esito = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idNotificaEsito);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idNotificaEsito);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public StatoAttivaNotificaEsitoEntity getByIdNotificaEsito(FatturaAttivaNotificaEsitoEntity notificaEsito, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {

        TypedQuery<StatoAttivaNotificaEsitoEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaEsitoEntity sa WHERE sa.notificaEsitoEntity = :notificaEsitoEntity ORDER BY sa.data DESC", StatoAttivaNotificaEsitoEntity.class);
        query.setParameter("notificaEsitoEntity", notificaEsito);
        try {
            List<StatoAttivaNotificaEsitoEntity> ent = query.getResultList();
            if(ent.size() == 0){
                throw new NoResultException();
            }
            return ent.get(0);
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaEsito.getIdNotificaEsito());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }

    public List<StatoAttivaNotificaEsitoEntity> getListStatiByNotificaEsito(FatturaAttivaNotificaEsitoEntity notificaEsito, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
        TypedQuery<StatoAttivaNotificaEsitoEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaEsitoEntity sa WHERE sa.notificaEsitoEntity = :notificaEsitoEntity ORDER BY sa.data DESC", StatoAttivaNotificaEsitoEntity.class);
        query.setParameter("notificaEsitoEntity", notificaEsito);

            List<StatoAttivaNotificaEsitoEntity> ent = query.getResultList();
            return ent;
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaEsito.getIdNotificaEsito());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

}
