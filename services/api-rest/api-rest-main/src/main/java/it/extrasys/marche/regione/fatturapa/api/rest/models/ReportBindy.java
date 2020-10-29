package it.extrasys.marche.regione.fatturapa.api.rest.models;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ";", generateHeaderColumns = true )
public class ReportBindy {

    @DataField(pos = 1)
    private String dataRielaborazione;

    @DataField(pos = 2)
    private String utente;

    @DataField(pos = 3)
    private String nomeCoda;

    @DataField(pos = 4)
    private String identificativoSdi;

    @DataField(pos = 5)
    private String numeroRielaborazioni;

    @DataField(pos = 6)
    private String stackTrace;

    public String getDataRielaborazione() {
        return dataRielaborazione;
    }

    public void setDataRielaborazione(String dataRielaborazione) {
        this.dataRielaborazione = dataRielaborazione;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public String getNomeCoda() {
        return nomeCoda;
    }

    public void setNomeCoda(String nomeCoda) {
        this.nomeCoda = nomeCoda;
    }

    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public String getNumeroRielaborazioni() {
        return numeroRielaborazioni;
    }

    public void setNumeroRielaborazioni(String numeroRielaborazioni) {
        this.numeroRielaborazioni = numeroRielaborazioni;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
