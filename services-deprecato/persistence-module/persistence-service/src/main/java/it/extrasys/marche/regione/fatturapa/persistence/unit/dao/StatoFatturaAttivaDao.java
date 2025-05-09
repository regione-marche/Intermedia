package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoFatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoFatturaAttivaPK;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

public class StatoFatturaAttivaDao extends GenericDao<StatoFatturaAttivaEntity,StatoFatturaAttivaPK> {

    public List<StatoFatturaAttivaEntity> getStatoFromIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<StatoFatturaAttivaEntity> statoFatturaAttivaEntityList = null;

        try {
            TypedQuery<StatoFatturaAttivaEntity> query = entityManager.createQuery("SELECT statoFatturaAttiva FROM StatoFatturaAttivaEntity statoFatturaAttiva " +
                    "WHERE statoFatturaAttiva.fatturaAttiva.idFatturaAttiva = :idFatturaAttiva ORDER BY statoFatturaAttiva.data DESC", StatoFatturaAttivaEntity.class);
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            statoFatturaAttivaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException();
        }

        return statoFatturaAttivaEntityList;
    }

}
