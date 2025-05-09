package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.MailInToEntiAttivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.MailInToEntiAttivaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Created by agosteeno on 14/03/15.
 */
public class MailInToEntiAttivaManager {
    private static final Logger LOG = LoggerFactory.getLogger(MailInToEntiAttivaManager.class);

    private EntityManagerFactory entityManagerFactory;

    private MailInToEntiAttivaDao mailInToEntiAttivaDao;

    public EnteEntity getEnteFromIndirizzoEmail(String indirizzoEmail) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAException {

        EntityManager entityManager = null;
        EnteEntity enteEntity = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntity = mailInToEntiAttivaDao.getEnteByIndirizzoEmail(indirizzoEmail, entityManager);

        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch(FatturaPAEnteNonTrovatoException e){
            throw e;
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {

            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return enteEntity;
    }

    public List<MailInToEntiAttivaEntity> getMailInToEntiAttivaEntitiesByEnte(EnteEntity enteEntity) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAException {
        EntityManager entityManager = null;
        List<MailInToEntiAttivaEntity> mailInToEntiAttivaEntities = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            mailInToEntiAttivaEntities = mailInToEntiAttivaDao.getEmailInAttivaByEnte(enteEntity, entityManager);

        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch(FatturaPAEnteNonTrovatoException e){
            throw e;
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {

            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return mailInToEntiAttivaEntities;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public MailInToEntiAttivaDao getMailInToEntiAttivaDao() {
        return mailInToEntiAttivaDao;
    }

    public void setMailInToEntiAttivaDao(MailInToEntiAttivaDao mailInToEntiAttivaDao) {
        this.mailInToEntiAttivaDao = mailInToEntiAttivaDao;
    }
}
