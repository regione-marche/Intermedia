
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Test Ciclo Attivo/Ciclo Passivo: Test da eseguire
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "test",
    "id_utente"
})
public class TestRunRequest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("test")
    @JsonPropertyDescription("")
    private List<TestRunDetail> test = new ArrayList<TestRunDetail>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_utente")
    @JsonPropertyDescription("")
    private Integer idUtente;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("test")
    public List<TestRunDetail> getTest() {
        return test;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("test")
    public void setTest(List<TestRunDetail> test) {
        this.test = test;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_utente")
    public Integer getIdUtente() {
        return idUtente;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_utente")
    public void setIdUtente(Integer idUtente) {
        this.idUtente = idUtente;
    }

}
