package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaPK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 20/02/15.
 */
public class StatoFatturaDao extends GenericDao<StatoFatturaEntity, StatoFatturaPK> {

    private static final Logger LOG = LoggerFactory.getLogger(StatoFatturaDao.class);

    /**
     * @param numeroProtocollo        il numero di protocollo della  Fattura
     * @param committenteIdFiscaleIVA partita IVA o CodiceFiscale del Committente
     * @return l' ultimo statoFattura relativo alla fattura che ha come committente  committenteIdFiscaleIVA.
     */
    public StatoFatturaEntity findUltimoStatoFatturaPerNumeroProtocolloECommittenteIdFiscaleIvaEDataFattura(String numeroProtocollo, String committenteIdFiscaleIVA, EntityManager entityManager) throws NoResultException {

        StatoFatturaEntity statoFatturaEntity = null;

        Timestamp maxData;

        String queryMaxData = " SELECT MAX(sf.data) AS maxData " +
                "FROM StatoFatturaEntity sf " +
                "WHERE  sf.datiFattura.numeroProtocollo = :numeroProtocollo  AND " +
                "sf.datiFattura.committenteIdFiscaleIVA = :committenteIdFiscaleIVA";

        TypedQuery<Timestamp> maxDataQuery = entityManager.createQuery(queryMaxData, Timestamp.class);

        maxDataQuery.setParameter("numeroProtocollo", numeroProtocollo);
        maxDataQuery.setParameter("committenteIdFiscaleIVA", committenteIdFiscaleIVA);

        maxData = maxDataQuery.getSingleResult();

        String queryString = "SELECT statoFattura FROM StatoFatturaEntity statoFattura " +
                " WHERE  statoFattura.datiFattura.numeroProtocollo = :numeroProtocollo  AND " +
                " statoFattura.datiFattura.committenteIdFiscaleIVA = :committenteIdFiscaleIVA AND " +
                " statoFattura.data = :data";
        TypedQuery<StatoFatturaEntity> query = entityManager.createQuery(queryString, StatoFatturaEntity.class);
        query.setParameter("numeroProtocollo", numeroProtocollo);
        query.setParameter("committenteIdFiscaleIVA", committenteIdFiscaleIVA);
        query.setParameter("data", maxData);

        statoFatturaEntity = query.getSingleResult();


        return statoFatturaEntity;

    }

    public StatoFatturaEntity findUltimoStatoFatturaPerIdDatiFatturaEDataFattura(BigInteger idDatiFattura, EntityManager entityManager) throws NoResultException {

        StatoFatturaEntity statoFatturaEntity = null;

        Timestamp maxData;

        String queryMaxData = " SELECT MAX(sf.data) AS maxData " +
                "FROM StatoFatturaEntity sf " +
                "WHERE  sf.datiFattura.idDatiFattura = :idDatiFattura";

        TypedQuery<Timestamp> maxDataQuery = entityManager.createQuery(queryMaxData, Timestamp.class);

        maxDataQuery.setParameter("idDatiFattura", idDatiFattura);

        maxData = maxDataQuery.getSingleResult();

        String queryString = "SELECT statoFattura FROM StatoFatturaEntity statoFattura " +
                " WHERE  statoFattura.datiFattura.idDatiFattura = :idDatiFattura AND " +
                " statoFattura.data = :data";
        TypedQuery<StatoFatturaEntity> query = entityManager.createQuery(queryString, StatoFatturaEntity.class);
        query.setParameter("idDatiFattura", idDatiFattura);
        query.setParameter("data", maxData);

        statoFatturaEntity = query.getSingleResult();


        return statoFatturaEntity;

    }

    public Date getDataCambiamentoStatoFatturaByIdentificativoSdi(BigInteger identificativoSdI, CodificaStatiEntity codiceStato, EntityManager entityManager) {

        TypedQuery<StatoFatturaEntity> query = entityManager.createQuery("SELECT sfe FROM StatoFatturaEntity sfe WHERE sfe.datiFattura.identificativoSdI = :identificativoSdI AND sfe.stato.codStato = :codStato ", StatoFatturaEntity.class);
        query.setParameter("identificativoSdI", identificativoSdI);
        query.setParameter("codStato", codiceStato.getCodStato().getValue());


        List<StatoFatturaEntity> resultsList = query.getResultList();

        if (resultsList.size() > 0) {
            return resultsList.get(0).getData();
        }

        return null;
    }


