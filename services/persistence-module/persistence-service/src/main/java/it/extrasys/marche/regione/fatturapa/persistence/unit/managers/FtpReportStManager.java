package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.FtpReportStDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp.FtpReportStEntity;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class FtpReportStManager {

    private static final Logger LOG = LoggerFactory.getLogger(FtpReportStManager.class);

    private EntityManagerFactory entityManagerFactory;
    private FtpReportStDao ftpReportStDao;

    public FtpReportStEntity salvaFI(String codiceEnte, String nomeSupporto) throws FatturaPAException {

        EntityManager entityManager = null;
        FtpReportStEntity ftpReportStEntity = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            ftpReportStEntity = new FtpReportStEntity();
            ftpReportStEntity.setCodiceEnte(codiceEnte);
            ftpReportStEntity.setNomeSupportoZip(nomeSupporto.replaceAll(".done", ""));
            ftpReportStEntity.setFtpIn(true);
            ftpReportStEntity.setFtpOut(false);
            ftpReportStEntity.setTimestampRicezione(new Timestamp(System.currentTimeMillis()));

            ftpReportStEntity = ftpReportStDao.create(ftpReportStEntity, entityManager);
            ftpReportStDao.create(ftpReportStEntity, entityManager);

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

        return ftpReportStEntity;
    }

    public FtpReportStEntity salvaFO(String codiceEnte, String nomeSupporto) throws FatturaPAException {

        EntityManager entityManager = null;
        FtpReportStEntity ftpReportStEntity = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            ftpReportStEntity = new FtpReportStEntity();
            ftpReportStEntity.setCodiceEnte(codiceEnte);
            ftpReportStEntity.setNomeSupportoZip(nomeSupporto.replaceAll(".done", ""));
            ftpReportStEntity.setFtpIn(false);
            ftpReportStEntity.setFtpOut(true);
            ftpReportStEntity.setTimestampInvio(new Timestamp(System.currentTimeMillis()));

            ftpReportStEntity = ftpReportStDao.create(ftpReportStEntity, entityManager);

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

        return ftpReportStEntity;
    }

    public void updateStatusFOSupporto(BigInteger idFtpReportSt, Boolean errore, String descErrore) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            int row = ftpReportStDao.updateStatusFOSupporto(idFtpReportSt, errore, descErrore, entityManager);

            if(row == 0){
                throw new FatturaPAException("Nessun record aggiornamento per la tabella FTP_REPORT_ST con id = [" + idFtpReportSt + "]");
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

    public void updateStatusFISupporto(BigInteger idFtpReportSt, Boolean errore, String descErrore) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            int row = ftpReportStDao.updateStatusFISupporto(idFtpReportSt, errore, descErrore, entityManager);

            if(row == 0){
                throw new FatturaPAException("Nessun record aggiornamento per la tabella FTP_REPORT_ST con id = [" + idFtpReportSt + "]");
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

    public void updateStatusFIEsito(BigInteger idFtpReportSt, Boolean errore, String descErrore) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            int row = ftpReportStDao.updateStatusFIEsito(idFtpReportSt, errore, descErrore, entityManager);

            if(row == 0){
                throw new FatturaPAException("Nessun record aggiornamento per la tabella FTP_REPORT_ST con id = [" + idFtpReportSt + "]");
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

    public LinkedList<FtpReportStEntity> getRecordForReportStFO(@Header("ente") String codiceEnte, @Header("dataReport") String dataReport) throws FatturaPAException {

        EntityManager entityManager = null;
        LinkedList<FtpReportStEntity> ftpReportStEntities = new LinkedList<>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            if(StringUtils.isEmpty(dataReport)){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dataReport = sdf.format(new Date());
            }

            ftpReportStEntities.addAll(ftpReportStDao.getRecordForReportStFO(codiceEnte, dataReport, entityManager));

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

        return ftpReportStEntities;
    }

    public LinkedList<FtpReportStEntity> getRecordForReportStFI(@Header("ente") String codiceEnte, @Header("dataReport") String dataReport) throws FatturaPAException {

        EntityManager entityManager = null;
        LinkedList<FtpReportStEntity> ftpReportStEntities = new LinkedList<>();

        try {

            entityManager = entityManagerFactory.createEntityManager();

            if(StringUtils.isEmpty(dataReport)){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dataReport = sdf.format(new Date());
            }

            ftpReportStEntities.addAll(ftpReportStDao.getRecordForReportStFI(codiceEnte, dataReport, entityManager));

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

        return ftpReportStEntities;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public FtpReportStDao getFtpReportStDao() {
        return ftpReportStDao;
    }

    public void setFtpReportStDao(FtpReportStDao ftpReportStDao) {
        this.ftpReportStDao = ftpReportStDao;
    }
}