package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaRicevutaConsegnaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaNotificaScartoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaRicevutaConsegnaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoAttivaRicevutaConsegnaPK;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class StatoAttivaRicevutaConsegnaDao extends GenericDao<StatoAttivaRicevutaConsegnaEntity, StatoAttivaRicevutaConsegnaPK> {


    public List<StatoAttivaRicevutaConsegnaEntity> getStatoRicevutaConsegnaFromRicevutaConsegna(FatturaAttivaRicevutaConsegnaEntity ricevutaConsegnaEntity, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<StatoAttivaRicevutaConsegnaEntity> statoAttivaRicevutaConsegnaEntityList = null;

        try {
            TypedQuery<StatoAttivaRicevutaConsegnaEntity> query = entityManager.createQuery("SELECT statoAttivaRicevutaConsegna FROM StatoAttivaRicevutaConsegnaEntity statoAttivaRicevutaConsegna " +
                    "WHERE statoAttivaRicevutaConsegna.ricevutaConsegnaEntity = :ricevutaConsegnaEntity ORDER BY statoAttivaRicevutaConsegna.data DESC", StatoAttivaRicevutaConsegnaEntity.class);
            query.setParameter("ricevutaConsegnaEntity", ricevutaConsegnaEntity);

            statoAttivaRicevutaConsegnaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return statoAttivaRicevutaConsegnaEntityList;
    }

    public int deleteStatoRicevutaconsegnaByIdFatturaAttiva(BigInteger idRicevutaConsegna, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM stato_attiva_ricevuta_consegna WHERE id_ricevuta_consegna = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, idRicevutaConsegna);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + idRicevutaConsegna);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public StatoAttivaRicevutaConsegnaEntity getByIdRicevutaConsegna(FatturaAttivaRicevutaConsegnaEntity ricevutaConsegna, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {

        TypedQuery<StatoAttivaRicevutaConsegnaEntity> query = entityManager.createQuery("SELECT sa FROM StatoAttivaRicevutaConsegnaEntity sa WHERE sa.ricevutaConsegnaEntity = :ricevutaConsegnaEntity ORDER BY sa.data", StatoAttivaRicevutaConsegnaEntity.class);
        query.setParameter("ricevutaConsegnaEntity", ricevutaConsegna);
        try {
            List<StatoAttivaRicevutaConsegnaEntity> ent = query.getResultList();
            if(ent.size() == 0){
                throw new NoResultException();
            }
            return ent.get(0);
        } catch (NoResultException e) {
            throw new NoResultException("Nessuna notifica trovata per  " + ricevutaConsegna.getIdRicevutaConsegna());
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

    }


    public List<StatoAttivaRicevutaConsegnaEntity> getListStatiByRicevutaConsegna(FatturaAttivaRicevutaConsegnaEntity ricevutaConsegna, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<StatoAttivaRicevutaConsegnaEntity> statoAttivaRicevutaConsegnaEntityList = null;

        try {
            String queryString="SELECT sa FROM StatoAttivaRicevutaConsegnaEntity sa WHERE sa.ricevutaConsegnaEntity = :ricevutaConsegnaEntity ORDER BY sa.data";

            TypedQuery<StatoAttivaRicevutaConsegnaEntity> query = entityManager.createQuery(queryString, StatoAttivaRicevutaConsegnaEntity.class);
            query.setParameter("ricevutaConsegnaEntity", ricevutaConsegna);

            statoAttivaRicevutaConsegnaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return statoAttivaRicevutaConsegnaEntityList;
    }
}
