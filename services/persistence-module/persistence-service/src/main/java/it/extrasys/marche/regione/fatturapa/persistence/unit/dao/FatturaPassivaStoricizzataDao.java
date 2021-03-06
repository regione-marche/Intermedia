package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.storico.FatturaPassivaStoricizzataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FatturaPassivaStoricizzataDao extends GenericDao<FatturaPassivaStoricizzataEntity, BigInteger> {

    private static final Logger LOG = LoggerFactory.getLogger(FatturaPassivaStoricizzataDao.class);
    private static long year = 31557600000L;

    public List<Object[]> getFattureUltimAnno(EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        LOG.info("********** FatturaPassivaStoricizzataDao: getFattureUltimAnno **********");
        Date now = new Date();
        long nowMilli = now.getTime();
        long yearAgoMilli = nowMilli - year;
        Date yearAgo = new Date(yearAgoMilli);

        try {
            String queryString = "SELECT tc.desc_canale, e.invio_unico, e.id_endpoint_protocollo_ca, cca.desc_canale  FROM fattura_passiva_storicizzata  as df " +
                    " JOIN ente AS e ON df.codice_ufficio = e.codice_ufficio " +
                    " JOIN tipo_canale tc on e.id_tipo_canale = tc.cod_tipo_canale " +
                    " left JOIN endpoint_ca as ec ON e.id_endpoint_protocollo_ca =ec.id_endpoint_ca " +
                    " full outer join canale_ca as cca on ec.id_canale_ca = cca.cod_canale " +
                    " WHERE  df.data_ricezione_sdi BETWEEN ? AND ?";

            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, yearAgo);
            query.setParameter(2, now);
            return query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

    public List<Object[]> getFattureUltimAnnoUtente(UtenteEntity utenteEntity, EntityManager entityManager) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        LOG.info("********** FatturaPassivaStoricizzataDao: getFattureUltimAnnoUtente **********");

        Date now = new Date();
        long nowMilli = now.getTime();
        long yearAgoMilli = nowMilli - year;
        Date yearAgo = new Date(yearAgoMilli);

        try {
            String queryString = "SELECT tc.desc_canale, e.invio_unico, e.id_endpoint_protocollo_ca, cca.desc_canale FROM fattura_passiva_storicizzata as dfe " +
                    " JOIN ente AS e ON dfe.codice_ufficio = e.codice_ufficio " +
                    " join utente_ente ue ON (ue.id_ente = e.id_ente ) " +
                    " join utenti u ON(ue.id_utente = u.id_utente ) " +
                    " JOIN tipo_canale tc on e.id_tipo_canale = tc.cod_tipo_canale " +
                    " left JOIN endpoint_ca as ec ON e.id_endpoint_protocollo_ca = ec.id_endpoint_ca " +
                    " full outer join canale_ca as cca on ec.id_canale_ca = cca.cod_canale  " +
                    " WHERE dfe.data_ricezione_sdi BETWEEN ? AND ? and u.username = ?";

            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, yearAgo);
            query.setParameter(2, now);
            query.setParameter(3, utenteEntity.getUsername());
            return query.getResultList();

        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
    }

}
