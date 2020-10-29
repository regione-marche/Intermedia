package it.extrasys.marche.regione.fatturapa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBUtils {

	
	public static EntityManager setupEntityManager() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("openjpa.ConnectionURL", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=CREATE TABLE IF NOT EXISTS PG_CLASS (RELNAME text, RELKIND text)");
        properties.put("openjpa.ConnectionDriverName", "org.h2.Driver");
        properties.put("openjpa.Log", "DefaultLevel=INFO, Tool=INFO");
        properties.put("openjpa.TransactionMode","local");
        properties.put("openjpa.ConnectionFactoryProperties","autocommit=true");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit", properties);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        return entityManager;
    }
}
