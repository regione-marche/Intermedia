package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
    "data_test",
    "identificativo_sdi",
    "username",
    "nome_file",
    "invio_unico",
    "protocollo",
    "registrazione",
    "esito_committente",
    "data_esito_committente",
    "nome_file_esito_committente"
})
public class PCTestDto {

    @JsonProperty ("data_test")
    @JsonPropertyDescription ("")
    private Date dataTest;

    @JsonProperty ("identificativo_sdi")
    @JsonPropertyDescription ("")
    private String identificativoSdi;

    @JsonProperty ("username")
    @JsonPropertyDescription ("")
    private String username;

    @JsonProperty ("nome_file")
    @JsonPropertyDescription ("")
    private String nomeFile;

    @JsonProperty ("invio_unico")
    @JsonPropertyDescription ("")
    private Boolean invioUnico;

    @JsonProperty ("protocollo")
    @JsonPropertyDescription ("")
    private Boolean protocollo;

    @JsonProperty ("registrazione")
    @JsonPropertyDescription ("")
    private Boolean registrazione;

    @JsonProperty ("esito_committente")
    @JsonPropertyDescription ("")
    private Boolean esitoCommittente;

    @JsonProperty ("data_esito_committente")
    @JsonPropertyDescription ("")
    private Date dataEsitoCommittente;

    @JsonProperty ("nome_file_esito_committente")
    @JsonPropertyDescription ("")
    private String nomeFileEsitoCommittente;

    @JsonProperty ("data_test")
    public Date getDataTest() {
        return dataTest;
    }

    public PCTestDto(){
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

    @JsonProperty ("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty ("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty ("nome_file")
    public String getNomeFile() {
        return nomeFile;
    }

    @JsonProperty ("nome_file")
    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    @JsonProperty ("esito_committente")
    public Boolean isEsitoCommittente() {
        return esitoCommittente;
    }

    @JsonProperty ("esito_committente")
    public void setEsitoCommittente(Boolean esitoCommittente) {
        this.esitoCommittente = esitoCommittente;
    }

    @JsonProperty ("registrazione")
    public Boolean isRegistrazione() {
        return registrazione;
    }

    @JsonProperty ("registrazione")
    public void setRegistrazione(Boolean registrazione) {
        this.registrazione = registrazione;
    }

    @JsonProperty ("protocollo")
    public Boolean isProtocollo() {
        return protocollo;
    }

    @JsonProperty ("protocollo")
    public void setProtocollo(Boolean protocollo) {
        this.protocollo = protocollo;
    }

    @JsonProperty ("invio_unico")
    public Boolean isInvioUnico() {
        return invioUnico;
    }

    @JsonProperty ("invio_unico")
    public void setInvioUnico(Boolean invioUnico) {
        this.invioUnico = invioUnico;
    }

    @JsonProperty ("data_esito_committente")
    public Date getDataEsitoCommittente() {
        return dataEsitoCommittente;
    }

    @JsonProperty ("data_esito_committente")
    public void setDataEsitoCommittente(Date dataEsitoCommittente) {
        this.dataEsitoCommittente = dataEsitoCommittente;
    }

    @JsonProperty ("nome_file_esito_committente")
    public String getNomeFileEsitoCommittente() {
        return nomeFileEsitoCommittente;
    }

    @JsonProperty ("nome_file_esito_committente")
    public void setNomeFileEsitoCommittente(String nomeFileEsitoCommittente) {
        this.nomeFileEsitoCommittente = nomeFileEsitoCommittente;
    }
}
