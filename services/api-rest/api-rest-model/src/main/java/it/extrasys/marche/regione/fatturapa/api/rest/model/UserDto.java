package it.extrasys.marche.regione.fatturapa.api.rest.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "username",
        "password",
        "nome",
        "cognome",
        "codici_ufficio",
        "ruolo"
})
public class UserDto {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("username")
    @JsonPropertyDescription("")
    private String username;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("password")
    @JsonPropertyDescription("")
    private String password;

    @JsonProperty("nome")
    @JsonPropertyDescription("")
    private String nome;

    @JsonProperty("cognome")
    @JsonPropertyDescription("")
    private String cognome;

    @JsonProperty("codici_ufficio")
    @JsonPropertyDescription("")
    private List<String> codiciUfficio;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("ruolo")
    @JsonPropertyDescription("")
    private String ruolo;

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("ruolo")
    public String getRuolo() {
        return ruolo;
    }

    @JsonProperty("ruolo")
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonProperty("cognome")
    public String getCognome() {
        return cognome;
    }

    @JsonProperty("cognome")
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @JsonProperty("codici_ufficio")
    public List<String> getCodiciUfficio() {
        return codiciUfficio;
    }

    @JsonProperty("codici_ufficio")
    public void setCodiciUfficio(List<String> codiciUfficio) {
        this.codiciUfficio = codiciUfficio;
    }
}
