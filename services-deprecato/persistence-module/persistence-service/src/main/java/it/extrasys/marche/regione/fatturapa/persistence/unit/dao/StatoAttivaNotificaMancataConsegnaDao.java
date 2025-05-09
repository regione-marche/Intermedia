package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaNotificaMancataConsegnaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaEsitoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaMancataConsegnaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaMancataConsegnaPK;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class StatoAttivaNotificaMancataConsegnaDao extends GenericDao<StatoAttivaNotificaMancataConsegnaEntity,StatoAttivaNotificaMancataConsegnaPK> {

    public int deleteStatoMancataConsegnaByIdMancataConsegna(BigInteger idNotificaMancataConsegna, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM stato_attiva_notifica_mancata_consegna WHERE id_notifica_mancata_consegna = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idNotificaMancataConsegna);
            return query.executeUpdate();
        }catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idNotificaMancataConsegna);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public StatoAttivaNotificaMancataConsegnaEntity getByIdNotificaMancataConsegna(FatturaAttivaNotificaMancataConsegnaEntity notificaMancata, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {

        TypedQuery<StatoAttivaNotificaMancataConsegnaEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaMancataConsegnaEntity sa WHERE sa.notificaMancataConsegnaEntity = :notificaMancataConsegnaEntity ORDER BY sa.data", StatoAttivaNotificaMancataConsegnaEntity.class);
        query.setParameter("notificaMancataConsegnaEntity", notificaMancata);
        try {
            List<StatoAttivaNotificaMancataConsegnaEntity> ent = query.getResultList();
            if(ent.size() == 0){
                throw new NoResultException();
            }
            return ent.get(0);
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaMancata.getIdNotificaMancataConsegna());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }

    public List<StatoAttivaNotificaMancataConsegnaEntity> getListStatiByMancataConsegna(FatturaAttivaNotificaMancataConsegnaEntity notificaMancata, EntityManager entityManager) throws FatturaPaPersistenceException {

        TypedQuery<StatoAttivaNotificaMancataConsegnaEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaNotificaMancataConsegnaEntity sa WHERE sa.notificaMancataConsegnaEntity = :notificaMancataConsegnaEntity ORDER BY sa.data", StatoAttivaNotificaMancataConsegnaEntity.class);
        query.setParameter("notificaMancataConsegnaEntity", notificaMancata);
        try {
            List<StatoAttivaNotificaMancataConsegnaEntity> ent = query.getResultList();

            return ent;
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + notificaMancata.getIdNotificaMancataConsegna());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

}
