
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Ricerca Fatture Ciclo Passivo: Response della ricerca 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "results"
})
public class InvoicesPCResponse {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("results")
    @JsonPropertyDescription("")
    private List<InvoicesPCResult> results = new ArrayList<InvoicesPCResult>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("results")
    public List<InvoicesPCResult> getResults() {
        return results;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("results")
    public void setResults(List<InvoicesPCResult> results) {
        this.results = results;
    }

}
