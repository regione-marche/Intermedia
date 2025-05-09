package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntePaleoCaEntity;

import java.io.Serializable;

public class EntePaleoCA implements Serializable {

    private String nomeEnte;
    private String address;
    private String wsdlURL;
    private EntePaleoCaEntity entePaleoCaEntity;

    public String getNomeEnte() {
        return nomeEnte;
    }

    public void setNomeEnte(String nomeEnte) {
        this.nomeEnte = nomeEnte;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWsdlURL() {
        return wsdlURL;
    }

    public void setWsdlURL(String wsdlURL) {
        this.wsdlURL = wsdlURL;
    }

    public EntePaleoCaEntity getEntePaleoCaEntity() {
        return entePaleoCaEntity;
    }

    public void setEntePaleoCaEntity(EntePaleoCaEntity entePaleoCaEntity) {
        this.entePaleoCaEntity = entePaleoCaEntity;
    }
}