package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaDecorrenzaTerminiPK;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class StatoAttivaNotificaDecorrenzaTerminiDao extends GenericDao<StatoAttivaNotificaDecorrenzaTerminiEntity, StatoAttivaNotificaDecorrenzaTerminiPK> {

    public int deleteStatoNotificadecTerminiByIdDecTermini(BigInteger idNotificaDecorrenzaTermini, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM stato_attiva_notifica_decorrenza_termini WHERE id_notifica_decorrenza_termini = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idNotificaDecorrenzaTermini);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idNotificaDecorrenzaTermini);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public StatoAttivaNotificaDecorrenzaTerminiEntity getByIdNotificaDecorrenza(FatturaAttivaNotificaDecorrenzaTerminiEntity notificaDecorrenza, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {

        TypedQuery<StatoAttivaNotificaDecorrenzaTerminiEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaDecorrenzaTerminiEntity sa WHERE sa.notificaDecorrenzaTerminiEntity = :notificaDecorrenzaTerminiEntity ORDER BY sa.data DESC", StatoAttivaNotificaDecorrenzaTerminiEntity.class);
        query.setParameter("notificaDecorrenzaTerminiEntity", notificaDecorrenza);
        try {
            List<StatoAttivaNotificaDecorrenzaTerminiEntity> ent = query.getResultList();
            if (ent.size() == 0) {
                throw new NoResultException();
            }
            return ent.get(0);
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaDecorrenza.getIdNotificaDecorrenzaTermini());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }


    public List<StatoAttivaNotificaDecorrenzaTerminiEntity> getListStatiByNotificaDecTermini(FatturaAttivaNotificaDecorrenzaTerminiEntity notificaDecorrenza, EntityManager entityManager) throws FatturaPaPersistenceException {

        TypedQuery<StatoAttivaNotificaDecorrenzaTerminiEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaDecorrenzaTerminiEntity sa WHERE sa.notificaDecorrenzaTerminiEntity = :notificaDecorrenzaTerminiEntity ORDER BY sa.data DESC", StatoAttivaNotificaDecorrenzaTerminiEntity.class);
        query.setParameter("notificaDecorrenzaTerminiEntity", notificaDecorrenza);
        try {
            List<StatoAttivaNotificaDecorrenzaTerminiEntity> ent = query.getResultList();
            return ent;
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaDecorrenza.getIdNotificaDecorrenzaTermini());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
