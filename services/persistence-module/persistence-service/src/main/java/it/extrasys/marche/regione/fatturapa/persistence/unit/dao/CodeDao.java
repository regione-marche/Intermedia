package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodeEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

public class CodeDao extends GenericDao<CodeEntity, BigInteger> {

    public CodeEntity getCodaByNome(String nome,  EntityManager entityManager) throws FatturaPaPersistenceException {
        List<CodeEntity> code = null;
        String queryString = "SELECT c FROM CodeEntity c WHERE c.nome = :nome "; // AND c.ciclo = :ciclo";

        try {
            TypedQuery<CodeEntity> query = entityManager.createQuery(queryString, CodeEntity.class);
            query.setParameter("nome", nome);
          //  query.setParameter("ciclo", ciclo);

            code = query.getResultList();
        } catch (PersistenceException e) {
            throw new FatturaPaPersistenceException();
        }
        if (code != null && code.size() > 0) {
            return code.get(0);
        } else {
            return null;
        }
    }
}
