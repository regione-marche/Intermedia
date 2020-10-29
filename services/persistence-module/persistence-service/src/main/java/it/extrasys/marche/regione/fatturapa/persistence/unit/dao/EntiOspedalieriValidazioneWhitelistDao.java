package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntiOspedalieriValidazioneWhitelistEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;

/**
 * Created by agosteeno on 24/11/15.
 */
public class EntiOspedalieriValidazioneWhitelistDao extends GenericDao<EntiOspedalieriValidazioneWhitelistEntity,BigInteger>{

    public EntiOspedalieriValidazioneWhitelistEntity getEnteByIdFiscaleCedente(String idFiscaleCedente, EntityManager entityManager) throws FatturaPaPersistenceException {

        EntiOspedalieriValidazioneWhitelistEntity entiOspedalieriValidazioneWhitelistEntity = null;

        try {
            TypedQuery<EntiOspedalieriValidazioneWhitelistEntity> query = entityManager.createQuery("SELECT ente FROM EntiOspedalieriValidazioneWhitelistEntity ente WHERE ente.idFiscaleCedente = :idFiscaleCedente", EntiOspedalieriValidazioneWhitelistEntity.class);
            query.setParameter("idFiscaleCedente", idFiscaleCedente);

            entiOspedalieriValidazioneWhitelistEntity = query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException();
        }

        return entiOspedalieriValidazioneWhitelistEntity;
    }
}
