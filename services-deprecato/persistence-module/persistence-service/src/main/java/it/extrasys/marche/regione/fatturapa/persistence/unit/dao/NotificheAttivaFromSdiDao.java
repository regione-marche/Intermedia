package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.NotificheAttivaFromSdiEntity;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 21/03/15.
 */
public class NotificheAttivaFromSdiDao extends GenericDao<NotificheAttivaFromSdiEntity, BigInteger> {

    public NotificheAttivaFromSdiEntity getNotificaAttivaFromIdentificativoSdi(BigInteger identificativoSdi, String tipoNotificaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {

        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = null;

        try {
            TypedQuery<NotificheAttivaFromSdiEntity> query = entityManager.createQuery(
                    "SELECT notificaAttiva " +
                            "FROM NotificheAttivaFromSdiEntity notificaAttiva " +
                            "WHERE notificaAttiva.identificativoSdi = :identificativoSdi AND notificaAttiva.tipoNotificaAttivaFromSdiEntity.codTipoNotificaFromSdi = :tipoNotifica",
                    NotificheAttivaFromSdiEntity.class);

            query.setParameter("identificativoSdi", identificativoSdi);
            query.setParameter("tipoNotifica", tipoNotificaAttiva);

            query.setMaxResults(1);

            notificheAttivaFromSdiEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Identificativo SDI " + identificativoSdi);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificheAttivaFromSdiEntity;
    }

    public NotificheAttivaFromSdiEntity getNotificaAttivaFromIdSdi(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPaPersistenceException {

        try {
            TypedQuery<NotificheAttivaFromSdiEntity> query = entityManager.createQuery(
                    "SELECT notificaAttiva " +
                            "FROM NotificheAttivaFromSdiEntity notificaAttiva " +
                            "WHERE notificaAttiva.identificativoSdi = :identificativoSdi ORDER BY notificaAttiva.dataRicezioneRispostaSDI DESC",
                    NotificheAttivaFromSdiEntity.class);

            query.setParameter("identificativoSdi", identificativoSdi);

            List<NotificheAttivaFromSdiEntity> list = query.getResultList();

            if(list == null || list.size() == 0){
                throw new NoResultException();
            }

            return list.get(0);

        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Identificativo SDI " + identificativoSdi);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public List<Object[]> getNotificheAttivaByEnte(BigInteger ente, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<Object[]> list = null;

        try {
            String queryString = "SELECT na, fa.idFatturaAttiva " +
                    " FROM NotificheAttivaFromSdiEntity na, FatturaAttivaEntity fa " +
                    " WHERE na.identificativoSdi=fa.identificativoSdi and fa.ente.idEnte = :ente ";

            OpenJPAQuery query = OpenJPAPersistence.cast(entityManager.createQuery(queryString, NotificheAttivaFromSdiEntity.class));

            query.setParameter("ente", ente);
            list = query.getResultList();
        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return list;
    }

    public NotificheAttivaFromSdiEntity getNotificaAttivaFromSdIByIdentificativiSdI(BigInteger identificativoSdI, String tipoNotificaAttiva, EntityManager entityManager) throws FatturaPaPersistenceException {

        NotificheAttivaFromSdiEntity notificheAttivaFromSdiEntity = null;

        try {

            TypedQuery<NotificheAttivaFromSdiEntity> query = entityManager.createQuery(
                    "SELECT nafs " +
                            "FROM NotificheAttivaFromSdiEntity nafs " +
                            "WHERE nafs.identificativoSdi = :identificativoSdI AND nafs.ricevutaComunicazione IS NULL AND nafs.tipoNotificaAttivaFromSdiEntity.codTipoNotificaFromSdi = :tipoNotifica " +
                            "ORDER BY nafs.idNotificaAttivaFromSdi DESC", NotificheAttivaFromSdiEntity.class
            );

            query.setParameter("identificativoSdI", identificativoSdI);
            query.setParameter("tipoNotifica", tipoNotificaAttiva);

            List<NotificheAttivaFromSdiEntity> notificheAttivaFromSdiEntityList = query.getResultList();

            if (notificheAttivaFromSdiEntityList == null || notificheAttivaFromSdiEntityList.isEmpty()) {
                throw new FatturaPaPersistenceException("Nessuna notifica trovata per Identificativo SDI " + identificativoSdI);
            } else {
                notificheAttivaFromSdiEntity = notificheAttivaFromSdiEntityList.get(0);
            }

        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Identificativo SDI " + identificativoSdI);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificheAttivaFromSdiEntity;
    }


    public List<String> getTipoNotificheFromSdIByIdentificativoSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {
        List<String> notificheFromSdiList = null;
        try {
            String queryString = "SELECT DISTINCT id_tipo_notifica_attiva_from_sdi " +
                    " FROM notifiche_attiva_from_sdi WHERE identificativosdi = ?";

            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, identificativoSdI);

            notificheFromSdiList = query.getResultList();
        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
        return notificheFromSdiList;
    }

    public int deleteByIdentificativoSdi(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPaPersistenceException {
        try {
            String queryString = "DELETE FROM notifiche_attiva_from_sdi WHERE identificativosdi = ?";
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, identificativoSdi);
            return query.executeUpdate();
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Nessuna notifica trovata per Id fattura attiva " + identificativoSdi);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }


    public List<Object[]> getNotificheAttiveAfterDate(String interval, EntityManager entityManager) {
        String queryString = "SELECT fa.id_fattura_attiva, fa.identificativo_sdi, s.data, csa.cod_stato, csa.desc_stato, fa.nome_file, e.codice_ufficio, desc_canale " +
                             " FROM fattura_attiva fa, stato_fattura_attiva s, codifica_stati_attiva csa, ente e, tipo_canale tc " +
                             " WHERE fa.id_fattura_attiva=s.id_fattura_attiva and s.id_cod_stato = csa.cod_stato AND e.id_ente=fa.ente " +
                             " AND e.ambiente_cicloattivo = 'PRODUZIONE' AND tc.cod_tipo_canale=e.id_tipo_canale " +
                             " AND s.data > CURRENT_TIMESTAMP - INTERVAL '" + interval + " days' AND fa.fatturazione_test='F'";

        Query query = entityManager.createNativeQuery(queryString);

        List<Object[]> result = query.getResultList();

        return result;
    }

}