package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.MailInToEntiAttivaEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 14/03/15.
 */
public class MailInToEntiAttivaDao extends GenericDao<MailInToEntiAttivaEntity,BigInteger> {

    public List<MailInToEntiAttivaEntity> getEmailInAttivaByEnte(EnteEntity enteEntity, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        List<MailInToEntiAttivaEntity> mailInToEntiAttivaEntityList = null;

        try {
            TypedQuery<MailInToEntiAttivaEntity> query = entityManager.createQuery("SELECT mail FROM MailInToEntiAttivaEntity mail WHERE mail.enteEntity = :enteEntity", MailInToEntiAttivaEntity.class);
            query.setParameter("enteEntity", enteEntity);

            mailInToEntiAttivaEntityList = query.getResultList();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException();
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException();
        }

        return mailInToEntiAttivaEntityList;
    }

    public EnteEntity getEnteByIndirizzoEmail(String indirizzoEmail, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        EnteEntity enteEntity = null;
        MailInToEntiAttivaEntity mailInToEntiAttivaEntity = null;

        String indirizzoEmailPulito = indirizzoEmail.trim().toLowerCase();

        try {
            TypedQuery<MailInToEntiAttivaEntity> query = entityManager.createQuery("SELECT enteEntity FROM MailInToEntiAttivaEntity enteEntity WHERE enteEntity.email = :email", MailInToEntiAttivaEntity.class);
            query.setParameter("email", indirizzoEmail);

            mailInToEntiAttivaEntity = query.getSingleResult();

            enteEntity = mailInToEntiAttivaEntity.getEnteEntity();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException();
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException();
        }

        return enteEntity;
    }
}
