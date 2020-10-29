
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Ciclo Passivo: Request configurazione dell'integrazione Sistema di Registrazione
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tipo_canale_ca",
    "endpoint",
    "porta",
    "user",
    "password",
    "username_utente",
    "ambiente_cicloPassivo",
    "certificato"
})
public class PCregistrationRequest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale_ca")
    @JsonPropertyDescription("")
    private String tipoCanaleCa;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpoint")
    @JsonPropertyDescription("")
    private String endpoint;
    /**
     * 
     * 
     */
    @JsonProperty("porta")
    @JsonPropertyDescription("")
    private Integer porta;
    /**
     * 
     * 
     */
    @JsonProperty("user")
    @JsonPropertyDescription("")
    private String user;
    /**
     * 
     * 
     */
    @JsonProperty("password")
    @JsonPropertyDescription("")
    private String password;
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
    @JsonProperty("certificato")
    @JsonPropertyDescription("")
    private boolean certificato;
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
    @JsonProperty("tipo_canale_ca")
    public String getTipoCanaleCa() {
        return tipoCanaleCa;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale_ca")
    public void setTipoCanaleCa(String tipoCanaleCa) {
        this.tipoCanaleCa = tipoCanaleCa;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpoint")
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 
     * 
     */
    @JsonProperty("porta")
    public Integer getPorta() {
        return porta;
    }

    /**
     * 
     * 
     */
    @JsonProperty("porta")
    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    /**
     * 
     * 
     */
    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    /**
     * 
     * 
     */
    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 
     * 
     */
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    /**
     * 
     * 
     */
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
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

    public boolean getCertificato() {
        return certificato;
    }

    public void setCertificato(boolean certificato) {
        this.certificato = certificato;
    }
}
