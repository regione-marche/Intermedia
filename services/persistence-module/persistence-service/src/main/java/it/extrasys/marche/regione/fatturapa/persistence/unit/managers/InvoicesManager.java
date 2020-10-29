package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.util.List;

public class InvoicesManager {

    private static final Logger LOG = LoggerFactory.getLogger(InvoicesManager.class);

    private EntityManagerFactory entityManagerFactory;

    private FatturaAttivaNotificaEsitoDao fatturaAttivaNotificaEsitoDao;

    private FatturaAttivaNotificaDecorrenzaTerminiDao fatturaAttivaNotificaDecorrenzaTerminiDao;

    private FatturaAttivaAttestaioneTrasmissioneFatturaDao fatturaAttivaAttestaioneTrasmissioneFatturaDao;

    private FatturaAttivaNotificaMancataConsegnaDao fatturaAttivaNotificaMancataConsegnaDao;

    private FatturaAttivaNotificaScartoDao fatturaAttivaNotificaScartoDao;

    private FatturaAttivaRicevutaConsegnaDao fatturaAttivaRicevutaConsegnaDao;



    private StatoAttivaNotificaEsitoDao statoAttivaNotificaEsitoDao;

    private StatoAttivaNotificaDecorrenzaTerminiDao statoAttivaNotificaDecorrenzaTerminiDao;

    private StatoAttivaAttestazioneTrasmissioneFatturaDao statoAttivaAttestazioneTrasmissioneFatturaDao;

    private StatoAttivaNotificaMancataConsegnaDao statoAttivaNotificaMancataConsegnaDao;

    private StatoAttivaNotificaScartoDao statoAttivaNotificaScartoDao;

    private StatoAttivaRicevutaConsegnaDao statoAttivaRicevutaConsegnaDao;

    private StatoFatturaAttivaDao statoFatturaAttivaDao;


