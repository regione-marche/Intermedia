package it.extrasys.marche.regione.fatturapa.mock.sdi.ricevi;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {

	static EntityManager entityManager;

	private static String maxIdAttiva = "select max(identificativo_sdi) from fattura_attiva";
	private static String fattureAttive = "select count(id_fattura_attiva) from fattura_attiva";

	public static Integer getNextIdentificativoAttivaSdI(HashMap<String, String> properties) {
		if (entityManager == null)
			entityManager = setupEntityManager(properties);
		Integer id = 0;
		List<Integer> listId = null;
		Integer count = (Integer) entityManager.createNativeQuery(fattureAttive, Integer.class).getSingleResult();
		if (count != 0) {
			listId = entityManager.createNativeQuery(maxIdAttiva, Integer.class).getResultList();
		}

		if(listId == null || listId.size() == 0) {
			id = 0;
		} else {
			id = listId.get(0);
		}

		return id + 1;

	}

	public static EntityManager setupEntityManager(HashMap<String, String> properties) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit", properties);
		entityManager = entityManagerFactory.createEntityManager();

		return entityManager;
	}
}
