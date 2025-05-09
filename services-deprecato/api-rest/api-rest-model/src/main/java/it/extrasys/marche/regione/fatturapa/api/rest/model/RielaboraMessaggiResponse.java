package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "esito",
  "messaggio"
})
public class RielaboraMessaggiResponse {
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("esito")
    @JsonPropertyDescription("")
    private String esito;

    @JsonProperty("messaggio")
    @JsonPropertyDescription("")
    private String messaggio;

    @JsonProperty("esito")
    public String getEsito() {
        return esito;
    }

    @JsonProperty("esito")
    public void setEsito(String esito) {
        this.esito = esito;
    }

    @JsonProperty("messaggio")
    public String getMessaggio() {
        return messaggio;
    }

    @JsonProperty("messaggio")
    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
