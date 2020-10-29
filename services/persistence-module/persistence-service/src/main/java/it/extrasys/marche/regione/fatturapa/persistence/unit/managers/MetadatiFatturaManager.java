package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.MetadatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;

/**
 * Created by agosteeno on 27/03/15.
 */
public class MetadatiFatturaManager {

    private static final Logger LOG = LoggerFactory.getLogger(DatiFatturaManager.class);

    private EntityManagerFactory entityManagerFactory;

    private MetadatiFatturaDao metadatiFatturaDao;

    public MetadatiFatturaEntity getMetadatiByIdentificativoSdi(BigInteger identificativoSdi) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        MetadatiFatturaEntity metadatiFatturaEntity = null;
        try {

            metadatiFatturaEntity = metadatiFatturaDao.getMetadatiByIdentificativoSdi(identificativoSdi, entityManager);

        } catch (FatturaPaPersistenceException e){
            throw e;
        } catch (FatturaPAEnteNonTrovatoException e){
            throw e;
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return metadatiFatturaEntity;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public MetadatiFatturaDao getMetadatiFatturaDao() {
        return metadatiFatturaDao;
    }

    public void setMetadatiFatturaDao(MetadatiFatturaDao metadatiFatturaDao) {
        this.metadatiFatturaDao = metadatiFatturaDao;
    }
}
