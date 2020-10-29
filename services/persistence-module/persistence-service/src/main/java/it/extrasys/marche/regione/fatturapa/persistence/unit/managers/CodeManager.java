package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodeDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CodeManager {
    private static final Logger LOG = LoggerFactory.getLogger(CodeManager.class);

    private EntityManagerFactory entityManagerFactory;
    private CodeDao codeDao;

    public CodeEntity getCodaByNome(String nome) throws FatturaPAException {
        EntityManager entityManager = null;
        CodeEntity codeEntity = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            codeEntity = codeDao.getCodaByNome(nome, entityManager);
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        }
        return codeEntity;
    }


        public EntityManagerFactory getEntityManagerFactory () {
            return entityManagerFactory;
        }

        public void setEntityManagerFactory (EntityManagerFactory entityManagerFactory){
            this.entityManagerFactory = entityManagerFactory;
        }

        public CodeDao getCodeDao () {
            return codeDao;
        }

        public void setCodeDao (CodeDao codeDao){
            this.codeDao = codeDao;
        }
    }
