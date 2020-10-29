package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 03/03/15.
 */
public class NotificaFromSdiDao extends GenericDao<NotificaFromSdiEntity, BigInteger> {

    /*
    public NotificaFromSdiEntity getNotificaScartoByIdentificativoSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {
            TypedQuery<NotificaFromSdiEntity> query = entityManager.createQuery( "SELECT nfs " +
                                                                                    "FROM NotificaFromSdiEntity nfs " +
                                                                                    "WHERE nfs.identificativoSdI = :identificativoSdI AND nfs.nomeFileScarto LIKE :fileName AND nfs.numeroProtocolloNotifica IS NULL", NotificaFromSdiEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);
            query.setParameter("fileName", "%_SE_%");

            notificaFromSdiEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaFromSdiEntity;
    }

    public NotificaFromSdiEntity getNotificaFromSdIByIdentificativiSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {

            TypedQuery<NotificaFromSdiEntity> query = entityManager.createQuery( "SELECT nfs " +
                                                                                    "FROM NotificaFromSdiEntity nfs " +
                                                                                    "WHERE nfs.identificativoSdI = :identificativoSdI AND nfs.nomeFileScarto IS NULL AND nfs.numeroProtocolloNotifica IS NULL", NotificaFromSdiEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);

            notificaFromSdiEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("NotificaFromSdiEntity non trovata per identificativoSdI " + identificativoSdI);
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException();
        }

        return notificaFromSdiEntity;
    }
    */

    public NotificaFromSdiEntity getNotificaScartoByIdentificativoSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {
            TypedQuery<NotificaFromSdiEntity> query = entityManager.createQuery( "SELECT nfs " +
                    "FROM NotificaFromSdiEntity nfs " +
                    "WHERE nfs.identificativoSdI = :identificativoSdI AND nfs.nomeFileScarto LIKE :fileName AND nfs.numeroProtocolloNotifica IS NULL " +
                    "ORDER BY nfs.idNotificaFromSdi DESC", NotificaFromSdiEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);
            query.setParameter("fileName", "%_SE_%");

            List<NotificaFromSdiEntity> notificaFromSdiEntityList = query.getResultList();

            if(notificaFromSdiEntityList == null || notificaFromSdiEntityList.isEmpty()){
                throw new FatturaPAFatturaNonTrovataException("Nessuna notifica trovata per identificativoSdI = " + identificativoSdI);
            }else{
                notificaFromSdiEntity = notificaFromSdiEntityList.get(0);
            }

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return notificaFromSdiEntity;
    }

    public NotificaFromSdiEntity getNotificaFromSdIByIdentificativiSdI(BigInteger identificativoSdI, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        NotificaFromSdiEntity notificaFromSdiEntity = null;

        try {

            TypedQuery<NotificaFromSdiEntity> query = entityManager.createQuery( "SELECT nfs " +
                    "FROM NotificaFromSdiEntity nfs " +
                    "WHERE nfs.identificativoSdI = :identificativoSdI AND nfs.nomeFileScarto IS NULL AND nfs.numeroProtocolloNotifica IS NULL " +
                    "ORDER BY nfs.idNotificaFromSdi DESC", NotificaFromSdiEntity.class);
            query.setParameter("identificativoSdI", identificativoSdI);

            List<NotificaFromSdiEntity> notificaFromSdiEntityList = query.getResultList();

            if(notificaFromSdiEntityList == null || notificaFromSdiEntityList.isEmpty()){
                throw new FatturaPAFatturaNonTrovataException("Nessuna notifica trovata per identificativoSdI = " + identificativoSdI);
            }else{
                notificaFromSdiEntity = notificaFromSdiEntityList.get(0);
            }

        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException("NotificaFromSdiEntity non trovata per identificativoSdI " + identificativoSdI);
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException();
        }

        return notificaFromSdiEntity;
    }
}
