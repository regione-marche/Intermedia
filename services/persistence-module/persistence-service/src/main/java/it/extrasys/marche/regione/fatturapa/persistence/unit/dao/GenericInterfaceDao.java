package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/02/15.
 */
public interface GenericInterfaceDao<T, PK extends Serializable> {

    public T create(T t,EntityManager entityManager);

    public T read(PK id,EntityManager entityManager);

    public T update(T t,EntityManager entityManager);

    public void delete(T t,EntityManager entityManager);
    
    public Long countAll(EntityManager entityManager) ;

    public List<T> findAll(EntityManager entityManager);

}
