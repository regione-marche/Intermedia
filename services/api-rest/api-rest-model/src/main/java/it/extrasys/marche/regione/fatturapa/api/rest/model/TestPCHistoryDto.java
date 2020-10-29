package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
    "codice_ufficio",
    "lista_test"
})
public class TestPCHistoryDto {

    @JsonProperty ("codice_ufficio")
    @JsonPropertyDescription ("")
    private String codiceUfficio;

    @JsonProperty ("lista_test")
    @JsonPropertyDescription ("")
    private List<PCTestDto> listaTest;

    @JsonProperty ("codice_ufficio")
    public String getCodiceUfficio() {
        return codiceUfficio;
    }

    @JsonProperty ("codice_ufficio")
    public void setCodiceUfficio(String codiceUfficio) {
        this.codiceUfficio = codiceUfficio;
    }

    @JsonProperty ("lista_test")
    public List<PCTestDto> getListaTest() {
        return listaTest;
    }

    @JsonProperty ("lista_test")
    public void setListaTest(List<PCTestDto> listaTest) {
        this.listaTest = listaTest;
    }
}
