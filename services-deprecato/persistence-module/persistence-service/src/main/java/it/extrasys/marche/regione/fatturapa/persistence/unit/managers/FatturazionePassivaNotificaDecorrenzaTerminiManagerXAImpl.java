package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaDecorrenzaTerminiType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatalException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 06/03/15.
 */
public class FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl.class);

    private EntityManagerFactory entityManagerFactory;

    private NotificaDecorrenzaTerminiDao notificaDecorrenzaTerminiDao;

    private DatiFatturaDao datiFatturaDao;

    private MessaggiSDIDao messaggiSDIDao;

    private CodificaStatiDao codificaStatiDao;

    private StatoFatturaDao statoFatturaDao;

    public NotificaDecorrenzaTerminiEntity salvaNotificaDecorrenzaTermini(String nomeFile, BigInteger identificativoSdI, NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType, byte[] fileNotificaDecorrenzaTermini, String originalSoapMessage) throws FatturaPAException {

        EntityManager entityManager = null;
        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            if (!verificaStatoGiaPresente(identificativoSdI, entityManager)) {

                //REVO-49
                CodificaStatiEntity codiceStato = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_DECORRENZA_TERMINI, entityManager);

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
                //Non viene salvato sul DB
                notificaDecorrenzaTerminiEntity.setNomeCedentePrestatore(datiFatturaEntity.getNomeCedentePrestatore());

                notificaDecorrenzaTerminiDao.create(notificaDecorrenzaTerminiEntity, entityManager);

                entityManager.flush();
                entityManager.clear();

                //REVO-49
                //SALVATAGGIO STATO

                StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();
                statoFatturaEntity.setStato(codiceStato);
                statoFatturaEntity.setDatiFattura(datiFatturaEntity);
                statoFatturaDao.create(statoFatturaEntity, entityManager);

                entityManager.flush();
                entityManager.clear();

                //FINE SALVATAGGIO STATO

                // SALVO IL MESSAGGIO ORIGINALE SE DIVERSO DA NULL

                if (originalSoapMessage != null && !originalSoapMessage.trim().isEmpty()) {

                    MessaggiSDIEntity messaggiSDIEntity = new MessaggiSDIEntity();
                    messaggiSDIEntity.setIdentificativoSdI(identificativoSdI);
                    messaggiSDIEntity.setMessaggioSdI(originalSoapMessage);

                    messaggiSDIDao.create(messaggiSDIEntity, entityManager);

                    entityManager.flush();
                    entityManager.clear();
                }
            }
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        } finally {

            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return notificaDecorrenzaTerminiEntity;
    }

    private boolean verificaStatoGiaPresente(BigInteger identificativoSdI, EntityManager entityManager) {

        List<StatoFatturaEntity> statoFatturaList = statoFatturaDao.getStatoFattureByIdentificativoSdI(identificativoSdI, entityManager);

        for (StatoFatturaEntity statoFattura : statoFatturaList) {

            if (statoFattura.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_DECORRENZA_TERMINI)) {
                return true;
            }

        }

        return false;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public NotificaDecorrenzaTerminiDao getNotificaDecorrenzaTerminiDao() {
        return notificaDecorrenzaTerminiDao;
    }

    public void setNotificaDecorrenzaTerminiDao(NotificaDecorrenzaTerminiDao notificaDecorrenzaTerminiDao) {
        this.notificaDecorrenzaTerminiDao = notificaDecorrenzaTerminiDao;
    }

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }

    public MessaggiSDIDao getMessaggiSDIDao() {
        return messaggiSDIDao;
    }

    public void setMessaggiSDIDao(MessaggiSDIDao messaggiSDIDao) {
        this.messaggiSDIDao = messaggiSDIDao;
    }

    public CodificaStatiDao getCodificaStatiDao() {
        return codificaStatiDao;
    }

    public void setCodificaStatiDao(CodificaStatiDao codificaStatiDao) {
        this.codificaStatiDao = codificaStatiDao;
    }

    public StatoFatturaDao getStatoFatturaDao() {
        return statoFatturaDao;
    }

    public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
        this.statoFatturaDao = statoFatturaDao;
    }
}