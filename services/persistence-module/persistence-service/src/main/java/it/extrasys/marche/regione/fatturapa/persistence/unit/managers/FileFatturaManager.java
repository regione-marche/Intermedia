package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.FileFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;

public class FileFatturaManager {
    private static final Logger LOG = LoggerFactory.getLogger(FileFatturaManager.class);

    private EntityManagerFactory entityManagerFactory;

    private FileFatturaDao fileFatturaDao;


    public FileFatturaEntity getFileFatturaByIdFileFattura(BigInteger idFilefattura) throws FatturaPAFatturaNonTrovataException {
        EntityManager entityManager = null;
        FileFatturaEntity file = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            file = fileFatturaDao.getFileFatturaByIdFilefattura(idFilefattura, entityManager);

        } catch (Exception e) {
            throw new FatturaPAFatturaNonTrovataException(e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return file;
    }

    public FileFatturaEntity getFileFatturaByNomeFileFattura(String nomeFileFattura) throws FatturaPAFatturaNonTrovataException {
        EntityManager entityManager = null;
        FileFatturaEntity file = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            file = fileFatturaDao.getFileFatturaByNomeFilefattura(nomeFileFattura, entityManager);

        } catch (Exception e) {
            throw new FatturaPAFatturaNonTrovataException(e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return file;
    }

    public FileFatturaEntity getFileFatturaByIdentificativoSdi(BigInteger identificativoSdi) throws FatturaPAFatturaNonTrovataException {
        EntityManager entityManager = null;
        FileFatturaEntity file = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            file = fileFatturaDao.getFileFatturaByIdentificativiSdI(identificativoSdi, entityManager);

        } catch (Exception e) {
            throw new FatturaPAFatturaNonTrovataException(e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return file;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FileFatturaDao getFileFatturaDao() {
        return fileFatturaDao;
    }

    public void setFileFatturaDao(FileFatturaDao fileFatturaDao) {
        this.fileFatturaDao = fileFatturaDao;
    }
}
