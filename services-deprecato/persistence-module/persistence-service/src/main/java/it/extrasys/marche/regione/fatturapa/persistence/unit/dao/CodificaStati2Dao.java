package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStati2Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 20/02/15.
 */
public class CodificaStati2Dao extends GenericDao<CodificaStati2Entity,String> {

    private static final Logger LOG = LoggerFactory.getLogger(CodificaStati2Dao.class);


    public CodificaStati2Entity getByIdCodStato(String idCodStato, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {
        LOG.info("********** getByIdCodStato **********");
        TypedQuery<CodificaStati2Entity> query = entityManager.createQuery("SELECT cs FROM CodificaStati2Entity cs WHERE cs.codStato = :codStato",CodificaStati2Entity.class);
        query.setParameter("codStato", idCodStato);
        try {
            CodificaStati2Entity cs = query.getSingleResult();
            return cs;
        } catch (NoResultException e){
            throw new NoResultException("Nessuno stato per codStato: "+idCodStato);
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException(e.getMessage());
        }

    }
}
