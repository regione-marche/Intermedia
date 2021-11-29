package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaFromEntiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaFromSdiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 03/03/15.
 */
public class NotificaFromSdiManager {

    private static final Logger LOG = LoggerFactory.getLogger(NotificaFromSdiManager.class);

    private static final String NOTIFICA_OK_DESCRIZIONE = "Notifica Accettata dallo SDI";
    private static final String NOTIFICA_SCARTO_DESCRIZIONE = "Notifica Non Accettata dallo SDI";

    private EntityManagerFactory entityManagerFactory;

    private NotificaFromSdiDao notificaFromSdiDao;

    private NotificaFromEntiDao notificaFromEntiDao;

    /**
     * Deprecato perche' non tiene conto del fatto che idComunizione possa essere null e dunque nn valorizzato.
     * <p>
     * Usare la salvaNotificaFromSdi
     *
     * @param idComunicazione
     * @param nomeFile
     * @param originalMessage
     * @return
     * @throws FatturaPaPersistenceException
     * @throws FatturaPAException
     */
    @Deprecated
    public NotificaFromSdiEntity salvaNotificaScarto(String idComunicazione, String nomeFile, String originalMessage) throws FatturaPaPersistenceException, FatturaPAException {


        EntityManager entityManager = null;
        NotificaFromSdiEntity notificaFromSdiEntity = new NotificaFromSdiEntity();

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            NotificaFromEntiEntity notificaFromEnti = notificaFromEntiDao.getNotificaFromEntiByIdComunicazione(idComunicazione, entityManager);

            //implicita la notifica KO, visto che siamo salvando la notifica scarto
            notificaFromSdiEntity.setEsito(EsitoNotificaType.ES_00);
            notificaFromSdiEntity.setDescrizione("Notifica Non Accettata dallo SDI");
            notificaFromSdiEntity.setNomeFileScarto(nomeFile);
            notificaFromSdiEntity.setNotificaFromEntiEntity(notificaFromEnti);
            notificaFromSdiEntity.setContenutoFile(originalMessage);

            notificaFromSdiDao.create(notificaFromSdiEntity, entityManager);

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

        return notificaFromSdiEntity;
    }

