package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class CodificaFlagWarningDao extends GenericDao<CodificaFlagWarningEntity, CodificaFlagWarningEntity.CODICI_FLAG_WARNING> {


    private static final Logger LOG = LoggerFactory.getLogger(it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiDao.class);


    public CodificaFlagWarningEntity read(CodificaFlagWarningEntity.CODICI_FLAG_WARNING id, EntityManager entityManager) {
        return entityManager.find(entityClass, id.getValue());
    }
}
