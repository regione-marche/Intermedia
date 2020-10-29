package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.ZipFtpEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by agosteeno on 23/03/15.
 * <p>
 * questo manager gestisce le possibili notifiche ricevute dallo sdi per la fatturazione attiva:
 * <p>
 * - RicevutaConsegna
 * - NotificaMancataConsegna
 * - NotificaScarto
 * - NotificaEsito
 * - NotificaDecorrenzaTermini
 * - AttestazioneTrasmissioneFattura
 */
public class FatturaAttivaNotificheManager {

    private static final Logger LOG = LoggerFactory.getLogger(FatturaAttivaNotificheManager.class);

    private EntityManagerFactory entityManagerFactory;

    private CodificaStatiAttivaDao codificaStatiAttivaDao;

    private FatturaAttivaRicevutaConsegnaDao fatturaAttivaRicevutaConsegnaDao;

    private FatturaAttivaNotificaMancataConsegnaDao fatturaAttivaNotificaMancataConsegnaDao;

    private FatturaAttivaNotificaScartoDao fatturaAttivaNotificaScartoDao;

    private FatturaAttivaNotificaEsitoDao fatturaAttivaNotificaEsitoDao;

    private FatturaAttivaNotificaDecorrenzaTerminiDao fatturaAttivaNotificaDecorrenzaTerminiDao;

    private FatturaAttivaAttestaioneTrasmissioneFatturaDao fatturaAttivaAttestaioneTrasmissioneFatturaDao;

    private StatoAttivaAttestazioneTrasmissioneFatturaDao statoAttivaAttestazioneTrasmissioneFatturaDao;

    private StatoAttivaRicevutaConsegnaDao statoAttivaRicevutaConsegnaDao;

    private StatoAttivaNotificaMancataConsegnaDao statoAttivaNotificaMancataConsegnaDao;

    private StatoAttivaNotificaScartoDao statoAttivaNotificaScartoDao;

    private StatoAttivaNotificaEsitoDao statoAttivaNotificaEsitoDao;

    private StatoAttivaNotificaDecorrenzaTerminiDao statoAttivaNotificaDecorrenzaTerminiDao;

