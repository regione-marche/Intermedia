package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 23/02/15.
 */
public class DatiFatturaManager {

    private static final Logger LOG = LoggerFactory.getLogger(DatiFatturaManager.class);

    private EntityManagerFactory entityManagerFactory;

    private DatiFatturaDao datiFatturaDao;

    private StatoFatturaDao statoFatturaDao;

    private CodificaStatiDao codificaStatiDao;

    public void registrazioneFatturaNotificaScarto(String identificativoSdI, String codificaStatoEntity) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        List<DatiFatturaEntity> fatture = getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        for (DatiFatturaEntity fattura : fatture) {
            entityManager.getTransaction().begin();
            datiFatturaDao.update(fattura, entityManager);
            entityManager.getTransaction().commit();
            aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
        }
    }

    public void protocollaFattura(String identificativoSdI, String idProtocollo, String codificaStatoEntity) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        //String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.PROTOCOLLATA.getValue();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        List<DatiFatturaEntity> fatture = getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));
        for (DatiFatturaEntity fattura : fatture) {
            fattura.setNumeroProtocollo(idProtocollo);
            entityManager.getTransaction().begin();
            datiFatturaDao.update(fattura, entityManager);
            entityManager.getTransaction().commit();
            aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
        }
    }

    public void protocollaNotifica(String identificativoSdI, String codificaStatoEntity) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        //String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_PROTOCOLLATA.getValue();

        List<DatiFatturaEntity> fatture = getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        for (DatiFatturaEntity fattura : fatture) {

            aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);

        }
    }

    public void registraFatturebyIdentificativoSDI(String identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            LOG.info("REGISTRAZIONE FATTURE MANAGER START  - IDENTIFICATIVO SDI " + identificativoSdI);
            List<DatiFatturaEntity> fatture = datiFatturaDao.getFattureByIdentificativoSdi(new BigInteger(identificativoSdI), entityManager);
            LOG.info("REGISTRAZIONE FATTURE MANAGER: FATTURE TROVATE: " + fatture.size() + "  - IDENTIFICATIVO SDI " + identificativoSdI);

            String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.REGISTRATA.getValue();
            CodificaStatiEntity codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.parse(codificaStatoEntity), entityManager);
            for (DatiFatturaEntity fattura : fatture) {
                LOG.info("REGISTRAZIONE FATTURE MANAGER: REGISTRAZIONE FATTURA NUMERO: " + fattura.getNumeroFattura() + "  - IDENTIFICATIVO SDI " + identificativoSdI);
                StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();
                Date now = new Date();
                statoFatturaEntity.setData(new Timestamp(now.getTime()));
                statoFatturaEntity.setDatiFattura(fattura);
                statoFatturaEntity.setStato(codificaStatiEntity);
                statoFatturaDao.create(statoFatturaEntity, entityManager);
                LOG.info("REGISTRAZIONE FATTURE MANAGER: REGISTRATA FATTURA NUMERO: " + fattura.getNumeroFattura() + "  - IDENTIFICATIVO SDI " + identificativoSdI);
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
        LOG.info("REGISTRAZIONE FATTURE MANAGER END - IDENTIFICATIVO SDI " + identificativoSdI);
    }

    @Deprecated
    public void registraFattura(String idFiscaleCommittente, String numeroProtocollo, String numeroFattura) throws FatturaPaPersistenceException, FatturaPAException {

        String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.REGISTRATA.getValue();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        //FIXME cambiare la successiva con la funzione che cerca per codiceUfficio e non idFiscaleCommittente
        DatiFatturaEntity fattura = getFatturaByNumeroFatturaAndIdFiscaleCommittente(idFiscaleCommittente, numeroProtocollo, numeroFattura);
        aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
    }


    public void rifiutaFatturaPerValidazioneFallita(String identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.RIFIUTATA_PER_VALIDAZIONE_FALLITA.getValue();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<DatiFatturaEntity> fatture = datiFatturaDao.getFattureByIdentificativoSdi(new BigInteger(identificativoSdI), entityManager);

        for (DatiFatturaEntity fattura : fatture) {
            aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
        }
    }


    public void accettaFattura(DatiFatturaEntity fattura) throws FatturaPaPersistenceException, FatturaPAException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        StatoFatturaEntity statoFatturaAttuale = statoFatturaDao.getLastStato(fattura.getIdDatiFattura(), entityManager);

        String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI.getValue();

        if ((CodificaStatiEntity.CODICI_STATO_FATTURA.REGISTRATA.getValue().compareTo(statoFatturaAttuale.getStato().getCodStato().getValue()) == 0) ||
                (CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_DECORRENZA_TERMINI.getValue().compareTo(statoFatturaAttuale.getStato().getCodStato().getValue()) == 0)) {
            aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
        }
    }

    public void notificaFatturePerDecorrenzaTermini(String identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI.getValue();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        List<DatiFatturaEntity> fatture = getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));
        for (DatiFatturaEntity fattura : fatture) {

            /*
             * questo controllo viene effettuato perché in casi di lotti di fatture alcune di esse potrebbero essere state accettate/rifiutate dall'ente è dunque per queste
             * non deve essere aggiornato lo stato di "accettazione per decorrenza termini". Questo è vero se l'ultimo stato della fattura è "notifica_accettata"
             */
            StatoFatturaEntity stato = statoFatturaDao.findUltimoStatoFatturaPerIdDatiFatturaEDataFattura(fattura.getIdDatiFattura(), entityManager);
            if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_ACCETTATA.getValue().compareTo(stato.getStato().getCodStato().getValue()) != 0)
                aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
        }

    }

    public void notificaFatturePerNotificaEsitoScartata(String identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        String codificaStatoEntity = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA_CONSEGNATA.getValue();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        List<DatiFatturaEntity> fatture = getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));
        for (DatiFatturaEntity fattura : fatture) {

            /*
             * questo controllo viene effettuato perché in casi di lotti di fatture alcune di esse potrebbero non essere state accettate/rifiutate dall'ente è dunque per queste
             * non deve essere aggiornato lo stato. Questo è vero solo se l'ultimo stato della fattura è "notifica_scartata"
             */
            StatoFatturaEntity stato = statoFatturaDao.findUltimoStatoFatturaPerIdDatiFatturaEDataFattura(fattura.getIdDatiFattura(), entityManager);
            if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA.getValue().compareTo(stato.getStato().getCodStato().getValue()) == 0) {
                aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), codificaStatoEntity);
            }
        }

    }

    /**
     * Restituisce i record relativi al numero protocollo, idFiscaleCommittente e numeroFattura passati. Se il numeroFattura e' null o vuoto restituisce una
     * lista di record, altrimenti il record puntuale
     * <p/>
     * FIXME la classe e' deprecata perche' e' stato essere sostituito l'idFiscaleCommitttente con il codiceUfficio. Usare altra funziona analoga
     *
     * @param numeroProtocollo
     * @param idFiscaleCommittente
     * @param numeroFattura
     * @return
     * @throws FatturaPaPersistenceException
     * @throws FatturaPAException
     */
    @Deprecated
    public List<DatiFatturaEntity> getFatturaByNumeroProtocolloIdFiscaleCommittenteAndNumeroFattura(String numeroProtocollo, String idFiscaleCommittente, String numeroFattura) throws FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = null;

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<DatiFatturaEntity>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            if (numeroFattura == null || "".equals(numeroFattura)) {

                datiFatturaEntityList.addAll(datiFatturaDao.getFattureByIdFiscaleCommittenteAndNumeroProtocollo(idFiscaleCommittente, numeroProtocollo, entityManager));
            } else {

                datiFatturaEntityList.add(datiFatturaDao.getFattura(idFiscaleCommittente, numeroProtocollo, numeroFattura, entityManager));

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

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFatturaByNumeroProtocolloCodiceUfficioAndNumeroFattura(String numeroProtocollo, String codiceUfficio, String numeroFattura) throws FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = null;

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<DatiFatturaEntity>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            if (numeroFattura == null || "".equals(numeroFattura)) {

                datiFatturaEntityList.addAll(datiFatturaDao.getFattureByCodiceUfficioAndNumeroProtocollo(codiceUfficio, numeroProtocollo, entityManager));
            } else {

                datiFatturaEntityList.add(datiFatturaDao.getFatturaByCodiceUfficioNumeroProtocolloAndNumeroFattura(codiceUfficio, numeroProtocollo, numeroFattura, entityManager));

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

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFattureByNumeroProtocollo(String numeroProtocollo) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** getFatturaByNumeroProtocollo **********");

        EntityManager entityManager = null;

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<DatiFatturaEntity>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            return datiFatturaDao.getFattureByNumeroProtocollo(numeroProtocollo, entityManager);

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

    }

    public List<DatiFatturaEntity> getFattureByCodiceDestinatarioAndDate(String codiceDestinatario, Date from, Date to, String orderBy, Integer numberOfElements, Integer pageNumber, String ordering) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** getFattureByCodiceDestinatarioAndDate **********");

        EntityManager entityManager = null;

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<DatiFatturaEntity>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            return datiFatturaDao.getFattureByCodiceDestinatarioAndDate(codiceDestinatario, from, to, orderBy, numberOfElements, pageNumber, ordering, entityManager);

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

    }


    public List<Object[]> getFattureBetweenDate(Date from, Date to) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** DatiFatturaManager: getFattureBetweenDate **********");

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            return datiFatturaDao.getFattureBetweenDate(entityManager, from, to);

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

    }


    public List<Object[]> getFattureBetweenDateUtente(Date from, Date to, UtenteEntity utenteEntity) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** DatiFatturaManager: getFattureBetweenDateUtente **********");

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            return datiFatturaDao.getFattureBetweenDateUtente(from, to, utenteEntity, entityManager);

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

    }


    public int getCountFattureByCodiceDestinatarioAndDate(String codiceDestinatario, Date from, Date to) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** getFattureByCodiceDestinatarioAndDate **********");

        EntityManager entityManager = null;

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<DatiFatturaEntity>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            return datiFatturaDao.getCountFattureByCodiceDestinatarioAndDate(codiceDestinatario, from, to, entityManager);

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

    }

    public List<DatiFatturaEntity> getFattureByNomeFileFattura(String nomeFileFattura) throws FatturaPaPersistenceException, FatturaPAException, FatturaPAFatturaNonTrovataException {

        LOG.info("********** getFatturaByNomeFileFattura **********");

        EntityManager entityManager = null;

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<DatiFatturaEntity>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            return datiFatturaDao.getFattureByNomeFileFattura(nomeFileFattura, entityManager);

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

    }


    /**
     * La ricerca per idFiscaleCommittente non deve essere fatta, perche' non e' un campo "sicuro". Usare la ricerca per codiceUfficio
     *
     * @param identificativoSdI
     * @return
     * @throws FatturaPAException
     */
    @Deprecated
    public String getIdFiscaleCommittenteByIdentificativoSdI(BigInteger identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        String idFiscaleCommittente;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            idFiscaleCommittente = datiFatturaDao.getIdFiscaleCommittenteByIdentificativoSdI(identificativoSdI, entityManager);

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

        return idFiscaleCommittente;
    }

    /*
    FIXME cambiare mettendo al posto di idFiscaleCommittente il codiceUfficio
     */
    public DatiFatturaEntity getFatturaByNumeroFatturaAndIdFiscaleCommittente(String idFiscaleCommittente, String numeroProtocollo, String numeroFattura) throws FatturaPAException {

        EntityManager entityManager = null;

        DatiFatturaEntity datiFatturaEntity;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            datiFatturaEntity = datiFatturaDao.getFattura(idFiscaleCommittente, numeroProtocollo, numeroFattura, entityManager);

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

        return datiFatturaEntity;
    }

    /*
     * in questo momento devo aggiornare la fattura dopo una notifica di esito. Questo significa che devo aggiornare lo stato della fattura e contemporaneamente
     * aggiornare il progressivo univoco
     *
     * ATTENZIONE il numero progressivo in realta' potrebbe contenere anche lettere, ma in questo caso verranno utilizzati solo i numeri
     */
    public void aggiornaStatoFatturaEsito(BigInteger idFattura, String codificaStatoEntity) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            DatiFatturaEntity datiFatturaEntity = datiFatturaDao.read(idFattura, entityManager);

            String numeroProgressivo = getNumeroProgressivo(datiFatturaEntity);

            datiFatturaEntity.setProgressivoInvioNotifica(numeroProgressivo);

            datiFatturaDao.update(datiFatturaEntity, entityManager);

            StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();
            Date now = new Date();
            statoFatturaEntity.setData(new Timestamp(now.getTime()));
            statoFatturaEntity.setDatiFattura(datiFatturaEntity);

            CodificaStatiEntity codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.parse(codificaStatoEntity), entityManager);

            statoFatturaEntity.setStato(codificaStatiEntity);

            statoFatturaDao.create(statoFatturaEntity, entityManager);

            //Geatione caso Notifica_Scarto_Per_Reinvio
            if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA_PER_REINVIO.getValue().equals(codificaStatoEntity)) {

                statoFatturaEntity = new StatoFatturaEntity();
                now = new Date();
                statoFatturaEntity.setData(new Timestamp(now.getTime()));
                statoFatturaEntity.setDatiFattura(datiFatturaEntity);

                codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.parse("010"), entityManager);

                statoFatturaEntity.setStato(codificaStatiEntity);

                statoFatturaDao.create(statoFatturaEntity, entityManager);
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

    private void aggiornaStatoFattura(DatiFatturaEntity datiFatturaEntity, String codificaStatoEntity, EntityManager entityManager) throws FatturaPAException {


        try {
            CodificaStatiEntity codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.parse(codificaStatoEntity), entityManager);

            StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();
            Date now = new Date();
            statoFatturaEntity.setData(new Timestamp(now.getTime()));
            statoFatturaEntity.setDatiFattura(datiFatturaEntity);
            statoFatturaEntity.setStato(codificaStatiEntity);

            statoFatturaDao.create(statoFatturaEntity, entityManager);

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

    public List<DatiFatturaEntity> getFatturaByIdentificativoSDI(BigInteger identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException {
        EntityManager entityManager = null;

        LOG.info("********** getFatturaByIdentificativoSDI **********");

        List<DatiFatturaEntity> datiFatturaEntityList;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdi(identificativoSdI, entityManager);

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

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFatturaByIdentificativoSDITest(BigInteger identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException {
        EntityManager entityManager = null;

        LOG.info("********** getFatturaByIdentificativoSDITest **********");

        List<DatiFatturaEntity> datiFatturaEntityList;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdiTest(identificativoSdI, entityManager);

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

        return datiFatturaEntityList;
    }

    public List<DatiFatturaEntity> getFatturaByIdentificativoSDIProduzione(BigInteger identificativoSdI) throws FatturaPaPersistenceException, FatturaPAException {
        EntityManager entityManager = null;

        LOG.info("********** getFatturaByIdentificativoSDIProduzione **********");

        List<DatiFatturaEntity> datiFatturaEntityList;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdiProduzione(identificativoSdI, entityManager);

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

        return datiFatturaEntityList;
    }

    private String getNumeroProgressivo(DatiFatturaEntity datiFatturaEntity) {
        String actualNumber = datiFatturaEntity.getProgressivoInvioNotifica();
        String numeroProgressivo = "";

        if (actualNumber == null || "".equals(actualNumber)) {
            numeroProgressivo = "001";
        } else {
            int number = Integer.valueOf(actualNumber);

            number++;

            // fill di zero a sinistra
            numeroProgressivo = String.format("%010d", number);
        }

        return numeroProgressivo;
    }

    /**
     * Aggiorna il valore del campo numero progressivo, solo per le fatture che non sono nello stato
     * CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE
     *
     * @param datiFatturaEntityList
     * @param numeroProgressivo
     * @throws FatturaPAException
     */
    public void aggiornaNumeroProgressivo(List<DatiFatturaEntity> datiFatturaEntityList, String numeroProgressivo) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            for (DatiFatturaEntity dfe : datiFatturaEntityList) {

                dfe.setNumeroProtocollo(numeroProgressivo);

                /*
                 * ora devo prendere l'ultimo stato della fattura (quello con la data ultima) e se e'
                 * CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE allora non la devo modificare
                 */
                StatoFatturaEntity statoFatturaEntity = statoFatturaDao.getLastStato(dfe.getIdDatiFattura(), entityManager);

                if (!CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE.equals(statoFatturaEntity.getStato().getCodStato())) {

                    datiFatturaDao.update(dfe, entityManager);
                }

            }
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

    /**
     * restituisce l'ultimo stato della fattura, basandosi sulla data
     *
     * @param stati
     * @return
     * @deprecated Use instead getUltimoStatoFattura!
     */
    @Deprecated
    public static StatoFatturaEntity getUltimoStato(List<StatoFatturaEntity> stati) {

        if (stati == null || stati.size() == 0) {
            return null;
        }

        StatoFatturaEntity statoFatturaEntityTmp = stati.get(0);

        for (StatoFatturaEntity sfe : stati) {
            if (sfe.getData().after(statoFatturaEntityTmp.getData())) {
                statoFatturaEntityTmp = sfe;
            }
        }

        return statoFatturaEntityTmp;
    }


    public StatoFatturaEntity getUltimoStatoFattura(BigInteger idDatiFattura) throws Exception {

        EntityManager entityManager = null;
        StatoFatturaEntity ultimoStato;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            ultimoStato = statoFatturaDao.getLastStato(idDatiFattura, entityManager);

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

        return ultimoStato;
    }

    /**
     * Metodo che aggiorna lo stato di una fattura nei casi di mail Accettazione e Consegna relative al protocollo PEC
     *
     * @param idDatiFattura
     * @param codificaStato la codifica dello stato (si trova cosi': entity.getCodStato().getValue() dove entity e' un oggetto di tipo CodificaStatiEntity)
     * @throws Exception
     */
    public void aggiornaStatoFatturaSpedizionePec(BigInteger idDatiFattura, String codificaStato) throws Exception {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            StatoFatturaPK statoFatturaPK = new StatoFatturaPK();
            statoFatturaPK.setDatiFattura(idDatiFattura);
            statoFatturaPK.setStato(codificaStato);

            StatoFatturaEntity statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

            if (statoFatturaEntity == null) {
                /*
                record non presente, posso inserire; distinguo il caso accettazione da quello consegna, perche' nel caso consegna controllo anche se e' gia' presente
                anche lo stato di accettazione: se si ok, se no inserisco anche questo (in questo caso infatti e' infatti implicitamente accettata)
                 */

                if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_CONSEGNATA.getValue())) {
                    statoFatturaPK.setStato(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue());

                    statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

                    if (statoFatturaEntity == null) {
                        //anche l'accettazione non e' stata inserita, posso farlo qua
                        aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue(), entityManager);
                        LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC FATTURA ACCETTATA");
                        aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_CONSEGNATA.getValue(), entityManager);
                        LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC FATTURA CONSEGNATA");

                    } else {
                        //accettazione gia' presente, inserisco solo la consegna
                        aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_CONSEGNATA.getValue(), entityManager);
                        LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC FATTURA CONSEGNATA");
                    }
                } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue())) {

                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC FATTURA ACCETTATA");

                } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_CONSEGNATA.getValue())) {
                    statoFatturaPK.setStato(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_ACCETTATA.getValue());

                    statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

                    if (statoFatturaEntity == null) {
                        //anche l'accettazione non e' stata inserita, posso farlo qua
                        aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_ACCETTATA.getValue(), entityManager);
                        LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC DECORRENZA ACCETTATA");
                        aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_CONSEGNATA.getValue(), entityManager);
                        LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC DECORRENZA CONSEGNATA");

                    } else {
                        //accettazione gia' presente, inserisco solo la consegna
                        aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_CONSEGNATA.getValue(), entityManager);
                        LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC DECORRENZA CONSEGNATA");
                    }
                } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue())) {

                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_ACCETTATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec: aggiornamento stato fattura a PEC DECORRENZA ACCETTATA");
                }
            } else {
                LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePec, idFattura " + idDatiFattura + "; stato " + codificaStato + " gia' presente");
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

    public void aggiornaStatoFatturaSpedizionePecCA(BigInteger idDatiFattura, String codificaStato) throws Exception {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            StatoFatturaPK statoFatturaPK = new StatoFatturaPK();
            statoFatturaPK.setDatiFattura(idDatiFattura);
            statoFatturaPK.setStato(codificaStato);

            StatoFatturaEntity statoFatturaEntity = null;

            //Fattura
            if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_CONSEGNATA.getValue())) {
                statoFatturaPK.setStato(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_ACCETTATA.getValue());

                statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

                if (statoFatturaEntity == null) {
                    //anche l'accettazione non e' stata inserita, posso farlo qua
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_ACCETTATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA FATTURA ACCETTATA");
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA FATTURA CONSEGNATA");

                } else {
                    //accettazione gia' presente, inserisco solo la consegna
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA FATTURA CONSEGNATA");
                }
            } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_ACCETTATA.getValue())) {

                aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_ACCETTATA.getValue(), entityManager);
                LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA FATTURA ACCETTATA");
            }

            //Dec Termini
            else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_CONSEGNATA.getValue())) {
                statoFatturaPK.setStato(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_ACCETTATA.getValue());

                statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

                if (statoFatturaEntity == null) {
                    //anche l'accettazione non e' stata inserita, posso farlo qua
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_ACCETTATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA DECORRENZA ACCETTATA");
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA DECORRENZA CONSEGNATA");

                } else {
                    //accettazione gia' presente, inserisco solo la consegna
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA DECORRENZA CONSEGNATA");
                }
            } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_ACCETTATA.getValue())) {

                aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_ACCETTATA.getValue(), entityManager);
                LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA DECORRENZA ACCETTATA");
            }

            //Esito Committente
            else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_CONSEGNATA.getValue())) {
                statoFatturaPK.setStato(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_ACCETTATA.getValue());

                statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

                if (statoFatturaEntity == null) {
                    //anche l'accettazione non e' stata inserita, posso farlo qua
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_ACCETTATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA EC ACCETTATA");
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA EC CONSEGNATA");

                } else {
                    //accettazione gia' presente, inserisco solo la consegna
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA EC CONSEGNATA");
                }
            } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_ACCETTATA.getValue())) {

                aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_ACCETTATA.getValue(), entityManager);
                LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA EC ACCETTATA");
            }

            //Scarto Esito Committente
            else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_CONSEGNATA.getValue())) {
                statoFatturaPK.setStato(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_ACCETTATA.getValue());

                statoFatturaEntity = statoFatturaDao.read(statoFatturaPK, entityManager);

                if (statoFatturaEntity == null) {
                    //anche l'accettazione non e' stata inserita, posso farlo qua
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_ACCETTATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA SCARTO EC ACCETTATA");
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA SCARTO EC CONSEGNATA");

                } else {
                    //accettazione gia' presente, inserisco solo la consegna
                    aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_CONSEGNATA.getValue(), entityManager);
                    LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA SCARTO EC CONSEGNATA");
                }
            } else if (codificaStato.equals(CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_ACCETTATA.getValue())) {

                aggiornaStatoFatturaSenzaTransazione(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_ACCETTATA.getValue(), entityManager);
                LOG.info("DatiFatturaManager - aggiornaStatoFatturaSpedizionePecCA: aggiornamento stato fattura a PEC CA SCARTO EC ACCETTATA");
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

    /**
     * Metodo necessario quando si vuole aggiornare lo stato di una fattura mentre una transazione e' gia' aperta (e' quasi uguale al metodo "aggiornaStatoFatturaEsito",
     * solo che non apre la transazione).
     *
     * @param idFattura
     * @param codificaStatoValue
     * @param entityManager
     */
    private void aggiornaStatoFatturaSenzaTransazione(BigInteger idFattura, String codificaStatoValue, EntityManager entityManager) {
        DatiFatturaEntity datiFatturaEntity = datiFatturaDao.read(idFattura, entityManager);

        String numeroProgressivo = getNumeroProgressivo(datiFatturaEntity);

        datiFatturaEntity.setProgressivoInvioNotifica(numeroProgressivo);

        datiFatturaDao.update(datiFatturaEntity, entityManager);

        StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();
        Date now = new Date();
        statoFatturaEntity.setData(new Timestamp(now.getTime()));
        statoFatturaEntity.setDatiFattura(datiFatturaEntity);

        CodificaStatiEntity codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.parse(codificaStatoValue), entityManager);

        statoFatturaEntity.setStato(codificaStatiEntity);

        statoFatturaDao.create(statoFatturaEntity, entityManager);
    }

    /**
     * Cancella gli stati della fattura per procedere ad un reinvio
     *
     * @param tipoOp
     * @param identificativiSdi
     * @throws FatturaPAException
     */
    public void cancellaStatiPerReinvio(String tipoOp, String identificativiSdi) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            if ("Fatture".equals(tipoOp)) {
                statoFatturaDao.cancellaStatiFatturaPerReinvio(identificativiSdi, entityManager);
            } else {
                statoFatturaDao.cancellaStatiDecTerminiPerReinvio(identificativiSdi, entityManager);
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


    public List<Object[]> getStatoFatturaUltimoStatoRicevutaByCodiceDestinatario(String codDest) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<Object[]> fattureRicevute = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fattureRicevute = datiFatturaDao.getStatoFatturaUltimoStatoRicevutaByCodiceDestinatario(codDest, entityManager);
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
        return fattureRicevute;
    }

    public StatoFatturaEntity getUltimoStatoFattura(DatiFatturaEntity datiFatturaEntity) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        StatoFatturaEntity dfe = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            dfe = datiFatturaDao.getUltimoStatoFattura(datiFatturaEntity, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e) {
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
        return dfe;
    }

    public List<StatoFatturaEntity> getStatiFattura(DatiFatturaEntity datiFatturaEntity) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<StatoFatturaEntity> dfe = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            dfe = datiFatturaDao.getStatiFattura(datiFatturaEntity, entityManager);
        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (NoResultException e) {
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
        return dfe;
    }

    public List<Object[]> getStatoFatturaUltimoStatoProtocolloByCodiceDestinatario(String codDest) throws FatturaPAException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        List<Object[]> fattureRicevute = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fattureRicevute = datiFatturaDao.getStatoFatturaUltimoStatoProtocolloByCodiceDestinatario(codDest, entityManager);
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
        return fattureRicevute;
    }


    public void aggiornaStatoFattureFtp(List<BigInteger> idFatture, String codStato, String nomeFileZip, String codiceEnte) throws FatturaPAException {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Date now = new Date();
            CodificaStatiEntity codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.parse(codStato), entityManager);
            ZipFtpEntity zipFtpEntity = new ZipFtpEntity();
            zipFtpEntity.setDataInvio(now);
            zipFtpEntity.setNomeFileZip(nomeFileZip);
            zipFtpEntity.setFtpOut(true);
            zipFtpEntity.setFtpIn(false);
            zipFtpEntity.setCodiceEnte(codiceEnte);

            for (BigInteger idFattura : idFatture) {
                DatiFatturaEntity datiFatturaEntity = datiFatturaDao.read(idFattura, entityManager);
                // datiFatturaDao.update(datiFatturaEntity, entityManager);

                StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();

                statoFatturaEntity.setData(new Timestamp(now.getTime()));
                statoFatturaEntity.setDatiFattura(datiFatturaEntity);
                statoFatturaEntity.setStato(codificaStatiEntity);
                statoFatturaEntity.setZipFtpEntity(zipFtpEntity);
                statoFatturaDao.create(statoFatturaEntity, entityManager);
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

    public List<Object[]> getUltimoStatoFatture(String interval) throws FatturaPAException {
        EntityManager entityManager = null;
        List<Object[]> fattureRicevute = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            fattureRicevute = datiFatturaDao.getUltimoStatoFatture(entityManager, interval);
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
        return fattureRicevute;
    }

    public CodificaStatiEntity getCodificaStatoByIdCodStato(String idCodStato) throws NoResultException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        CodificaStatiEntity cs = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            cs = codificaStatiDao.getByIdCodStato(idCodStato, entityManager);
        } catch (NoResultException e) {
            return null;
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return cs;
    }


    public Long getCountUltimoStatoFatture(String interval) throws FatturaPAException {
        EntityManager entityManager = null;
        Long count = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            count = datiFatturaDao.getCountUltimoStatoFatture(entityManager, interval);
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
        return count;
    }


    public BigInteger getMaxIdentificativoSdiTest() throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            BigInteger max = datiFatturaDao.getMaxIdentificativoSdiTest(entityManager);

            return max;
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

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }

    public StatoFatturaDao getStatoFatturaDao() {
        return statoFatturaDao;
    }

    public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
        this.statoFatturaDao = statoFatturaDao;
    }

    public CodificaStatiDao getCodificaStatiDao() {
        return codificaStatiDao;
    }

    public void setCodificaStatiDao(CodificaStatiDao codificaStatiDao) {
        this.codificaStatiDao = codificaStatiDao;
    }
}
