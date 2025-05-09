package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaScartoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaMancataConsegnaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaScartoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaScartoPK;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class StatoAttivaNotificaScartoDao extends GenericDao<StatoAttivaNotificaScartoEntity, StatoAttivaNotificaScartoPK> {

    public int deleteStatoNotificaScartoByIdNotificaScarto(BigInteger idNotificaScarto, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM stato_attiva_notifica_scarto WHERE id_notifica_scarto = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idNotificaScarto);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idNotificaScarto);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public StatoAttivaNotificaScartoEntity getByIdNotificaScarto(FatturaAttivaNotificaScartoEntity notificaScarto, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {

        TypedQuery<StatoAttivaNotificaScartoEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaScartoEntity sa WHERE sa.notificaScartoEntity = :notificaScartoEntity ORDER BY sa.data DESC", StatoAttivaNotificaScartoEntity.class);
        query.setParameter("notificaScartoEntity", notificaScarto);
        try {
            List<StatoAttivaNotificaScartoEntity> ent = query.getResultList();
            if (ent.size() == 0) {
                throw new NoResultException();
            }
            return ent.get(0);
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaScarto.getIdNotificaScarto());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }


    public List<StatoAttivaNotificaScartoEntity> getListStatiByNotificaScarto(FatturaAttivaNotificaScartoEntity notificaScarto, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            TypedQuery<StatoAttivaNotificaScartoEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaScartoEntity sa WHERE sa.notificaScartoEntity = :notificaScartoEntity ORDER BY sa.data DESC", StatoAttivaNotificaScartoEntity.class);
            query.setParameter("notificaScartoEntity", notificaScarto);
            List<StatoAttivaNotificaScartoEntity> ent = query.getResultList();

            return ent;
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaScarto.getIdNotificaScarto());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }
}
