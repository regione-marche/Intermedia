package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatalException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaDecorrenzaTerminiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 1/03/15.
 */
public class FatturazionePassivaNotificaDecorrenzaTerminiManager {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl.class);

    private EntityManagerFactory entityManagerFactory;

    private NotificaDecorrenzaTerminiDao notificaDecorrenzaTerminiDao;

    private DatiFatturaDao datiFatturaDao;


    public byte[] getFileFatturaByIdentificativiSdI(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = notificaDecorrenzaTerminiDao.getNotificaDecorrenzaTerminiByIdentificativiSdI(new BigInteger(identificativoSdI), entityManager);

            entityManager.clear();
            entityManager.flush();

            entityManager.getTransaction().commit();

            return notificaDecorrenzaTerminiEntity.getContenutoFile();

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il file originale della Decorrenza Termini avente identificativo SDI :" + identificativoSdI + "" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public NotificaDecorrenzaTerminiEntity salvaNotificaDecorrenzaTermini(String nomeFile, BigInteger identificativoSdI, byte[] fileNotificaDecorrenzaTermini) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaDecorrenzaTerminiEntity = new NotificaDecorrenzaTerminiEntity();

            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdi(identificativoSdI, entityManager);

            if (datiFatturaEntityList == null || datiFatturaEntityList.size() == 0) {
                throw new FatturaPAFatalException("Non esistono fatture per l'identificavo SDI  " + identificativoSdI);
            }

            DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);
            String cedenteIdFiscaleIva = datiFatturaEntity.getCedenteIdFiscaleIVA();
            String committenteIdFiscaleIVA = datiFatturaEntity.getCommittenteIdFiscaleIVA();

            notificaDecorrenzaTerminiEntity.setCodiceUfficio(datiFatturaEntity.getCodiceDestinatario());
            notificaDecorrenzaTerminiEntity.setNomeFile(nomeFile);
            notificaDecorrenzaTerminiEntity.setIdentificativoSdi(identificativoSdI);
            notificaDecorrenzaTerminiEntity.setIdFiscaleCommittente(committenteIdFiscaleIVA);
            notificaDecorrenzaTerminiEntity.setContenutoFile(fileNotificaDecorrenzaTermini);

            notificaDecorrenzaTerminiDao.create(notificaDecorrenzaTerminiEntity, entityManager);

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

        return notificaDecorrenzaTerminiEntity;

    }

    public NotificaDecorrenzaTerminiEntity protocollaNotificaDecorrenzaTermini(BigInteger identificativoSdI, String numeroProtocollo) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            notificaDecorrenzaTerminiEntity = getNotificaDecorrenzaTerminiDao().getNotificaDecorrenzaTerminiByIdentificativiSdI(identificativoSdI, entityManager);

            notificaDecorrenzaTerminiEntity.setNumeroProtocollo(numeroProtocollo);

            notificaDecorrenzaTerminiDao.update(notificaDecorrenzaTerminiEntity, entityManager);

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

        return notificaDecorrenzaTerminiEntity;
    }

    public NotificaDecorrenzaTerminiEntity getNotificaDecorrenzaTerminiByIdentificativoSDI(BigInteger identificativoSdI) throws Exception {

        EntityManager entityManager = null;

        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            notificaDecorrenzaTerminiEntity = notificaDecorrenzaTerminiDao.getNotificaDecorrenzaTerminiByIdentificativiSdI(identificativoSdI, entityManager);

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

        return notificaDecorrenzaTerminiEntity;
    }


    public List<NotificaDecorrenzaTerminiEntity> getNotificaDecorrenzaTerminiFtpByEnte(String codDest) throws FatturaPAException {
        EntityManager entityManager = null;
        List<NotificaDecorrenzaTerminiEntity> notificaDecorrenzaTermini;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            notificaDecorrenzaTermini = notificaDecorrenzaTerminiDao.getNotificaDecorrenzaTerminiFtpByEnte(codDest, entityManager);
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
        return notificaDecorrenzaTermini;
    }


    public NotificaDecorrenzaTerminiDao getNotificaDecorrenzaTerminiDao() {
        return notificaDecorrenzaTerminiDao;
    }

    public void setNotificaDecorrenzaTerminiDao(NotificaDecorrenzaTerminiDao notificaDecorrenzaTerminiDao) {
        this.notificaDecorrenzaTerminiDao = notificaDecorrenzaTerminiDao;
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
