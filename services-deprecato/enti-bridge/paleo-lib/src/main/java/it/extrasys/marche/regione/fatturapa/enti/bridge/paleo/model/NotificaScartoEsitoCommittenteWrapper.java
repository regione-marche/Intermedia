package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;

import java.io.Serializable;
import java.util.List;

public class NotificaScartoEsitoCommittenteWrapper implements Serializable {

    private String notificaScartoEsitoCommittente;

    private List<String> classificazioniList;

    private String segnaturaProtocolloFattura;

    private String segnaturaProtocolloNotifica;

    //private boolean accettata;

    private EntePaleoCA entePaleoCA;

    private EnteEntity enteEntity;

    public String getNotificaScartoEsitoCommittente() {
        return notificaScartoEsitoCommittente;
    }

    public void setNotificaScartoEsitoCommittente(String notificaScartoEsitoCommittente) {
        this.notificaScartoEsitoCommittente = notificaScartoEsitoCommittente;
    }

    public List<String> getClassificazioniList() {
        return classificazioniList;
    }

    public void setClassificazioniList(List<String> classificazioniList) {
        this.classificazioniList = classificazioniList;
    }

    public String getSegnaturaProtocolloNotifica() {
        return segnaturaProtocolloNotifica;
    }

    public void setSegnaturaProtocolloNotifica(String segnaturaProtocolloNotifica) {
        this.segnaturaProtocolloNotifica = segnaturaProtocolloNotifica;
    }

    public EntePaleoCA getEntePaleoCA() {
        return entePaleoCA;
    }

    public void setEntePaleoCA(EntePaleoCA entePaleoCA) {
        this.entePaleoCA = entePaleoCA;
    }

    public String getSegnaturaProtocolloFattura() {
        return segnaturaProtocolloFattura;
    }

    public void setSegnaturaProtocolloFattura(String segnaturaProtocolloFattura) {
        this.segnaturaProtocolloFattura = segnaturaProtocolloFattura;
    }

    public EnteEntity getEnteEntity() {
        return enteEntity;
    }

    public void setEnteEntity(EnteEntity enteEntity) {
        this.enteEntity = enteEntity;
    }
}