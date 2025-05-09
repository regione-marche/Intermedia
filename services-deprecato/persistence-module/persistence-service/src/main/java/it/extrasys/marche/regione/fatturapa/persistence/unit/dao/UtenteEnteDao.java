package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.UtenteEnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;


public class UtenteEnteDao extends GenericDao<UtenteEnteEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(UtenteEnteDao.class);

    public List<UtenteEnteEntity> getUtenteEnteFromUtente(UtenteEntity utenteEntity, EntityManager entityManager) {

        LOG.info("*************** getUtenteEnteFromUtente ***************");

        TypedQuery<UtenteEnteEntity> query = entityManager.createQuery("SELECT ue FROM UtenteEnteEntity ue WHERE ue.utente = :utente", UtenteEnteEntity.class);
        query.setParameter("utente", utenteEntity);
        List<UtenteEnteEntity> list = query.getResultList();

        return list;
    }

    public List<UtenteEnteEntity> getUtenteEnteFromEnte(EnteEntity enteEntity, EntityManager entityManager) {

        LOG.info("*************** getUtenteEnteFromEnte ***************");

        TypedQuery<UtenteEnteEntity> query = entityManager.createQuery("SELECT ue FROM UtenteEnteEntity ue WHERE ue.ente = :ente", UtenteEnteEntity.class);
        query.setParameter("ente", enteEntity);
        List<UtenteEnteEntity> list = query.getResultList();

        return list;
    }

    public UtenteEnteEntity getUtenteEnte(UtenteEnteEntity utenteEnteEntity, EntityManager entityManager) {

        LOG.info("*************** getUtenteEnte ***************");

        TypedQuery<UtenteEnteEntity> query = entityManager.createQuery("SELECT ue FROM UtenteEnteEntity ue WHERE ue.ente =: ente AND ue.utente =: utente", UtenteEnteEntity.class);
        query.setParameter("ente", utenteEnteEntity.getEnte());
        query.setParameter("utente", utenteEnteEntity.getUtente());

        try{
            return query.getSingleResult();
        }
        catch (NoResultException e){
            return null;
        }

    }

    public boolean checkAuth(UtenteEnteEntity utenteEnteEntity, EntityManager entityManager){

        LOG.info("*************** checkAuth ***************");

        String admin = "admin";
        String superadmin = "superadmin";
        if(admin.equalsIgnoreCase(utenteEnteEntity.getUtente().getRuolo()) || superadmin.equalsIgnoreCase(utenteEnteEntity.getUtente().getRuolo())){
            return true;
        }
        else {
            UtenteEnteEntity utenteEnteEntity1 = getUtenteEnte(utenteEnteEntity, entityManager);
            return utenteEnteEntity1 != null;
        }
    }

    public boolean checkAuth(UtenteEnteEntity utenteEnteEntity, boolean justAdmin, EntityManager entityManager){

        LOG.info("*************** checkAuth ***************");

        String admin = "admin";
        String superadmin = "superadmin";

        if (admin.equalsIgnoreCase(utenteEnteEntity.getUtente().getRuolo())) {
            return true;
        }
        else if (superadmin.equalsIgnoreCase(utenteEnteEntity.getUtente().getRuolo())){
            return true;
        }
        else{
            return false;
        }
    }

    public void deleteCollegamento(UtenteEnteEntity utenteEnteEntity, EntityManager entityManager){

        LOG.info("*************** deleteCollegamento ***************");
        TypedQuery<UtenteEnteEntity> query = entityManager.createQuery("DELETE ue FROM UtenteEnteEntity ue WHERE ue.idUtenteEnte =: id", UtenteEnteEntity.class);
        query.setParameter("id", utenteEnteEntity.getIdUtenteEnte());

        query.executeUpdate();

    }


}