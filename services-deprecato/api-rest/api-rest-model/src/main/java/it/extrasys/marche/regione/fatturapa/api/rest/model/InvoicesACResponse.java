
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Ricerca Fatture Ciclo Attivo: Response della ricerca 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "results"
})
public class InvoicesACResponse {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("results")
    @JsonPropertyDescription("")
    private List<InvoicesACResult> results = new ArrayList<InvoicesACResult>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("results")
    public List<InvoicesACResult> getResults() {
        return results;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("results")
    public void setResults(List<InvoicesACResult> results) {
        this.results = results;
    }

}
