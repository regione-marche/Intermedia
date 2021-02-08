package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonAutorizzatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;


public class EnteDao extends GenericDao<EnteEntity, BigInteger> {


    private static final Logger LOG = LoggerFactory.getLogger(EnteDao.class);

    public EnteEntity getEnteByIdFiscaleCommittenteAndCodiceUfficio(String idFiscaleCommittente, String codiceUfficio, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EnteEntity enteEntity = null;

        LOG.info("*************** EnteDao: getEnteByIdFiscaleCommittenteAndCodiceUfficio ***************");

        try {
            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE ente.idFiscaleCommittente = :idFiscaleCommittente and ente.codiceUfficio = :codiceUfficio", EnteEntity.class);
            query.setParameter("idFiscaleCommittente", idFiscaleCommittente);
            query.setParameter("codiceUfficio", codiceUfficio);

            enteEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for idFiscaleCommittente " + idFiscaleCommittente + " and codiceUfficio " + codiceUfficio);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public List<EnteEntity> getEnteByIdFiscaleCommittente(String idFiscaleCommittente, String username, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPAUtenteNonTrovatoException {
        List<EnteEntity> enteEntityList = null;

        LOG.info("*************** EnteDao: getEnteByIdFiscaleCommittente ***************");

        try {

            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE UPPER(ente.idFiscaleCommittente) = :idFiscaleCommittente", EnteEntity.class);
            query.setParameter("idFiscaleCommittente", idFiscaleCommittente.toUpperCase());

            enteEntityList = query.getResultList();

            if(enteEntityList == null || enteEntityList.size() == 0){
                throw new NoResultException();
            }

            return enteEntityList;

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for idFiscaleCommittente " + idFiscaleCommittente);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        } catch (Exception e){
            throw e;
        }
    }

    public List<EnteEntity> getEnteByDenominazioneEnte(String denominazioneEnte, String usernameUtente, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        List<EnteEntity> enteEntityList = null;

        LOG.info("********** EnteDao: getEnteByDenominazioneEnte **********");

        try {
            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE UPPER(ente.denominazioneEnte) LIKE :denominazioneEnte", EnteEntity.class);
            query.setParameter("denominazioneEnte", "%" + denominazioneEnte.toUpperCase() + "%");

            enteEntityList = query.getResultList();

            if(enteEntityList == null || enteEntityList.size() == 0){
                throw new NoResultException();
            }

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for denominazioneEnte " + denominazioneEnte);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntityList;
    }

    public List<EnteEntity> getAllEnti(EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        List<EnteEntity> enteEntityList = null;

        LOG.info("********** EnteDao: getEnteByUser **********");

        try {


            TypedQuery<EnteEntity> query;

            query = entityManager.createQuery("SELECT ente FROM EnteEntity ente ORDER BY ente.denominazioneEnte ASC", EnteEntity.class);

            enteEntityList = query.getResultList();

            if(enteEntityList == null || enteEntityList.size() == 0){
                throw new NoResultException();
            }


        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntityList;
    }

    public EnteEntity getEnteByCodiceUfficio(String codiceUfficio, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        EnteEntity enteEntity = null;

        LOG.info("*************** EnteDao: getEnteByCodiceUfficio ***************");

        try {
            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE UPPER(ente.codiceUfficio) = :codiceUfficio", EnteEntity.class);
            query.setParameter("codiceUfficio", codiceUfficio.toUpperCase());

            enteEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato per codiceUfficio " + codiceUfficio);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }


    public List<EnteEntity> getEnteByPecAddress(String pecAddress, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        List<EnteEntity> enteEntityList = null;

        LOG.info("*************** EnteDao: getEnteByPecAddress ***************");

        try {
            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE ente.emailPec LIKE :patternEmail", EnteEntity.class);
            query.setParameter("patternEmail", "%" + pecAddress + "%");

            enteEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for emailPec " + pecAddress);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntityList;
    }

    public List<EnteEntity> getEnteFtpInvioSingoloByTipoCanale(String tipoCanale, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        List<EnteEntity> enteEntity = null;

        LOG.info("*************** EnteDao: getEnteFtpInvioSingoloByTipoCanale ***************");

        try {
            String queryString = "SELECT ente FROM EnteEntity ente JOIN ente.endpointProtocolloCa prot WHERE ente.tipoCanale.codTipoCanale = '004' AND ente.endpointRegistrazioneCa IS NULL " +
                    " AND ente.endpointProtocolloCa IS NOT NULL AND prot.canaleCa.codCanale  = :tipoCanale";
            TypedQuery<EnteEntity> query = entityManager.createQuery(queryString, EnteEntity.class);
            query.setParameter("tipoCanale", tipoCanale);

            enteEntity = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for tipoCanale " + tipoCanale);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public List<EnteEntity> getEnteFtpInvioProtocolloByTipoCanale(String tipoCanale, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        List<EnteEntity> enteEntity = null;

        LOG.info("*************** EnteDao: getEnteFtpInvioProtocolloByTipoCanale ***************");

        try {
            String queryString = "SELECT ente FROM EnteEntity ente JOIN ente.endpointProtocolloCa prot WHERE ente.tipoCanale.codTipoCanale = '004' AND ente.endpointRegistrazioneCa IS NOT NULL" +
                    " AND ente.endpointProtocolloCa IS NOT NULL AND prot.canaleCa.codCanale  = :tipoCanale";
            TypedQuery<EnteEntity> query = entityManager.createQuery(queryString, EnteEntity.class);
            query.setParameter("tipoCanale", tipoCanale);

            enteEntity = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for tipoCanale " + tipoCanale);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public List<EnteEntity> getEnteFtpInvioGestionaleByTipoCanale(String tipoCanale, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        List<EnteEntity> enteEntity = null;

        LOG.info("*************** EnteDao: getEnteFtpInvioGestionaleByTipoCanale ***************");

        try {
            String queryString = "SELECT ente FROM EnteEntity ente JOIN ente.endpointRegistrazioneCa reg WHERE ente.tipoCanale.codTipoCanale = '004' AND ente.endpointRegistrazioneCa IS NOT NULL" +
                    " AND ente.endpointProtocolloCa IS NOT NULL AND reg.canaleCa.codCanale  = :tipoCanale";
            TypedQuery<EnteEntity> query = entityManager.createQuery(queryString, EnteEntity.class);
            query.setParameter("tipoCanale", tipoCanale);

            enteEntity = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for tipoCanale " + tipoCanale);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public List<EnteEntity> getEnteFtpInvioFatturaAttivaByTipoCanale(String tipoCanale, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        List<EnteEntity> enteEntity = null;

        LOG.info("*************** EnteDao: getEnteFtpInvioFatturaAttivaByTipoCanale ***************");

        try {
            String queryString = "SELECT ente FROM EnteEntity ente JOIN ente.endpointFattureAttivaCa reg WHERE ente.tipoCanale.codTipoCanale = '004' AND ente.endpointFattureAttivaCa IS NOT NULL " +
                    " AND reg.canaleCa.codCanale  = :tipoCanale";
            TypedQuery<EnteEntity> query = entityManager.createQuery(queryString, EnteEntity.class);
            query.setParameter("tipoCanale", tipoCanale);

            enteEntity = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for tipoCanale " + tipoCanale);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public List<EnteEntity> getEnteFtpRicezioneFatturaByTipoCanale(String tipoCanale, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        List<EnteEntity> enteEntity = null;

        LOG.info("*************** EnteDao: getEnteFtpRicezioneFatturaByTipoCanale ***************");

        try {
            String queryString = "SELECT ente FROM EnteEntity ente JOIN ente.endpointFattureAttivaCa reg WHERE ente.tipoCanale.codTipoCanale = '004' AND ente.endpointFattureAttivaCa IS NOT NULL " +
                    " AND reg.canaleCa.codCanale  = :tipoCanale";
            TypedQuery<EnteEntity> query = entityManager.createQuery(queryString, EnteEntity.class);
            query.setParameter("tipoCanale", tipoCanale);

            enteEntity = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for tipoCanale " + tipoCanale);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public List<EnteEntity> getEnteFtpByTipoCanale(String tipoCanale, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException {
        List<EnteEntity> enteEntity = null;

        LOG.info("*************** EnteDao: getEnteFtpByTipoCanale ***************");

        try {
            String queryString = "SELECT ente FROM EnteEntity ente WHERE ente.tipoCanale.codTipoCanale = :tipoCanale ";
            TypedQuery<EnteEntity> query = entityManager.createQuery(queryString, EnteEntity.class);
            query.setParameter("tipoCanale", tipoCanale);

            enteEntity = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato for tipoCanale " + tipoCanale);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

    public EnteEntity getEnteByEndpointAttivaPecCA(String pecAddress, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        EnteEntity enteEntity = null;

        LOG.info("*************** EnteDao: getEnteByEndpointAttivaPecCA ***************");

        try {

            /*
            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE ente.endpointFattureAttivaCa.endpoint = :pecAddress AND ente.endpointFattureAttivaCa.canaleCa.codCanale = :tipoCanale", EnteEntity.class);
            query.setParameter("pecAddress", pecAddress);
            */
            //Bisogna fare un like in quanto in caso di più pec non funziona perchè è stringa unica separata da ';'
            TypedQuery<EnteEntity> query = entityManager.createQuery("SELECT ente FROM EnteEntity ente WHERE ente.endpointFattureAttivaCa.endpoint LIKE :pecAddress AND ente.endpointFattureAttivaCa.canaleCa.codCanale = :tipoCanale", EnteEntity.class);
            query.setParameter("pecAddress", "%" + pecAddress + "%");
            query.setParameter("tipoCanale", "004");

            enteEntity = query.getSingleResult();

        } catch (NonUniqueResultException e) {
            throw new FatturaPaPersistenceException("Trovati più enti per pecAddress = [" + pecAddress + "]");
        }catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Ente non trovato per pecAddress = [" + pecAddress + "]");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }

}