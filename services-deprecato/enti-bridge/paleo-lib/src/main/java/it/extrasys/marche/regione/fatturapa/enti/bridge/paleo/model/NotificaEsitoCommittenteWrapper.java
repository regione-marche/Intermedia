package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class NotificaEsitoCommittenteWrapper implements Serializable {

    private String notificaEsitoCommittente;

    private List<String> classificazioniList;

    private String segnaturaProtocolloFattura;

    private String segnaturaProtocolloNotifica;

    private boolean accettata;

    private EntePaleoCA entePaleoCA;

    private EnteEntity enteEntity;

    public String getNotificaEsitoCommittente() {
        return notificaEsitoCommittente;
    }

    public void setNotificaEsitoCommittente(String notificaEsitoCommittente) {
        this.notificaEsitoCommittente = notificaEsitoCommittente;
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

    public boolean isAccettata() {
        return accettata;
    }

    public void setAccettata(boolean accettata) {
        this.accettata = accettata;
    }

    public String getSegnaturaProtocolloFattura() {
        return segnaturaProtocolloFattura;
    }

    public void setSegnaturaProtocolloFattura(String segnaturaProtocolloFattura) {
        this.segnaturaProtocolloFattura = segnaturaProtocolloFattura;
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
