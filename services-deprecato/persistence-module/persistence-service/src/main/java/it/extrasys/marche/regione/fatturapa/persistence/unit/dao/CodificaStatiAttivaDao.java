package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class CodificaStatiAttivaDao extends GenericDao<CodificaStatiAttivaEntity,CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA> {

    private static final Logger LOG = LoggerFactory.getLogger(CodificaStatiAttivaDao.class);


    public CodificaStatiAttivaEntity read(CodificaStatiAttivaEntity.CODICI_STATO_FATTURA_ATTIVA id, EntityManager entityManager) {
        return entityManager.find(entityClass, id.getValue());
    }
}
