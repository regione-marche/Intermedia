package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.converters.FatturaElettronicaAttivaToStatoFatturaAttivaEntityConverter;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiAttivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.EnteDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.FatturaAttivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaAttivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoFatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 15/03/15.
 */
public class FatturaAttivaManagerImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FatturaAttivaManagerImpl.class);

    private EntityManagerFactory entityManagerFactory;

    private FatturaAttivaDao fatturaAttivaDao;

    private StatoFatturaAttivaDao statoFatturaAttivaDao;

    private CodificaStatiAttivaDao codificaStatiAttivaDao;

    private EnteDao enteDao;

    private FatturaElettronicaAttivaToStatoFatturaAttivaEntityConverter statoConverter;

    public FatturaAttivaEntity salvaFatturaAttiva(byte[] fileFatturaOriginale, String nomeFileFattura, EnteEntity enteEntity,
                                                  String formatoTrasmissione, String codiceDestinatario, String pecDestinatario,
                                                  String ricevutaComunicazione, Boolean isTest) throws FatturaPAException {

        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            CodificaStatiAttivaEntity codiceStato = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.RICEVUTA, entityManager);

            //REGMA-21
            //Verifico se è flusso semplificato ovvero se il destinatario è sempre gestito da IntermediaMarche
            boolean isFlussoSemplificato = isFlussoSemplificato(entityManager, codiceDestinatario);

            StatoFatturaAttivaEntity statoFatturaAttivaEntity;

            statoFatturaAttivaEntity = statoConverter.convert(nomeFileFattura, formatoTrasmissione, codiceDestinatario, pecDestinatario, fileFatturaOriginale, enteEntity, isTest, isFlussoSemplificato);

            statoFatturaAttivaEntity.setStato(codiceStato);

            fatturaAttivaEntity = fatturaAttivaDao.create(statoFatturaAttivaEntity.getFatturaAttiva(), entityManager);
            statoFatturaAttivaDao.create(statoFatturaAttivaEntity, entityManager);

            if (ricevutaComunicazione != null && !"".equals(ricevutaComunicazione)) {
                fatturaAttivaEntity.setRicevutaComunicazione(ricevutaComunicazione);
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

        return fatturaAttivaEntity;

    }

    public void salvaIdentificativoSdIAttiva(BigInteger idFattura, BigInteger identificativoSdi) throws FatturaPAException {

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaDao.read(idFattura, entityManager);
            fatturaAttivaEntity.setIdentificativoSdi(identificativoSdi);
            fatturaAttivaDao.update(fatturaAttivaEntity, entityManager);

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

    public void aggiornaStatoFatturaAttivaEsito(BigInteger idFatturaAttivaEntity, String codificaStatoAttiva) throws FatturaPAException {

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            StatoFatturaAttivaEntity statoFatturaAttivaEntity = new StatoFatturaAttivaEntity();
            CodificaStatiAttivaEntity codificaStatiAttivaEntity = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.parse(codificaStatoAttiva), entityManager);

            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaDao.read(idFatturaAttivaEntity, entityManager);

            statoFatturaAttivaEntity.setFatturaAttiva(fatturaAttivaEntity);
            statoFatturaAttivaEntity.setStato(codificaStatiAttivaEntity);

            statoFatturaAttivaDao.create(statoFatturaAttivaEntity, entityManager);

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

    public FatturaAttivaEntity getFatturaAttivaFromIdentificativSdi(BigInteger identificativoSdi) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaEntity = fatturaAttivaDao.getFatturaAttivaFromIdentificativoSdi(identificativoSdi, entityManager);

        } catch (FatturaPAFatturaNonTrovataException e) {
            throw e;
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

        return fatturaAttivaEntity;
    }

    public List<FatturaAttivaEntity> getFatturaAttivaListFromIdentificativoSdi(BigInteger identificativoSdi) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            return fatturaAttivaDao.getFatturaAttivaListFromIdentificativoSdi(identificativoSdi, entityManager);

        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException e) {
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

    public List<FatturaAttivaEntity> getFatturaAttivaListFromIdentificativoSdiTest(BigInteger identificativoSdi) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            return fatturaAttivaDao.getFatturaAttivaListFromIdentificativoSdiTest(identificativoSdi, entityManager);

        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException e) {
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


    public List<FatturaAttivaEntity> getFatturaAttivaListFromCodiceUfficioMittenteAndDate(String codiceUfficio, Date from, Date to) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            return fatturaAttivaDao.getFatturaAttivaListFromCodiceUfficioMittenteAndDate(codiceUfficio, from, to, entityManager);

        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException e) {
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

    public FatturaAttivaEntity getFatturaAttivaFromIdFatturaAttiva(BigInteger idFatturaAttiva) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaEntity = fatturaAttivaDao.getFatturaAttivaFromIdFatturaAttiva(idFatturaAttiva, entityManager);

        } catch (FatturaPAFatturaNonTrovataException e) {
            throw e;
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

        return fatturaAttivaEntity;
    }


    public List<FatturaAttivaEntity> getFatturaAttivaFromNomeFileFattura(String nomeFile) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        EntityManager entityManager = null;
        List<FatturaAttivaEntity> fatturaAttivaList = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            fatturaAttivaList = fatturaAttivaDao.getFatturaAttivaFromNomeFileFattura(nomeFile, entityManager);

        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException e) {
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

        return fatturaAttivaList;
    }

    public List<StatoFatturaAttivaEntity> getStatiFatturaAttivaList(BigInteger idFatturaAttiva) throws FatturaPAException, FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            return statoFatturaAttivaDao.getStatoFromIdFatturaAttiva(idFatturaAttiva, entityManager);

        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException e) {
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

    public List<FatturaAttivaEntity> getFatturaAttivaListUltimoMese() throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** FatturaAttivaManagerImpl: getFatturaAttivaListUltimoMese **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            return fatturaAttivaDao.getFatturaAttivaListUltimoMese(entityManager);

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

    public List<FatturaAttivaEntity> getFatturaAttivaListUltimoMeseUtente(UtenteEntity utenteEntity) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** FatturaAttivaManagerImpl: getFatturaAttivaListUltimoMeseUtente **********");

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            return fatturaAttivaDao.getFatturaAttivaListUltimoMeseUtente(utenteEntity, entityManager);

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


    public List<Object[]> getFatturaAttivaBetweenDate(Date from, Date to) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** FatturaAttivaManagerImpl: getFatturaAttivaBetweenDate **********");

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            return fatturaAttivaDao.getFatturaAttivaBetweenDate(from, to, entityManager);
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


    public List<Object[]> getFatturaAttivaBetweenDateUtente(Date from, Date to, UtenteEntity utenteEntity) throws FatturaPaPersistenceException, FatturaPAException {

        LOG.info("********** FatturaAttivaManagerImpl: getFatturaAttivaBetweenDateUtente **********");

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            return fatturaAttivaDao.getFatturaAttivaBetweenDateUtente(from, to, utenteEntity, entityManager);

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
     * REGMA-112 aggiunta per flusso semplificato
     * <p>
     * imposta il flag fatturazione interna per la fattura con identificativo SdI passato per parametro
     *
     * @param identificativoSdI
     */
    public void impostaFlagFatturazioneInterna(BigInteger identificativoSdI, boolean flag) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            fatturaAttivaEntity = fatturaAttivaDao.getFatturaAttivaFromIdentificativoSdi(identificativoSdI, entityManager);

            fatturaAttivaEntity.setFatturazioneInterna(flag);
            fatturaAttivaDao.update(fatturaAttivaEntity, entityManager);

            entityManager.getTransaction().commit();

        } catch (FatturaPAFatturaNonTrovataException e) {
            throw e;
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


    public FatturaAttivaEntity salvaFatturaAttivaFtp(byte[] fileFatturaOriginale, String nomeFileFattura, FatturaElettronicaType fatturaElettronicaType, EnteEntity enteEntity,
                                                     String formatoTrasmissione, Boolean isTest) throws FatturaPAException {

        EntityManager entityManager = null;
        FatturaAttivaEntity fatturaAttivaEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            CodificaStatiAttivaEntity codiceStato = codificaStatiAttivaDao.read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA.RICEVUTA, entityManager);

            //REGMA-21
            //Verifico se è flusso semplificato ovvero se il destinatario è sempre gestito da IntermediaMarche
            boolean isFlussoSemplificato = isFlussoSemplificato(entityManager, fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario());

            StatoFatturaAttivaEntity statoFatturaAttivaEntity;

            statoFatturaAttivaEntity = statoConverter.convert(nomeFileFattura, formatoTrasmissione, fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario(), null, fileFatturaOriginale, enteEntity, isTest, isFlussoSemplificato);

            statoFatturaAttivaEntity.setStato(codiceStato);

            fatturaAttivaEntity = fatturaAttivaDao.create(statoFatturaAttivaEntity.getFatturaAttiva(), entityManager);
            statoFatturaAttivaDao.create(statoFatturaAttivaEntity, entityManager);

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

        return fatturaAttivaEntity;

    }

    /*
    Return: - identificativoSdiList[0]: id_fattura_attiva
            - identificativoSdiList[1]: identificativoSdi
            - identificativoSdiList[2]: nome_file
            - identificativoSdiList[3]: data_ricezione_from_sdi
            - identificativoSdiList[4]: fatturazione_interna
            - identificativoSdiList[5]: codice_destinatario
            - identificativoSdiList[6]: pec_destinatario
            - identificativoSdiList[7]: ricevuta_comunicazione
     */

    public List<Object[]> getFattureAttiveListBeforeDate(Date dataRiferimento) throws FatturaPAException {
        EntityManager entityManager = null;
        List<Object[]> identificativoSdiList = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            identificativoSdiList = fatturaAttivaDao.getIdentificativoSdiListBeforeDate(dataRiferimento, entityManager);

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare l'identificativoSdI della Fattura/Lotto Fatture :" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return identificativoSdiList;
    }


    public BigInteger getMaxIdentificativoSdiTest() throws FatturaPAException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return fatturaAttivaDao.getMaxIdentificativoSdiTest(entityManager);
        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare max identificativoSdI di test" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    //REGMA-21
    private boolean isFlussoSemplificato(EntityManager entityManager, String codiceDestinatario) throws FatturaPaPersistenceException {

        boolean isFlussoSemplificato = false;

        try {

            //EnteEntity enteEntity = enteDao.getEnteByCodiceUfficio(codiceDestinatario, entityManager);
            //Fatta query in modo da escludere gli enti in stato STAGING
            EnteEntity enteEntity = enteDao.getEnteByCodiceUfficioFlussoSemplificato(codiceDestinatario, entityManager);

            LOG.info("FatturaAttivaManagerImpl - isFlussoSemplificato - ENTE " + codiceDestinatario + " aderente al servizio IntermediaMarche; FlussoSemplificato = true");

            isFlussoSemplificato = true;

        } catch (FatturaPaPersistenceException e) {
            throw e;
        } catch (FatturaPAEnteNonTrovatoException e) {
            //Ente non trovato, dunque il destinatario non e' aderente al servizio intermediaMarche
            LOG.info("FatturaAttivaManagerImpl - isFlussoSemplificato - ENTE " + codiceDestinatario + " NON aderente al servizio IntermediaMarche; FlussoSemplificato = false");
        }

        return isFlussoSemplificato;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FatturaAttivaDao getFatturaAttivaDao() {
        return fatturaAttivaDao;
    }

    public void setFatturaAttivaDao(FatturaAttivaDao fatturaAttivaDao) {
        this.fatturaAttivaDao = fatturaAttivaDao;
    }

    public StatoFatturaAttivaDao getStatoFatturaAttivaDao() {
        return statoFatturaAttivaDao;
    }

    public void setStatoFatturaAttivaDao(StatoFatturaAttivaDao statoFatturaAttivaDao) {
        this.statoFatturaAttivaDao = statoFatturaAttivaDao;
    }

    public CodificaStatiAttivaDao getCodificaStatiAttivaDao() {
        return codificaStatiAttivaDao;
    }

    public void setCodificaStatiAttivaDao(CodificaStatiAttivaDao codificaStatiAttivaDao) {
        this.codificaStatiAttivaDao = codificaStatiAttivaDao;
    }

    public EnteDao getEnteDao() {
        return enteDao;
    }

    public void setEnteDao(EnteDao enteDao) {
        this.enteDao = enteDao;
    }

    public FatturaElettronicaAttivaToStatoFatturaAttivaEntityConverter getStatoConverter() {
        return statoConverter;
    }

    public void setStatoConverter(FatturaElettronicaAttivaToStatoFatturaAttivaEntityConverter statoConverter) {
        this.statoConverter = statoConverter;
    }
}
