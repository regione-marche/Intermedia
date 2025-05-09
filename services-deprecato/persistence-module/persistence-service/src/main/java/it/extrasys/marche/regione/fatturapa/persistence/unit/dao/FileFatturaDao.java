package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManagerXAImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 15/03/15.
 */
public class FileFatturaDao extends GenericDao<FileFatturaEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(FileFatturaDao.class);


    public FileFatturaEntity getFileFatturaByIdentificativiSdI(BigInteger identificativoSdI, EntityManager entityManager) {
        TypedQuery<FileFatturaEntity> query = entityManager.createQuery("SELECT ffe FROM FileFatturaEntity ffe WHERE ffe.identificativoSdI = :identificativoSdI", FileFatturaEntity.class);
        query.setParameter("identificativoSdI", identificativoSdI);

        return query.getSingleResult();

    }

    public FileFatturaEntity getFileFatturaByIdFilefattura(BigInteger idFileFattura, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException {
        FileFatturaEntity file = null;
        try {
            TypedQuery<FileFatturaEntity> query = entityManager.createQuery("SELECT ffe FROM FileFatturaEntity ffe WHERE ffe.idFileFattura = :idFileFattura", FileFatturaEntity.class);
            query.setParameter("idFileFattura", idFileFattura);

            file = query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException("File fattura con id " + idFileFattura + " non trovato ");
        }
        return file;
    }

    public FileFatturaEntity getFileFatturaByNomeFilefattura(String nomeFileFattura, EntityManager entityManager) throws FatturaPAFatturaNonTrovataException {
        FileFatturaEntity file = null;
        try {
            TypedQuery<FileFatturaEntity> query = entityManager.createQuery("SELECT ffe FROM FileFatturaEntity ffe WHERE ffe.nomeFileFattura = :nomeFileFattura", FileFatturaEntity.class);
            query.setParameter("nomeFileFattura", nomeFileFattura);

            file = query.getSingleResult();
        } catch (NoResultException e) {
            throw new FatturaPAFatturaNonTrovataException("File fattura con nomeFileFattura " + nomeFileFattura + " non trovato ");
        }
        return file;
    }

        public List<Long> getIdentificativoSdiListBeforeDate(Date dataRiferimento, EntityManager entityManager){

        Query query = entityManager.createNativeQuery("SELECT ffe.identificativo_sdi FROM file_fattura ffe WHERE ffe.data_ricezione <= ? AND fatturazione_test = false");
        query.setParameter(1, dataRiferimento);

        List<Long> list = query.getResultList();

        return list;

    }

    public Long getCountIdentificativoSdiListBeforeDate(Date dataRiferimento, EntityManager entityManager){

        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM file_fattura ffe WHERE ffe.data_ricezione <= ?");
        query.setParameter(1, dataRiferimento);

        Long list = (Long) query.getSingleResult();

        return list;

    }


    public int deleteByIdentificativoSdi(BigInteger identificativoSdI, EntityManager entityManager){
        String queryString = "DELETE FROM file_fattura WHERE identificativo_sdi = ? ";

        Query query = entityManager.createNativeQuery(queryString);

        query.setParameter(1, identificativoSdI);
        int row = query.executeUpdate();

        return row;
    }

}
