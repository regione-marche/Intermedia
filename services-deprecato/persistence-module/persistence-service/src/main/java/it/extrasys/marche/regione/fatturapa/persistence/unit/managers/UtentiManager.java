package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonAutorizzatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.ChiaveDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.UtenteEnteDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.UtentiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.UtentiServizioDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.UtenteEnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteServizioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UtentiManager {

    private static final Logger LOG = LoggerFactory.getLogger(UtentiManager.class);

    private EntityManagerFactory entityManagerFactory;
    private UtentiDao utentiDao;
    private UtentiServizioDao utentiServizioDao;
    private ChiaveDao chiaveDao;
    private UtenteEnteDao utenteEnteDao;

    public UtenteEntity getUtenteByUsername(String usernameUtente) throws FatturaPAException, FatturaPAUtenteNonTrovatoException {

        LOG.info("********** UtentiManager: getUtenteByUsername **********");

        EntityManager entityManager = null;
        UtenteEntity utenteEntity;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            utenteEntity = utentiDao.getUtenteByUsername(usernameUtente, entityManager);


        } catch (FatturaPAUtenteNonTrovatoException e) {
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

        return utenteEntity;
    }

    public List<UtenteEntity> getAllUsers() throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        LOG.info("********** UtentiManager: getUtenteByUsername **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            List<UtenteEntity> utenteEntityList = utentiDao.getAllUsers(entityManager);

            return utenteEntityList;

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException(e.getMessage(), e);
        } catch (FatturaPAUtenteNonTrovatoException e) {
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
    }

    public void deleteCollegamentoUtenteEnte(UtenteEnteEntity utenteEnteEntity) throws FatturaPAException {

        LOG.info("********** UtentiManager: deleteCollegamentoUtenteEnte **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            utenteEnteDao.delete(utenteEnteEntity, entityManager);

            entityTransaction.commit();

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

    public void updateUtente(UtenteEntity utenteEntity, boolean cifraPsw) throws FatturaPAException {

        LOG.info("********** UtentiManager: updateUtente **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            if(cifraPsw) {
                String psw = utenteEntity.getPassword();
                utenteEntity.setPassword(CommonUtils.encryptPassword(psw, chiaveDao.getChiave(entityManager)));
            }

            utentiDao.update(utenteEntity, entityManager);

            entityTransaction.commit();

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

    public UtenteEntity createUtente(UtenteEntity utenteEntity) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        LOG.info("********** UtentiManager: createUtente **********");

        EntityManager entityManager = null;


        try {

            entityManager = entityManagerFactory.createEntityManager();


            try {
                UtenteEntity utenteEntity2 = utentiDao.getUtenteByUsername(utenteEntity.getUsername(), entityManager);

                if(utenteEntity2 != null){
                    throw new FatturaPAException("username "+utenteEntity.getUsername()+" già presente!");
                }

            } catch (FatturaPAUtenteNonTrovatoException e){
                EntityTransaction entityTransaction = entityManager.getTransaction();
                entityTransaction.begin();

                String psw = utenteEntity.getPassword();
                utenteEntity.setPassword(CommonUtils.encryptPassword(psw, chiaveDao.getChiave(entityManager)));
                utentiDao.createUtente(utenteEntity, entityManager);

                entityTransaction.commit();
            }


        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException(e.getMessage(), e);
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

        return utenteEntity;
    }

    public void createUtenteEnte(UtenteEnteEntity utenteEnteEntity) throws FatturaPAException {

        LOG.info("********** UtentiManager: createUtenteEnte **********");

        EntityManager entityManager = null;

        try{
        entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        utenteEnteDao.create(utenteEnteEntity, entityManager);

        entityTransaction.commit();


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

    public UtenteServizioEntity getUtenteServizioByUsername(String usernameUtente) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        LOG.info("********** UtentiManager: getUtenteServizioByUsername **********");

        EntityManager entityManager = null;
        UtenteServizioEntity utenteServizioEntity;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            utenteServizioEntity = utentiServizioDao.getUtenteByUsername(usernameUtente, entityManager);


        } catch (FatturaPAUtenteNonTrovatoException e) {
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

        return utenteServizioEntity;
    }

    public List<UtenteEnteEntity> getUtenteEnteFromUtente(UtenteEntity utenteEntity) throws FatturaPAException {

        LOG.info("********** UtentiManager: getUtenteEnteFromUtente **********");

        EntityManager entityManager = null;


        try {

            entityManager = entityManagerFactory.createEntityManager();

            List<UtenteEnteEntity> utenteEnteEntityList = utenteEnteDao.getUtenteEnteFromUtente(utenteEntity, entityManager);

            return utenteEnteEntityList;

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

    public List<UtenteEnteEntity> getUtenteEnteFromEnte(EnteEntity enteEntity) throws FatturaPAException {

        LOG.info("********** UtentiManager: getUtenteEnteFromEnte **********");

        EntityManager entityManager = null;


        try {

            entityManager = entityManagerFactory.createEntityManager();

            List<UtenteEnteEntity> utenteEnteEntityList = utenteEnteDao.getUtenteEnteFromEnte(enteEntity, entityManager);

            return utenteEnteEntityList;

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

    public String encryptPassword(String psw) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        LOG.info("********** UtentiManager: encryptPassword **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            return CommonUtils.encryptPassword(psw, chiaveDao.getChiave(entityManager));

        } catch(IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw e;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public String decryptPassword(String enc) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        LOG.info("********** UtentiManager: decryptPassword **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            return CommonUtils.decryptPassword(enc, chiaveDao.getChiave(entityManager));

        } catch(IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw e;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public boolean checkAuth(UtenteEnteEntity utenteEnteEntity) {
        LOG.info("********** UtentiManager: checkAuth **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();
        return utenteEnteDao.checkAuth(utenteEnteEntity, entityManager);
    }

    public boolean checkAuth(UtenteEnteEntity utenteEnteEntity, boolean justAdmin) {
        LOG.info("********** UtentiManager: checkAuth **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();
        return utenteEnteDao.checkAuth(utenteEnteEntity, justAdmin, entityManager);
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public UtentiDao getUtentiDao() {
        return utentiDao;
    }

    public void setUtentiDao(UtentiDao utentiDao) {
        this.utentiDao = utentiDao;
    }

    public UtentiServizioDao getUtentiServizioDao() {
        return utentiServizioDao;
    }

    public void setUtentiServizioDao(UtentiServizioDao utentiServizioDao) {
        this.utentiServizioDao = utentiServizioDao;
    }

    public ChiaveDao getChiaveDao() {
        return chiaveDao;
    }

    public void setChiaveDao(ChiaveDao chiaveDao) {
        this.chiaveDao = chiaveDao;
    }

    public UtenteEnteDao getUtenteEnteDao() {
        return utenteEnteDao;
    }

    public void setUtenteEnteDao(UtenteEnteDao utenteEnteDao) {
        this.utenteEnteDao = utenteEnteDao;
    }
}
