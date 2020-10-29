package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaAttestazioneTrasmissioneFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaAttestazioneTrasmissioneFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaAttestazioneTrasmissioneFatturaPK;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class StatoAttivaAttestazioneTrasmissioneFatturaDao extends GenericDao<StatoAttivaAttestazioneTrasmissioneFatturaEntity, StatoAttivaAttestazioneTrasmissioneFatturaPK> {

    public int deleteStatoAttestazioneTrasmByIdTrasmissione(BigInteger idAttestazioneTrasmissioneFattura, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM stato_attiva_attestazione_trasmissione_fattura WHERE id_attestazione_trasmissione_fattura = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idAttestazioneTrasmissioneFattura);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idAttestazioneTrasmissioneFattura);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public StatoAttivaAttestazioneTrasmissioneFatturaEntity getByIdAttestazione(FatturaAttivaAttestazioneTrasmissioneFatturaEntity attestazione, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {

        TypedQuery<StatoAttivaAttestazioneTrasmissioneFatturaEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaAttestazioneTrasmissioneFatturaEntity sa WHERE sa.attestazioneTrasmissioneFatturaEntity = :attestazioneTrasmissioneFatturaEntity ORDER BY sa.data", StatoAttivaAttestazioneTrasmissioneFatturaEntity.class);
        query.setParameter("attestazioneTrasmissioneFatturaEntity", attestazione);
        try {
            List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> ent = query.getResultList();
            if (ent.size() == 0) {
                throw new NoResultException();
            }
            return ent.get(0);
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + attestazione.getIdAttestazioneTrasmissioneFattura());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }


    public List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> getListStatiByAttestazioneTrasmissione(FatturaAttivaAttestazioneTrasmissioneFatturaEntity attestazione, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            TypedQuery<StatoAttivaAttestazioneTrasmissioneFatturaEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaAttestazioneTrasmissioneFatturaEntity sa WHERE sa.attestazioneTrasmissioneFatturaEntity = :attestazioneTrasmissioneFatturaEntity ORDER BY sa.data", StatoAttivaAttestazioneTrasmissioneFatturaEntity.class);
            query.setParameter("attestazioneTrasmissioneFatturaEntity", attestazione);

            List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> ent = query.getResultList();
            return ent;
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + attestazione.getIdAttestazioneTrasmissioneFattura());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
