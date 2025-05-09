package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAUtenteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.EnteDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.UtentiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.UtentiManager;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class EntitiesResourceTest {

    @Mock
    private EnteManager enteManager;
    @Mock
    private EnteDao enteDao;
    @Mock
    private UtentiManager utentiManager;
    @Mock
    private UtentiDao utentiDao;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void test01() throws FatturaPAUtenteNonTrovatoException {
        /*setup();
        Mockito.when(utentiDao.getUtenteByUsername("pippo", entityManager)).thenReturn(TestObj.getUserAdmin());
        Mockito.when(enteDao.read(BigInteger.valueOf(1), entityManager)).thenReturn(TestObj.getEnte());
        EnteEntity enteEntity = enteDao.read(BigInteger.valueOf(1), entityManager);
        UtenteEntity utenteEntity = TestObj.getUserAdmin();
        assertEquals(false, utentiDao.checkAuth(enteEntity, utenteEntity, true));*/

    }

    private void setup(){
        enteManager = Mockito.mock(EnteManager.class);
        enteDao = Mockito.mock(EnteDao.class);
        utentiManager = Mockito.mock(UtentiManager.class);
        entityManager = Mockito.mock(EntityManager.class);
        utentiDao = Mockito.mock(UtentiDao.class);
        entityManagerFactory = Mockito.mock(EntityManagerFactory.class);
    }
}
