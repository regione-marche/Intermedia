package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.api.rest.model.TipoCanaleCaList;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPACanaleNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CanaleCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EndpointCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;


public class CanaleCaDao extends GenericDao<CanaleCaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(CanaleCaDao.class);

    public CanaleCaEntity getCanaleCaById(String idCanaleCa, EntityManager entityManager) throws FatturaPACanaleNonTrovatoException {

        LOG.info("*************** getCanaleCaById ***************");

        TypedQuery<CanaleCaEntity> querySelectTipoCanale = entityManager.createQuery("SELECT canaleCa FROM CanaleCaEntity canaleCa WHERE canaleCa.codCanale = :idCanaleCa", CanaleCaEntity.class);
        querySelectTipoCanale.setParameter("idCanaleCa", idCanaleCa);
        try {
            CanaleCaEntity CanaleCaEntity = querySelectTipoCanale.getSingleResult();
            return CanaleCaEntity;
        }catch(NoResultException e){
            throw new FatturaPACanaleNonTrovatoException("CanaleCa di tipo "+idCanaleCa+" non trovato");
        }
    }

    public TipoCanaleCaList getAllTipoCanaleCa(EntityManager entityManager) throws FatturaPACanaleNonTrovatoException {

        LOG.info("*************** getAllTipoCanaleCa ***************");

        TypedQuery<CanaleCaEntity> query = entityManager.createQuery("SELECT canaleCa FROM CanaleCaEntity canaleCa", CanaleCaEntity.class);
        try {
            List<CanaleCaEntity> canaleCaEntityList = query.getResultList();
            TipoCanaleCaList list = new TipoCanaleCaList();
            for(CanaleCaEntity c : canaleCaEntityList){
                list.addTipo2List(c.getCodCanale()+"-"+c.getDescCanale());
            }
            return list;
        }catch(NoResultException e){
            throw new FatturaPACanaleNonTrovatoException("Tipi CanaleCa non presenti sul db");
        }
    }

}