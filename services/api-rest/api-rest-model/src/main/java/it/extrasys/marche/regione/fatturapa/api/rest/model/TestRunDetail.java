
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Test Ciclo Attivo/Test Ciclo Passivo: Dettaglio test da eseguire
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fase",
    "tipo_test",
    "identificativo_sdi"
})
public class TestRunDetail {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("fase")
    @JsonPropertyDescription("")
    private String fase;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_test")
    @JsonPropertyDescription("")
    private String tipoTest;
    /**
     * 
     * 
     */
    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    private String identificativoSdi;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("fase")
    public String getFase() {
        return fase;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("fase")
    public void setFase(String fase) {
        this.fase = fase;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_test")
    public String getTipoTest() {
        return tipoTest;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_test")
    public void setTipoTest(String tipoTest) {
        this.tipoTest = tipoTest;
    }

    /**
     * 
     * 
     */
    @JsonProperty("identificativo_sdi")
    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    /**
     * 
     * 
     */
    @JsonProperty("identificativo_sdi")
    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

}