    /**
     * Deprecato perche' non tiene conto del fatto che idComunizione possa essere null e dunque nn valorizzato.
     * <p>
     * Usare la salvaNotificaFromSdi
     *
     * @param idComunicazione
     * @param originalMessage
     * @throws FatturaPaPersistenceException
     * @throws FatturaPAException
     */
    @Deprecated
    public void salvaNotificaOk(String idComunicazione, String originalMessage) throws FatturaPaPersistenceException, FatturaPAException {

        EntityManager entityManager = null;
        NotificaFromSdiEntity notificaFromSdiEntity = new NotificaFromSdiEntity();

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            //NotificaFromEntiEntity notificaFromEnti = notificaFromEntiDao.read(idNotificaFromEnte, entityManager);
            NotificaFromEntiEntity notificaFromEnti = notificaFromEntiDao.getNotificaFromEntiByIdComunicazione(idComunicazione, entityManager);

            //implicita la notifica OK
            notificaFromSdiEntity.setEsito(EsitoNotificaType.ES_01);
            notificaFromSdiEntity.setDescrizione("Notifica Accettata dallo SDI");
            notificaFromSdiEntity.setNotificaFromEntiEntity(notificaFromEnti);
            notificaFromSdiEntity.setContenutoFile(originalMessage);

            notificaFromSdiDao.create(notificaFromSdiEntity, entityManager);
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
     * Salva il record relativo alla notifica ricevuta dallo sdi. Questo e' collegato, tramite l'idComunicazione,
     * ad una relativa notifica_from_enti
     *
     * @param idComunicazione     l'id della comunicazione con l'ente
     * @param esito               l'esito della comunicazione: true = ok, false = scarto
     * @param nomeFile            il nome file scarto (caso scarto con esito = false
     * @param originalMessage     il messaggio originale da salvare
     * @param contenutoFileScarto contenuto del file di scarto, valorizzato solo se esito=false
     * @throws FatturaPaPersistenceException
     * @throws FatturaPAException
     */
    public NotificaFromSdiEntity salvaNotificaFromSdi(String idComunicazione, boolean esito, String nomeFile, String originalMessage, String identificativoSdI, String contenutoFileScarto) throws FatturaPaPersistenceException, FatturaPAException {
        EntityManager entityManager = null;
        NotificaFromSdiEntity notificaFromSdiEntity = new NotificaFromSdiEntity();

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();


            if (idComunicazione == null || "".equals(idComunicazione)) {
                /*
                    se idComunicazione e' null o vuoto significa che non c'e' una notifica_from_enti associata a questa comunicazione
                    come nel caso di rifiuto automatico dovuto alla validazione (asur)
                 */
                notificaFromSdiEntity.setNotificaFromEntiEntity(null);

            } else {
                NotificaFromEntiEntity notificaFromEnti = notificaFromEntiDao.getNotificaFromEntiByIdComunicazione(idComunicazione, entityManager);
                notificaFromEnti.setDataRicezioneRispostaSDI(new Date());
                notificaFromSdiEntity.setNotificaFromEntiEntity(notificaFromEnti);
            }

            if (esito) {
                notificaFromSdiEntity.setEsito(EsitoNotificaType.ES_01);
                notificaFromSdiEntity.setDescrizione(NOTIFICA_OK_DESCRIZIONE);
                notificaFromSdiEntity.setContenutoFile(originalMessage);
            } else {
                notificaFromSdiEntity.setEsito(EsitoNotificaType.ES_00);
                notificaFromSdiEntity.setDescrizione(NOTIFICA_SCARTO_DESCRIZIONE);
                notificaFromSdiEntity.setContenutoFile(contenutoFileScarto);
                notificaFromSdiEntity.setNomeFileScarto(nomeFile);
            }

            if (identificativoSdI != null && !"".equals(identificativoSdI))
                notificaFromSdiEntity.setIdentificativoSdI(new BigInteger(identificativoSdI));

            notificaFromSdiDao.create(notificaFromSdiEntity, entityManager);
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

        return notificaFromSdiEntity;
    }

    public NotificaFromSdiEntity salvaNotificaECScartataFromSdi(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaFromSdiEntity notificaFromSdiEntity = new NotificaFromSdiEntity();

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaFromSdiEntity.setEsito(EsitoNotificaType.ES_00);
            notificaFromSdiEntity.setDescrizione("Notifica EC inviata in protocollazione");

            if (identificativoSdI != null && !"".equals(identificativoSdI))
                notificaFromSdiEntity.setIdentificativoSdI(new BigInteger(identificativoSdI));

            notificaFromSdiDao.create(notificaFromSdiEntity, entityManager);
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

        return notificaFromSdiEntity;
    }

    public NotificaFromSdiEntity protocollaNotificaECFromSdI(BigInteger identificativoSdI, String numeroProtocollo) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaFromSdiEntity = notificaFromSdiDao.getNotificaFromSdIByIdentificativiSdI(identificativoSdI, entityManager);

            notificaFromSdiEntity.setNumeroProtocolloNotifica(numeroProtocollo);

            notificaFromSdiDao.update(notificaFromSdiEntity, entityManager);

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

        return notificaFromSdiEntity;
    }

    public NotificaFromSdiEntity protocollaNotificaScartoECFromSdI(BigInteger identificativoSdI, String numeroProtocollo) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaFromSdiEntity = notificaFromSdiDao.getNotificaScartoByIdentificativoSdI(identificativoSdI, entityManager);

            notificaFromSdiEntity.setNumeroProtocolloNotifica(numeroProtocollo);

            notificaFromSdiDao.update(notificaFromSdiEntity, entityManager);

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

        return notificaFromSdiEntity;
    }

    public List<NotificaFromSdiEntity> getScartoEsitoFtpByEnte(String codiceUfficio) throws FatturaPAException {

        EntityManager entityManager = null;

        List<NotificaFromSdiEntity> notificaFromSdiEntityList = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            notificaFromSdiEntityList = notificaFromEntiDao.getScartoEsitoFtpByEnte(codiceUfficio, entityManager);

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
        return notificaFromSdiEntityList;
    }

    public List<NotificaFromSdiEntity> getScartoEsitoFtpByEnteG1G4(String codiceUfficio) throws FatturaPAException {

        EntityManager entityManager = null;

        List<NotificaFromSdiEntity> notificaFromSdiEntityList = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            notificaFromSdiEntityList = notificaFromEntiDao.getScartoEsitoFtpByEnteG1G4(codiceUfficio, entityManager);

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
        return notificaFromSdiEntityList;
    }

    public NotificaFromSdiEntity getScartoEsitoFromSdI(BigInteger identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            //Ritorna ultima notifica scarto esito
            notificaFromSdiEntity = notificaFromSdiDao.getNotificaScartoByIdentificativoSdI(identificativoSdI, entityManager);

        } catch (Exception e) {

            throw new FatturaPAException(e.getMessage(), e);

        } finally {

            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return notificaFromSdiEntity;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public NotificaFromSdiDao getNotificaFromSdiDao() {
        return notificaFromSdiDao;
    }

    public void setNotificaFromSdiDao(NotificaFromSdiDao notificaFromSdiDao) {
        this.notificaFromSdiDao = notificaFromSdiDao;
    }

    public NotificaFromEntiDao getNotificaFromEntiDao() {
        return notificaFromEntiDao;
    }

    public void setNotificaFromEntiDao(NotificaFromEntiDao notificaFromEntiDao) {
        this.notificaFromEntiDao = notificaFromEntiDao;
    }
}
