
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Test Ciclo Attivo: Dettaglio dello storico dei test
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fase",
    "tipo_canale_ca",
    "nome_file",
    "data_fine_test",
    "tipo_test",
    "stato_test",
    "identificativo_sdi",
    "id_test_ciclo_attivo"
})
public class TestACHistoryDetail {

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
    @JsonProperty("tipo_canale_ca")
    @JsonPropertyDescription("")
    private String tipoCanaleCa;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_file")
    @JsonPropertyDescription("")
    private String nomeFile;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_fine_test")
    @JsonPropertyDescription("")
    private Date dataFineTest;
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
     * (Required)
     * 
     */
    @JsonProperty("stato_test")
    @JsonPropertyDescription("")
    private String statoTest;
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
    @JsonProperty("id_test_ciclo_attivo")
    @JsonPropertyDescription("")
    private Integer idTestCicloAttivo;

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
    @JsonProperty("tipo_canale_ca")
    public String getTipoCanaleCa() {
        return tipoCanaleCa;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale_ca")
    public void setTipoCanaleCa(String tipoCanaleCa) {
        this.tipoCanaleCa = tipoCanaleCa;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_file")
    public String getNomeFile() {
        return nomeFile;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_file")
    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_fine_test")
    public Date getDataFineTest() {
        return dataFineTest;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_fine_test")
    public void setDataFineTest(Date dataFineTest) {
        this.dataFineTest = dataFineTest;
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
     * (Required)
     * 
     */
    @JsonProperty("stato_test")
    public String getStatoTest() {
        return statoTest;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("stato_test")
    public void setStatoTest(String statoTest) {
        this.statoTest = statoTest;
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

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_test_ciclo_attivo")
    public Integer getIdTestCicloAttivo() {
        return idTestCicloAttivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_test_ciclo_attivo")
    public void setIdTestCicloAttivo(Integer idTestCicloAttivo) {
        this.idTestCicloAttivo = idTestCicloAttivo;
    }

}
