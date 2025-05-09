
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Anagrafica Ente: Response configurazione dell'ente
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nome",
    "nome_ufficio_fatturazione",
    "id_fiscale_committente",
    "codice_ufficio",
    "email_supporto",
    "tipo_canale",
    "ciclo_attivo",
    "ciclo_passivo",
    "campi_opzionali",
    "username_utente",
    "ambiente_cicloPassivo",
    "ambiente_cicloAttivo",
    "id_ente"
})
public class EntitiesRequestPut {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome")
    @JsonPropertyDescription("")
    private String nome;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_ufficio_fatturazione")
    @JsonPropertyDescription("")
    private String nomeUfficioFatturazione;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_fiscale_committente")
    @JsonPropertyDescription("")
    private String idFiscaleCommittente;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio")
    @JsonPropertyDescription("")
    private String codiceUfficio;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("email_supporto")
    @JsonPropertyDescription("")
    private String emailSupporto;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale")
    @JsonPropertyDescription("")
    private String tipoCanale;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ciclo_attivo")
    @JsonPropertyDescription("")
    private Boolean cicloAttivo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ciclo_passivo")
    @JsonPropertyDescription("")
    private Boolean cicloPassivo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("campi_opzionali")
    @JsonPropertyDescription("")
    private Boolean campiOpzionali;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("username_utente")
    @JsonPropertyDescription("")
    private String usernameUtente;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloPassivo")
    @JsonPropertyDescription("")
    private String ambienteCicloPassivo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloAttivo")
    @JsonPropertyDescription("")
    private String ambienteCicloAttivo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_ente")
    @JsonPropertyDescription("")
    private Integer idEnte;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_ufficio_fatturazione")
    public String getNomeUfficioFatturazione() {
        return nomeUfficioFatturazione;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_ufficio_fatturazione")
    public void setNomeUfficioFatturazione(String nomeUfficioFatturazione) {
        this.nomeUfficioFatturazione = nomeUfficioFatturazione;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_fiscale_committente")
    public String getIdFiscaleCommittente() {
        return idFiscaleCommittente;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_fiscale_committente")
    public void setIdFiscaleCommittente(String idFiscaleCommittente) {
        this.idFiscaleCommittente = idFiscaleCommittente;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio")
    public String getCodiceUfficio() {
        return codiceUfficio;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio")
    public void setCodiceUfficio(String codiceUfficio) {
        this.codiceUfficio = codiceUfficio;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("email_supporto")
    public String getEmailSupporto() {
        return emailSupporto;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("email_supporto")
    public void setEmailSupporto(String emailSupporto) {
        this.emailSupporto = emailSupporto;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale")
    public String getTipoCanale() {
        return tipoCanale;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale")
    public void setTipoCanale(String tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ciclo_attivo")
    public Boolean getCicloAttivo() {
        return cicloAttivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ciclo_attivo")
    public void setCicloAttivo(Boolean cicloAttivo) {
        this.cicloAttivo = cicloAttivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ciclo_passivo")
    public Boolean getCicloPassivo() {
        return cicloPassivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ciclo_passivo")
    public void setCicloPassivo(Boolean cicloPassivo) {
        this.cicloPassivo = cicloPassivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("campi_opzionali")
    public Boolean getCampiOpzionali() {
        return campiOpzionali;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("campi_opzionali")
    public void setCampiOpzionali(Boolean campiOpzionali) {
        this.campiOpzionali = campiOpzionali;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloPassivo")
    public String getAmbienteCicloPassivo() {
        return ambienteCicloPassivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloPassivo")
    public void setAmbienteCicloPassivo(String ambienteCicloPassivo) {
        this.ambienteCicloPassivo = ambienteCicloPassivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloAttivo")
    public String getAmbienteCicloAttivo() {
        return ambienteCicloAttivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloAttivo")
    public void setAmbienteCicloAttivo(String ambienteCicloAttivo) {
        this.ambienteCicloAttivo = ambienteCicloAttivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_ente")
    public Integer getIdEnte() {
        return idEnte;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_ente")
    public void setIdEnte(Integer idEnte) {
        this.idEnte = idEnte;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("username_utente")
    public String getUsernameUtente() {
        return usernameUtente;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("username_utente")
    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }
}
