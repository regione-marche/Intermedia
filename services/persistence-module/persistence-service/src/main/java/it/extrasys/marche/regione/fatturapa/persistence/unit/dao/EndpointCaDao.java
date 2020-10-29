package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EndpointCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.EndpointAttivaCaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;

public class EndpointCaDao extends GenericDao<EndpointCaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(EndpointCaDao.class);

    public void updateEndpointCa(EndpointCaEntity endpointCaEntity, EntityManager entityManager){

        LOG.info("*************** updateEndpointCa ***************");

        TypedQuery<EndpointCaEntity> queryUpdate = entityManager.createQuery("UPDATE EndpointCaEntity e " +
                "SET e.canaleCa = :canaleCa, e.endpoint = :endpoint," +
                " e.username = :username, e.password = :password," +
                " e.path = :path, e.certificato = :certificato, e.dataUltimaModifica = :dataUltimaModifica," +
                " e.utenteModifica = :utenteModifica " +
                "WHERE e.idEndpointCa =: idEndpointCa", EndpointCaEntity.class);
        queryUpdate.setParameter("canaleCa", endpointCaEntity.getCanaleCa());
        queryUpdate.setParameter("endpoint", endpointCaEntity.getEndpoint());
        queryUpdate.setParameter("username", endpointCaEntity.getUsername());
        queryUpdate.setParameter("password", endpointCaEntity.getPassword());
        queryUpdate.setParameter("path", endpointCaEntity.getPath());
        queryUpdate.setParameter("certificato", endpointCaEntity.getCertificato());
        queryUpdate.setParameter("dataUltimaModifica", endpointCaEntity.getDataUltimaModifica());
        queryUpdate.setParameter("utenteModifica", endpointCaEntity.getUtenteModifica());
        queryUpdate.setParameter("idEndpointCa", endpointCaEntity.getIdEndpointCa());

    }

}