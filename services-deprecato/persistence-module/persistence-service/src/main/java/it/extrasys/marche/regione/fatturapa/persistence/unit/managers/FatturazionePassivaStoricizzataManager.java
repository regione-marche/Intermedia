package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.persistence.camel.model.ReportFatturazioneStorico;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.TipoCanaleEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.storico.FatturaPassivaStoricizzataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FatturazionePassivaStoricizzataManager {

    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaStoricizzataManager.class);

    private EntityManagerFactory entityManagerFactory;

    private StatoFatturaDao statoFatturaDao;
    private DatiFatturaDao datiFatturaDao;
    private FileFatturaDao fileFatturaDao;
    private MetadatiFatturaDao metadatiFatturaDao;
    private FatturaPassivaStoricizzataDao fatturaPassivaStoricizzataDao;


    /**
     * recupera le info della fattura da storicizzare, direttamente nel formato da salvare
     *
     * @param identificativoSdi
     * @return
     */
    public List<ReportFatturazioneStorico> storicizzaFatturaPassivaPerIdentificativoSdi(String identificativoSdi) throws Exception {

        EntityManager entityManager = null;

        List<ReportFatturazioneStorico> reportList = new ArrayList<>();
        ReportFatturazioneStorico report = new ReportFatturazioneStorico();
        try {
            report.setIdentificativoSdI(identificativoSdi);

            entityManager = entityManagerFactory.createEntityManager();
            List<Object[]> datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdiNative(new BigInteger(identificativoSdi), entityManager);
            for (Object[] dfe : datiFatturaEntityList) {

                List<Object[]> statoFatturaEntityList = statoFatturaDao.getStatoFattureByIdFattura(BigInteger.valueOf((Long) dfe[9]), entityManager);

                List<BigInteger> listIdDatiFattura = new ArrayList<>();
                for (Object[] obj : datiFatturaEntityList) {
                    listIdDatiFattura.add(BigInteger.valueOf((Long) obj[9]));
                }

                FatturaPassivaStoricizzataEntity fpse = new FatturaPassivaStoricizzataEntity();
                fpse.setIdentificativoSdI(BigInteger.valueOf((Long) dfe[0]));
                fpse.setCedenteIdFiscaleIva((String) dfe[1]);
                fpse.setCommittenteIdFiscaleIva((String) dfe[2]);
                fpse.setCodiceUfficio((String) dfe[3]);
                fpse.setDataFattura((Date) dfe[4]);
                fpse.setDataRicezioneSdi((Date) dfe[5]);
                //verificare come caricare questo dato, visto che è presente solo nell'attiva per ora
                if ("T".equalsIgnoreCase((String) dfe[10])) {
                    fpse.setFatturazioneInterna(true);
                } else if ("F".equalsIgnoreCase((String) dfe[10])) {
                    fpse.setFatturazioneInterna(false);
                }
                fpse.setNomeFileFattura((String) dfe[6]);
                fpse.setNumeroFattura((String) dfe[7]);
                fpse.setNumeroProtocollo((String) dfe[8]);
                fpse.setStatoFattura(getSequenzaStatiFatturaFromIdentificativoSdI(statoFatturaEntityList));
                fpse.setTipoCanale(TipoCanaleEntity.TIPO_CANALE.parse((String)dfe[11]).name());
                fpse.setDataInserimento(new Date());

                entityManager.getTransaction().begin();
                //Scrive sulla nuova tabella
                fatturaPassivaStoricizzataDao.create(fpse, entityManager);

                //Cancella i dati dalle vecchie tabelle
                statoFatturaDao.deleteByIdDatiFattura(listIdDatiFattura, entityManager);
                metadatiFatturaDao.nativeDeleteByIdentificativoSdI(BigInteger.valueOf((Long) dfe[0]), entityManager);
                fileFatturaDao.deleteByIdentificativoSdi(BigInteger.valueOf((Long) dfe[0]), entityManager);
                datiFatturaDao.deleteByIdentificativoSdi(BigInteger.valueOf((Long) dfe[0]), entityManager);
                entityManager.getTransaction().commit();
                report.setEsito("Success");
                reportList.add(report);
            }
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            report.setEsito("Error");
            reportList.add(report);
            LOG.error("STORICIZZAZIONE FATTURE PASSIVA - Errore " + e.getMessage() + " durante salvataggio identificativo sdi: [" + identificativoSdi + "]", e);
        }
        return reportList;
    }

    /**
     * prende la sequenza degli stati  e li contatena in una stringa che restituisce
     *
     * @param statoFatturaEntityList
     * @return la lista degli stati contatenati in una stringa
     */
    private String getSequenzaStatiFatturaFromIdentificativoSdI(List<Object[]> statoFatturaEntityList) {

        if (statoFatturaEntityList == null || statoFatturaEntityList.isEmpty()) {
            return null;
        }

        String sequenzaStati = "";

        for (Object[] sfe : statoFatturaEntityList) {
            sequenzaStati = sequenzaStati + (String) sfe[0] + " - " + (Date) sfe[1] + "; ";
        }

        return sequenzaStati;
    }

    public List<Object[]> getFattureUltimAnno() throws Exception {

        LOG.info("********** FatturaPassivaStoricizzataManager: getFattureUltimAnno **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();
        List<Object[]> fatturaPassivaStoricizzataEntityList = null;

       // try {
            fatturaPassivaStoricizzataEntityList = fatturaPassivaStoricizzataDao.getFattureUltimAnno(entityManager);
      /*  } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }

        }*/
        return fatturaPassivaStoricizzataEntityList;
    }

    public List<Object[]> getFattureUltimAnnoUtente(UtenteEntity utenteEntity) throws Exception {

        LOG.info("********** FatturaPassivaStoricizzataManager: getFattureUltimAnnoUtente **********");

        EntityManager entityManager = null;

        entityManager = entityManagerFactory.createEntityManager();
        List<Object[]> fatturaPassivaStoricizzataEntityList = null;

       try {

            fatturaPassivaStoricizzataEntityList = fatturaPassivaStoricizzataDao.getFattureUltimAnnoUtente(utenteEntity, entityManager);

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }

        }
        return fatturaPassivaStoricizzataEntityList;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
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

    public MetadatiFatturaDao getMetadatiFatturaDao() {
        return metadatiFatturaDao;
    }

    public void setMetadatiFatturaDao(MetadatiFatturaDao metadatiFatturaDao) {
        this.metadatiFatturaDao = metadatiFatturaDao;
    }

    public FatturaPassivaStoricizzataDao getFatturaPassivaStoricizzataDao() {
        return fatturaPassivaStoricizzataDao;
    }

    public void setFatturaPassivaStoricizzataDao(FatturaPassivaStoricizzataDao fatturaPassivaStoricizzataDao) {
        this.fatturaPassivaStoricizzataDao = fatturaPassivaStoricizzataDao;
    }
}
