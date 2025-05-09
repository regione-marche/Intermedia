package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "identificativo_sdi",
        "data_ultimo_stato",
        "codice_ufficio_destinatario",
        "data_creazione",
        "nome_file",
        "tipo_canale",
        "stato",
        "flag"
})
public class WarningStatiFattureResponse {

    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    private String identificativoSdi;

    @JsonProperty("data_ultimo_stato")
    @JsonPropertyDescription("")
    private Date dataUltimoStato;

    @JsonProperty("codice_ufficio_destinatario")
    @JsonPropertyDescription("")
    private String codiceUfficioDestinatario;

    @JsonProperty("data_creazione")
    @JsonPropertyDescription("")
    private Date dataCreazione;

    @JsonProperty("nome_file")
    @JsonPropertyDescription("")
    private String nomeFile;

    @JsonProperty("tipo_canale")
    @JsonPropertyDescription("")
    private String tipoCanale;

    @JsonProperty("stato")
    @JsonPropertyDescription("")
    private String stato;

    @JsonProperty("flag")
    @JsonPropertyDescription("")
    private String flag;


    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    @JsonProperty("data_ultimo_stato")
    public Date getDataUltimoStato() {
        return dataUltimoStato;
    }

    public void setDataUltimoStato(Date dataUltimoStato) {
        this.dataUltimoStato = dataUltimoStato;
    }

    @JsonProperty("data_creazione")
    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    @JsonProperty("codice_ufficio_destinatario")
    @JsonPropertyDescription("")
    public String getCodiceUfficioDestinatario() {
        return codiceUfficioDestinatario;
    }

    public void setCodiceUfficioDestinatario(String codiceUfficioDestinatario) {
        this.codiceUfficioDestinatario = codiceUfficioDestinatario;
    }

    @JsonProperty("nome_file")
    @JsonPropertyDescription("")
    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    @JsonProperty("tipo_canale")
    @JsonPropertyDescription("")
    public String getTipoCanale() {
        return tipoCanale;
    }

    public void setTipoCanale(String tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    @JsonProperty("stato")
    @JsonPropertyDescription("")
    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @JsonProperty("flag")
    @JsonPropertyDescription("")
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
