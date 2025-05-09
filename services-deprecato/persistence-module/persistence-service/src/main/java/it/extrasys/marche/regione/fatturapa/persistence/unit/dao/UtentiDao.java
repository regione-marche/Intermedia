package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

public class UtentiDao extends GenericDao<UtenteEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(UtentiDao.class);

    public UtenteEntity getUtenteById(String idUtente, EntityManager entityManager) throws FatturaPAUtenteNonTrovatoException {

        LOG.info("*************** getUtenteById ***************");

        TypedQuery<UtenteEntity> query = entityManager.createQuery("SELECT utente FROM UtenteEntity utente WHERE utente.idUtente = :idUtente", UtenteEntity.class);
        BigInteger idUtente2 = new BigInteger(idUtente);
        query.setParameter("idUtente", idUtente2);

        try {
            UtenteEntity utenteEntity = query.getSingleResult();
            return utenteEntity;
        }catch (NoResultException e){
            throw new FatturaPAUtenteNonTrovatoException("Utente "+idUtente+" non trovato");
        }
    }

    public UtenteEntity getUtenteByUsername(String username, EntityManager entityManager) throws FatturaPAUtenteNonTrovatoException {

        LOG.info("*************** getUtenteByUsername ***************");

        TypedQuery<UtenteEntity> query = entityManager.createQuery("SELECT utente FROM UtenteEntity utente WHERE utente.username = :username", UtenteEntity.class);
        query.setParameter("username", username);

        try {
            UtenteEntity utenteEntity = query.getSingleResult();
            return utenteEntity;
        }catch (NoResultException e){
            throw new FatturaPAUtenteNonTrovatoException("Utente "+username+" non trovato");
        }
    }

    public List<UtenteEntity> getAllUsers(EntityManager entityManager) throws FatturaPAUtenteNonTrovatoException {

        LOG.info("*************** getAllUsers ***************");

        String superadmin = "superadmin";

        TypedQuery<UtenteEntity> query = entityManager.createQuery("SELECT utente FROM UtenteEntity utente WHERE utente.ruolo <> :superadmin", UtenteEntity.class);
        query.setParameter("superadmin", superadmin);
        try {
            List<UtenteEntity> utenteEntityList = query.getResultList();
            if(utenteEntityList.size() == 0){
                throw new NoResultException();
            }
            return utenteEntityList;
        }catch (NoResultException e){
            throw new FatturaPAUtenteNonTrovatoException("Nessun utente trovato");
        }
    }


    public void createUtente(UtenteEntity utenteEntity, EntityManager entityManager){

        LOG.info("*************** createUtente ***************");
        entityManager.persist(utenteEntity);
    }

}
