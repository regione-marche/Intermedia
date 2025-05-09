package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 26/01/15.
 */

public class MetadatiFatturaDao extends GenericDao<MetadatiFatturaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(MetadatiFatturaDao.class);

    public MetadatiFatturaEntity getMetadatiByIdentificativoSdi(BigInteger identificativoSdi, EntityManager entityManager) throws FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        MetadatiFatturaEntity metadatiFatturaEntity = null;

        try {
            TypedQuery<MetadatiFatturaEntity> query = entityManager.createQuery("SELECT metadati FROM MetadatiFatturaEntity metadati WHERE metadati.identificativoSdI = :identificativoSdI", MetadatiFatturaEntity.class);
            query.setParameter("identificativoSdI", identificativoSdi);

            metadatiFatturaEntity = query.getSingleResult();

        } catch (NoResultException e) {
            throw new FatturaPAEnteNonTrovatoException("Medatadi non trovati for identificativoSdi " + identificativoSdi);
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }

        return metadatiFatturaEntity;
    }

    public void nativeDeleteByIdentificativoSdI(BigInteger identificativoSdI, EntityManager entityManager) throws Exception {
        String queryString = "DELETE FROM METADATI_FATTURA WHERE IDENTIFICATIVO_SDI = ? ";

        try {
            Query nativeQuery = entityManager.createNativeQuery(queryString);

            nativeQuery.setParameter(1, identificativoSdI);
            int update = nativeQuery.executeUpdate();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
