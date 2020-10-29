package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "tipo_fattura_attiva",
        "tipo_notifica_attiva"
})
public class ConfigActiveCycleDTO {

    @JsonProperty ("tipo_fattura_attiva")
    @JsonPropertyDescription ("")
    private String tipoFatturaAttiva;

    @JsonProperty ("tipo_notifica_attiva")
    @JsonPropertyDescription ("")
    private String tipoNotificaAttiva;

    public ConfigActiveCycleDTO(){}

    @JsonProperty ("tipo_notifica_attiva")
    public String getTipoNotificaAttiva() {
        return tipoNotificaAttiva;
    }

    @JsonProperty ("tipo_notifica_attiva")
    public void setTipoNotificaAttiva(String tipoNotificaAttiva) {
        this.tipoNotificaAttiva = tipoNotificaAttiva;
    }

    @JsonProperty ("tipo_fattura_attiva")
    public String getTipoFatturaAttiva() {
        return tipoFatturaAttiva;
    }

    @JsonProperty ("tipo_fattura_attiva")
    public void setTipoFatturaAttiva(String tipoFatturaAttiva) {
        this.tipoFatturaAttiva = tipoFatturaAttiva;
    }
}
