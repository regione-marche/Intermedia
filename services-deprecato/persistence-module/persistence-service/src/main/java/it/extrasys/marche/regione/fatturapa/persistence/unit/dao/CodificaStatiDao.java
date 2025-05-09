package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 20/02/15.
 */
public class CodificaStatiDao extends GenericDao<CodificaStatiEntity,CodificaStatiEntity.CODICI_STATO_FATTURA> {

    private static final Logger LOG = LoggerFactory.getLogger(CodificaStatiDao.class);


    public CodificaStatiEntity read(CodificaStatiEntity.CODICI_STATO_FATTURA id, EntityManager entityManager) {
        return entityManager.find(entityClass, id.getValue());
    }

    public CodificaStatiEntity getByIdCodStato(String idCodStato, EntityManager entityManager) throws FatturaPaPersistenceException, NoResultException {
        TypedQuery<CodificaStatiEntity> query = entityManager.createQuery("SELECT cs FROM CodificaStatiEntity cs WHERE cs.codStato = :codStato",CodificaStatiEntity.class);
        query.setParameter("codStato", idCodStato);
        try {
            CodificaStatiEntity cs = query.getSingleResult();
            return cs;
        } catch (NoResultException e){
            throw new NoResultException("Nessuno stato per codStato: "+idCodStato);
        } catch (PersistenceException e){
            throw new FatturaPaPersistenceException(e.getMessage());
        }

    }
}
