package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteServizioEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;

public class UtentiServizioDao extends GenericDao<UtenteServizioEntity, BigInteger> {

    public UtenteServizioEntity getUtenteByUsername(String username, EntityManager entityManager) throws FatturaPAUtenteNonTrovatoException {

        TypedQuery<UtenteServizioEntity> query = entityManager.createQuery("SELECT utente FROM UtenteServizioEntity utente WHERE utente.username = :username", UtenteServizioEntity.class);
        query.setParameter("username", username);

        try {
            UtenteServizioEntity utenteServizioEntity = query.getSingleResult();
            return utenteServizioEntity;
        }catch (NoResultException e){
            throw new FatturaPAUtenteNonTrovatoException("UtenteServizio "+username+" non trovato");
        }
    }

}
