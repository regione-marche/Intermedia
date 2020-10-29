package it.extrasys.marche.regione.fatturapa.api.rest.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "username_servizio",
        "password_servizio",
        "username_utente"
})
public class UserTokenRequest {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("username_servizio")
    @JsonPropertyDescription("")
    private String usernameServizio;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("password_servizio")
    @JsonPropertyDescription("")
    private String passwordServizio;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("username_utente")
    @JsonPropertyDescription("")
    private String usernameUtente;

    @JsonProperty("username_servizio")
    public String getUsernameServizio() {
        return usernameServizio;
    }

    @JsonProperty("username_servizio")
    public void setUsernameServizio(String usernameServizio) {
        this.usernameServizio = usernameServizio;
    }

    @JsonProperty("password_servizio")
    public String getPasswordServizio() {
        return passwordServizio;
    }

    @JsonProperty("password_servizio")
    public void setPasswordServizio(String passwordServizio) {
        this.passwordServizio = passwordServizio;
    }

    @JsonProperty("username_utente")
    public String getUsernameUtente() {
        return usernameUtente;
    }

    @JsonProperty("username_utente")
    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }
}
