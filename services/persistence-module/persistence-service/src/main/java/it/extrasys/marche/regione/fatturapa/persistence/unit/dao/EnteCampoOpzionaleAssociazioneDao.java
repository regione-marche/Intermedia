package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACampoOpzionaleNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CampoOpzionaleFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteCampoOpzionaleAssociazioneEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

public class EnteCampoOpzionaleAssociazioneDao extends GenericDao<EnteCampoOpzionaleAssociazioneEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(EnteCampoOpzionaleAssociazioneDao.class);

    public List<EnteCampoOpzionaleAssociazioneEntity> getAssociazioniFromEnte(EnteEntity enteEntity, EntityManager entityManager) throws FatturaPaPersistenceException {

        LOG.info("********** EnteCampoOpzionaleAssociazioneDao: getAssociazioniFromEnte **********");

        TypedQuery<EnteCampoOpzionaleAssociazioneEntity> query = entityManager.createQuery("SELECT eco FROM EnteCampoOpzionaleAssociazioneEntity eco WHERE eco.ente = :ente ORDER BY eco.campoOpzionale.idCampo ASC", EnteCampoOpzionaleAssociazioneEntity.class);
        query.setParameter("ente", enteEntity);
        try {
            return query.getResultList();
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException(e.getMessage());
        }
    }


}