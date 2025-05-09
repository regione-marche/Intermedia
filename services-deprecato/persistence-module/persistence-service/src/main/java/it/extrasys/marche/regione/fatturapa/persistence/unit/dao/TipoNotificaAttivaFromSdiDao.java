package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;

import javax.persistence.EntityManager;

/**
 * Created by agosteeno on 21/03/15.
 */
public class TipoNotificaAttivaFromSdiDao extends GenericDao<TipoNotificaAttivaFromSdiEntity, TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI> {

    public TipoNotificaAttivaFromSdiEntity read(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI id, EntityManager entityManager) {
        return entityManager.find(entityClass, id.getValue());
    }

}
