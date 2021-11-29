package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp.FtpCodificaStatiEsitoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp.FtpCodificaStatiSupportoEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp.FtpReportStEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FtpReportStDao extends GenericDao<FtpReportStEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(FtpReportStDao.class);

    public int updateStatusFOSupporto(BigInteger idFtpReportSt, Boolean errore, String descErroreSupporto, EntityManager entityManager){

        String queryString = "UPDATE FTP_REPORT_ST set STATO_SUPPORTO = ?, DESC_ERRORE_SUPPORTO = ? WHERE id = ? ";

        Query query = entityManager.createNativeQuery(queryString);

        if(errore){
            query.setParameter(1, FtpCodificaStatiSupportoEntity.FTP_CODICI_STATO_SUPPORTO.ELABORAZIONE_ERRORE.getValue());
            query.setParameter(2, descErroreSupporto);
        }else{
            query.setParameter(1, FtpCodificaStatiSupportoEntity.FTP_CODICI_STATO_SUPPORTO.ELABORAZIONE_COMPLETATA.getValue());
            query.setParameter(2, descErroreSupporto);
        }
        query.setParameter(3, idFtpReportSt);

        int row = query.executeUpdate();

        return row;
    }

    public int updateStatusFISupporto(BigInteger idFtpReportSt, Boolean errore, String descErroreSupporto, EntityManager entityManager){

        String queryString = "UPDATE FTP_REPORT_ST set STATO_SUPPORTO = ?, DESC_ERRORE_SUPPORTO = ? WHERE id = ? ";

        Query query = entityManager.createNativeQuery(queryString);

        if(errore){
            query.setParameter(1, FtpCodificaStatiSupportoEntity.FTP_CODICI_STATO_SUPPORTO.ELABORAZIONE_ERRORE.getValue());
            query.setParameter(2, descErroreSupporto);
        }else{
            query.setParameter(1, FtpCodificaStatiSupportoEntity.FTP_CODICI_STATO_SUPPORTO.ELABORAZIONE_COMPLETATA.getValue());
            query.setParameter(2, descErroreSupporto);
        }
        query.setParameter(3, idFtpReportSt);

        int row = query.executeUpdate();

        return row;
    }

    public int updateStatusFIEsito(BigInteger idFtpReportSt, Boolean errore, String descErroreEsito, EntityManager entityManager){

        String queryString = "UPDATE FTP_REPORT_ST set STATO_ESITO = ?, DESC_ERRORE_ESITO = ?, TIMESTAMP_INVIO = ?  WHERE id = ? ";

        Query query = entityManager.createNativeQuery(queryString);

        if(errore){
            query.setParameter(1, FtpCodificaStatiEsitoEntity.FTP_CODICI_STATO_ESITO.EO_ERRORE_INVIO.getValue());
            query.setParameter(2, descErroreEsito);
            query.setParameter(3, null);
        }else{
            query.setParameter(1, FtpCodificaStatiEsitoEntity.FTP_CODICI_STATO_ESITO.EO_INVIATO.getValue());
            query.setParameter(2, descErroreEsito);
            query.setParameter(3, new Timestamp(System.currentTimeMillis()));
        }

        query.setParameter(4, idFtpReportSt);

        int row = query.executeUpdate();

        return row;
    }

    public List<FtpReportStEntity> getRecordForReportStFO(String codiceEnte, String data, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAException {

        List<FtpReportStEntity> ftpReportStEntities = new LinkedList<>();

        try {

            Timestamp[] timestamps = getEstremiTimeStamp(data);

            String queryString =
                    "SELECT ftpReportSt " +
                    "FROM FtpReportStEntity ftpReportSt " +
                    "WHERE ftpReportSt.codiceEnte = :codiceEnte AND ftpReportSt.ftpOut = true AND ftpReportSt.timestampInvio >= :timestampInvioInf AND ftpReportSt.timestampInvio <= :timestampInvioSup " +
                    "ORDER BY ftpReportSt.timestampInvio";

            TypedQuery<FtpReportStEntity> query = entityManager.createQuery(queryString, FtpReportStEntity.class);
            query.setParameter("codiceEnte", codiceEnte);
            query.setParameter("timestampInvioInf", timestamps[0]);
            query.setParameter("timestampInvioSup", timestamps[1]);

            ftpReportStEntities = query.getResultList();

        } catch (NoResultException e) {
            return ftpReportStEntities;
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage());
        }

        return ftpReportStEntities;
    }

    public List<FtpReportStEntity> getRecordForReportStFI(String codiceEnte, String data, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAException {

        List<FtpReportStEntity> ftpReportStEntities = new LinkedList<>();

        try {

            Timestamp[] timestamps = getEstremiTimeStamp(data);

            String queryString =
                    "SELECT ftpReportSt " +
                    "FROM FtpReportStEntity ftpReportSt " +
                    "WHERE ftpReportSt.codiceEnte = :codiceEnte AND ftpReportSt.ftpIn = true AND ftpReportSt.timestampRicezione >= :timestampRicezioneInf AND ftpReportSt.timestampRicezione <= :timestampRicezioneSup " +
                    "ORDER BY ftpReportSt.timestampRicezione";

            TypedQuery<FtpReportStEntity> query = entityManager.createQuery(queryString, FtpReportStEntity.class);
            query.setParameter("codiceEnte", codiceEnte);
            query.setParameter("timestampRicezioneInf", timestamps[0]);
            query.setParameter("timestampRicezioneSup", timestamps[1]);

            ftpReportStEntities = query.getResultList();

        } catch (NoResultException e) {
            return ftpReportStEntities;
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage());
        }

        return ftpReportStEntities;
    }

    private Timestamp[] getEstremiTimeStamp(String data) throws ParseException {

        Timestamp[] timestamps = new Timestamp[2];

        data = getDataMenoUnGiorno(data);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date dateInf = dateFormat.parse(data + " 00:00:00");
        long timeInf = dateInf.getTime();
        timestamps[0] = new Timestamp(timeInf);

        Date dateSup = dateFormat.parse(data + " 23:59:59");
        long timeSup = dateSup.getTime();
        timestamps[1] = new Timestamp(timeSup);

        return timestamps;
    }

    private String getDataMenoUnGiorno(String data) throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFormat.parse(data));
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        return dateFormat.format(calendar.getTime());
    }
}