package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaFromEntiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by agosteeno on 03/03/15.
 */
public class NotificaFromEntiManager {

    private EntityManagerFactory entityManagerFactory;

    private NotificaFromEntiDao notificaFromEntiDao;

    private DatiFatturaDao datiFatturaDao;

    public NotificaFromEntiEntity salvaNotificaECPec(String notificaECBase64, String nomeFile) throws FatturaPAException, FatturaPAFatturaNonTrovataException {

        EntityManager entityManager = null;
        NotificaFromEntiEntity notificaFromEntiEntity = new NotificaFromEntiEntity();

        String notificaEC = "";

        try {
            notificaEC = new String(Base64.decodeBase64(notificaECBase64));
        } catch (Exception e) {
            throw new FatturaPAException("Errore Interno: " + e.getMessage(), e);
        }

        NotificaEsitoCommittenteType notificaEsitoCommittente = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaEsitoCommittente = JaxBUtils.getNotificaEsitoCommittenteType(notificaEC.getBytes());
            DatiFatturaEntity datiFatturaEntity = null;

            if (notificaEsitoCommittente.getRiferimentoFattura() != null && StringUtils.isNotEmpty(notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura())) {
                datiFatturaEntity = datiFatturaDao.getFattureByIdentificativoSdINumeroFattura(notificaEsitoCommittente.getIdentificativoSdI(), notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura(), entityManager);
            } else {
                datiFatturaEntity = datiFatturaDao.getFattureByIdentificativoSdi(notificaEsitoCommittente.getIdentificativoSdI(), entityManager).get(0);
            }
            notificaFromEntiEntity.setEsito(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.parse(notificaEsitoCommittente.getEsito().value()));
            notificaFromEntiEntity.setDescrizione(notificaEsitoCommittente.getDescrizione());
            notificaFromEntiEntity.setMessageIdCommittente(notificaEsitoCommittente.getMessageIdCommittente());
            notificaFromEntiEntity.setIdentificativoSdI(datiFatturaEntity.getIdentificativoSdI());
            notificaFromEntiEntity.setCodUfficio(datiFatturaEntity.getCodiceDestinatario());
            notificaFromEntiEntity.setIdFiscaleCommittente(datiFatturaEntity.getCommittenteIdFiscaleIVA());
            if (notificaEsitoCommittente.getRiferimentoFattura() != null && StringUtils.isNotEmpty(notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura())) {
                notificaFromEntiEntity.setNumeroFattura(notificaEsitoCommittente.getRiferimentoFattura().getNumeroFattura());
            }
            notificaFromEntiEntity.setNumeroProtocollo(datiFatturaEntity.getNumeroProtocollo());
            notificaFromEntiEntity.setDataFattura(datiFatturaEntity.getDataFattura());
            notificaFromEntiEntity.setDataRicezioneFromEnte(new Date());
            notificaFromEntiEntity.setNomeFile(nomeFile);

            // Salvo la notifica
            notificaFromEntiEntity = notificaFromEntiDao.create(notificaFromEntiEntity, entityManager);
            entityManager.getTransaction().commit();

        } catch (NoResultException e) {

            throw new FatturaPAFatturaNonTrovataException("Fattura Non Trovata: Identificativo SdI: " + notificaEsitoCommittente.getIdentificativoSdI());

        } catch (Exception e) {

            throw new FatturaPAException("Errore Interno:" + e.getMessage(), e);

        }finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return notificaFromEntiEntity;
    }

    public NotificaFromEntiEntity protocollaNotificaEsitoCommittente(BigInteger identificativoSdI, String numeroProtocollo) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaFromEntiEntity notificaFromEntiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaFromEntiEntity = notificaFromEntiDao.getNotificaEsitoCommittenteByIdentificativiSdI(identificativoSdI, entityManager);

            notificaFromEntiEntity.setNumeroProtocollo(numeroProtocollo);

            notificaFromEntiDao.update(notificaFromEntiEntity, entityManager);

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

        return notificaFromEntiEntity;
    }

    public String getNomeFileECFromIdentificativoSdi(BigInteger identificativoSdi) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            NotificaFromEntiEntity notificaFromEntiEntity = notificaFromEntiDao.getNotificaEsitoCommittenteByIdentificativiSdI(identificativoSdi, entityManager);

            return notificaFromEntiEntity.getNomeFile();

        } catch (FatturaPAFatturaNonTrovataException e) {
            return null;
        }

    }

    public NotificaFromEntiDao getNotificaFromEntiDao() {
        return notificaFromEntiDao;
    }

    public void setNotificaFromEntiDao(NotificaFromEntiDao notificaFromEntiDao) {
        this.notificaFromEntiDao = notificaFromEntiDao;
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
}