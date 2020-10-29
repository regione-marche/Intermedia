package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaTestCicloNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.TestCicloPassivoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;


public class TestCicloPassivoDao extends GenericDao<TestCicloPassivoEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(TestCicloPassivoDao.class);

    public List<TestCicloPassivoEntity> getTestCicloPassivoByEnte(EnteEntity enteEntity, EntityManager entityManager) throws FatturaPaTestCicloNonTrovatoException {

        LOG.info("*************** getTestCicloPassivoByEnte ***************");

        TypedQuery<TestCicloPassivoEntity> query = entityManager.createQuery("SELECT t FROM TestCicloPassivoEntity t WHERE t.ente = :ente ORDER BY t.identificativoSdi DESC", TestCicloPassivoEntity.class);
        query.setParameter("ente", enteEntity);

        List<TestCicloPassivoEntity> retList = query.getResultList();

        if (retList.size() == 0) {
            throw new FatturaPaTestCicloNonTrovatoException();
        }

        return retList;
    }

    public int deleteBeforeDate(Timestamp date, EntityManager entityManager) throws FatturaPaPersistenceException {
        String queryString = "DELETE FROM TestCicloPassivoEntity  WHERE dataTest < :date";

        try {
            Query query = entityManager.createQuery(queryString);

            int i = query.executeUpdate();

            return i;
        } catch (NoResultException e) {
            throw new FatturaPaPersistenceException("Errore Ripulitura TestCicloPassivo");
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

}