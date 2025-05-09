package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStati2Dao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStati2Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CodificaStati2Manager {

    private static final Logger LOG = LoggerFactory.getLogger(CodificaStati2Manager.class);

    private EntityManagerFactory entityManagerFactory;
    private CodificaStati2Dao codificaStati2Dao;

    public CodificaStati2Entity getByIdCodStato(String codStato) throws FatturaPAException {

        LOG.info("********** getByIdCodStato **********");

        EntityManager entityManager = null;
        CodificaStati2Entity ret = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            ret = codificaStati2Dao.getByIdCodStato(codStato, entityManager);
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        }
        return ret;
    }


    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public CodificaStati2Dao getCodificaStati2Dao() {
        return codificaStati2Dao;
    }

    public void setCodificaStati2Dao(CodificaStati2Dao codificaStati2Dao) {
        this.codificaStati2Dao = codificaStati2Dao;
    }
}
