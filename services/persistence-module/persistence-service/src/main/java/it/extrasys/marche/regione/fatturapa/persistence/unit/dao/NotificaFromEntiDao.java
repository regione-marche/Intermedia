package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 20/02/15.
 */
public class NotificaFromEntiDao extends GenericDao<NotificaFromEntiEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificaFromEntiDao.class);

    public NotificaFromEntiEntity getNotificaFromEntiByIdComunicazione(String idComunicazione, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        NotificaFromEntiEntity notificaFromEntiEntity = null;

        try {
            TypedQuery<NotificaFromEntiEntity> query = entityManager.createQuery("SELECT notificaFromEnteEntity FROM NotificaFromEntiEntity notificaFromEnteEntity WHERE notificaFromEnteEntity.idComunicazione = :idComunicazione", NotificaFromEntiEntity.class);
            query.setParameter("idComunicazione", idComunicazione);

            notificaFromEntiEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("NotificaFromEnti non trovata for idComunicazione " + idComunicazione);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaFromEntiEntity;

    }

    public NotificaFromEntiEntity getNotificaEsitoCommittenteByIdentificativiSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        NotificaFromEntiEntity notificaFromEntiEntity = null;

        try {
            TypedQuery<NotificaFromEntiEntity> query = entityManager.createQuery("SELECT notificaFromEnteEntity FROM NotificaFromEntiEntity notificaFromEnteEntity WHERE notificaFromEnteEntity.identificativoSdI = :identificativoSdI ORDER BY notificaFromEnteEntity.idNotifica DESC", NotificaFromEntiEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);

            //notificaFromEntiEntity = query.getSingleResult();
            List<NotificaFromEntiEntity> notificaFromEntiEntityList = query.getResultList();

            if(notificaFromEntiEntityList == null || notificaFromEntiEntityList.isEmpty()){
                throw new FatturaPAFatturaNonTrovataException("Nessuna notifica trovata per identificativoSdI = " + identificativoSdI);
            }else{
                notificaFromEntiEntity = notificaFromEntiEntityList.get(0);
            }

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("NotificaFromEnti non trovata per identificativoSdI " + identificativoSdI);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaFromEntiEntity;
    }


    public List<NotificaFromSdiEntity> getScartoEsitoFtpByEnte(String codDest, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {
        List<NotificaFromSdiEntity> notificaFromSdiEntityList = null;
        try {
            String queryString = "SELECT nfs FROM NotificaFromSdiEntity nfs WHERE nfs.esito='ES00' AND nfs.contenutoFile IS NOT NULL AND nfs.identificativoSdI IN ( " +
                    " SELECT DISTINCT dati.identificativoSdI FROM StatoFatturaEntity dfe JOIN dfe.datiFattura dati " +
                    " WHERE dfe.datiFattura.codiceDestinatario = :codDest " +
                    " AND dfe.stato.codStato = '011' AND dfe.data = ( " +
                    " SELECT MAX(dfe2.data) FROM StatoFatturaEntity dfe2 JOIN dfe2.datiFattura dati2 " +
                    " WHERE dati2.identificativoSdI = dati.identificativoSdI AND dfe2.datiFattura.codiceDestinatario = :codDest " +
                    " ) )";

            TypedQuery<NotificaFromSdiEntity> query = entityManager.createQuery(queryString, NotificaFromSdiEntity.class);
            query.setParameter("codDest", codDest);
            notificaFromSdiEntityList = query.getResultList();


        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaFromSdiEntityList;
    }

    public List<NotificaFromSdiEntity> getScartoEsitoFtpByEnteG1G4(String codDest, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException, FatturaPaPersistenceException {

        List<NotificaFromSdiEntity> notificaFromSdiEntityList = null;

        try {

            String queryString =
                    "SELECT nfs.* " +
                    "FROM notifiche_from_sdi nfs " +
                    "WHERE nfs.esito='ES00' " +
                    "AND nfs.contenuto_file IS NOT NULL " +
                    "AND nfs.identificativo_sdi IS NOT NULL " +
                    "AND nfs.identificativo_sdi IN ( " +
                        "SELECT DISTINCT dati.identificativo_sdi " +
                        "FROM stato_fattura dfe, dati_fattura dati " +
                        "where dfe.id_dati_fattura = dati.id_dati_fattura " +
                        "AND dati.codice_destinatario = ? " +
                        "AND dfe.id_cod_stato = '011' AND dfe.data = ( " +
                            "SELECT MAX(dfe2.data) " +
                            "FROM stato_fattura dfe2, dati_fattura dati2 " +
                            "WHERE dfe2.id_dati_fattura = dati2.id_dati_fattura " +
                            "AND dati2.identificativo_sdi = dati.identificativo_sdi " +
                            "AND dati2.codice_destinatario = ? " +
                        ")" +
                    ")";

            LOG.info("@@@@@ Codice Dest: "+codDest+" @@@@@");

            Query query = entityManager.createNativeQuery(queryString, NotificaFromSdiEntity.class);
            query.setParameter(1, codDest);
            query.setParameter(2, codDest);

            //LOG.info("@@@@@ Query:\n\n" + query.toString() + "\n\n@@@@@");

            notificaFromSdiEntityList = (List<NotificaFromSdiEntity>)query.getResultList();

            LOG.info("@@@@@ Numero righe trovate: "+notificaFromSdiEntityList.size()+" @@@@@");

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaFromSdiEntityList;
    }
}