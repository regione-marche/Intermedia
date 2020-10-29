package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 15/03/15.
 */
public class FatturaAttivaDao extends GenericDao<FatturaAttivaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(FatturaAttivaDao.class);
    private static long month = 2629800000L;
    private static long year = 31557600000L;

    public FatturaAttivaEntity getFatturaAttivaFromIdentificativoSdi(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.identificativoSdi = :identificativoSdi ", FatturaAttivaEntity.class);
            query.setParameter("identificativoSdi", identificativoSdi);

            fatturaAttivaEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return fatturaAttivaEntity;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaFromNomeFile(String nomeFile, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<FatturaAttivaEntity> fatturaAttivaEntityList = null;

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.nomeFile = :nomeFile ", FatturaAttivaEntity.class);
            query.setParameter("nomeFile", nomeFile);

            fatturaAttivaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return fatturaAttivaEntityList;
    }

    public FatturaAttivaEntity getFatturaAttivaFromIdFatturaAttiva(BigInteger idFatturaAttiva, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.idFatturaAttiva = :idFatturaAttiva ", FatturaAttivaEntity.class);
            query.setParameter("idFatturaAttiva", idFatturaAttiva);

            fatturaAttivaEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return fatturaAttivaEntity;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaFromNomeFileFattura(String nomeFile, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        FatturaAttivaEntity fatturaAttivaEntity = null;
        List<FatturaAttivaEntity> retList = new ArrayList<>();

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.nomeFile = :nomeFile AND fatturaAttiva.fatturazioneTest = :fatturazioneTest", FatturaAttivaEntity.class);
            query.setParameter("nomeFile", nomeFile);
            query.setParameter("fatturazioneTest", false);

            retList = query.getResultList();

            if (retList.size() == 0) {
                throw new FatturaPAFatturaNonTrovataException("Fattura " + nomeFile + " non trovata");
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return retList;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaListFromIdentificativoSdi(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<FatturaAttivaEntity> retList = new ArrayList<>();

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.identificativoSdi = :identificativoSdi AND fatturaAttiva.fatturazioneTest = :fatturazioneTest", FatturaAttivaEntity.class);
            query.setParameter("identificativoSdi", identificativoSdi);
            query.setParameter("fatturazioneTest", false);

            retList = query.getResultList();

            if (retList.size() == 0) {
                throw new FatturaPAFatturaNonTrovataException();
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return retList;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaListFromIdentificativoSdiTest(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<FatturaAttivaEntity> retList = new ArrayList<>();

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.identificativoSdi = :identificativoSdi AND fatturaAttiva.fatturazioneTest = :fatturazioneTest", FatturaAttivaEntity.class);
            query.setParameter("identificativoSdi", identificativoSdi);
            query.setParameter("fatturazioneTest", true);

            retList = query.getResultList();

            if (retList.size() == 0) {
                throw new FatturaPAFatturaNonTrovataException();
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return retList;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaListUltimoMese(EntityManager entityManager) throws FatturaPaPersistenceException {

        LOG.info("********** FatturaAttivaDao: getFatturaAttivaListUltimoMese **********");

        Date now = new Date();
        long nowMilli = now.getTime();
        long monthAgoMilli = nowMilli - month;
        Date monthAgo = new Date(monthAgoMilli);

        List<FatturaAttivaEntity> retList = new ArrayList<>();

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.fatturazioneTest = false AND fatturaAttiva.dataRicezioneFromEnti BETWEEN :from AND :to ", FatturaAttivaEntity.class);
            query.setParameter("from", monthAgo);
            query.setParameter("to", now);

            retList = query.getResultList();

            if (retList.size() == 0) {
                return null;
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return retList;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaListUltimoMeseUtente(UtenteEntity utenteEntity, EntityManager entityManager) throws FatturaPaPersistenceException {

        LOG.info("********** FatturaAttivaDao: getFatturaAttivaListUltimoMeseUtente **********");

        Date now = new Date();
        long nowMilli = now.getTime();
        long monthAgoMilli = nowMilli - month;
        Date monthAgo = new Date(monthAgoMilli);

        List<FatturaAttivaEntity> retList = new ArrayList<>();

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva, UtenteEnteEntity ue WHERE fatturaAttiva.fatturazioneTest = false AND fatturaAttiva.ente = ue.ente AND ue.utente = :utente AND fatturaAttiva.dataRicezioneFromEnti BETWEEN :from AND :to ", FatturaAttivaEntity.class);
            query.setParameter("from", monthAgo);
            query.setParameter("to", now);
            query.setParameter("utente", utenteEntity);

            retList = query.getResultList();

            if (retList.size() == 0) {
                return null;
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return retList;
    }


    public List<Object[]> getFatturaAttivaBetweenDate(Date from, Date to, EntityManager entityManager) throws FatturaPaPersistenceException {

        LOG.info("********** FatturaAttivaDao: getFatturaAttivaBetweenDate **********");

        try {
            String queryString = "SELECT tc.desc_canale, e.id_endpoint_fatture_attiva_ca, cca.desc_canale FROM fattura_attiva fa " +
                    " JOIN ente AS e ON fa.codice_destinatario = e.codice_ufficio " +
                    " JOIN tipo_canale tc on e.id_tipo_canale = tc.cod_tipo_canale " +
                    " left join endpoint_attiva_ca ca on e.id_endpoint_fatture_attiva_ca = ca.id_endpoint_ca " +
                    " full outer join canale_ca as cca on ca.id_canale_ca = cca.cod_canale " +
                    " WHERE fa.fatturazione_test = 'F' AND  fa.data_ricezione_from_enti " +
                    " BETWEEN ? AND ?";

            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, from);
            query.setParameter(2, to);

            return query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }



    public List<Object[]> getFatturaAttivaBetweenDateUtente(Date from, Date to, UtenteEntity utenteEntity, EntityManager entityManager) throws FatturaPaPersistenceException {

        LOG.info("********** FatturaAttivaDao: getFatturaAttivaBetweenDateUtente **********");

        try {
            String queryString = "SELECT tc.desc_canale, e.id_endpoint_fatture_attiva_ca, cca.desc_canale FROM fattura_attiva as dfe " +
                    "JOIN ente AS e ON dfe.codice_destinatario = e.codice_ufficio " +
                    "join utente_ente ue ON (ue.id_ente = e.id_ente ) " +
                    "join utenti u ON(ue.id_utente = u.id_utente ) " +
                    "JOIN tipo_canale tc on e.id_tipo_canale = tc.cod_tipo_canale " +
                    "left JOIN endpoint_ca as ec ON e.id_endpoint_protocollo_ca =ec.id_endpoint_ca " +
                    "full outer join canale_ca as cca on ec.id_canale_ca = cca.cod_canale " +
                    "WHERE dfe.fatturazione_test = 'F' and u.username =? " +
                    "AND dfe.data_ricezione_from_enti BETWEEN ? AND ?";

            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, utenteEntity.getUsername());
            query.setParameter(2, from);
            query.setParameter(3, to);

            return query.getResultList();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }


    public List<FatturaAttivaEntity> getFatturaAttivaListFromCodiceUfficioMittenteAndDate(String codiceUfficio, Date from, Date to, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<FatturaAttivaEntity> retList = new ArrayList<>();
        List<FatturaAttivaEntity> retList2 = new ArrayList<>();

        try {
            TypedQuery<FatturaAttivaEntity> query = entityManager.createQuery("SELECT fatturaAttiva FROM FatturaAttivaEntity fatturaAttiva WHERE fatturaAttiva.fatturazioneTest = :fatturazioneTest AND fatturaAttiva.dataRicezioneFromEnti BETWEEN :from AND :to ", FatturaAttivaEntity.class);
            query.setParameter("from", from);
            query.setParameter("to", to);
            query.setParameter("fatturazioneTest", false);

            retList = query.getResultList();

            if (retList.size() == 0) {
                throw new FatturaPAFatturaNonTrovataException();
            }

            for (FatturaAttivaEntity entity : retList) {
                if (codiceUfficio.equals(entity.getEnte().getCodiceUfficio())) {
                    retList2.add(entity);
                }
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return retList2;
    }

    public List<Object[]> getIdentificativoSdiListBeforeDate(Date dataRiferimento, EntityManager entityManager) {

        String queryString = "SELECT fa.id_fattura_attiva, fa.identificativo_sdi, fa.nome_file, data_ricezione_from_enti, fatturazione_interna, codice_destinatario, pec_destinatario, ricevuta_comunicazione, e.codice_ufficio " +
                " FROM fattura_attiva fa, ente e WHERE fa.ente=e.id_ente AND fa.data_ricezione_from_enti <= ? AND fatturazione_test = false";

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, dataRiferimento);

        List<Object[]> list = query.getResultList();

        return list;
    }

    public int deleteByIdentificativoSdi(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM fattura_attiva WHERE identificativo_sdi = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, identificativoSdi);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + identificativoSdi);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }


    public BigInteger getMaxIdentificativoSdiTest(EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "SELECT MAX(fa.identificativoSdi) FROM FatturaAttivaEntity fa  WHERE fa.fatturazioneTest = true";

            TypedQuery<BigInteger> query = entityManager.createQuery(queryString, BigInteger.class);

            BigInteger max = query.getSingleResult();
            if (max == null) {
                return BigInteger.ZERO;
            } else {
                return max;
            }
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("nessun max identificativo sdi di test");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}