    public BigInteger salvaStatoRicevutaConsegna(FatturaAttivaEntity fatturaAttivaEntity, String codificaStato) throws FatturaPAException {

        EntityManager entityManager = null;
        BigInteger idNotifica;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            StatoAttivaRicevutaConsegnaEntity statoAttivaRicevutaConsegnaEntity = new StatoAttivaRicevutaConsegnaEntity();
            FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegnaEntity = new FatturaAttivaRicevutaConsegnaEntity();

            fatturaAttivaRicevutaConsegnaEntity.setData(now);
            fatturaAttivaRicevutaConsegnaEntity.setFatturaAttiva(fatturaAttivaEntity);

            CodificaStatiAttivaEntity codificaStatiEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStato), entityManager);

            statoAttivaRicevutaConsegnaEntity.setRicevutaConsegnaEntity(fatturaAttivaRicevutaConsegnaEntity);
            statoAttivaRicevutaConsegnaEntity.setStato(codificaStatiEntity);
            statoAttivaRicevutaConsegnaEntity.setData(now);

            FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegnaEntityCreata = fatturaAttivaRicevutaConsegnaDao.create(fatturaAttivaRicevutaConsegnaEntity, entityManager);
            statoAttivaRicevutaConsegnaDao.create(statoAttivaRicevutaConsegnaEntity, entityManager);

            idNotifica = fatturaAttivaRicevutaConsegnaEntityCreata.getIdRicevutaConsegna();

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

        return idNotifica;
    }

    public BigInteger salvaStatoNotificaMancataConsegna(FatturaAttivaEntity fatturaAttivaEntity, String codificaStato) throws FatturaPAException {

        EntityManager entityManager = null;
        BigInteger idNotifica = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            StatoAttivaNotificaMancataConsegnaEntity statoAttivaNotificaMancataConsegnaEntity = new StatoAttivaNotificaMancataConsegnaEntity();
            FatturaAttivaNotificaMancataConsegnaEntity fatturaAttivaNotificaMancataConsegnaEntity = new FatturaAttivaNotificaMancataConsegnaEntity();

            fatturaAttivaNotificaMancataConsegnaEntity.setData(now);
            fatturaAttivaNotificaMancataConsegnaEntity.setFatturaAttiva(fatturaAttivaEntity);

            CodificaStatiAttivaEntity codificaStatiEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStato), entityManager);

            statoAttivaNotificaMancataConsegnaEntity.setNotificaMancataConsegnaEntity(fatturaAttivaNotificaMancataConsegnaEntity);
            statoAttivaNotificaMancataConsegnaEntity.setStato(codificaStatiEntity);
            statoAttivaNotificaMancataConsegnaEntity.setData(now);

            FatturaAttivaNotificaMancataConsegnaEntity fatturaAttivaNotificaMancataConsegnaCreata = fatturaAttivaNotificaMancataConsegnaDao.create(fatturaAttivaNotificaMancataConsegnaEntity, entityManager);
            statoAttivaNotificaMancataConsegnaDao.create(statoAttivaNotificaMancataConsegnaEntity, entityManager);

            idNotifica = fatturaAttivaNotificaMancataConsegnaCreata.getIdNotificaMancataConsegna();

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

        return idNotifica;
    }

    public BigInteger salvaStatoNotificaScarto(FatturaAttivaEntity fatturaAttivaEntity, String codificaStato) throws FatturaPAException {

        EntityManager entityManager = null;
        BigInteger idNotifica;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            StatoAttivaNotificaScartoEntity statoAttivaNotificaScartoEntity = new StatoAttivaNotificaScartoEntity();
            FatturaAttivaNotificaScartoEntity fatturaAttivaNotificaScartoEntity = new FatturaAttivaNotificaScartoEntity();

            fatturaAttivaNotificaScartoEntity.setData(now);
            fatturaAttivaNotificaScartoEntity.setFatturaAttiva(fatturaAttivaEntity);

            CodificaStatiAttivaEntity codificaStatiEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStato), entityManager);

            statoAttivaNotificaScartoEntity.setNotificaScartoEntity(fatturaAttivaNotificaScartoEntity);
            statoAttivaNotificaScartoEntity.setStato(codificaStatiEntity);
            statoAttivaNotificaScartoEntity.setData(now);

            FatturaAttivaNotificaScartoEntity fatturaAttivaNotificaScartoEntityCreata = fatturaAttivaNotificaScartoDao.create(fatturaAttivaNotificaScartoEntity, entityManager);
            statoAttivaNotificaScartoDao.create(statoAttivaNotificaScartoEntity, entityManager);

            idNotifica = fatturaAttivaNotificaScartoEntityCreata.getIdNotificaScarto();

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

        return idNotifica;
    }

    public BigInteger salvaStatoNotificaEsito(FatturaAttivaEntity fatturaAttivaEntity, String codificaStato) throws FatturaPAException {

        EntityManager entityManager = null;
        BigInteger idNotifica = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            StatoAttivaNotificaEsitoEntity statoAttivaNotificaEsitoEntity = new StatoAttivaNotificaEsitoEntity();
            FatturaAttivaNotificaEsitoEntity fatturaAttivaNotificaEsitoEntity = new FatturaAttivaNotificaEsitoEntity();

            fatturaAttivaNotificaEsitoEntity.setData(now);
            fatturaAttivaNotificaEsitoEntity.setFatturaAttiva(fatturaAttivaEntity);

            CodificaStatiAttivaEntity codificaStatiEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStato), entityManager);

            statoAttivaNotificaEsitoEntity.setNotificaEsitoEntity(fatturaAttivaNotificaEsitoEntity);
            statoAttivaNotificaEsitoEntity.setStato(codificaStatiEntity);
            statoAttivaNotificaEsitoEntity.setData(now);

            FatturaAttivaNotificaEsitoEntity fatturaAttivaNotificaEsitoEntityCreata = fatturaAttivaNotificaEsitoDao.create(fatturaAttivaNotificaEsitoEntity, entityManager);
            statoAttivaNotificaEsitoDao.create(statoAttivaNotificaEsitoEntity, entityManager);

            idNotifica = fatturaAttivaNotificaEsitoEntityCreata.getIdNotificaEsito();

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

        return idNotifica;
    }

    public BigInteger salvaStatoNotificaDecorrenzaTermini(FatturaAttivaEntity fatturaAttivaEntity, String codificaStato) throws FatturaPAException {

        EntityManager entityManager = null;

        BigInteger idNotifica = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            StatoAttivaNotificaDecorrenzaTerminiEntity statoAttivaNotificaDecorrenzaTerminiEntity = new StatoAttivaNotificaDecorrenzaTerminiEntity();
            FatturaAttivaNotificaDecorrenzaTerminiEntity fatturaAttivaNotificaDecorrenzaTerminiEntity = new FatturaAttivaNotificaDecorrenzaTerminiEntity();

            fatturaAttivaNotificaDecorrenzaTerminiEntity.setData(now);
            fatturaAttivaNotificaDecorrenzaTerminiEntity.setFatturaAttiva(fatturaAttivaEntity);

            CodificaStatiAttivaEntity codificaStatiEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStato), entityManager);

            statoAttivaNotificaDecorrenzaTerminiEntity.setNotificaDecorrenzaTerminiEntity(fatturaAttivaNotificaDecorrenzaTerminiEntity);
            statoAttivaNotificaDecorrenzaTerminiEntity.setStato(codificaStatiEntity);
            statoAttivaNotificaDecorrenzaTerminiEntity.setData(now);

            FatturaAttivaNotificaDecorrenzaTerminiEntity fatturaAttivaNotificaDecorrenzaTerminiEntityCreata = fatturaAttivaNotificaDecorrenzaTerminiDao.create(fatturaAttivaNotificaDecorrenzaTerminiEntity, entityManager);
            statoAttivaNotificaDecorrenzaTerminiDao.create(statoAttivaNotificaDecorrenzaTerminiEntity, entityManager);

            idNotifica = fatturaAttivaNotificaDecorrenzaTerminiEntityCreata.getIdNotificaDecorrenzaTermini();

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

        return idNotifica;
    }

    public BigInteger salvaStatoAttestazioneTrasmissioneFattura(FatturaAttivaEntity fatturaAttivaEntity, String codificaStato) throws FatturaPAException {

        EntityManager entityManager = null;

        BigInteger idNotifica;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            StatoAttivaAttestazioneTrasmissioneFatturaEntity statoAttivaAttestazioneTrasmissioneFatturaEntity = new StatoAttivaAttestazioneTrasmissioneFatturaEntity();
            FatturaAttivaAttestazioneTrasmissioneFatturaEntity fatturaAttivaAttestazioneTrasmissioneFatturaEntity = new FatturaAttivaAttestazioneTrasmissioneFatturaEntity();

            fatturaAttivaAttestazioneTrasmissioneFatturaEntity.setData(now);
            fatturaAttivaAttestazioneTrasmissioneFatturaEntity.setFatturaAttiva(fatturaAttivaEntity);

            CodificaStatiAttivaEntity codificaStatiEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStato), entityManager);

            statoAttivaAttestazioneTrasmissioneFatturaEntity.setAttestazioneTrasmissioneFatturaEntity(fatturaAttivaAttestazioneTrasmissioneFatturaEntity);
            statoAttivaAttestazioneTrasmissioneFatturaEntity.setStato(codificaStatiEntity);
            statoAttivaAttestazioneTrasmissioneFatturaEntity.setData(now);

            FatturaAttivaAttestazioneTrasmissioneFatturaEntity fatturaAttivaAttestazioneTrasmissioneFatturaEntityCreata = fatturaAttivaAttestaioneTrasmissioneFatturaDao.create(fatturaAttivaAttestazioneTrasmissioneFatturaEntity, entityManager);
            statoAttivaAttestazioneTrasmissioneFatturaDao.create(statoAttivaAttestazioneTrasmissioneFatturaEntity, entityManager);

            idNotifica = fatturaAttivaAttestazioneTrasmissioneFatturaEntityCreata.getIdAttestazioneTrasmissioneFattura();

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

        return idNotifica;
    }

    /**
     * Aggiorna lo stato della notifica ricevuta dallo sdi
     *
     * @param codificaStatiAttivaValue        il codice dello stato da settare (preso dalla enum CodificaStatiAttivaEntity)
     * @param idFatturaAttivaRicevutaConsegna l'id della fattura per la quale si vuole aggiungere il nuovo stato
     */
    public void aggiornaStatoRiceviConsegna(String codificaStatiAttivaValue, BigInteger idFatturaAttivaRicevutaConsegna) throws FatturaPAException {
        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegnaEntity = fatturaAttivaRicevutaConsegnaDao.read(idFatturaAttivaRicevutaConsegna, entityManager);

            StatoAttivaRicevutaConsegnaEntity statoAttivaRicevutaConsegnaEntity = new StatoAttivaRicevutaConsegnaEntity();
            statoAttivaRicevutaConsegnaEntity.setRicevutaConsegnaEntity(fatturaAttivaRicevutaConsegnaEntity);
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatiAttivaValue), entityManager);
            statoAttivaRicevutaConsegnaEntity.setStato(codificaStatiAttivaEntity);
            statoAttivaRicevutaConsegnaEntity.setData(now);

            statoAttivaRicevutaConsegnaDao.create(statoAttivaRicevutaConsegnaEntity, entityManager);

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

    public void aggiornaStatoMancataConsegna(String codificaStatiAttivaValue, BigInteger idFatturaAttivaMancataConsegna) throws FatturaPAException {
        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            FatturaAttivaNotificaMancataConsegnaEntity fatturaAttivaNotificaMancataConsegnaEntity = fatturaAttivaNotificaMancataConsegnaDao.read(idFatturaAttivaMancataConsegna, entityManager);

            StatoAttivaNotificaMancataConsegnaEntity statoAttivaNotificaMancataConsegnaEntity = new StatoAttivaNotificaMancataConsegnaEntity();
            statoAttivaNotificaMancataConsegnaEntity.setNotificaMancataConsegnaEntity(fatturaAttivaNotificaMancataConsegnaEntity);
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatiAttivaValue), entityManager);
            statoAttivaNotificaMancataConsegnaEntity.setStato(codificaStatiAttivaEntity);
            statoAttivaNotificaMancataConsegnaEntity.setData(now);

            getStatoAttivaNotificaMancataConsegnaDao().create(statoAttivaNotificaMancataConsegnaEntity, entityManager);

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

    public void aggiornaStatoNotificaScarto(String codificaStatiAttivaValue, BigInteger idFatturaAttivaNotificaScarto) throws FatturaPAException {
        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            FatturaAttivaNotificaScartoEntity fatturaAttivaNotificaScartoEntity = fatturaAttivaNotificaScartoDao.read(idFatturaAttivaNotificaScarto, entityManager);

            StatoAttivaNotificaScartoEntity statoAttivaNotificaScartoEntity = new StatoAttivaNotificaScartoEntity();
            statoAttivaNotificaScartoEntity.setNotificaScartoEntity(fatturaAttivaNotificaScartoEntity);
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatiAttivaValue), entityManager);
            statoAttivaNotificaScartoEntity.setStato(codificaStatiAttivaEntity);
            statoAttivaNotificaScartoEntity.setData(now);

            getStatoAttivaNotificaScartoDao().create(statoAttivaNotificaScartoEntity, entityManager);

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

    public void aggiornaStatoNotificaEsito(String codificaStatiAttivaValue, BigInteger idFatturaAttivaNotificaEsito) throws FatturaPAException {
        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            FatturaAttivaNotificaEsitoEntity fatturaAttivaNotificaScartoEntity = fatturaAttivaNotificaEsitoDao.read(idFatturaAttivaNotificaEsito, entityManager);

            StatoAttivaNotificaEsitoEntity statoAttivaNotificaEsitoEntity = new StatoAttivaNotificaEsitoEntity();
            statoAttivaNotificaEsitoEntity.setNotificaEsitoEntity(fatturaAttivaNotificaScartoEntity);
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatiAttivaValue), entityManager);
            statoAttivaNotificaEsitoEntity.setStato(codificaStatiAttivaEntity);
            statoAttivaNotificaEsitoEntity.setData(now);

            getStatoAttivaNotificaEsitoDao().create(statoAttivaNotificaEsitoEntity, entityManager);

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

    public void aggiornaStatoNotificaDecorrenzaTermini(String codificaStatiAttivaValue, BigInteger idFatturaAttivaNotificaDecorrenzaTermini) throws FatturaPAException {
        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            FatturaAttivaNotificaDecorrenzaTerminiEntity fatturaAttivaNotificaDecorrenzaTerminiEntity = fatturaAttivaNotificaDecorrenzaTerminiDao.read(idFatturaAttivaNotificaDecorrenzaTermini, entityManager);

            StatoAttivaNotificaDecorrenzaTerminiEntity statoAttivaNotificaDecorrenzaTerminiEntity = new StatoAttivaNotificaDecorrenzaTerminiEntity();
            statoAttivaNotificaDecorrenzaTerminiEntity.setNotificaDecorrenzaTerminiEntity(fatturaAttivaNotificaDecorrenzaTerminiEntity);
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatiAttivaValue), entityManager);
            statoAttivaNotificaDecorrenzaTerminiEntity.setStato(codificaStatiAttivaEntity);
            statoAttivaNotificaDecorrenzaTerminiEntity.setData(now);

            getStatoAttivaNotificaDecorrenzaTerminiDao().create(statoAttivaNotificaDecorrenzaTerminiEntity, entityManager);

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

    public void aggiornaStatoAttestazioneTrasmissioneFattura(String codificaStatiAttivaValue, BigInteger idFatturaAttivaAttestazioneTrasmissioneFattura) throws FatturaPAException {
        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Timestamp now = new Timestamp(new Date().getTime());

            FatturaAttivaAttestazioneTrasmissioneFatturaEntity fatturaAttivafatturaAttivaAttestazioneTrasmissioneFatturaEntity = fatturaAttivaAttestaioneTrasmissioneFatturaDao.read(idFatturaAttivaAttestazioneTrasmissioneFattura, entityManager);

            StatoAttivaAttestazioneTrasmissioneFatturaEntity statoAttivaAttestazioneTrasmissioneFatturaEntity = new StatoAttivaAttestazioneTrasmissioneFatturaEntity();
            statoAttivaAttestazioneTrasmissioneFatturaEntity.setAttestazioneTrasmissioneFatturaEntity(fatturaAttivafatturaAttivaAttestazioneTrasmissioneFatturaEntity);
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatiAttivaValue), entityManager);
            statoAttivaAttestazioneTrasmissioneFatturaEntity.setStato(codificaStatiAttivaEntity);
            statoAttivaAttestazioneTrasmissioneFatturaEntity.setData(now);

            getStatoAttivaAttestazioneTrasmissioneFatturaDao().create(statoAttivaAttestazioneTrasmissioneFatturaEntity, entityManager);

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


    public List<BigInteger> getFatturaAttivaNotificaDecorrenzaTerminiUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<BigInteger> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = fatturaAttivaNotificaDecorrenzaTerminiDao.getIdFatturaAttivaNotificaDecorrenzaTerminiUltimoStatoRicevutaByIdFatturaAttiva(idFattureAttive, entityManager);
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
        return list;
    }

    public List<StatoAttivaRicevutaConsegnaEntity> getStatoRicevutaConsegnaFromRicevutaConsegna(FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegnaEntity) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<StatoAttivaRicevutaConsegnaEntity> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = statoAttivaRicevutaConsegnaDao.getStatoRicevutaConsegnaFromRicevutaConsegna(fatturaAttivaRicevutaConsegnaEntity, entityManager);
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
        return list;
    }


    public List<BigInteger> getIdFatturaAttivaRicevutaConsegnaUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<BigInteger> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = fatturaAttivaRicevutaConsegnaDao.getIdFatturaAttivaRicevutaConsegnaUltimoStatoRicevutaByIdFatturaAttiva(idFattureAttive, entityManager);
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
        return list;
    }


    public List<BigInteger> getIdFatturaAttivaNotificaMancataConsegnaUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<BigInteger> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = fatturaAttivaNotificaMancataConsegnaDao.getIdFatturaAttivaNotificaMancataConsegnaUltimoStatoRicevutaByIdFatturaAttiva(idFattureAttive, entityManager);
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
        return list;
    }


    public List<BigInteger> getIdFatturaAttivaNotificaScartoUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<BigInteger> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = fatturaAttivaNotificaScartoDao.getIdFatturaAttivaNotificaScartoUltimoStatoRicevutaByIdFatturaAttiva(idFattureAttive, entityManager);
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
        return list;
    }


    public List<BigInteger> getIdFatturaAttivaNotificaEsitoUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<BigInteger> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = fatturaAttivaNotificaEsitoDao.getIdFatturaAttivaNotificaEsitoUltimoStatoRicevutaByIdFatturaAttiva(idFattureAttive, entityManager);
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
        return list;
    }


    public List<BigInteger> getIdFatturaAttivaAttestazioneTrasmissioneFatturaUltimoStatoRicevutaByIdFatturaAttiva(List<BigInteger> idFattureAttive) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<BigInteger> list = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            list = fatturaAttivaAttestaioneTrasmissioneFatturaDao.getIdFatturaAttivaAttestazioneTrasmissioneFatturaUltimoStatoRicevutaByIdFatturaAttiva(idFattureAttive, entityManager);
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
        return list;
    }


    /*
    La mappa contiene < tipo_notifica, List<idFattureAttive> >
     */
    public void aggiornaStatoFatturaAttivaNotifiche(Map<String, List<BigInteger>> mapNotifiche, String nomeZipFile, String codiceStatoFattura, String codiceEnte) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Date date = new Date();
            Timestamp now = new Timestamp(date.getTime());

            ZipFtpEntity zipFtpEntity = new ZipFtpEntity();
            zipFtpEntity.setNomeFileZip(nomeZipFile);
            zipFtpEntity.setDataInvio(date);
            zipFtpEntity.setFtpOut(true);
            zipFtpEntity.setCodiceEnte(codiceEnte);


            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codiceStatoFattura), entityManager);

            for (Map.Entry<String, List<BigInteger>> f : mapNotifiche.entrySet()) {

                List<BigInteger> idFattureAttive = f.getValue();

                if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.name().equalsIgnoreCase(f.getKey())) {
                    for (BigInteger id : idFattureAttive) {
                        FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegna = fatturaAttivaRicevutaConsegnaDao.getFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(id, entityManager);
                        StatoAttivaRicevutaConsegnaEntity stato = new StatoAttivaRicevutaConsegnaEntity();
                        stato.setRicevutaConsegnaEntity(fatturaAttivaRicevutaConsegna);
                        stato.setStato(codificaStatiAttivaEntity);
                        stato.setData(now);
                        stato.setZipFtpEntity(zipFtpEntity);

                        statoAttivaRicevutaConsegnaDao.create(stato, entityManager);
                    }
                } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.name().equalsIgnoreCase(f.getKey())) {
                    for (BigInteger id : idFattureAttive) {
                        FatturaAttivaNotificaMancataConsegnaEntity fatturaAttivaNotificaMancataConsegna = fatturaAttivaNotificaMancataConsegnaDao.getFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(id, entityManager);
                        StatoAttivaNotificaMancataConsegnaEntity stato = new StatoAttivaNotificaMancataConsegnaEntity();
                        stato.setNotificaMancataConsegnaEntity(fatturaAttivaNotificaMancataConsegna);
                        stato.setStato(codificaStatiAttivaEntity);
                        stato.setData(now);
                        stato.setZipFtpEntity(zipFtpEntity);

                        statoAttivaNotificaMancataConsegnaDao.create(stato, entityManager);

                    }
                } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.name().equalsIgnoreCase(f.getKey())) {
                    for (BigInteger id : idFattureAttive) {
                        FatturaAttivaNotificaScartoEntity fatturaAttivaNotificaScarto = fatturaAttivaNotificaScartoDao.getFatturaAttivaNotificaScartoByIdFatturaAttiva(id, entityManager);
                        StatoAttivaNotificaScartoEntity stato = new StatoAttivaNotificaScartoEntity();
                        stato.setNotificaScartoEntity(fatturaAttivaNotificaScarto);
                        stato.setStato(codificaStatiAttivaEntity);
                        stato.setData(now);
                        stato.setZipFtpEntity(zipFtpEntity);

                        statoAttivaNotificaScartoDao.create(stato, entityManager);
                    }
                } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.name().equalsIgnoreCase(f.getKey())) {
                    for (BigInteger id : idFattureAttive) {
                        FatturaAttivaNotificaEsitoEntity fatturaAttivaNotificaEsito = fatturaAttivaNotificaEsitoDao.getFatturaAttivaNotificaEsitoByIdFatturaAttiva(id, entityManager);
                        StatoAttivaNotificaEsitoEntity stato = new StatoAttivaNotificaEsitoEntity();
                        stato.setNotificaEsitoEntity(fatturaAttivaNotificaEsito);
                        stato.setStato(codificaStatiAttivaEntity);
                        stato.setData(now);
                        stato.setZipFtpEntity(zipFtpEntity);

                        statoAttivaNotificaEsitoDao.create(stato, entityManager);
                    }
                } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.name().equalsIgnoreCase(f.getKey())) {
                    for (BigInteger id : idFattureAttive) {
                        FatturaAttivaNotificaDecorrenzaTerminiEntity fatturaAttivaNotificaDecorrenzaTermini = fatturaAttivaNotificaDecorrenzaTerminiDao.getFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(id, entityManager);
                        StatoAttivaNotificaDecorrenzaTerminiEntity stato = new StatoAttivaNotificaDecorrenzaTerminiEntity();
                        stato.setNotificaDecorrenzaTerminiEntity(fatturaAttivaNotificaDecorrenzaTermini);
                        stato.setStato(codificaStatiAttivaEntity);
                        stato.setData(now);
                        stato.setZipFtpEntity(zipFtpEntity);

                        statoAttivaNotificaDecorrenzaTerminiDao.create(stato, entityManager);

                    }
                } else if (TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.name().equalsIgnoreCase(f.getKey())) {
                    for (BigInteger id : idFattureAttive) {
                        FatturaAttivaAttestazioneTrasmissioneFatturaEntity fatturaAttivaAttestazioneTrasmissione = fatturaAttivaAttestaioneTrasmissioneFatturaDao.getFatturaAttivaAttestazioneTrasmissioneByIdFatturaAttiva(id, entityManager);
                        StatoAttivaAttestazioneTrasmissioneFatturaEntity stato = new StatoAttivaAttestazioneTrasmissioneFatturaEntity();
                        stato.setAttestazioneTrasmissioneFatturaEntity(fatturaAttivaAttestazioneTrasmissione);
                        stato.setStato(codificaStatiAttivaEntity);
                        stato.setData(now);
                        stato.setZipFtpEntity(zipFtpEntity);

                        statoAttivaAttestazioneTrasmissioneFatturaDao.create(stato, entityManager);
                    }
                }
            }

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


    public FatturaAttivaRicevutaConsegnaEntity getFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(BigInteger id) throws FatturaPAException {
        EntityManager entityManager = null;
        FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegna = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaRicevutaConsegna = fatturaAttivaRicevutaConsegnaDao.getFatturaAttivaRicevutaConsegnaByIdFatturaAttiva(id, entityManager);

        } catch (FatturaPaPersistenceException pe) {
            return null;
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

        return fatturaAttivaRicevutaConsegna;
    }

    public FatturaAttivaRicevutaConsegnaEntity getFatturaAttivaRicevutaConsegnaFatturaAttiva(FatturaAttivaEntity fatturaAttivaEntity) throws FatturaPAException {
        EntityManager entityManager = null;
        FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegna = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaRicevutaConsegna = fatturaAttivaRicevutaConsegnaDao.getFatturaAttivaRicevutaConsegnaFatturaAttiva(fatturaAttivaEntity, entityManager);

        } catch (FatturaPaPersistenceException pe) {
            return null;
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

        return fatturaAttivaRicevutaConsegna;
    }

    public FatturaAttivaNotificaScartoEntity getFatturaAttivaNotificaScartoByIdFatturaAttiva(BigInteger id) throws FatturaPAException {
        EntityManager entityManager = null;
        FatturaAttivaNotificaScartoEntity fatturaAttivaNotificaScarto = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaNotificaScarto = fatturaAttivaNotificaScartoDao.getFatturaAttivaNotificaScartoByIdFatturaAttiva(id, entityManager);
        } catch (FatturaPaPersistenceException pe) {
            return null;
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
        return fatturaAttivaNotificaScarto;
    }


    public FatturaAttivaNotificaMancataConsegnaEntity getFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(BigInteger id) throws FatturaPAException {
        EntityManager entityManager = null;
        FatturaAttivaNotificaMancataConsegnaEntity fatturaAttivaNotificaMancataConsegna = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaNotificaMancataConsegna = fatturaAttivaNotificaMancataConsegnaDao.getFatturaAttivaNotificaMancataConsegnaByIdFatturaAttiva(id, entityManager);
        } catch (FatturaPaPersistenceException pe) {
            return null;
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
        return fatturaAttivaNotificaMancataConsegna;
    }


    public FatturaAttivaNotificaDecorrenzaTerminiEntity getFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(BigInteger id) throws FatturaPAException {
        EntityManager entityManager = null;
        FatturaAttivaNotificaDecorrenzaTerminiEntity fatturaAttivaNotificaDecorrenzaTermini = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaNotificaDecorrenzaTermini = fatturaAttivaNotificaDecorrenzaTerminiDao.getFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(id, entityManager);
        } catch (FatturaPaPersistenceException pe) {
            return null;
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

        return fatturaAttivaNotificaDecorrenzaTermini;
    }

    public List<StatoAttivaNotificaDecorrenzaTerminiEntity> getStatoFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPAException {
        EntityManager entityManager = null;
        List<StatoAttivaNotificaDecorrenzaTerminiEntity> result = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
          result =  fatturaAttivaNotificaDecorrenzaTerminiDao.getStatoFatturaAttivaNotificaDecorrenzaTerminiByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException pe) {
            return null;
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


    public FatturaAttivaNotificaEsitoEntity getFatturaAttivaNotificaEsitoByIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPAException {
        EntityManager entityManager = null;
        FatturaAttivaNotificaEsitoEntity fatturaAttivaNotificaEsito = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaNotificaEsito = fatturaAttivaNotificaEsitoDao.getFatturaAttivaNotificaEsitoByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException pe) {
            return null;
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
        return fatturaAttivaNotificaEsito;
    }


    public List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> statoFatturaAttivaAttestazioneTrasmissioneFatturaUltimoStatoRicevutaByIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPAException {
        EntityManager entityManager = null;
        List<StatoAttivaAttestazioneTrasmissioneFatturaEntity> statoAttivaAttestazioneTrasmissioneFattura = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            statoAttivaAttestazioneTrasmissioneFattura = fatturaAttivaAttestaioneTrasmissioneFatturaDao.statoFatturaAttivaAttestazioneTrasmissioneFatturaUltimoStatoRicevutaByIdFatturaAttiva(idFatturaAttiva, entityManager);
        } catch (FatturaPaPersistenceException pe) {
            return null;
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
        return statoAttivaAttestazioneTrasmissioneFattura;
    }


    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public CodificaStatiAttivaDao getCodificaStatiAttivaDao() {
        return codificaStatiAttivaDao;
    }

    public void setCodificaStatiAttivaDao(CodificaStatiAttivaDao codificaStatiAttivaDao) {
        this.codificaStatiAttivaDao = codificaStatiAttivaDao;
    }

    public StatoAttivaAttestazioneTrasmissioneFatturaDao getStatoAttivaAttestazioneTrasmissioneFatturaDao() {
        return statoAttivaAttestazioneTrasmissioneFatturaDao;
    }

    public void setStatoAttivaAttestazioneTrasmissioneFatturaDao(StatoAttivaAttestazioneTrasmissioneFatturaDao statoAttivaAttestazioneTrasmissioneFatturaDao) {
        this.statoAttivaAttestazioneTrasmissioneFatturaDao = statoAttivaAttestazioneTrasmissioneFatturaDao;
    }

    public StatoAttivaRicevutaConsegnaDao getStatoAttivaRicevutaConsegnaDao() {
        return statoAttivaRicevutaConsegnaDao;
    }

    public void setStatoAttivaRicevutaConsegnaDao(StatoAttivaRicevutaConsegnaDao statoAttivaRicevutaConsegnaDao) {
        this.statoAttivaRicevutaConsegnaDao = statoAttivaRicevutaConsegnaDao;
    }

    public StatoAttivaNotificaMancataConsegnaDao getStatoAttivaNotificaMancataConsegnaDao() {
        return statoAttivaNotificaMancataConsegnaDao;
    }

    public void setStatoAttivaNotificaMancataConsegnaDao(StatoAttivaNotificaMancataConsegnaDao statoAttivaNotificaMancataConsegnaDao) {
        this.statoAttivaNotificaMancataConsegnaDao = statoAttivaNotificaMancataConsegnaDao;
    }

    public StatoAttivaNotificaScartoDao getStatoAttivaNotificaScartoDao() {
        return statoAttivaNotificaScartoDao;
    }

    public void setStatoAttivaNotificaScartoDao(StatoAttivaNotificaScartoDao statoAttivaNotificaScartoDao) {
        this.statoAttivaNotificaScartoDao = statoAttivaNotificaScartoDao;
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

    public FatturaAttivaRicevutaConsegnaDao getFatturaAttivaRicevutaConsegnaDao() {
        return fatturaAttivaRicevutaConsegnaDao;
    }

    public void setFatturaAttivaRicevutaConsegnaDao(FatturaAttivaRicevutaConsegnaDao fatturaAttivaRicevutaConsegnaDao) {
        this.fatturaAttivaRicevutaConsegnaDao = fatturaAttivaRicevutaConsegnaDao;
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
}
