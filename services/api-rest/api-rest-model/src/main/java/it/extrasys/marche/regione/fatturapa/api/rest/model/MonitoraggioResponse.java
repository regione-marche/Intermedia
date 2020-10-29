package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "nome_coda",
        "label_coda",
        "numero_messaggi",
        "tipo_canale"
})
public class MonitoraggioResponse {

    /**
     * (Required)
     */
    @JsonProperty("nome_coda")
    @JsonPropertyDescription("Il nome della coda da restituire in caso di rielaborazione")
    private String nomeCoda;

    /**
     * (Required)
     */
    @JsonProperty("label_coda")
    @JsonPropertyDescription("Il nome della coda da mostrare nel cruscotto")
    private String labelCoda;

    /**
     * (Required)
     */
    @JsonProperty("numero_messaggi")
    @JsonPropertyDescription("")
    private Integer numeroMessaggi;

    /**
     * (Required)
     */
    @JsonProperty("tipo_canale")
    @JsonPropertyDescription("")
    private String tipoCanale;


    @JsonProperty("nome_coda")
    public String getNomeCoda() {
        return nomeCoda;
    }

    @JsonProperty("nome_coda")
    public void setNomeCoda(String nomeCoda) {
        this.nomeCoda = nomeCoda;
    }

    @JsonProperty("numero_messaggi")
    public Integer getNumeroMessaggi() {
        return numeroMessaggi;
    }

    @JsonProperty("numero_messaggi")
    public void setNumeroMessaggi(Integer numeroMessaggi) {
        this.numeroMessaggi = numeroMessaggi;
    }

    @JsonProperty("tipo_canale")
    public String getTipoCanale() {
        return tipoCanale;
    }

    @JsonProperty("tipo_canale")
    public void setTipoCanale(String tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    @JsonProperty("label_coda")
    public String getLabelCoda() {
        return labelCoda;
    }

    @JsonProperty("label_coda")
    public void setLabelCoda(String labelCoda) {
        this.labelCoda = labelCoda;
    }
}
