
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Dettaglio dei test in corso del ciclo attivo/ciclo passivo
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tipo_test",
    "identificativo_sdi"
})
public class TestInCorsoDetail {

    /**
     * 
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
     * 
     */
    @JsonProperty("tipo_test")
    public String getTipoTest() {
        return tipoTest;
    }

    /**
     * 
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
