package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model;

import java.io.Serializable;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */

public class FatturaElettronicaMetadatiPaleo implements Serializable {

    private String idDocumento;

    private String codiceFascicolo;

    private String codiceStruttura;

    private String numeroFattura;

    private String codiceFiscaleRUP;

    private String descrizione;

    private String nomeRUP;

    private String cognomeRUP;

    private String ruoloRUP;

    private String codiceUORUP;

    private boolean ruoloUnico;

    private boolean canaleAvanzato;

    private String fascicoloResult;

    private String protocolloEntrataNote;

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getCodiceFascicolo() {
        return codiceFascicolo;
    }

    public void setCodiceFascicolo(String codiceFascicolo) {
        this.codiceFascicolo = codiceFascicolo;
    }

    public String getCodiceStruttura() {
        return codiceStruttura;
    }

    public void setCodiceStruttura(String codiceStruttura) {
        this.codiceStruttura = codiceStruttura;
    }

    public String getNumeroFattura() {
        return numeroFattura;
    }

    public void setNumeroFattura(String numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public String getCodiceFiscaleRUP() {
        return codiceFiscaleRUP;
    }

    public void setCodiceFiscaleRUP(String codiceFiscaleRUP) {
        this.codiceFiscaleRUP = codiceFiscaleRUP;
    }

    public String getNomeRUP() {
        return nomeRUP;
    }

    public void setNomeRUP(String nomeRUP) {
        this.nomeRUP = nomeRUP;
    }

    public String getCognomeRUP() {
        return cognomeRUP;
    }

    public void setCognomeRUP(String cognomeRUP) {
        this.cognomeRUP = cognomeRUP;
    }

    public String getRuoloRUP() {
        return ruoloRUP;
    }

    public void setRuoloRUP(String ruoloRUP) {
        this.ruoloRUP = ruoloRUP;
    }

    public String getCodiceUORUP() {
        return codiceUORUP;
    }

    public void setCodiceUORUP(String codiceUORUP) {
        this.codiceUORUP = codiceUORUP;
    }

    public String getDescrizione() {

        return descrizione;
    }

    public void setDescrizione(String descrizione) {

        this.descrizione = descrizione;
    }

    public boolean isRuoloUnico() {
        return ruoloUnico;
    }

    public void setRuoloUnico(boolean ruoloUnico) {
        this.ruoloUnico = ruoloUnico;
    }

    public boolean isCanaleAvanzato() {
        return canaleAvanzato;
    }

    public void setCanaleAvanzato(boolean canaleAvanzato) {
        this.canaleAvanzato = canaleAvanzato;
    }

    public String getProtocolloEntrataNote() {
        return protocolloEntrataNote;
    }

    public void setProtocolloEntrataNote(String protocolloEntrataNote) {
        this.protocolloEntrataNote = protocolloEntrataNote;
    }

    public String getFascicoloResult() {
        return fascicoloResult;
    }

    public void setFascicoloResult(String fascicoloResult) {
        this.fascicoloResult = fascicoloResult;
    }
}