package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 06/03/15.
 */
public class NotificaDecorrenzaTerminiDao extends GenericDao<NotificaDecorrenzaTerminiEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificaDecorrenzaTerminiDao.class);

    public NotificaDecorrenzaTerminiEntity getNotificaDecorrenzaTerminiByIdentificativiSdI(BigInteger identificativoSdi, EntityManager entityManager) {


        TypedQuery<NotificaDecorrenzaTerminiEntity> query = entityManager.createQuery("SELECT ndt " +
                                                                                         "FROM NotificaDecorrenzaTerminiEntity ndt " +
                                                                                         "WHERE ndt.identificativoSdi = :identificativoSdi " +
                                                                                         "ORDER BY ndt.idNotificaDecorrenzaTermini DESC", NotificaDecorrenzaTerminiEntity.class);
        query.setParameter("identificativoSdi", identificativoSdi);

        List<NotificaDecorrenzaTerminiEntity> notificaDecorrenzaTerminiEntityList = query.getResultList();

        //return query.getSingleResult();
        return notificaDecorrenzaTerminiEntityList.get(0);
    }

    /*
        Prende tutte le decorrenze termini, per un dato ente, che devono essere inviate tramite ftp
    */
    public List<NotificaDecorrenzaTerminiEntity> getNotificaDecorrenzaTerminiFtpByEnte(String codDest, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        List<NotificaDecorrenzaTerminiEntity> notificaDecorrenzaTermini = null;

        try {
            String queryString = "SELECT ndt FROM NotificaDecorrenzaTerminiEntity ndt WHERE ndt.identificativoSdi IN ( " +
                    " SELECT DISTINCT dati.identificativoSdI FROM StatoFatturaEntity dfe JOIN dfe.datiFattura dati " +
                    " WHERE dfe.datiFattura.codiceDestinatario = :codDest " +
                    " AND dfe.stato.codStato = '019' AND dfe.data = ( " +
                    " SELECT MAX(dfe2.data) FROM StatoFatturaEntity dfe2 JOIN dfe2.datiFattura dati2 " +
                    " WHERE dati2.identificativoSdI = dati.identificativoSdI AND dfe2.datiFattura.codiceDestinatario = :codDest " +
                    " ) ) ";

            TypedQuery<NotificaDecorrenzaTerminiEntity> query = entityManager.createQuery(queryString, NotificaDecorrenzaTerminiEntity.class);
            query.setParameter("codDest", codDest);
            notificaDecorrenzaTermini = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaDecorrenzaTermini;
    }

    public List<NotificaDecorrenzaTerminiEntity> getNotificaDecorrenzaTerminiFtpByEnteG1G4(String codDest, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<NotificaDecorrenzaTerminiEntity> notificaDecorrenzaTerminiList = null;

        try {
            String queryString =
                "SELECT t2.* " +
                "FROM NOTIFICA_DECORRENZA_TERMINI t2 " +
                "WHERE (" +
                            "t2.IDENTIFICATIVO_SDI IN (" +
                                "SELECT DISTINCT t1.IDENTIFICATIVO_SDI " +
                                "FROM STATO_FATTURA t0, DATI_FATTURA t1 " +
                                "WHERE (t0.id_dati_fattura = t1.id_dati_fattura AND t1.CODICE_DESTINATARIO = ? AND t0.ID_COD_STATO = '019' AND t0.DATA = ( " +
                                    "SELECT MAX(t4.DATA) " +
                                    "FROM STATO_FATTURA t4, DATI_FATTURA t5 " +
                                    "WHERE (t4.id_dati_fattura = t5.id_dati_fattura AND " +
                                    "t5.IDENTIFICATIVO_SDI = t1.IDENTIFICATIVO_SDI AND t5.CODICE_DESTINATARIO = ?)" +
                                "))" +
                            ")" +
                        ")";

            LOG.info("@@@@@ Notifiche DT Codice Dest: "+codDest+" @@@@@");

            Query query = entityManager.createNativeQuery(queryString, NotificaDecorrenzaTerminiEntity.class);
            query.setParameter(1, codDest);
            query.setParameter(2, codDest);

            notificaDecorrenzaTerminiList = (List<NotificaDecorrenzaTerminiEntity>)query.getResultList();

            LOG.info("@@@@@ Notifiche DT Numero righe trovate: "+notificaDecorrenzaTerminiList.size()+" @@@@@");

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaDecorrenzaTerminiList;
    }
}