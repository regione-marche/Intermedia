package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import java.io.Serializable;

/**
 * Created by agosteeno on 15/04/16.
 */
public class Operatore implements Serializable {

    private String nome;

    private String cognome;

    private String codiceFiscale;

    private String uo;

    private String ruolo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getUo() {
        return uo;
    }

    public void setUo(String uo) {
        this.uo = uo;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
}
