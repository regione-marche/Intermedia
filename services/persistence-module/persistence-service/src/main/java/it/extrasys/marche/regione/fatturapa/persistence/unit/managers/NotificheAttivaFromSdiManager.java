package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificheAttivaFromSdiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.TipoNotificaAttivaFromSdiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.NotificheAttivaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 21/03/15.
 */
public class NotificheAttivaFromSdiManager {

    private static final Logger LOG = LoggerFactory.getLogger(NotificheAttivaFromSdiManager.class);

    private EntityManagerFactory entityManagerFactory;

    private NotificheAttivaFromSdiDao notificheAttivaFromSdiDao;

    private TipoNotificaAttivaFromSdiDao tipoNotificaAttivaFromSdiDao;

    public void salvaNotificaFromSdi(BigInteger identificativoSdi, String nomeFile, String tipoMessaggio, String messaggioOriginale) throws FatturaPAException {
        EntityManager entityManager = null;

        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = new NotificheAttivaFromSdiEntity();

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            TipoNotificaAttivaFromSdiEntity tipoNotificaAttivaFromSdiEntity = tipoNotificaAttivaFromSdiDao.read(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.parse(tipoMessaggio), entityManager);

            notificheAttivaFromSdiEntity.setIdentificativoSdi(identificativoSdi);
            notificheAttivaFromSdiEntity.setNomeFile(nomeFile);
            notificheAttivaFromSdiEntity.setTipoNotificaAttivaFromSdiEntity(tipoNotificaAttivaFromSdiEntity);
            notificheAttivaFromSdiEntity.setOriginalMessage(messaggioOriginale);
            notificheAttivaFromSdiEntity.setDataRicezioneRispostaSDI(new Date());

            notificheAttivaFromSdiDao.create(notificheAttivaFromSdiEntity, entityManager);

            entityManager.getTransaction().commit();

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
    }

    public NotificheAttivaFromSdiEntity getNotificaAttivaFromIdentificativSdi(BigInteger identificativoSdi, TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI tipo_notifica_from_sdi) throws FatturaPAException, FatturaPaPersistenceException {

        EntityManager entityManager = null;
        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            notificheAttivaFromSdiEntity = notificheAttivaFromSdiDao.getNotificaAttivaFromIdentificativoSdi(identificativoSdi, tipo_notifica_from_sdi.getValue(), entityManager);

        } catch (FatturaPaPersistenceException e) {
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

        return notificheAttivaFromSdiEntity;
    }

    public NotificheAttivaFromSdiEntity getNotificaAttivaFromIdSdi(BigInteger identificativoSdi) throws FatturaPAException, FatturaPaPersistenceException {

        EntityManager entityManager = null;
        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            notificheAttivaFromSdiEntity = notificheAttivaFromSdiDao.getNotificaAttivaFromIdSdi(identificativoSdi, entityManager);

        } catch (FatturaPaPersistenceException e) {
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

        return notificheAttivaFromSdiEntity;
    }

    public List<NotificheAttivaFromSdiEntity> getNotificheAttivaByEnte(EnteEntity ente) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<NotificheAttivaFromSdiEntity> notificheAttivaFromSdiEntity = new ArrayList<>();

        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<Object[]> resultSet = notificheAttivaFromSdiDao.getNotificheAttivaByEnte(ente.getIdEnte(), entityManager);

            for (Object[] obj : resultSet) {
                ((NotificheAttivaFromSdiEntity) obj[0]).setIdFatturaAttiva((BigInteger) obj[1]);
                notificheAttivaFromSdiEntity.add((NotificheAttivaFromSdiEntity) obj[0]);
            }
        } catch (FatturaPaPersistenceException e) {
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

        return notificheAttivaFromSdiEntity;
    }

    public NotificheAttivaFromSdiEntity salvaRicevutaComunicazioneNotificaAttivaFromSdI(BigInteger identificativoSdI, String tipoNotificaFromSdi, String ricevutaComunicazione) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            //notificheAttivaFromSdiEntity = notificheAttivaFromSdiDao.getNotificaAttivaFromIdentificativoSdi(identificativoSdI, tipoNotificaFromSdi, entityManager);

            notificheAttivaFromSdiEntity = notificheAttivaFromSdiDao.getNotificaAttivaFromSdIByIdentificativiSdI(identificativoSdI, tipoNotificaFromSdi, entityManager);

            notificheAttivaFromSdiEntity.setRicevutaComunicazione(ricevutaComunicazione);

            notificheAttivaFromSdiDao.update(notificheAttivaFromSdiEntity, entityManager);

            entityManager.getTransaction().commit();

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

        return notificheAttivaFromSdiEntity;
    }


    public List<Object[]> getNotificheAttiveAfterDate(String interval) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<Object[]> result = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            result = notificheAttivaFromSdiDao.getNotificheAttiveAfterDate(interval, entityManager);
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

        return result;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public NotificheAttivaFromSdiDao getNotificheAttivaFromSdiDao() {
        return notificheAttivaFromSdiDao;
    }

    public void setNotificheAttivaFromSdiDao(NotificheAttivaFromSdiDao notificheAttivaFromSdiDao) {
        this.notificheAttivaFromSdiDao = notificheAttivaFromSdiDao;
    }

    public TipoNotificaAttivaFromSdiDao getTipoNotificaAttivaFromSdiDao() {
        return tipoNotificaAttivaFromSdiDao;
    }

    public void setTipoNotificaAttivaFromSdiDao(TipoNotificaAttivaFromSdiDao tipoNotificaAttivaFromSdiDao) {
        this.tipoNotificaAttivaFromSdiDao = tipoNotificaAttivaFromSdiDao;
    }
}
