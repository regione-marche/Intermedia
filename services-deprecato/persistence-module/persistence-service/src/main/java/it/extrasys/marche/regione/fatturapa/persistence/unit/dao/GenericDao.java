package it.extrasys.marche.regione.fatturapa.persistence.unit.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/02/15.
 */
public class GenericDao<T, PK extends Serializable> implements GenericInterfaceDao<T, PK> {

    protected Class<T> entityClass;

    public GenericDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public T create(T t, EntityManager entityManager) {
        entityManager.persist(t);
        return t;
    }

    @Override
    public T read(PK id, EntityManager entityManager) {
        return entityManager.find(entityClass, id);
    }

    @Override
    public T update(T t, EntityManager entityManager) {
        return entityManager.merge(t);
    }

    @Override
    public void delete(T t, EntityManager entityManager) {
        t = entityManager.merge(t);
        entityManager.remove(t);
    }

    @Override
    public Long countAll(EntityManager entityManager) {

        final StringBuffer queryString = new StringBuffer("SELECT count(o) from ");

        queryString.append(entityClass.getSimpleName()).append(" o ");


        final Query query = entityManager.createQuery(queryString.toString());

        return (Long) query.getSingleResult();

    }

    @Override
    public List<T> findAll(EntityManager entityManager) {

        final StringBuffer queryString = new StringBuffer("SELECT allRows from ");

        queryString.append(entityClass.getSimpleName()).append(" allRows ");


        final Query query = entityManager.createQuery(queryString.toString());

        return (List<T>) query.getResultList();

    }

    /*
    Per generera la stringa della query con i parametri identificati da '?'
    Es: "? , ? , ? , ? , ?"
     */

    public static String createStringParamQuery(Integer numeroParametri) {
        StringJoiner joiner = new StringJoiner(" , ");
        for (int i = 0; i < numeroParametri; i++) {
            joiner.add("?");
        }
        return joiner.toString();

    }
}

