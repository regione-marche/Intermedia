package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACanaleNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.TipoCanaleEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Created by agosteeno on 14/03/15.
 */
public class TipoCanaleDao extends GenericDao<TipoCanaleEntity,TipoCanaleEntity.TIPO_CANALE>{

    public TipoCanaleEntity read(TipoCanaleEntity.TIPO_CANALE id, EntityManager entityManager) {
        return entityManager.find(entityClass, id.getValue());
    }

    public TipoCanaleEntity getTipoCanaleById(String tipoCanale, EntityManager entityManager) throws FatturaPACanaleNonTrovatoException {
        TypedQuery<TipoCanaleEntity> querySelectTipoCanale = entityManager.createQuery("SELECT tipoCanale FROM TipoCanaleEntity tipoCanale WHERE tipoCanale.codTipoCanale = :tipoCanale", TipoCanaleEntity.class);
        querySelectTipoCanale.setParameter("tipoCanale", tipoCanale);
        try {
            TipoCanaleEntity tipoCanaleEntity = querySelectTipoCanale.getSingleResult();
            return tipoCanaleEntity;
        }catch(NoResultException e){
            throw new FatturaPACanaleNonTrovatoException("Canale di tipo "+tipoCanale+" non trovato");
        }
    }
}
