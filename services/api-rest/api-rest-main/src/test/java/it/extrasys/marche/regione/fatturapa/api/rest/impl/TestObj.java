package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;

public class TestObj {
    public static UtenteEntity getUserAdmin(){
        UtenteEntity utenteEntity = new UtenteEntity();
        utenteEntity.setUsername("pippo");
        utenteEntity.setRuolo("admin");
        return utenteEntity;
    }
    public static EnteEntity getEnte(){
        EnteEntity enteEntity = new EnteEntity();
        UtenteEntity utenteEntity = new UtenteEntity();
        utenteEntity.setUsername("pippo");
        utenteEntity.setRuolo("admin");
        return enteEntity;
    }
}