    public List<StatoFatturaAttivaEntity> getStatoFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoFromIdFatturaAttiva **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoFatturaAttivaDao.getStatoFromIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        } catch (FatturaPAFatturaNonTrovataException e) {
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public StatoAttivaNotificaEsitoEntity getStatoAttivaNotificaEsitoEntityFromIdNotificaEsito(FatturaAttivaNotificaEsitoEntity fa) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoAttivaNotificaEsitoEntityFromIdNotificaEsito **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoAttivaNotificaEsitoDao.getByIdNotificaEsito(fa, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        }
        finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public StatoAttivaNotificaDecorrenzaTerminiEntity getStatoAttivaNotificaDecorrenzaTerminiEntityFromIdNotificaDecorrenza(FatturaAttivaNotificaDecorrenzaTerminiEntity notificaDecorrenza) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoAttivaNotificaDecorrenzaTerminiEntityFromIdNotificaDecorrenza **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoAttivaNotificaDecorrenzaTerminiDao.getByIdNotificaDecorrenza(notificaDecorrenza, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public StatoAttivaAttestazioneTrasmissioneFatturaEntity getStatoAttivaAttestazioneTrasmissioneFatturaEntityFromIdNotificaTrasmissione(FatturaAttivaAttestazioneTrasmissioneFatturaEntity notificaTrasmissione) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoAttivaAttestazioneTrasmissioneFatturaEntityFromIdNotificaTrasmissione **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoAttivaAttestazioneTrasmissioneFatturaDao.getByIdAttestazione(notificaTrasmissione, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public StatoAttivaNotificaMancataConsegnaEntity getStatoAttivaNotificaMancataConsegnaEntityFromIdNotificaMancata(FatturaAttivaNotificaMancataConsegnaEntity notificaMancata) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoAttivaNotificaMancataConsegnaEntityFromIdNotificaMancata **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoAttivaNotificaMancataConsegnaDao.getByIdNotificaMancataConsegna(notificaMancata, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public StatoAttivaNotificaScartoEntity getStatoAttivaNotificaScartoEntityFromIdNotificaScarto(FatturaAttivaNotificaScartoEntity notificaScarto) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoAttivaNotificaScartoEntityFromIdNotificaScarto **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoAttivaNotificaScartoDao.getByIdNotificaScarto(notificaScarto, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public StatoAttivaRicevutaConsegnaEntity getStatoAttivaRicevutaConsegnaFromIdRicevutaConsegna(FatturaAttivaRicevutaConsegnaEntity ricevutaConsegna) throws FatturaPaPersistenceException {
        LOG.info("********** InvoicesManager: getStatoAttivaRicevutaConsegnaFromIdRicevutaConsegna **********");
        EntityManager entityManager = null;
        entityManager = entityManagerFactory.createEntityManager();

        try {
            return statoAttivaRicevutaConsegnaDao.getByIdRicevutaConsegna(ricevutaConsegna, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public FatturaAttivaNotificaEsitoEntity getFatturaAttivaNotificaEsitoFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaEsitoFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaEsitoDao.getFatturaAttivaNotificaEsitoByIdFatturaAttiva(idFatturaAttiva, entityManager);


        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public List<FatturaAttivaNotificaEsitoEntity> getListFatturaAttivaNotificaEsitoFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaEsitoFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaEsitoDao.getListFatturaAttivaNotificaEsitoByIdFatturaAttiva(idFatturaAttiva, entityManager);


        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public FatturaAttivaNotificaDecorrenzaTerminiEntity getFatturaAttivaNotificaDecorrenzaTerminiFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaDecorrenzaTerminiFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaDecorrenzaTerminiDao.getFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(idFatturaAttiva, entityManager);

        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public List<FatturaAttivaNotificaDecorrenzaTerminiEntity> getListFatturaAttivaNotificaDecorrenzaTerminiFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaDecorrenzaTerminiFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaDecorrenzaTerminiDao.getListFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(idFatturaAttiva, entityManager);

        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public FatturaAttivaAttestazioneTrasmissioneFatturaEntity getFatturaAttivaAttestazioneTrasmissioneFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaAttestazioneTrasmissioneFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaAttestaioneTrasmissioneFatturaDao.getFatturaAttivaAttestazioneTrasmissioneByIdFatturaAttiva(idFatturaAttiva, entityManager);

        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public List<FatturaAttivaAttestazioneTrasmissioneFatturaEntity> getListFatturaAttivaAttestazioneTrasmissioneFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaAttestazioneTrasmissioneFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaAttestaioneTrasmissioneFatturaDao.getListFatturaAttivaAttestazioneTrasmissioneByIdFatturaAttiva(idFatturaAttiva, entityManager);

        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public FatturaAttivaNotificaMancataConsegnaEntity getFatturaAttivaNotificaMancataConsegnaFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaMancataConsegnaFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaMancataConsegnaDao.getFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public List<FatturaAttivaNotificaMancataConsegnaEntity> getListFatturaAttivaNotificaMancataConsegnaFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaMancataConsegnaFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaMancataConsegnaDao.getListFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        }  catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public FatturaAttivaNotificaScartoEntity getFatturaAttivaNotificaScartoFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaScartoFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaScartoDao.getFatturaAttivaNotificaScartoByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public List<FatturaAttivaNotificaScartoEntity> getListFatturaAttivaNotificaScartoFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaNotificaScartoFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {

            return fatturaAttivaNotificaScartoDao.getListFatturaAttivaNotificaScartoByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public List<FatturaAttivaRicevutaConsegnaEntity> getListFatturaAttivaRicevutaConsegnaFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPaPersistenceException {

        LOG.info("********** InvoicesManager: getFatturaAttivaRicevutaConsegnaFromIdFatturaAttiva **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();

        try {
            return fatturaAttivaRicevutaConsegnaDao.getListFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException e) {
            if(e.getMessage().contains("Nessuna")){
                return null;
            } else {
                throw e;
            }
        } catch (NoResultException e){
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FatturaAttivaNotificaEsitoDao getFatturaAttivaNotificaEsitoDao() {
        return fatturaAttivaNotificaEsitoDao;
    }

    public void setFatturaAttivaNotificaEsitoDao(FatturaAttivaNotificaEsitoDao fatturaAttivaNotificaEsitoDao) {
        this.fatturaAttivaNotificaEsitoDao = fatturaAttivaNotificaEsitoDao;
    }

    public FatturaAttivaNotificaDecorrenzaTerminiDao getFatturaAttivaNotificaDecorrenzaTerminiDao() {
        return fatturaAttivaNotificaDecorrenzaTerminiDao;
    }

    public void setFatturaAttivaNotificaDecorrenzaTerminiDao(FatturaAttivaNotificaDecorrenzaTerminiDao fatturaAttivaNotificaDecorrenzaTerminiDao) {
        this.fatturaAttivaNotificaDecorrenzaTerminiDao = fatturaAttivaNotificaDecorrenzaTerminiDao;
    }

    public FatturaAttivaAttestaioneTrasmissioneFatturaDao getFatturaAttivaAttestaioneTrasmissioneFatturaDao() {
        return fatturaAttivaAttestaioneTrasmissioneFatturaDao;
    }

    public void setFatturaAttivaAttestaioneTrasmissioneFatturaDao(FatturaAttivaAttestaioneTrasmissioneFatturaDao fatturaAttivaAttestaioneTrasmissioneFatturaDao) {
        this.fatturaAttivaAttestaioneTrasmissioneFatturaDao = fatturaAttivaAttestaioneTrasmissioneFatturaDao;
    }

    public FatturaAttivaNotificaMancataConsegnaDao getFatturaAttivaNotificaMancataConsegnaDao() {
        return fatturaAttivaNotificaMancataConsegnaDao;
    }

    public void setFatturaAttivaNotificaMancataConsegnaDao(FatturaAttivaNotificaMancataConsegnaDao fatturaAttivaNotificaMancataConsegnaDao) {
        this.fatturaAttivaNotificaMancataConsegnaDao = fatturaAttivaNotificaMancataConsegnaDao;
    }

    public FatturaAttivaNotificaScartoDao getFatturaAttivaNotificaScartoDao() {
        return fatturaAttivaNotificaScartoDao;
    }

    public void setFatturaAttivaNotificaScartoDao(FatturaAttivaNotificaScartoDao fatturaAttivaNotificaScartoDao) {
        this.fatturaAttivaNotificaScartoDao = fatturaAttivaNotificaScartoDao;
    }

    public FatturaAttivaRicevutaConsegnaDao getFatturaAttivaRicevutaConsegnaDao() {
        return fatturaAttivaRicevutaConsegnaDao;
    }

    public void setFatturaAttivaRicevutaConsegnaDao(FatturaAttivaRicevutaConsegnaDao fatturaAttivaRicevutaConsegnaDao) {
        this.fatturaAttivaRicevutaConsegnaDao = fatturaAttivaRicevutaConsegnaDao;
    }

    public StatoAttivaNotificaEsitoDao getStatoAttivaNotificaEsitoDao() {
        return statoAttivaNotificaEsitoDao;
    }

    public void setStatoAttivaNotificaEsitoDao(StatoAttivaNotificaEsitoDao statoAttivaNotificaEsitoDao) {
        this.statoAttivaNotificaEsitoDao = statoAttivaNotificaEsitoDao;
    }

    public StatoAttivaNotificaDecorrenzaTerminiDao getStatoAttivaNotificaDecorrenzaTerminiDao() {
        return statoAttivaNotificaDecorrenzaTerminiDao;
    }

    public void setStatoAttivaNotificaDecorrenzaTerminiDao(StatoAttivaNotificaDecorrenzaTerminiDao statoAttivaNotificaDecorrenzaTerminiDao) {
        this.statoAttivaNotificaDecorrenzaTerminiDao = statoAttivaNotificaDecorrenzaTerminiDao;
    }

    public StatoAttivaAttestazioneTrasmissioneFatturaDao getStatoAttivaAttestazioneTrasmissioneFatturaDao() {
        return statoAttivaAttestazioneTrasmissioneFatturaDao;
    }

    public void setStatoAttivaAttestazioneTrasmissioneFatturaDao(StatoAttivaAttestazioneTrasmissioneFatturaDao statoAttivaAttestazioneTrasmissioneFatturaDao) {
        this.statoAttivaAttestazioneTrasmissioneFatturaDao = statoAttivaAttestazioneTrasmissioneFatturaDao;
    }

    public StatoAttivaNotificaMancataConsegnaDao getStatoAttivaNotificaMancataConsegnaDao() {
        return statoAttivaNotificaMancataConsegnaDao;
    }

    public void setStatoAttivaNotificaMancataConsegnaDao(StatoAttivaNotificaMancataConsegnaDao statoAttivaNotificaMancataConsegnaDao) {
        this.statoAttivaNotificaMancataConsegnaDao = statoAttivaNotificaMancataConsegnaDao;
    }

    public StatoAttivaRicevutaConsegnaDao getStatoAttivaRicevutaConsegnaDao() {
        return statoAttivaRicevutaConsegnaDao;
    }

    public void setStatoAttivaRicevutaConsegnaDao(StatoAttivaRicevutaConsegnaDao statoAttivaRicevutaConsegnaDao) {
        this.statoAttivaRicevutaConsegnaDao = statoAttivaRicevutaConsegnaDao;
    }

    public StatoAttivaNotificaScartoDao getStatoAttivaNotificaScartoDao() {
        return statoAttivaNotificaScartoDao;
    }

    public void setStatoAttivaNotificaScartoDao(StatoAttivaNotificaScartoDao statoAttivaNotificaScartoDao) {
        this.statoAttivaNotificaScartoDao = statoAttivaNotificaScartoDao;
    }

    public StatoFatturaAttivaDao getStatoFatturaAttivaDao() {
        return statoFatturaAttivaDao;
    }

    public void setStatoFatturaAttivaDao(StatoFatturaAttivaDao statoFatturaAttivaDao) {
        this.statoFatturaAttivaDao = statoFatturaAttivaDao;
    }
}
