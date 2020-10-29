package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;
import java.util.List;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "data_test",
        "identificativo_sdi",
        "nome_file",
        "ricevuta_comunicazione",
        "fattura_attiva",
        "ricevuta_consegna",
        "nome_file_rc"
})
public class ACTestDto {

    @JsonProperty ("data_test")
    @JsonPropertyDescription ("")
    private Date dataTest;

    @JsonProperty ("identificativo_sdi")
    @JsonPropertyDescription ("")
    private String identificativoSdi;

    @JsonProperty ("nome_file")
    @JsonPropertyDescription ("")
    private String nomeFile;

    @JsonProperty ("ricevuta_comunicazione")
    @JsonPropertyDescription ("")
    private String ricevutaComunicazione;

    @JsonProperty ("fattura_attiva")
    @JsonPropertyDescription ("")
    private Boolean fatturaAttiva;

    @JsonProperty ("ricevuta_consegna")
    @JsonPropertyDescription ("")
    private Boolean ricevutaConsegna;

    @JsonProperty ("nome_file_rc")
    @JsonPropertyDescription ("")
    private String nomeFileRC;

    @JsonProperty ("data_test")
    public Date getDataTest() {
        return dataTest;
    }

    @JsonProperty ("data_test")
    public void setDataTest(Date dataTest) {
        this.dataTest = dataTest;
    }

    @JsonProperty ("identificativo_sdi")
    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    @JsonProperty ("identificativo_sdi")
    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    @JsonProperty ("nome_file")
    public String getNomeFile() {
        return nomeFile;
    }

    @JsonProperty ("nome_file")
    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    @JsonProperty ("ricevuta_comunicazione")
    public String getRicevutaComunicazione() {
        return ricevutaComunicazione;
    }

    @JsonProperty ("ricevuta_comunicazione")
    public void setRicevutaComunicazione(String ricevutaComunicazione) {
        this.ricevutaComunicazione = ricevutaComunicazione;
    }

    @JsonProperty ("fattura_attiva")
    public Boolean getFatturaAttiva() {
        return fatturaAttiva;
    }

    @JsonProperty ("fattura_attiva")
    public void setFatturaAttiva(Boolean fatturaAttiva) {
        this.fatturaAttiva = fatturaAttiva;
    }

    @JsonProperty ("ricevuta_consegna")
    public Boolean getRicevutaConsegna() {
        return ricevutaConsegna;
    }

    @JsonProperty ("ricevuta_consegna")
    public void setRicevutaConsegna(Boolean ricevutaConsegna) {
        this.ricevutaConsegna = ricevutaConsegna;
    }

    @JsonProperty ("nome_file_rc")
    public String getNomeFileRC() {
        return nomeFileRC;
    }

    @JsonProperty ("nome_file_rc")
    public void setNomeFileRC(String nomeFileRC) {
        this.nomeFileRC = nomeFileRC;
    }
}
