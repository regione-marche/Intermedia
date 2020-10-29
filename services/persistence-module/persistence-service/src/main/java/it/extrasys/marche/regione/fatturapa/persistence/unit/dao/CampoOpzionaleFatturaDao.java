package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACampoOpzionaleNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CampoOpzionaleFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;


public class CampoOpzionaleFatturaDao extends GenericDao<CampoOpzionaleFatturaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(CampoOpzionaleFatturaDao.class);

    public List<CampoOpzionaleFatturaEntity> getAll(EntityManager entityManager) throws FatturaPaPersistenceException {

        LOG.info("********** CampoOpzionaleFatturaDao: getAll **********");

        TypedQuery<CampoOpzionaleFatturaEntity> query = entityManager.createQuery("SELECT cof FROM CampoOpzionaleFatturaEntity cof ORDER BY cof.idCampo ASC", CampoOpzionaleFatturaEntity.class);

        try {
            return query.getResultList();
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException(e.getMessage());
        }

    }

    public CampoOpzionaleFatturaEntity getCampoOpzionaleFromDescCampo(String descCampo, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPACampoOpzionaleNonTrovatoException {

        LOG.info("********** EnteCampoOpzionaleAssociazioneDao: getCampoOpzionaleFromDescCampo **********");

        try {

            TypedQuery<CampoOpzionaleFatturaEntity> querySelect = entityManager.createQuery("SELECT c FROM CampoOpzionaleFatturaEntity c WHERE c.campo = :campo", CampoOpzionaleFatturaEntity.class);
            querySelect.setParameter("campo", descCampo);
            CampoOpzionaleFatturaEntity c = querySelect.getSingleResult();

            return c;
        } catch(NoResultException e){
            throw new FatturaPACampoOpzionaleNonTrovatoException(descCampo);
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException(e.getMessage());
        }
    }

}