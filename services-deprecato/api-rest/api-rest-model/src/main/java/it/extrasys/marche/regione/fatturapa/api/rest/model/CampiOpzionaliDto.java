package it.extrasys.marche.regione.fatturapa.api.rest.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder ({
        "campi"
})
public class CampiOpzionaliDto {

    @JsonProperty ("campi")
    @JsonPropertyDescription ("")
    private List<String> campi;

    @JsonProperty ("campi")
    public List<String> getCampi() {
        return campi;
    }

    @JsonProperty ("campi")
    public void setCampi(List<String> campi) {
        this.campi = campi;
    }
}
