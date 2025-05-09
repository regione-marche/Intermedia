package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.FileFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/03/15.
 */
public class FatturazionePassivaFatturaManager {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaFatturaManager.class);

    private EntityManagerFactory entityManagerFactory;

    private CodificaStatiDao codificaStatiDao;

    private StatoFatturaDao statoFatturaDao;

    private DatiFatturaDao datiFatturaDao;

    private FileFatturaDao fileFatturaDao;


    public Date recuperaDataRicezioneByIdentificativoSdI(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            CodificaStatiEntity codificaStatiEntity = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA, entityManager);

            Date dataRicezione = statoFatturaDao.getDataCambiamentoStatoFatturaByIdentificativoSdi(new BigInteger(identificativoSdI), codificaStatiEntity, entityManager);

            entityManager.clear();
            entityManager.flush();

            entityManager.getTransaction().commit();
            return dataRicezione;

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare la data di ricezione della Fattura/Lotto Fatture :" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }


    public String getSegnaturaProtocolloByIdentificativoSdI(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdi(new BigInteger(identificativoSdI), entityManager);

            if (datiFatturaEntityList.size() > 0 && datiFatturaEntityList.get(0) != null && datiFatturaEntityList.get(0).getNumeroProtocollo() != null) {
                entityManager.getTransaction().commit();
                return datiFatturaEntityList.get(0).getNumeroProtocollo();
            }

            entityManager.clear();
            entityManager.flush();

            entityManager.getTransaction().commit();
            return null;

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il Numero Protocollo della Fattura/Lotto Fatture :" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public FileFatturaEntity getFileFatturaEntityByIdentificativiSdI(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            FileFatturaEntity fileFatturaEntity = fileFatturaDao.getFileFatturaByIdentificativiSdI(new BigInteger(identificativoSdI), entityManager);

            entityManager.clear();

            entityManager.flush();

            entityManager.getTransaction().commit();

            return fileFatturaEntity;

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il file originale della Fattura/Lotto Fatture avente identificativo SDI :" + identificativoSdI + "" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public byte[] getFileFatturaByIdentificativiSdI(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            FileFatturaEntity fileFatturaEntity = fileFatturaDao.getFileFatturaByIdentificativiSdI(new BigInteger(identificativoSdI), entityManager);

            entityManager.clear();

            entityManager.flush();

            entityManager.getTransaction().commit();

            return fileFatturaEntity.getContenutoFile();

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il file originale della Fattura/Lotto Fatture avente identificativo SDI :" + identificativoSdI + "" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void aggiornaStatoFattureAProtocollateByIdentificativoSdI(String identificativoSdI, String numeroProtocollo) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            StatoFatturaEntity nuovoStatoFattura;

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            CodificaStatiEntity statoRicevuta = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA, entityManager);

            CodificaStatiEntity statoProtocollata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.PROTOCOLLATA, entityManager);

            List<DatiFatturaEntity> fattureByIdentificativoSdi = datiFatturaDao.getFattureByIdentificativoSdi(new BigInteger(identificativoSdI), entityManager);

            LOG.info("FatturazionePassivaFatturaManager:aggiornaStatoFattureAProtocollateByIdentificativoSdI: Protocollo:" + numeroProtocollo + " identificativoSdI:" + identificativoSdI + "numero Fatture da aggiornare: "
                    + fattureByIdentificativoSdi.size());

            for (DatiFatturaEntity fattura : fattureByIdentificativoSdi) {

                StatoFatturaEntity statoFatturaAttuale = statoFatturaDao.getLastStato(fattura.getIdDatiFattura(), entityManager);

                //StatoFatturaEntity statoFatturaAttuale = fattura.getLastStato();

                if (statoFatturaAttuale.getStato().getCodStato().getValue().compareTo(statoRicevuta.getCodStato().getValue())==0) {
                    nuovoStatoFattura = new StatoFatturaEntity();
                    statoFatturaAttuale.getDatiFattura().setNumeroProtocollo(numeroProtocollo);
                    nuovoStatoFattura.setDatiFattura(statoFatturaAttuale.getDatiFattura());
                    nuovoStatoFattura.setStato(statoProtocollata);
                    statoFatturaDao.create(nuovoStatoFattura, entityManager);
                    entityManager.clear();
                    entityManager.flush();
                } else {
                    throw new FatturaPAException("Errore durante l'aggiornamento dello stato fattura a PROTOCOLLATA: Fattura numero: " + statoFatturaAttuale.getDatiFattura().getNumeroFattura() + " identificativoSdI: " + identificativoSdI + " non è nello stato RICEVUTA");
                }
            }

            entityManager.getTransaction().commit();
        } catch (FatturaPAException e) {
            throw new FatturaPAException(e.getMessage());
        }
        catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il file originale della Fattura/Lotto Fatture avente identificativo SDI :" + identificativoSdI + "" + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }


    public void aggiornaStatoFattureARegistrateByIdentificativoSdI(String identificativoSdI) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            StatoFatturaEntity statoFatturaEntityRegistrata;

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            CodificaStatiEntity statoProtocollata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.PROTOCOLLATA, entityManager);

            CodificaStatiEntity statoRegistrata =   codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.REGISTRATA, entityManager);

            List<StatoFatturaEntity> statoFatturaEntityList = statoFatturaDao.getStatoFattureByIdentificativoSdI(new BigInteger(identificativoSdI), entityManager);

            for (StatoFatturaEntity statoFatturaEntityProtocollata : statoFatturaEntityList) {

                if (statoFatturaEntityProtocollata.getStato().getCodStato().equals(statoProtocollata)) {
                    statoFatturaEntityRegistrata = new StatoFatturaEntity();
                    statoFatturaEntityRegistrata.setDatiFattura(statoFatturaEntityProtocollata.getDatiFattura());
                    statoFatturaEntityRegistrata.setStato(statoRegistrata);
                    statoFatturaDao.create(statoFatturaEntityRegistrata, entityManager);
                    entityManager.clear();
                    entityManager.flush();
                } else {
                    throw new FatturaPAException("Errore durante l'aggiornamento dello stato fattura a REGISTRATA: Fattura numero: " + statoFatturaEntityProtocollata.getDatiFattura().getNumeroFattura() + " identificativoSdI: " + identificativoSdI + " non è nello stato PROTOCOLLATA");
                }
            }

            entityManager.getTransaction().commit();
        } catch (FatturaPAException e) {
            throw new FatturaPAException(e.getMessage());
        }
        catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il file originale della Fattura/Lotto Fatture avente identificativo SDI :" + identificativoSdI + "" + e.getMessage(), e);
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
     * Restituisce la lista di identificativiSdI delle fatture salvate sul database prima di una cerca data, per poterle
     * storicizzare.
     * Si tratta degli identificativi SdI validi solo per le seguenti tabelle
     * * file_fattura
     * * dati_fattura
     * * metadati_fattura (da questa non prendiamo però non si prende nulla)
     * * stato_fattura
     *
     * @param dataRiferimento
     * @return
     * @throws FatturaPAException
     */
    public List<String> getIdentificativoSdiListBeforeDate(Date dataRiferimento) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            List<Long> identificativoSdiListBeforeDate = fileFatturaDao.getIdentificativoSdiListBeforeDate(dataRiferimento,entityManager);

            entityManager.clear();
            entityManager.flush();

            entityManager.getTransaction().commit();


            List<String> identificativoSdiList = CommonUtils.transformFromListLong(identificativoSdiListBeforeDate);

            return identificativoSdiList;

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
    }

    public Long getCountIdentificativoSdiListBeforeDate(Date dataRiferimento) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            Long count = fileFatturaDao.getCountIdentificativoSdiListBeforeDate(dataRiferimento,entityManager);

            entityManager.clear();
            entityManager.flush();

            entityManager.getTransaction().commit();

            return count;

        } catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare il Numero Protocollo della Fattura/Lotto Fatture :" + e.getMessage(), e);
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

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }

    public FileFatturaDao getFileFatturaDao() {
        return fileFatturaDao;
    }

    public void setFileFatturaDao(FileFatturaDao fileFatturaDao) {
        this.fileFatturaDao = fileFatturaDao;
    }
}
