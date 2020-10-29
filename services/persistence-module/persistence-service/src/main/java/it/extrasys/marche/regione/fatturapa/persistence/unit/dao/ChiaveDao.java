package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.ChiaveEntity;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;


public class ChiaveDao extends GenericDao<ChiaveEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(ChiaveDao.class);

    public String getChiave(EntityManager entityManager) {

        LOG.info("*************** getChiave ***************");

        TypedQuery<ChiaveEntity> query = entityManager.createQuery("SELECT c FROM ChiaveEntity c", ChiaveEntity.class);
        ChiaveEntity chiaveEntity = query.getSingleResult();

        return new String(Base64.decodeBase64(chiaveEntity.getChiave()));
    }



}