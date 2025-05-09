package it.extrasys.marche.regione.fatturapa.enti.bridge.giunta.beans;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import org.apache.camel.Exchange;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public class RegistrazioneBean {

    private EntityManagerFactory entityManagerFactory;

    private DatiFatturaDao datiFatturaDao;

    private StatoFatturaDao statoFatturaDao;

    private CodificaStatiDao codificaStatiDao;

    public void setFatturaRegistrata(Exchange exchange) throws FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        EntityManager entityManager = null;

        BigInteger identificativoSdi;
        try {
            identificativoSdi = new BigInteger((String) exchange.getIn().getHeader("identificativoSdI"));
        }
        catch(ClassCastException e){
            identificativoSdi = BigInteger.valueOf((Long) exchange.getIn().getHeader("identificativoSdI"));
        }

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaDao.getFattureByIdentificativoSdi(identificativoSdi, entityManager);
            DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);
            StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();
            statoFatturaEntity.setData(new Timestamp(System.currentTimeMillis()));
            statoFatturaEntity.setStato(codificaStatiDao.getByIdCodStato("003", entityManager));
            statoFatturaEntity.setDatiFattura(datiFatturaEntity);
            statoFatturaDao.create(statoFatturaEntity, entityManager);
            entityManager.getTransaction().commit();

        } catch (FatturaPaPersistenceException | FatturaPAFatturaNonTrovataException e) {
            throw e;
        } finally {
        if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    }

    public DatiFatturaDao getDatiFatturaDao() {
        return datiFatturaDao;
    }

    public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
        this.datiFatturaDao = datiFatturaDao;
    }

    public StatoFatturaDao getStatoFatturaDao() {
        return statoFatturaDao;
    }

    public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
        this.statoFatturaDao = statoFatturaDao;
    }

    public CodificaStatiDao getCodificaStati2Dao() {
        return codificaStatiDao;
    }

    public void setCodificaStatiDao(CodificaStatiDao codificaStati2Dao) {
        this.codificaStatiDao = codificaStati2Dao;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory(){
        return this.entityManagerFactory;
    }
}
