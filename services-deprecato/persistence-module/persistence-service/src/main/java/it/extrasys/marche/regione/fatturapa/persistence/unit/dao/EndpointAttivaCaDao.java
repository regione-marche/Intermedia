package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.EndpointAttivaCaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;

public class EndpointAttivaCaDao extends GenericDao<EndpointAttivaCaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(EndpointAttivaCaDao.class);

    public void updateEndpointAttivaCa(EndpointAttivaCaEntity endpointAttivaCaEntity, EntityManager entityManager){

        LOG.info("*************** updateEndpointAttivaCa ***************");

        TypedQuery<EndpointAttivaCaEntity> queryUpdate = entityManager.createQuery("UPDATE EndpointAttivaCaEntity e " +
                "SET e.canaleCa = :canaleCa, e.endpoint = :endpoint," +
                " e.username = :username, e.password = :password," +
                " e.path = :path, e.certificato = :certificato, e.dataUltimaModifica = :dataUltimaModifica," +
                " e.utenteModifica = :utenteModifica " +
                "WHERE e.idEndpointCa =: idEndpointCa", EndpointAttivaCaEntity.class);
        queryUpdate.setParameter("canaleCa", endpointAttivaCaEntity.getCanaleCa());
        queryUpdate.setParameter("endpoint", endpointAttivaCaEntity.getEndpoint());
        queryUpdate.setParameter("username", endpointAttivaCaEntity.getUsername());
        queryUpdate.setParameter("password", endpointAttivaCaEntity.getPassword());
        queryUpdate.setParameter("path", endpointAttivaCaEntity.getPath());
        queryUpdate.setParameter("certificato", endpointAttivaCaEntity.getCertificato());
        queryUpdate.setParameter("dataUltimaModifica", endpointAttivaCaEntity.getDataUltimaModifica());
        queryUpdate.setParameter("utenteModifica", endpointAttivaCaEntity.getUtenteModifica());
        queryUpdate.setParameter("idEndpointCa", endpointAttivaCaEntity.getIdEndpointCa());

    }

}