    public List<StatoFatturaEntity> getStatoFattureByIdentificativoSdI(BigInteger identificativoSdI, EntityManager entityManager) {

        String queryString = "SELECT statoFattura FROM StatoFatturaEntity statoFattura JOIN FETCH statoFattura.datiFattura WHERE statoFattura.datiFattura.identificativoSdI = :identificativoSdI";

        TypedQuery<StatoFatturaEntity> query = entityManager.createQuery(queryString, StatoFatturaEntity.class);

        query.setParameter("identificativoSdI", identificativoSdI);

        List<StatoFatturaEntity> resultsList = query.getResultList();

        return resultsList;
    }

    public StatoFatturaEntity getLastStato(BigInteger idDatiFattura, EntityManager entityManager) {

        StatoFatturaEntity statoFatturaEntity = null;

        String queryString = "SELECT statoFattura " +
                "FROM StatoFatturaEntity statoFattura JOIN FETCH statoFattura.datiFattura " +
                "WHERE statoFattura.datiFattura.idDatiFattura = :idDatiFattura " +
                "ORDER BY statoFattura.data DESC";

        TypedQuery<StatoFatturaEntity> query = entityManager.createQuery(queryString, StatoFatturaEntity.class);

        query.setParameter("idDatiFattura", idDatiFattura);

        List<StatoFatturaEntity> resultsList = query.getResultList();

        if (resultsList.size() > 0) {
            return resultsList.get(0);
        }

        return null;
    }

    public List<StatoFatturaEntity> getStati(BigInteger idDatiFattura, EntityManager entityManager) {

        List<StatoFatturaEntity> statoFatturaEntityList = null;

        String queryString = "SELECT statoFattura " +
                "FROM StatoFatturaEntity statoFattura JOIN FETCH statoFattura.datiFattura " +
                "WHERE statoFattura.datiFattura.idDatiFattura = :idDatiFattura ";

        TypedQuery<StatoFatturaEntity> query = entityManager.createQuery(queryString, StatoFatturaEntity.class);

        query.setParameter("idDatiFattura", idDatiFattura);

        statoFatturaEntityList = query.getResultList();

        return statoFatturaEntityList;
    }

    public Integer cancellaStatiFatturaPerReinvio(String identificativiSdI, EntityManager entityManager) {

        String queryString = "delete " +
                "from stato_fattura " +
                "where (id_cod_stato = '009' or id_cod_stato = '013' or id_cod_stato = '014') and id_dati_fattura in (select id_dati_fattura " +
                "from dati_fattura " +
                "where identificativo_sdi in(" + identificativiSdI + "))";


        int row = entityManager.createNativeQuery(queryString).executeUpdate();

        return row;
    }

    public Integer cancellaStatiDecTerminiPerReinvio(String identificativiSdI, EntityManager entityManager) {

        String queryString = "delete " +
                "from stato_fattura " +
                "where (id_cod_stato = '008' or id_cod_stato = '015' or id_cod_stato = '016') and id_dati_fattura in (select id_dati_fattura " +
                "from dati_fattura " +
                "where identificativo_sdi in(" + identificativiSdI + "))";


        int row = entityManager.createNativeQuery(queryString).executeUpdate();

        return row;
    }


    public List<Object[]> getStatoFattureByIdFattura(BigInteger idDatiFattura, EntityManager entityManager) {

        String queryString = "SELECT cs.desc_stato, sf.data FROM stato_fattura sf, codifica_stati cs WHERE sf.id_cod_stato = cs.cod_stato  AND id_dati_fattura = ?";

        Query query = entityManager.createNativeQuery(queryString);

        query.setParameter(1, idDatiFattura);

        List<Object[]> resultsList = query.getResultList();

        return resultsList;
    }

    public Integer deleteByIdDatiFattura(List<BigInteger> idDatiFattura, EntityManager entityManager) {
        String queryString = "DELETE FROM stato_fattura sf WHERE id_dati_fattura IN ( ";
        String stringParamQuery = GenericDao.createStringParamQuery(idDatiFattura.size());

        queryString += stringParamQuery + " )";

        Query query = entityManager.createNativeQuery(queryString);

        for (int i = 1; i <= idDatiFattura.size(); i++) {
            query.setParameter(i, idDatiFattura.get(i - 1));
        }
        int row = query.executeUpdate();

        return row;
    }


}