package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 06/03/15.
 */
public class NotificaDecorrenzaTerminiDao extends GenericDao<NotificaDecorrenzaTerminiEntity, BigInteger> {

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

}
