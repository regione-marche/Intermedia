package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 24/02/15.
 */
public class FatturaElettronicaWrapper implements Serializable {

    private static final long serialVersionUID = 1909618038920833731L;

    private String fatturaElettronica;

    private String fatturaElettronicaHTML;

    private String segnaturaProtocollo;

    private Map<String, FatturaElettronicaMetadatiPaleo> fatturaMetadatiMap;

    private Set<String> UOset;

    private Cedente cedente;

    private EntePaleoCA entePaleoCA;

    private String mittente;

    private EnteEntity enteEntity;

    public String getFatturaElettronica() {
        return fatturaElettronica;
    }

    public void setFatturaElettronica(String fatturaElettronica) {
        this.fatturaElettronica = fatturaElettronica;
    }

    public Map<String, FatturaElettronicaMetadatiPaleo> getFatturaMetadatiMap() {

        if (fatturaMetadatiMap == null) {
            fatturaMetadatiMap = new HashMap<String, FatturaElettronicaMetadatiPaleo>();
        }

        return fatturaMetadatiMap;
    }

    public void setFatturaMetadatiMap(Map<String, FatturaElettronicaMetadatiPaleo> fatturaMetadatiMap) {
        this.fatturaMetadatiMap = fatturaMetadatiMap;
    }

    public Cedente getCedente() {
        return cedente;
    }

    public void setCedente(Cedente cedente) {
        this.cedente = cedente;
    }

    public EntePaleoCA getEntePaleoCA() {
        return entePaleoCA;
    }

    public void setEntePaleoCA(EntePaleoCA entePaleoCA) {
        this.entePaleoCA = entePaleoCA;
    }

    public String getFatturaElettronicaHTML() {
        return fatturaElettronicaHTML;
    }

    public void setFatturaElettronicaHTML(String fatturaElettronicaHTML) {
        this.fatturaElettronicaHTML = fatturaElettronicaHTML;
    }

    public String getSegnaturaProtocollo() {
        return segnaturaProtocollo;
    }

    public void setSegnaturaProtocollo(String segnaturaProtocollo) {
        this.segnaturaProtocollo = segnaturaProtocollo;
    }

    public Set<String> getUOset() {
        return UOset;
    }

    public void setUOset(Set<String> UOset) {
        this.UOset = UOset;
    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public EnteEntity getEnteEntity() {
        return enteEntity;
    }

    public void setEnteEntity(EnteEntity enteEntity) {
        this.enteEntity = enteEntity;
    }
}