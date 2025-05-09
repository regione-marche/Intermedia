
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Passaggio in Produzione: Request
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "copia_dati",
    "username_utente"
})
public class ProductionRequest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("copia_dati")
    @JsonPropertyDescription("")
    private Boolean copiaDati;
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
    @JsonProperty("copia_dati")
    public Boolean getCopiaDati() {
        return copiaDati;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("copia_dati")
    public void setCopiaDati(Boolean copiaDati) {
        this.copiaDati = copiaDati;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_utente")
    public String getUsernameUtente() {
        return usernameUtente;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_utente")
    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }

}
