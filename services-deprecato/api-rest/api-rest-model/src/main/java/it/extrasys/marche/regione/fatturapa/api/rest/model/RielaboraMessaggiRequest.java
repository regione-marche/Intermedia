package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "nome_coda",
        "ciclo"
})
public class RielaboraMessaggiRequest {

    /**
     * (Required)
     */
    @JsonProperty("nome_coda")
    @JsonPropertyDescription("La lista delle code da rielaborare")
    private List<String> nomeCoda = new ArrayList<String>();

    /**
     * (Required)
     */
    @JsonProperty("ciclo")
    @JsonPropertyDescription("Se monitoraggio del ciclo passivo o attivo")
    private String ciclo;

    @JsonProperty("nome_coda")
    public List<String> getNomeCoda() {
        return nomeCoda;
    }

    @JsonProperty("nome_coda")
    public void setNomeCoda(List<String> nomeCoda) {
        this.nomeCoda = nomeCoda;
    }

    @JsonProperty("ciclo")
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
}
