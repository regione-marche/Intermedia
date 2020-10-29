package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */

public class NotificaDecorrenzaTerminiWrapper implements Serializable {

    private String notificaDecorrenzaTermini;

    private String segnaturaProtocolloFattura;

    private Cedente cedente;

    private List<String> classificazioniList;

    private String SegnaturaProtocolloNotifica;

    private List<Operatore> operatoreList;

    private EntePaleoCA entePaleoCA;

    private EnteEntity enteEntity;

    public String getNotificaDecorrenzaTermini() {
        return notificaDecorrenzaTermini;
    }

    public void setNotificaDecorrenzaTermini(String notificaDecorrenzaTermini) {
        this.notificaDecorrenzaTermini = notificaDecorrenzaTermini;
    }

    public Cedente getCedente() {
        return cedente;
    }

    public void setCedente(Cedente cedente) {
        this.cedente = cedente;
    }

    public String getSegnaturaProtocolloFattura() {
        return segnaturaProtocolloFattura;
    }

    public void setSegnaturaProtocolloFattura(String segnaturaProtocolloFattura) {
        this.segnaturaProtocolloFattura = segnaturaProtocolloFattura;
    }

    public List<String> getClassificazioniList() {
        return classificazioniList;
    }

    public void setClassificazioniList(List<String> classificazioniList) {
        this.classificazioniList = classificazioniList;
    }

    public String getSegnaturaProtocolloNotifica() {
        return SegnaturaProtocolloNotifica;
    }

    public void setSegnaturaProtocolloNotifica(String segnaturaProtocolloNotifica) {
        SegnaturaProtocolloNotifica = segnaturaProtocolloNotifica;
    }

    public List<Operatore> getOperatoreList() {
        return operatoreList;
    }

    public void setOperatoreList(List<Operatore> operatoreList) {
        this.operatoreList = operatoreList;
    }

    public EntePaleoCA getEntePaleoCA() {
        return entePaleoCA;
    }

    public void setEntePaleoCA(EntePaleoCA entePaleoCA) {
        this.entePaleoCA = entePaleoCA;
    }

    public EnteEntity getEnteEntity() {
        return enteEntity;
    }

    public void setEnteEntity(EnteEntity enteEntity) {
        this.enteEntity = enteEntity;
    }
}