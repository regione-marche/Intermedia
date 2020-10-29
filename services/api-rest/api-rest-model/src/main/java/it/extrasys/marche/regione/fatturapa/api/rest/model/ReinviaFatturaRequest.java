package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public class ReinviaFatturaRequest {

    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    private List<String> identificativoSdi;

    @JsonProperty("only_registrazione")
    @JsonPropertyDescription("Se TRUE effettua solo la registrazione")
    private Boolean onlyRegistrazione;

    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    public List<String> getIdentificativoSdi() {
        return identificativoSdi;
    }

    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    public void setIdentificativoSdi(List<String> identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    @JsonProperty("only_registrazione")
    @JsonPropertyDescription("Se TRUE effettua solo la registrazione")
    public Boolean getOnlyRegistrazione() {
        return onlyRegistrazione;
    }

    @JsonProperty("only_registrazione")
    @JsonPropertyDescription("Se TRUE effettua solo la registrazione")
    public void setOnlyRegistrazione(Boolean onlyRegistrazione) {
        this.onlyRegistrazione = onlyRegistrazione;
    }
}
