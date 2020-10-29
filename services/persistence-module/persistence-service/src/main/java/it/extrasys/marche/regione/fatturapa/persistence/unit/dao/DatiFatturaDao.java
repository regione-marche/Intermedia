package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 20/02/15.
 */
public class DatiFatturaDao extends GenericDao<DatiFatturaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(DatiFatturaDao.class);
    private static final String ORDER_DEFAULT = "ORDER BY dfe.identificativoSdI";
    private static long month = 2629800000L;
    private static long year = 31557600000L;

    @Deprecated
    public String getIdFiscaleCommittenteByIdentificativoSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException {

        DatiFatturaEntity datiFatturaEntity = null;

        Query query = entityManager.createQuery("SELECT dfe.committenteIdFiscaleIVA FROM DatiFatturaEntity dfe WHERE dfe.identificativoSdI = :identificativoSdI");
        query.setParameter("identificativoSdI", identificativoSdI);

        return (String) query.getResultList().get(0);
    }

    /**
     * Deprecata perche' deve essere sostituito l'idFIscaleCommittente con il codiceUfficio (XXX in dati_fattura si chiama codiceDestinatario)
     *
     * @param idFiscaleCommittente
     * @param numeroProtocollo
     * @param numeroFattura
     * @param entityManager
     * @return
     * @throws FatturaPaPersistenceException
     * @throws FatturaPAFatturaNonTrovataException
     */
    @Deprecated
    public DatiFatturaEntity getFattura(String idFiscaleCommittente, String numeroProtocollo, String numeroFattura, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        DatiFatturaEntity datiFatturaEntity = null;
        idFiscaleCommittente = idFiscaleCommittente.trim();
        numeroProtocollo = numeroProtocollo.trim();
        numeroFattura = numeroFattura.trim();

        LOG.info("getFattura for idFiscaleCommittente \"" + idFiscaleCommittente + "\", numeroProtocollo \"" + numeroProtocollo + "\", numeroFattura \"" + numeroFattura + "\"");

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery(
                    "SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.committenteIdFiscaleIVA = :committenteIdFiscaleIVA and dfe.numeroProtocollo = :numeroProtocollo and dfe.numeroFattura = :numeroFattura", DatiFatturaEntity.class);
            query.setParameter("committenteIdFiscaleIVA", idFiscaleCommittente);
            query.setParameter("numeroProtocollo", numeroProtocollo);
            query.setParameter("numeroFattura", numeroFattura);

            datiFatturaEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntity;
    }

    public DatiFatturaEntity getFatturaByCodiceUfficioNumeroProtocolloAndNumeroFattura(String codiceUfficio, String numeroProtocollo, String numeroFattura, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        DatiFatturaEntity datiFatturaEntity = null;
        codiceUfficio = codiceUfficio.trim();
        numeroProtocollo = numeroProtocollo.trim();
        numeroFattura = numeroFattura.trim();

        LOG.info("getFattura for codiceUfficio \"" + codiceUfficio + "\", numeroProtocollo \"" + numeroProtocollo + "\", numeroFattura \"" + numeroFattura + "\"");

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery(
                    "SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.codiceDestinatario = :codiceDestinatario and dfe.numeroProtocollo = :numeroProtocollo and dfe.numeroFattura = :numeroFattura", DatiFatturaEntity.class);
            query.setParameter("codiceDestinatario", codiceUfficio);
            query.setParameter("numeroProtocollo", numeroProtocollo);
            query.setParameter("numeroFattura", numeroFattura);

            datiFatturaEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntity;
    }

    /**
     * Deprecata perche' si deve sostituire l'idFiscaleCommittente con il codiceUfficio (XXX in dati_fattura si chiama codiceDestinatario). Usare funziona analoga
     *
     * @param idFiscaleCommittente
     * @param numeroProtocollo
     * @param entityManager
     * @return
     * @throws FatturaPaPersistenceException
     * @throws FatturaPAFatturaNonTrovataException
     */
    @Deprecated
    public List<DatiFatturaEntity> getFattureByIdFiscaleCommittenteAndNumeroProtocollo(String idFiscaleCommittente, String numeroProtocollo, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.info("getFattura for idFiscaleCommittente \"" + idFiscaleCommittente + "\", numeroProtocollo \"" + numeroProtocollo + "\"");
        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.committenteIdFiscaleIVA = :committenteIdFiscaleIVA and dfe.numeroProtocollo = :numeroProtocollo", DatiFatturaEntity.class);
            query.setParameter("committenteIdFiscaleIVA", idFiscaleCommittente);
            query.setParameter("numeroProtocollo", numeroProtocollo);

            datiFatturaEntityList = query.getResultList();

            if (datiFatturaEntityList.size() == 0) {
                throw new FatturaPAFatturaNonTrovataException();
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByNomeFileFattura(String nomeFileFattura, EntityManager entityManager) throws FatturaPaPersistenceException {

        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.info("getFattura for nomeFileFattura \"" + nomeFileFattura + "\"");

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.nomeFile = :nomeFile AND dfe.fatturazioneTest = :fatturazioneTest ORDER BY dfe.dataCreazione DESC", DatiFatturaEntity.class);
            query.setParameter("nomeFile", nomeFileFattura);
            query.setParameter("fatturazioneTest", false);

            datiFatturaEntityList = query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByCodiceDestinatarioAndDate(String codiceDestinatario, Date from, Date to, String orderBy, Integer numberOfElements, Integer pageNumber, String ordering, EntityManager entityManager) throws FatturaPaPersistenceException {

        String queryString = "SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.codiceDestinatario = :codiceDestinatario AND dfe.fatturazioneTest = :fatturazioneTest AND dfe.dataCreazione BETWEEN :from AND :to ";

        if (StringUtils.isNotEmpty(orderBy)) {
            queryString += " ORDER BY dfe." + orderBy;
        } else {
            queryString += ORDER_DEFAULT;
        }

        if (StringUtils.isNotEmpty(ordering)) {
            queryString += " " + ordering;
        }

        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.info("getFattura for codice destinatario: " + codiceDestinatario + " and from " + from + " to " + to + " orderby " + orderBy);
        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery(queryString, DatiFatturaEntity.class);
            if (numberOfElements != null) {
                query.setMaxResults(numberOfElements);
            }
            if (pageNumber != null) {
                query.setFirstResult(pageNumber);
            }
            query.setParameter("codiceDestinatario", codiceDestinatario);
            query.setParameter("from", from);
            query.setParameter("to", to);
            query.setParameter("fatturazioneTest", false);

            datiFatturaEntityList = query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public int getCountFattureByCodiceDestinatarioAndDate(String codiceDestinatario, Date from, Date to, EntityManager entityManager) throws FatturaPaPersistenceException {

        Integer count = null;

        LOG.info("getFattura for codice destinatario: " + codiceDestinatario + " and from " + from + " to " + to);
        try {
            TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(dfe) FROM DatiFatturaEntity dfe WHERE dfe.codiceDestinatario = :codiceDestinatario AND dfe.fatturazioneTest = :fatturazioneTest AND dfe.dataCreazione BETWEEN :from AND :to", Long.class);
            query.setParameter("codiceDestinatario", codiceDestinatario);
            query.setParameter("from", from);
            query.setParameter("to", to);
            query.setParameter("fatturazioneTest", false);

            count = query.getSingleResult().intValue();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return count.intValue();
    }

    public List<DatiFatturaEntity> getFattureByNumeroProtocollo(String numeroProtocollo, EntityManager entityManager) throws FatturaPaPersistenceException {

        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.info("getFattureByNumeroProtocollo: numeroProtocollo \"" + numeroProtocollo + "\"");

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.numeroProtocollo = :numeroProtocollo AND dfe.fatturazioneTest = :fatturazioneTest ORDER BY dfe.dataCreazione DESC", DatiFatturaEntity.class);
            query.setParameter("numeroProtocollo", numeroProtocollo);
            query.setParameter("fatturazioneTest", false);

            datiFatturaEntityList = query.getResultList();


        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByCodiceUfficioAndNumeroProtocollo(String codiceUfficio, String numeroProtocollo, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.info("getFattura for codiceUfficio \"" + codiceUfficio + "\", numeroProtocollo \"" + numeroProtocollo + "\"");
        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.codiceDestinatario = :codiceDestinatario and dfe.numeroProtocollo = :numeroProtocollo", DatiFatturaEntity.class);
            query.setParameter("codiceDestinatario", codiceUfficio);
            query.setParameter("numeroProtocollo", numeroProtocollo);

            datiFatturaEntityList = query.getResultList();

            if (datiFatturaEntityList.size() == 0) {
                throw new FatturaPAFatturaNonTrovataException();
            }

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByIdentificativoSdi(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<DatiFatturaEntity> datiFatturaEntityList = null;

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.identificativoSdI = :identificativoSdI ORDER BY dfe.dataCreazione DESC", DatiFatturaEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);


            datiFatturaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException("Nessuna fattura per idSdi: " + identificativoSdI);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByIdentificativoSdiTest(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.debug("********** getFattureByIdentificativoSdiTest [" + identificativoSdI + "]**********");

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.identificativoSdI = :identificativoSdI AND dfe.fatturazioneTest = :fatturazioneTest ORDER BY dfe.dataCreazione DESC", DatiFatturaEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);
            query.setParameter("fatturazioneTest", true);

            datiFatturaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException("Nessuna fattura per idSdi: " + identificativoSdI);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByIdentificativoSdiProduzione(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<DatiFatturaEntity> datiFatturaEntityList = null;

        LOG.debug("********** getFattureByIdentificativoSdiProduzione [" + identificativoSdI + "]**********");

        try {
            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.identificativoSdI = :identificativoSdI AND dfe.fatturazioneTest = :fatturazioneTest ORDER BY dfe.dataCreazione DESC", DatiFatturaEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);
            query.setParameter("fatturazioneTest", false);

            datiFatturaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException("Nessuna fattura per idSdi: " + identificativoSdI);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }

    public DatiFatturaEntity getFattureByIdentificativoSdINumeroFattura(BigInteger identificativoSdI, String numeroFattura, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        DatiFatturaEntity datiFatturaEntity = null;

        try {

            TypedQuery<DatiFatturaEntity> query = entityManager.createQuery("SELECT dfe FROM DatiFatturaEntity dfe WHERE dfe.identificativoSdI = :identificativoSdI AND dfe.numeroFattura = :numeroFattura ", DatiFatturaEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);
            query.setParameter("numeroFattura", numeroFattura);

            datiFatturaEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntity;
    }


    public List<Object[]> getFattureBetweenDate(EntityManager entityManager, Date from, Date to) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        LOG.debug("********** DatiFatturaDao: getFattureBetweenDate **********");

        Date now = new Date();
        long nowMilli = now.getTime();
        long yearAgoMilli = nowMilli - year;
        Date yearAgo = new Date(yearAgoMilli);

        try {
            String queryString = "SELECT tc.desc_canale, e.invio_unico, e.id_endpoint_protocollo_ca, cca.desc_canale  FROM dati_fattura as df " +
                    " JOIN ente AS e ON df.codice_destinatario = e.codice_ufficio" +
                    " JOIN tipo_canale tc on e.id_tipo_canale = tc.cod_tipo_canale " +
                    " left JOIN endpoint_ca as ec ON e.id_endpoint_protocollo_ca =ec.id_endpoint_ca " +
                    " full outer join canale_ca as cca on ec.id_canale_ca = cca.cod_canale " +
                    " WHERE " +
                    " df.fatturazione_test = 'F' AND df.data_creazione BETWEEN ? AND ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, from);
            query.setParameter(2, to);

            return query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }



    public List<Object[]> getFattureBetweenDateUtente(Date from, Date to, UtenteEntity utenteEntity, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        LOG.info("********** DatiFatturaDao: getFattureBetweenDateUtente **********");

        try {
            String queryString ="SELECT tc.desc_canale, e.invio_unico, e.id_endpoint_protocollo_ca, cca.desc_canale FROM dati_fattura as dfe " +
                    "JOIN ente AS e ON dfe.codice_destinatario = e.codice_ufficio " +
                    "join utente_ente ue ON (ue.id_ente = e.id_ente ) " +
                    "join utenti u ON(ue.id_utente = u.id_utente ) " +
                    "JOIN tipo_canale tc on e.id_tipo_canale = tc.cod_tipo_canale " +
                    "left JOIN endpoint_ca as ec ON e.id_endpoint_protocollo_ca = ec.id_endpoint_ca " +
                    "full outer join canale_ca as cca on ec.id_canale_ca = cca.cod_canale " +
                    "WHERE dfe.fatturazione_test = 'F' AND dfe.data_creazione BETWEEN ? AND ? AND u.username = ?";
            Query query= entityManager.createNativeQuery(queryString);
            query.setParameter(1, from);
            query.setParameter(2, to);
            query.setParameter(3, utenteEntity.getUsername());
            return query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    /**
     * @param codDest codice destinatario dell'ente
     * @return l' ultima fattura con statoFattura='RICEVUTA' (con relativa fattura) relativo all'ente con codice_destinatario=codDest.
     */
    public List<Object[]> getStatoFatturaUltimoStatoRicevutaByCodiceDestinatario(String codDest, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        List<Object[]> statiFatturaEntityList = null;

        try {
        	/*
        	La query jpa ci mette troppo tempo ad essere eseguita!
        	 */

	        /* String queryString = "SELECT d FROM StatoFatturaEntity s JOIN s.datiFattura d WHERE s.datiFattura.codiceDestinatario = :codDest" +
                    " AND s.stato.codStato = '001' " +
                    " AND s.data = (SELECT MAX(s2.data) FROM StatoFatturaEntity s2 JOIN s2.datiFattura d2 " +
                    " WHERE d2.identificativoSdI = d.identificativoSdI ) ";*/

            String queryString = "SELECT d.id_dati_fattura, d.nome_file, ff.contenuto_file, d.identificativo_sdi, d.committente_id_fiscale_iva, mf.contenuto_file, mf.nome_file_metadati " +
                    " FROM stato_fattura s, dati_fattura d, file_fattura ff, metadati_fattura mf " +
                    " WHERE d.id_dati_fattura = s.id_dati_fattura and d.codice_destinatario = ? and d.id_file_fattura = ff.id_file_fattura " +
                    " AND mf.identificativo_sdi = d.identificativo_sdi " +
                    " AND s.id_cod_stato='001' AND s.data = " +
                    " (SELECT MAX(s2.data) FROM  stato_fattura s2,dati_fattura d2 " +
                    " WHERE d2.id_dati_fattura=s2.id_dati_fattura AND d2.identificativo_sdi=d.identificativo_sdi " +
                    " )";


            // TypedQuery<DatiFatturaEntity> query = entityManager.createQuery(queryString, DatiFatturaEntity.class);
            // query.setParameter("codDest", codDest);
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, codDest);
            statiFatturaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return statiFatturaEntityList;
    }

    public StatoFatturaEntity getUltimoStatoFattura(DatiFatturaEntity datiFatturaEntity, EntityManager entityManager) throws NoResultException, FatturaPaPersistenceException {
        try {
            TypedQuery<StatoFatturaEntity> query = entityManager.createQuery("SELECT sf FROM StatoFatturaEntity sf WHERE sf.datiFattura = :datiFatturaEntity ORDER BY sf.data DESC", StatoFatturaEntity.class);
            query.setParameter("datiFatturaEntity", datiFatturaEntity);

            List<StatoFatturaEntity> statoFatturaList = query.getResultList();

            if (statoFatturaList.size() == 0) {
                throw new NoResultException("Nessuno stato fattura per idDatiFattura: " + datiFatturaEntity.getIdDatiFattura());
            }
            return statoFatturaList.get(0);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }
    }

    public List<StatoFatturaEntity> getStatiFattura(DatiFatturaEntity datiFatturaEntity, EntityManager entityManager) throws NoResultException, FatturaPaPersistenceException {
        try {
            TypedQuery<StatoFatturaEntity> query = entityManager.createQuery("SELECT sf FROM StatoFatturaEntity sf WHERE sf.datiFattura = :datiFatturaEntity ORDER BY sf.data ASC", StatoFatturaEntity.class);
            query.setParameter("datiFatturaEntity", datiFatturaEntity);

            List<StatoFatturaEntity> statoFatturaList = query.getResultList();

            if (statoFatturaList.size() == 0) {
                throw new NoResultException("Nessuno stato fattura per idDatiFattura: " + datiFatturaEntity.getIdDatiFattura());
            }
            return statoFatturaList;
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException(e.getMessage());
        }
    }


    /**
     * @param codDest codice destinatario dell'ente
     * @return l' ultima fattura con statoFattura='PROTOCOLLATA' (con relativa fattura) relativo all'ente con codice_destinatario=codDest.
     */
    public List<Object[]> getStatoFatturaUltimoStatoProtocolloByCodiceDestinatario(String codDest, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        List<Object[]> statiFatturaEntityList = null;

        try {
        		/*
        	La query jpa ci mette troppo tempo ad essere eseguita!
        	 */
      /*      String queryString = "SELECT d FROM StatoFatturaEntity s JOIN s.datiFattura d WHERE s.datiFattura.codiceDestinatario = :codDest" +
                    " AND (s.stato.codStato = '002' OR s.stato.codStato = '024' OR s.stato.codStato = '041' ) " +
                    " AND s.data = (SELECT MAX(s2.data) FROM StatoFatturaEntity s2 JOIN s2.datiFattura d2 " +
                    " WHERE d2.identificativoSdI = d.identificativoSdI ) ";*/


            String queryString = "SELECT d.id_dati_fattura, d.nome_file, ff.contenuto_file, d.identificativo_sdi, d.committente_id_fiscale_iva, mf.contenuto_file, mf.nome_file_metadati " +
                    " FROM stato_fattura s, dati_fattura d , file_fattura ff , metadati_fattura mf " +
                    " WHERE d.id_dati_fattura = s.id_dati_fattura AND d.codice_destinatario = ? AND d.id_file_fattura = ff.id_file_fattura " +
                    " AND mf.identificativo_sdi = d.identificativo_sdi " +
                    " AND (s.id_cod_stato='002' OR s.id_cod_stato='024' OR s.id_cod_stato='041' OR s.id_cod_stato='061') AND s.data = " +
                    " (SELECT MAX(s2.data) FROM  stato_fattura s2,dati_fattura d2 " +
                    " WHERE d2.id_dati_fattura = s2.id_dati_fattura AND d2.identificativo_sdi=d.identificativo_sdi " +
                    " )";


			/*TypedQuery<DatiFatturaEntity> query = entityManager.createQuery(queryString, DatiFatturaEntity.class);
            query.setParameter("codDest", codDest);*/
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, codDest);
            statiFatturaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return statiFatturaEntityList;
    }


    public List<Object[]> getFattureByIdentificativoSdiNative(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<Object[]> datiFatturaEntityList = null;

        try {
            Query query = entityManager.createNativeQuery("SELECT df.identificativo_sdi, df.cedente_id_fiscale_iva, df.committente_id_fiscale_iva, df.codice_destinatario, df.data_fattura, df.data_creazione, df.nome_file, df.numero_fattura, df.numero_protocollo, df.id_dati_fattura, df.fatturazione_interna, e.id_tipo_canale " +
                    " FROM dati_fattura df, ente e  WHERE e.codice_ufficio=df.codice_destinatario AND identificativo_sdi = ? ");
            query.setParameter(1, identificativoSdI);

            datiFatturaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return datiFatturaEntityList;
    }


    public int deleteByIdentificativoSdi(BigInteger identificativoSdI, EntityManager entityManager) {
        String queryString = "DELETE FROM dati_fattura  WHERE identificativo_sdi = ? ";

        Query query = entityManager.createNativeQuery(queryString);

        query.setParameter(1, identificativoSdI);
        int row = query.executeUpdate();

        return row;
    }


    /*
    QUERY PER IL CRUSCOTTO DI MONITORAGGIO
     */
    public List<Object[]> getUltimoStatoFatture(EntityManager entityManager, String interval) {
        String queryString = "SELECT identificativo_sdi, m as data_ultimo_stato, codice_destinatario, data_creazione, nome_file, desc_stato, desc_canale, cod_stato, idf, nome_file " +
                "from ( " +
                "      SELECT * FROM ( " +
                "           SELECT DISTINCT r1.idf, id_cod_stato, m " +
                "           FROM " +
                "                (SELECT id_dati_fattura as idf ,max(data) as m FROM stato_fattura where data > CURRENT_TIMESTAMP - INTERVAL '" + interval + " days' GROUP BY id_dati_fattura ) as r1 " +
                "                INNER JOIN stato_fattura as r2 ON r1.idf=r2.id_dati_fattura and r1.m=r2.data " +
                "                      ) stati inner join dati_fattura on dati_fattura.id_dati_fattura=stati.idf AND dati_fattura.fatturazione_test='F' ) fatture " +
                "                            INNER JOIN codifica_stati ON codifica_stati.cod_stato=fatture.id_cod_stato " +
                "                          INNER JOIN ente ON ente.codice_ufficio=fatture.codice_destinatario AND ente.ambiente_ciclopassivo = 'PRODUZIONE' " +
                "                   INNER JOIN tipo_canale ON tipo_canale.cod_tipo_canale=ente.id_tipo_canale";

        Query query = entityManager.createNativeQuery(queryString);

        List<Object[]> result = query.getResultList();

        return result;
    }


    public Long getCountUltimoStatoFatture(EntityManager entityManager, String interval) {
        String queryString = "SELECT COUNT(*)  from ( " +
                "                    SELECT * FROM ( " +
                "                    SELECT DISTINCT r1.id_dati_fattura, id_cod_stato, m " +
                "                    FROM " +
                "                    (SELECT id_dati_fattura,max(data) as m FROM stato_fattura where data > CURRENT_TIMESTAMP - INTERVAL '" + interval + " days' GROUP BY id_dati_fattura ) as r1 " +
                "                    INNER JOIN stato_fattura as r2 ON r1.id_dati_fattura=r2.id_dati_fattura and r1.m=r2.data " +
                "                    ) stati inner join dati_fattura on dati_fattura.id_dati_fattura=stati.id_dati_fattura ) fatture " +
                "                    INNER JOIN codifica_stati ON codifica_stati.cod_stato=fatture.id_cod_stato " +
                "                    INNER JOIN ente ON ente.codice_ufficio=fatture.codice_destinatario " +
                "                    INNER JOIN tipo_canale ON tipo_canale.cod_tipo_canale=ente.id_tipo_canale";

        Query query = entityManager.createNativeQuery(queryString);

        Long result = (Long) query.getSingleResult();

        return result;
    }

    public BigInteger getMaxIdentificativoSdiTest(EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "select max(df.identificativoSdI) from DatiFatturaEntity df WHERE df.fatturazioneTest = true";

            TypedQuery<BigInteger> query = entityManager.createQuery(queryString, BigInteger.class);

            BigInteger max = query.getSingleResult();

            if (max == null) {
                return BigInteger.ZERO;
            }
            return max;
        } catch (NoResultException e) {
            return BigInteger.ZERO;
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }
}