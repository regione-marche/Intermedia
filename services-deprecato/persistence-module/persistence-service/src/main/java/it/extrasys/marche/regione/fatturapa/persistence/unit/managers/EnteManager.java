package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.api.rest.model.*;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.EndpointAttivaCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.UtenteEnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnteManager {

    private static final Logger LOG = LoggerFactory.getLogger(EnteManager.class);

    private TipoCanaleDao tipoCanaleDao;
    private EndpointAttivaCaDao endpointAttivaCaDao;
    private EndpointCaDao endpointCaDao;
    private CanaleCaDao canaleCaDao;
    private ChiaveDao chiaveDao;
    private EntePaleoCaDao entePaleoCaDao;
    private UtentiManager utentiManager;

    private EntityManagerFactory entityManagerFactory;

    private EnteDao enteDao;

    private UtentiDao utentiDao;

    private String pecProtocolloCaCoda;
    private String wsProtocolloAgidCaCoda;
    private String wsProtocolloPaleoCaCoda;
    private String wsRegistrazioneCaCoda;
    private String pecRegistrazioneCaCoda;
    private String wsCaAttivaRegistrazioneInvioQueue;
    private String pecCaInoltroNotifiche;

    public EnteEntity getEnteByCodiceUfficio(String codiceUfficio) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        EnteEntity enteEntity;

        LOG.info("*************** EnteManager: getEnteByCodiceUfficio ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntity = enteDao.getEnteByCodiceUfficio(codiceUfficio, entityManager);

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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

        return enteEntity;
    }


    public EnteEntity getEnteByIdFiscaleCommittenteAndCodiceUfficio(String idFiscaleCommittente, String codiceUfficio) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAException {

        EntityManager entityManager = null;
        EnteEntity enteEntity;

        LOG.info("*************** EnteManager: getEnteByIdFiscaleCommittenteAndCodiceUfficio ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntity = enteDao.getEnteByIdFiscaleCommittenteAndCodiceUfficio(idFiscaleCommittente, codiceUfficio, entityManager);

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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

        return enteEntity;
    }

    public void updateEnte(EnteEntity enteEntity) throws FatturaPAException {

        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: updateEnte ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            enteDao.update(enteEntity, entityManager);

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

    public List<EnteEntity> getEnteByIdFiscaleCommittente(String idFiscaleCommittente, String username) throws FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList = null;

        LOG.info("*************** EnteManager: getEnteByIdFiscaleCommittente ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntityList = enteDao.getEnteByIdFiscaleCommittente(idFiscaleCommittente, username, entityManager);

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException e) {
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

        return enteEntityList;
    }

    public List<EnteEntity> getEnteByDenominazioneEnte(String denominazioneEnte, String username) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException {

        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList = null;

        LOG.info("********** EnteManager: getEnteByDenominazioneEnte **********");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntityList = enteDao.getEnteByDenominazioneEnte(denominazioneEnte, username, entityManager);

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException e) {
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

        return enteEntityList;
    }

    public List<EnteEntity> getEnteByUser(String usernameUtente) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException {

        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList = null;

        LOG.info("********** EnteManager: getEnteByUser **********");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(usernameUtente, entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteEntity);

            boolean isAdmin = utentiManager.checkAuth(utenteEnteEntity, true);
            if(isAdmin) {
                enteEntityList = enteDao.getAllEnti(entityManager);
            }
            else throw new FatturaPAUtenteNonAutorizzatoException();

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException e) {
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

        return enteEntityList;
    }

    public EnteEntity getEnteById(BigInteger idEnte) throws FatturaPAException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        EnteEntity enteEntity;

        LOG.info("*************** EnteManager: getEnteById ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntity = enteDao.read(idEnte, entityManager);

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

        return enteEntity;
    }

    public List<EnteEntity> getEnteByPecAddress(String pecAddress) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        LOG.info("*************** EnteManager: getEnteByPecAddress ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntityList = enteDao.getEnteByPecAddress(pecAddress, entityManager);

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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

        return enteEntityList;
    }

    public List<EnteEntity> getEnteFtpInvioSingoloByTipoCanale(String idTipoCanale) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        //LOG.info("*************** EnteManager: getEnteFtpInvioSingoloByTipoCanale ***************");

        try {
            entityManager = entityManagerFactory.createEntityManager();
            enteEntityList = enteDao.getEnteFtpInvioSingoloByTipoCanale(idTipoCanale, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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
        return enteEntityList;
    }


    public List<EnteEntity> getEnteFtpInvioProtocolloByTipoCanale(String idTipoCanale) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        //LOG.info("*************** EnteManager: getEnteFtpInvioProtocolloByTipoCanale ***************");

        try {
            entityManager = entityManagerFactory.createEntityManager();
            enteEntityList = enteDao.getEnteFtpInvioProtocolloByTipoCanale(idTipoCanale, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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
        return enteEntityList;
    }


    public List<EnteEntity> getEnteFtpInvioGestionaleByTipoCanale(String idTipoCanale) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        //LOG.info("*************** EnteManager: getEnteFtpInvioGestionaleByTipoCanale ***************");

        try {
            entityManager = entityManagerFactory.createEntityManager();
            enteEntityList = enteDao.getEnteFtpInvioGestionaleByTipoCanale(idTipoCanale, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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
        return enteEntityList;
    }


    public List<EnteEntity> getEnteFtpByTipoCanale(String idTipoCanale) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        LOG.info("*************** EnteManager: getEnteFtpByTipoCanale ***************");

        try {
            entityManager = entityManagerFactory.createEntityManager();
            enteEntityList = enteDao.getEnteFtpByTipoCanale(idTipoCanale, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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
        return enteEntityList;
    }


    public List<EnteEntity>  getEnteFtpInvioFatturaAttivaByTipoCanale(String idTipoCanale) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        LOG.info("*************** EnteManager: getEnteFtpInvioFatturaAttivaByTipoCanale ***************");

        try {
            entityManager = entityManagerFactory.createEntityManager();
            enteEntityList = enteDao.getEnteFtpInvioFatturaAttivaByTipoCanale(idTipoCanale, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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
        return enteEntityList;
    }

    public List<EnteEntity>  getEnteFtpRicezioneFatturaByTipoCanale(String idTipoCanale) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<EnteEntity> enteEntityList;

        //LOG.info("*************** EnteManager: getEnteFtpRicezioneFatturaByTipoCanale ***************");

        try {
            entityManager = entityManagerFactory.createEntityManager();
            enteEntityList = enteDao.getEnteFtpRicezioneFatturaByTipoCanale(idTipoCanale, entityManager);
        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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
        return enteEntityList;
    }

    public EnteEntity getEnteByEndpointAttivaCA(String endpoint) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAEnteNonTrovatoException {

        EntityManager entityManager = null;
        EnteEntity enteEntity;

        LOG.info("*************** EnteManager: getEnteByEndpointAttivaCA ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntity = enteDao.getEnteByEndpointAttivaPecCA(endpoint, entityManager);

        } catch (FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
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

        return enteEntity;
    }

    public BigInteger updateEnteById(EntitiesRequestPut body) throws FatturaPACanaleNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException {

        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: updateEnteById ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(body.getIdEnte());

            EnteEntity enteEntityGet = enteDao.read(idEnte2, entityManager);

            if(enteEntityGet == null){
                throw new FatturaPAEnteNonTrovatoException();
            }

            UtenteEntity utenteMod = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteMod);
            utenteEnteEntity.setEnte(enteEntityGet);

            if(!utentiManager.checkAuth(utenteEnteEntity)){
                throw new FatturaPAUtenteNonAutorizzatoException("L'utente non ha i diritti");
            }

            //controllo campi opzionali
            if(body.getNome() != null)
                enteEntityGet.setDenominazioneEnte(body.getNome());
            if(body.getNomeUfficioFatturazione() != null)
                enteEntityGet.setNome(body.getNomeUfficioFatturazione());
            if(body.getIdFiscaleCommittente() != null)
                enteEntityGet.setIdFiscaleCommittente(body.getIdFiscaleCommittente());
            if(body.getCodiceUfficio() != null) {
                TypedQuery<EnteEntity> querySelectCodUff = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE UPPER(ente.codiceUfficio) = :codiceUff", EnteEntity.class);
                querySelectCodUff.setParameter("codiceUff", body.getCodiceUfficio().toUpperCase());
                List<EnteEntity> entList = querySelectCodUff.getResultList();
                if(entList.size() == 0 || (entList.size() == 1 && entList.get(0).getIdEnte().equals(idEnte2)))
                    enteEntityGet.setCodiceUfficio(body.getCodiceUfficio());
                else throw new FatturaPAException("codice ufficio "+ body.getCodiceUfficio() + "già associato ad un altro ente");
            }
            if(body.getEmailSupporto() != null)
                enteEntityGet.setContatti(body.getEmailSupporto());

            if(body.getTipoCanale() != null)
                enteEntityGet.setTipoCanale(tipoCanaleDao.getTipoCanaleById(body.getTipoCanale(), entityManager));


            enteEntityGet.setUtenteModifica(utenteMod);
            enteEntityGet.setDataUltimaModifica(new Date());

            //set campi obbligatori
            enteEntityGet.setCicloAttivo(body.getCicloAttivo());
            enteEntityGet.setCicloPassivo(body.getCicloPassivo());
            enteEntityGet.setCampiOpzionali(body.getCampiOpzionali());



            if(!body.getCicloAttivo()){
                if(enteEntityGet.getEndpointFattureAttivaCa() != null){
                    endpointAttivaCaDao.delete(enteEntityGet.getEndpointFattureAttivaCa(), entityManager);
                    enteEntityGet.setEndpointFattureAttivaCa(null);
                }
                if(enteEntityGet.getEndpointNotificheAttivaCa() != null){
                    endpointAttivaCaDao.delete(enteEntityGet.getEndpointNotificheAttivaCa(), entityManager);
                    enteEntityGet.setEndpointNotificheAttivaCa(null);
                }
            }

            if(!body.getCicloPassivo()){
                if(enteEntityGet.getEndpointRegistrazioneCa() != null){
                    endpointCaDao.delete(enteEntityGet.getEndpointRegistrazioneCa(), entityManager);
                    enteEntityGet.setEndpointRegistrazioneCa(null);
                }
                if(enteEntityGet.getEndpointEsitoCommittenteCa() != null){
                    endpointCaDao.delete(enteEntityGet.getEndpointEsitoCommittenteCa(), entityManager);
                    enteEntityGet.setEndpointEsitoCommittenteCa(null);
                }
                if(enteEntityGet.getEndpointProtocolloCa() != null){
                    endpointCaDao.delete(enteEntityGet.getEndpointProtocolloCa(), entityManager);
                    enteEntityGet.setEndpointProtocolloCa(null);
                }
            }

            enteDao.update(enteEntityGet, entityManager);

            entityTransaction.commit();



            return idEnte2;

        } catch ( FatturaPAEnteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException e) {
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

    public BigInteger insertEnteEntity(EntitiesRequest body) throws FatturaPACanaleNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: insertEnteEntity ***************");

        try {
            EnteEntity enteEntity = new EnteEntity();

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            TypedQuery<EnteEntity> querySelect = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE ente.codiceUfficio = :codUff", EnteEntity.class);
            querySelect.setParameter("codUff", body.getCodiceUfficio());

            List<EnteEntity> entList  = querySelect.getResultList();

            if(entList.size() > 0)
                throw new FatturaPaPersistenceException("Ente con codice ufficio "+body.getCodiceUfficio()+" già esistente");

            UtenteEntity utenteMod = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utenteMod);

            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException ("L'utente non ha i diritti");
            }

            enteEntity.setDenominazioneEnte(body.getNome());
            enteEntity.setNome(body.getNomeUfficioFatturazione());
            enteEntity.setIdFiscaleCommittente(body.getIdFiscaleCommittente());
            enteEntity.setCodiceUfficio(body.getCodiceUfficio());
            enteEntity.setContatti(body.getEmailSupporto());

            enteEntity.setTipoCanale(tipoCanaleDao.getTipoCanaleById(body.getTipoCanale(), entityManager));

            enteEntity.setCicloAttivo(body.getCicloAttivo());
            enteEntity.setCicloPassivo(body.getCicloPassivo());
            enteEntity.setCampiOpzionali(body.getCampiOpzionali());
            enteEntity.setInvioUnico(body.getInvioUnico ());
            enteEntity.setAmbienteCicloPassivo(body.getAmbienteCicloPassivo());
            enteEntity.setAmbienteCicloAttivo(body.getAmbienteCicloAttivo());

            enteEntity.setUtenteModifica(utenteMod);
            enteEntity.setDataUltimaModifica(new Date());

            entityManager.persist(enteEntity);

            entityTransaction.commit();

            TypedQuery<EnteEntity> querySelect2 = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE ente.codiceUfficio = :codUff", EnteEntity.class);
            querySelect2.setParameter("codUff", body.getCodiceUfficio());
            EnteEntity ente = querySelect2.getSingleResult();
            BigInteger idEnte = ente.getIdEnte();
            return idEnte;

        } catch (FatturaPaPersistenceException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAUtenteNonTrovatoException e) {
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

    public void servizioEntitiesActiveCycleRicezioneFatturePUT(Integer idEnte, ACinvoicesRequest body) throws BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException, FatturaPACanaleNonTrovatoException, IllegalBlockSizeException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesActiveCycleRicezioneFatturePUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesActiveCycleRicezioneFatturePUT: ente con tipo canale associato != CA");
            }

            if(!enteEntity.getAmbienteCicloAttivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("Errore ambiente ciclo attivo");
            }

            EndpointAttivaCaEntity endpointAttivaCaEntity = enteEntity.getEndpointFattureAttivaCa();

            //elimino direttamente il vecchio endpoint e con la post creo il nuovo e lo collego all'ente
            if(endpointAttivaCaEntity != null){
                if(endpointAttivaCaEntity.getCanaleCa().getCodCanale().equalsIgnoreCase(body.getTipoCanaleCa())){
                    body.setCertificato(endpointAttivaCaEntity.getCertificato());
                }
                endpointAttivaCaDao.delete(endpointAttivaCaEntity, entityManager);
            }

            entityTransaction.commit();

            servizioEntitiesActiveCycleRicezioneFatturePOST(idEnte, body);

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
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


    public void servizioEntitiesActiveCycleRicezioneFatturePOST(Integer idEnte, ACinvoicesRequest body) throws BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException, FatturaPACanaleNonTrovatoException, IllegalBlockSizeException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesActiveCycleRicezioneFatturePOST ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesActiveCycleRicezioneFatturePOST: ente con tipo canale associato != CA");
            }

            if(!enteEntity.getAmbienteCicloAttivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("Errore ambiente ciclo attivo");
            }

            //creo l'endpoint da collegare all'ente
            EndpointAttivaCaEntity endpointAttivaCaEntity = new EndpointAttivaCaEntity();

            String tipoCanaleCa = body.getTipoCanaleCa();
            endpointAttivaCaEntity.setCanaleCa(canaleCaDao.getCanaleCaById(tipoCanaleCa, entityManager));



            if(tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.PEC.getValue())){
                endpointAttivaCaEntity.setEndpoint(body.getEndpoint());
                enteEntity.setCodaInvioEnteAttiva(pecCaInoltroNotifiche);
            }
            else if(tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.FTP.getValue())){
                List<String> endPath = setEndpointFtp(body.getEndpoint());
                endpointAttivaCaEntity.setEndpoint(endPath.get(0));
                endpointAttivaCaEntity.setPath(endPath.get(1));
                endpointAttivaCaEntity.setUsername(body.getUser());
                endpointAttivaCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }
            else if(tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.WS.getValue()) || tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())){
                endpointAttivaCaEntity.setEndpoint(body.getEndpoint());
                endpointAttivaCaEntity.setUsername(body.getUser());
                endpointAttivaCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
                enteEntity.setCodaInvioEnteAttiva(wsCaAttivaRegistrazioneInvioQueue);
            }

            endpointAttivaCaEntity.setCertificato(body.getCertificato());
            endpointAttivaCaEntity.setDataUltimaModifica(new Date());
            endpointAttivaCaEntity.setUtenteModifica(utenteEntity);

            entityManager.persist(endpointAttivaCaEntity);

            enteEntity.setEndpointFattureAttivaCa(endpointAttivaCaEntity);

            enteDao.update(enteEntity, entityManager);

            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
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

    public void servizioEntitiesActiveCycleInoltroNotifichePUT(Integer idEnte, ACnotificationsRequest body) throws BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException, FatturaPACanaleNonTrovatoException, IllegalBlockSizeException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesActiveCycleInoltroNotifichePUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesActiveCycleInoltroNotifichePUT: ente con tipo canale associato != CA");
            }

            if(!enteEntity.getAmbienteCicloAttivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("Errore ambiente ciclo attivo");
            }

            EndpointAttivaCaEntity endpointAttivaCaEntity = enteEntity.getEndpointNotificheAttivaCa();

            if(endpointAttivaCaEntity != null){
                if(endpointAttivaCaEntity.getCanaleCa().getCodCanale().equalsIgnoreCase(body.getTipoCanaleCa())){
                    body.setCertificato(endpointAttivaCaEntity.getCertificato());
                }
                endpointAttivaCaDao.delete(endpointAttivaCaEntity, entityManager);
            }

            entityTransaction.commit();

            servizioEntitiesActiveCycleInoltroNotifichePOST(idEnte, body);

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
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

    public void servizioEntitiesActiveCycleInoltroNotifichePOST(Integer idEnte, ACnotificationsRequest body) throws BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException, FatturaPACanaleNonTrovatoException, IllegalBlockSizeException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesActiveCycleInoltroNotifichePOST ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesActiveCycleInoltroNotifichePOST: ente con tipo canale associato != CA");
            }

            if(!enteEntity.getAmbienteCicloAttivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("Errore ambiente ciclo attivo");
            }

            EndpointAttivaCaEntity endpointAttivaCaEntity = new EndpointAttivaCaEntity();

            String tipoCanaleCa = body.getTipoCanaleCa();
            endpointAttivaCaEntity.setCanaleCa(canaleCaDao.getCanaleCaById(tipoCanaleCa, entityManager));


            if(tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.PEC.getValue())){
                endpointAttivaCaEntity.setEndpoint(body.getEndpoint());
            }
            else if(tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.FTP.getValue())){
                List<String> endPath = setEndpointFtp(body.getEndpoint());
                endpointAttivaCaEntity.setEndpoint(endPath.get(0));
                endpointAttivaCaEntity.setPath(endPath.get(1));
                endpointAttivaCaEntity.setUsername(body.getUser());
                endpointAttivaCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }
            else if(tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.WS.getValue()) || tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanaleCa.equals(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())){
                endpointAttivaCaEntity.setEndpoint(body.getEndpoint());
                endpointAttivaCaEntity.setUsername(body.getUser());
                endpointAttivaCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }

            endpointAttivaCaEntity.setCertificato(body.getCertificato());
            endpointAttivaCaEntity.setDataUltimaModifica(new Date());
            endpointAttivaCaEntity.setUtenteModifica(utenteEntity);

            enteEntity.setEndpointNotificheAttivaCa(endpointAttivaCaEntity);

            entityManager.persist(endpointAttivaCaEntity);

            enteDao.update(enteEntity, entityManager);

            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPACanaleNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
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

    public void servizioEntitiesActiveCyclePassaggioProduzionePUT(Integer idEnte, String username) throws FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesActiveCyclePassaggioProduzionePUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(username, entityManager);

            if(!enteEntity.isCicloAttivo()){
                throw new FatturaPAException("Ciclo Attivo settato a false");
            }

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            enteEntity.setAmbienteCicloAttivo("PRODUZIONE");

            enteDao.update(enteEntity, entityManager);

            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException e) {
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

    public void servizioEntitiesPassiveCyclePassaggioProduzionePUT(Integer idEnte, String username) throws FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCyclePassaggioProduzionePUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(username, entityManager);

            if(!enteEntity.getCicloPassivo()){
                throw new FatturaPAException("Ciclo Passivo settato a false");
            }

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            enteEntity.setAmbienteCicloPassivo("PRODUZIONE");

            enteDao.update(enteEntity, entityManager);

            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException e) {
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

    public void servizioEntitiesPassiveCycleProtocolloPUT(Integer idEnte, PCprotocolRequest body) throws FatturaPACanaleNonTrovatoException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, BadPaddingException, FatturaPAException, IllegalBlockSizeException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCycleProtocolloPUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleProtocolloPUT: ente con tipo canale associato != CA");
            }

            //controllo che l'ambiente di ciclo passivo sia STAGING
            if(!enteEntity.getAmbienteCicloPassivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleProtocolloPUT: ambienteCicloPassivo != STAGING");
            }

            EndpointCaEntity endpointCaEntity = enteEntity.getEndpointProtocolloCa();
            EntePaleoCaEntity entePaleoCaEntity = enteEntity.getEntePaleoCaEntity();

            if(endpointCaEntity != null){
                if(endpointCaEntity.getCanaleCa().getCodCanale().equalsIgnoreCase(body.getTipoCanaleCa())){
                    body.setCertificato(endpointCaEntity.getCertificato());
                }
                endpointCaDao.delete(endpointCaEntity, entityManager);
            }

            entityTransaction.commit();

            servizioEntitiesPassiveCycleProtocolloPOST(idEnte, body);

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPACanaleNonTrovatoException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | FatturaPAException
                | IllegalBlockSizeException e) {
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

    public void servizioEntitiesPassiveCycleProtocolloPOST(Integer idEnte, PCprotocolRequest body) throws FatturaPACanaleNonTrovatoException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, BadPaddingException, FatturaPAException, IllegalBlockSizeException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCycleProtocolloPOST ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleProtocolloPOST: ente con tipo canale associato != CA");
            }

            //controllo che l'ambiente di ciclo passivo sia STAGING
            if(!enteEntity.getAmbienteCicloPassivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleProtocolloPOST: ambienteCicloPassivo != STAGING");
            }

            String tipoCanaleCa = body.getTipoCanaleCa();

            CanaleCaEntity canaleCaEntity = canaleCaDao.getCanaleCaById(tipoCanaleCa, entityManager);

            enteEntity.setInvioUnico(body.getInvioUnico());

            if(body.getInvioUnico()){
                EndpointCaEntity endpointRegistrazioneCaEntity = enteEntity.getEndpointRegistrazioneCa();
                enteEntity.setCodaGestionaleCa(null);
                if(endpointRegistrazioneCaEntity != null){
                    endpointCaDao.delete(endpointRegistrazioneCaEntity, entityManager);
                    enteEntity.setEndpointRegistrazioneCa(null);
                }
            }

            EntePaleoCaEntity entePaleoCaEntity1 = enteEntity.getEntePaleoCaEntity();
            if(entePaleoCaEntity1 != null){
                entePaleoCaDao.delete(entePaleoCaEntity1, entityManager);
                enteEntity.setEntePaleoCaEntity(null);
            }

            EndpointCaEntity endpointCaProtocollo = enteEntity.getEndpointProtocolloCa();
            if(endpointCaProtocollo != null){
                endpointCaDao.delete(endpointCaProtocollo, entityManager);
            }

            EndpointCaEntity endpointCaEntity = new EndpointCaEntity();
            endpointCaEntity.setCanaleCa(canaleCaEntity);

            EntePaleoCaEntity entePaleoCaEntity = new EntePaleoCaEntity();

            if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.PEC.getValue())){
                endpointCaEntity.setEndpoint(body.getEndpoint());
                enteEntity.setCodaProtocolloCa(pecProtocolloCaCoda);
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS.getValue()) && body.getInvioUnico())) {
                enteEntity.setCodaProtocolloCa(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) ? wsProtocolloAgidCaCoda : wsRegistrazioneCaCoda);
                endpointCaEntity.setEndpoint(body.getEndpoint());
                endpointCaEntity.setUsername(body.getUser());
                endpointCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.FTP.getValue())){
                List<String> endPath = setEndpointFtp(body.getEndpoint());
                endpointCaEntity.setEndpoint(endPath.get(0));
                endpointCaEntity.setPath(endPath.get(1));
                endpointCaEntity.setUsername(body.getUser());
                endpointCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())){
                endpointCaEntity.setEndpoint(body.getEndpoint());
                enteEntity.setCodaProtocolloCa(wsProtocolloPaleoCaCoda);
                entePaleoCaEntity.setUserIdWs(body.getUser());
                entePaleoCaEntity.setPasswordWs(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
                entePaleoCaEntity.setProtocollistaNome(body.getProtocollistaNome());
                entePaleoCaEntity.setProtocollistaCognome(body.getProtocollistaCognome());
                entePaleoCaEntity.setProtocollistaUo(body.getProtocollistaUo());
                entePaleoCaEntity.setProtocollistaRuolo(body.getProtocollistaRuolo());
                entePaleoCaEntity.setResponsabileProcedimentoNome(body.getResponsabileProcedimentoNome());
                entePaleoCaEntity.setResponsabileProcedimentoCognome(body.getResponsabileProcedimentoCognome());
                entePaleoCaEntity.setResponsabileProcedimentoUo(body.getResponsabileProcedimentoUo());
                entePaleoCaEntity.setResponsabileProcedimentoRuolo(body.getResponsabileProcedimentoRuolo());
                entePaleoCaEntity.setCodiceRegistro(body.getCodiceRegistro());
                entePaleoCaEntity.setCodiceAMM(body.getCodiceAmm());
                endpointCaEntity.setCertificato(body.getCertificato());

                entityManager.persist(entePaleoCaEntity);

                enteEntity.setEntePaleoCaEntity(entePaleoCaEntity);
            }

            endpointCaEntity.setCertificato(body.getCertificato());
            endpointCaEntity.setDataUltimaModifica(new Date());
            endpointCaEntity.setUtenteModifica(utenteEntity);

            EndpointCaEntity endpointRegistrazioneCa = enteEntity.getEndpointRegistrazioneCa();

            if(body.getInvioUnico()){
                if(endpointRegistrazioneCa != null) {
                    endpointCaDao.delete(endpointRegistrazioneCa, entityManager);
                    enteEntity.setEndpointRegistrazioneCa(null);
                }
            }

            entityManager.persist(endpointCaEntity);

            enteEntity.setEndpointProtocolloCa(endpointCaEntity);

            enteDao.update(enteEntity, entityManager);

            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPACanaleNonTrovatoException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | FatturaPAException
                | IllegalBlockSizeException e) {
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

    public void servizioEntitiesPassiveCycleRegistrazionePUT(Integer idEnte, PCregistrationRequest body) throws FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAException, FatturaPACanaleNonTrovatoException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCycleRegistrazionePUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleRegistrazionePUT: ente con tipo canale associato != CA");
            }

            //controllo che l'ambiente di ciclo passivo sia STAGING
            if(!enteEntity.getAmbienteCicloPassivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleRegistrazionePUT: ambienteCicloPassivo != STAGING");
            }

            if(enteEntity.getInvioUnico()){
                throw new FatturaPAException("servizioEntitiesPassiveCycleRegistrazionePUT: invioUnico == true");
            }

            EndpointCaEntity endpointCaEntity = enteEntity.getEndpointRegistrazioneCa();

            if(endpointCaEntity != null){
                if(endpointCaEntity.getCanaleCa().getCodCanale().equalsIgnoreCase(body.getTipoCanaleCa())){
                    body.setCertificato(endpointCaEntity.getCertificato());
                }
                endpointCaDao.delete(endpointCaEntity, entityManager);
            }

            entityTransaction.commit();

            servizioEntitiesPassiveCycleRegistrazionePOST(idEnte, body);

        } catch (FatturaPAUtenteNonTrovatoException
                | BadPaddingException
                | FatturaPAUtenteNonAutorizzatoException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException e) {
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

    public void servizioEntitiesPassiveCycleRegistrazionePOST(Integer idEnte, PCregistrationRequest body) throws FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, IllegalBlockSizeException, FatturaPAException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, FatturaPACanaleNonTrovatoException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCycleRegistrazionePOST ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsernameUtente(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleRegistrazionePOST: ente con tipo canale associato != CA");
            }

            //controllo che l'ambiente di ciclo passivo sia STAGING
            if(!enteEntity.getAmbienteCicloPassivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleRegistrazionePOST: ambienteCicloPassivo != STAGING");
            }

            if(enteEntity.getInvioUnico()){
                throw new FatturaPAException("servizioEntitiesPassiveCycleRegistrazionePUT: invioUnico == true");
            }

            String tipoCanaleCa = body.getTipoCanaleCa();

            CanaleCaEntity canaleCaEntity = canaleCaDao.getCanaleCaById(tipoCanaleCa, entityManager);


            EndpointCaEntity endpointCaEntity = new EndpointCaEntity();
            endpointCaEntity.setCanaleCa(canaleCaEntity);

            if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.PEC.getValue())){
                endpointCaEntity.setEndpoint(body.getEndpoint());
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS.getValue())){
                enteEntity.setCodaGestionaleCa(wsRegistrazioneCaCoda);
                endpointCaEntity.setEndpoint(body.getEndpoint());
                endpointCaEntity.setUsername(body.getUser());
                endpointCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.FTP.getValue())){
                List<String> endPath = setEndpointFtp(body.getEndpoint());
                endpointCaEntity.setEndpoint(endPath.get(0));
                endpointCaEntity.setPath(endPath.get(1));
                endpointCaEntity.setUsername(body.getUser());
                endpointCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }

            endpointCaEntity.setCertificato(body.getCertificato());
            endpointCaEntity.setDataUltimaModifica(new Date());
            endpointCaEntity.setUtenteModifica(utenteEntity);

            enteEntity.setEndpointRegistrazioneCa(endpointCaEntity);

            entityManager.persist(endpointCaEntity);

            enteDao.update(enteEntity, entityManager);

            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPACanaleNonTrovatoException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | FatturaPAException
                | IllegalBlockSizeException e) {
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

    public void servizioEntitiesPassiveCycleEsitoCommittentePUT(Integer idEnte, PCesitoCommittenteRequest body) throws FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, IllegalBlockSizeException, BadPaddingException, FatturaPACanaleNonTrovatoException, NoSuchAlgorithmException, FatturaPAException, NoSuchPaddingException, InvalidKeyException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCycleEsitoCommittentePUT ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsername(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleEsitoCommittentePUT: ente con tipo canale associato != CA");
            }

            //controllo che l'ambiente di ciclo passivo sia STAGING
            if(!enteEntity.getAmbienteCicloPassivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleEsitoCommittentePUT: ambienteCicloPassivo != STAGING");
            }

            EndpointCaEntity endpointCaEntity = enteEntity.getEndpointEsitoCommittenteCa();

            if(endpointCaEntity != null){
                if(endpointCaEntity.getCanaleCa().getCodCanale().equalsIgnoreCase(body.getTipoCanaleCa())){
                    body.setCertificato(endpointCaEntity.getCertificato());
                }
                endpointCaDao.delete(endpointCaEntity, entityManager);
            }

            entityTransaction.commit();

            servizioEntitiesPassiveCycleEsitoCommittentePOST(idEnte, body);

        } catch (FatturaPAUtenteNonTrovatoException
                | IllegalBlockSizeException
                | FatturaPACanaleNonTrovatoException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | FatturaPAException
                | FatturaPAUtenteNonAutorizzatoException e) {
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

    public void servizioEntitiesPassiveCycleEsitoCommittentePOST(Integer idEnte, PCesitoCommittenteRequest body) throws FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException, IllegalBlockSizeException, FatturaPACanaleNonTrovatoException, NoSuchAlgorithmException, FatturaPAException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        EntityManager entityManager = null;

        LOG.info("*************** EnteManager: servizioEntitiesPassiveCycleEsitoCommittentePOST ***************");

        try {

            entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            BigInteger idEnte2 = BigInteger.valueOf(idEnte);

            EnteEntity enteEntity = enteDao.read(idEnte2, entityManager);

            UtenteEntity utenteEntity = utentiDao.getUtenteByUsername(body.getUsername(), entityManager);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(enteEntity);
            utenteEnteEntity.setUtente(utenteEntity);

            //controllo i diritti
            if(!utentiManager.checkAuth(utenteEnteEntity, true)) {
                throw new FatturaPAUtenteNonAutorizzatoException();
            }

            //controllo che il tipo_canale dell'ente sia CA
            if(!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleProtocolloPUT: ente con tipo canale associato != CA");
            }

            //controllo che l'ambiente di ciclo passivo sia STAGING
            if(!enteEntity.getAmbienteCicloPassivo().equalsIgnoreCase("STAGING")) {
                throw new FatturaPAException("servizioEntitiesPassiveCycleProtocolloPUT: ambienteCicloPassivo != STAGING");
            }

            String tipoCanaleCa = body.getTipoCanaleCa();

            CanaleCaEntity canaleCaEntity = canaleCaDao.getCanaleCaById(tipoCanaleCa, entityManager);


            EndpointCaEntity endpointCaEntity = new EndpointCaEntity();
            endpointCaEntity.setCanaleCa(canaleCaEntity);

            if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.PEC.getValue())){
                endpointCaEntity.setEndpoint(body.getEndpoint());
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS.getValue()) || tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())){
                endpointCaEntity.setEndpoint(body.getEndpoint());
                endpointCaEntity.setUsername(body.getUser());
                endpointCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }
            else if(tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.FTP.getValue())){
                List<String> endPath = setEndpointFtp(body.getEndpoint());
                endpointCaEntity.setEndpoint(endPath.get(0));
                endpointCaEntity.setPath(endPath.get(1));
                endpointCaEntity.setUsername(body.getUser());
                endpointCaEntity.setPassword(CommonUtils.encryptPassword(body.getPassword(), chiaveDao.getChiave(entityManager)));
            }

            endpointCaEntity.setCertificato(body.getCertificato());
            endpointCaEntity.setDataUltimaModifica(new Date());
            endpointCaEntity.setUtenteModifica(utenteEntity);

            enteEntity.setEndpointEsitoCommittenteCa(endpointCaEntity);

            entityManager.persist(endpointCaEntity);

            enteDao.update(enteEntity, entityManager);


            entityTransaction.commit();

        } catch (FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPACanaleNonTrovatoException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | NoSuchPaddingException
                | BadPaddingException
                | FatturaPAException
                | IllegalBlockSizeException e) {
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

    public TipoCanaleCaList getAllTipoCanaleCa() throws FatturaPACanaleNonTrovatoException {

        LOG.info("*************** EnteManager: getAllTipoCanaleCa ***************");

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            return canaleCaDao.getAllTipoCanaleCa(entityManager);

        } catch (FatturaPACanaleNonTrovatoException e) {
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

    private List<String> setEndpointFtp(String endpoint){
        List<String> ret = new ArrayList<>();
        String[] split = endpoint.split("/");
        ret.add(split[0]+"//"+split[2]);
        String pathFtp = null;
        for(int i=3; i<split.length; i++) {
            if(i != split.length - 1) {
                if(pathFtp == null){
                    pathFtp = split[i]+"/";
                }
                else {
                    pathFtp = pathFtp.concat(split[i] + "/");
                }
            }
            else if(i == split.length - 1 ){
                if(pathFtp == null){
                    pathFtp = split[i];
                }
                else {
                    pathFtp = pathFtp.concat(split[i]);
                }
            }
        }
        ret.add(pathFtp);

        return ret;
    }

    public List<EnteEntity> getEnteByCodiceUfficioFtpReportSt(String codiceUfficio) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {

        EntityManager entityManager = null;
        EnteEntity enteEntity;
        List<EnteEntity> enteEntityList = new ArrayList<>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            enteEntity = enteDao.getEnteByCodiceUfficio(codiceUfficio, entityManager);
            enteEntityList.add(enteEntity);

        } catch (FatturaPaPersistenceException | FatturaPAEnteNonTrovatoException e) {
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

        return enteEntityList;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EnteDao getEnteDao() {
        return enteDao;
    }

    public void setEnteDao(EnteDao enteDao) {
        this.enteDao = enteDao;
    }

    public UtentiDao getUtentiDao() {
        return utentiDao;
    }

    public void setUtentiDao(UtentiDao utentiDao) {
        this.utentiDao = utentiDao;
    }

    public void setTipoCanaleDao(TipoCanaleDao tipoCanaleDao) {
        this.tipoCanaleDao = tipoCanaleDao;
    }

    public void setEndpointAttivaCaDao(EndpointAttivaCaDao endpointAttivaCaDao) {
        this.endpointAttivaCaDao = endpointAttivaCaDao;
    }

    public void setEndpointCaDao(EndpointCaDao endpointCaDao) {
        this.endpointCaDao = endpointCaDao;
    }

    public void setCanaleCaDao(CanaleCaDao canaleCaDao) {
        this.canaleCaDao = canaleCaDao;
    }

    public void setChiaveDao(ChiaveDao chiaveDao) {
        this.chiaveDao = chiaveDao;
    }

    public void setEntePaleoCaDao(EntePaleoCaDao entePaleoCaDao) {
        this.entePaleoCaDao = entePaleoCaDao;
    }

    public UtentiManager getUtentiManager() {
        return utentiManager;
    }

    public void setUtentiManager(UtentiManager utentiManager) {
        this.utentiManager = utentiManager;
    }

    public String getPecProtocolloCaCoda() {
        return pecProtocolloCaCoda;
    }

    public void setPecProtocolloCaCoda(String pecProtocolloCaCoda) {
        this.pecProtocolloCaCoda = pecProtocolloCaCoda;
    }

    public String getWsProtocolloPaleoCaCoda() {
        return wsProtocolloPaleoCaCoda;
    }

    public void setWsProtocolloPaleoCaCoda(String wsProtocolloPaleoCaCoda) {
        this.wsProtocolloPaleoCaCoda = wsProtocolloPaleoCaCoda;
    }

    public String getWsProtocolloAgidCaCoda() {
        return wsProtocolloAgidCaCoda;
    }

    public void setWsProtocolloAgidCaCoda(String wsProtocolloAgidCaCoda) {
        this.wsProtocolloAgidCaCoda = wsProtocolloAgidCaCoda;
    }

    public String getWsRegistrazioneCaCoda() {
        return wsRegistrazioneCaCoda;
    }

    public void setWsRegistrazioneCaCoda(String wsRegistrazioneCaCoda) {
        this.wsRegistrazioneCaCoda = wsRegistrazioneCaCoda;
    }

    public String getPecRegistrazioneCaCoda() {
        return pecRegistrazioneCaCoda;
    }

    public void setPecRegistrazioneCaCoda(String pecRegistrazioneCaCoda) {
        this.pecRegistrazioneCaCoda = pecRegistrazioneCaCoda;
    }

    public String getWsCaAttivaRegistrazioneInvioQueue() {
        return wsCaAttivaRegistrazioneInvioQueue;
    }

    public void setWsCaAttivaRegistrazioneInvioQueue(String wsCaAttivaRegistrazioneInvioQueue) {
        this.wsCaAttivaRegistrazioneInvioQueue = wsCaAttivaRegistrazioneInvioQueue;
    }

    public String getPecCaInoltroNotifiche() {
        return pecCaInoltroNotifiche;
    }

    public void setPecCaInoltroNotifiche(String pecCaInoltroNotifiche) {
        this.pecCaInoltroNotifiche = pecCaInoltroNotifiche;
    }
}
