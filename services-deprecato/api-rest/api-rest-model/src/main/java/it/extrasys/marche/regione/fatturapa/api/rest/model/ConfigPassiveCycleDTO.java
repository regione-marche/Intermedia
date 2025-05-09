package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "invio_unico",
        "tipo_esito_committente",
        "tipo_protocollo",
        "tipo_registrazione"
})
public class ConfigPassiveCycleDTO {

    @JsonProperty ("invio_unico")
    @JsonPropertyDescription ("")
    private boolean invioUnico;

    @JsonProperty ("tipo_esito_committente")
    @JsonPropertyDescription ("")
    private String tipoEsitoCommittente;

    @JsonProperty ("tipo_protocollo")
    @JsonPropertyDescription ("")
    private String tipoProtocollo;

    @JsonProperty ("tipo_registrazione")
    @JsonPropertyDescription ("")
    private String tipoRegistrazione;

    public ConfigPassiveCycleDTO(){
        this.invioUnico = false;
    }

    @JsonProperty ("invio_unico")
    public boolean isInvioUnico() {
        return invioUnico;
    }

    @JsonProperty ("invio_unico")
    public void setInvioUnico(boolean invioUnico) {
        this.invioUnico = invioUnico;
    }

    @JsonProperty ("tipo_registrazione")
    public String getTipoRegistrazione() {
        return tipoRegistrazione;
    }

    @JsonProperty ("tipo_registrazione")
    public void setTipoRegistrazione(String tipoRegistrazione) {
        this.tipoRegistrazione = tipoRegistrazione;
    }

    @JsonProperty ("tipo_esito_committente")
    public String getTipoEsitoCommittente() {
        return tipoEsitoCommittente;
    }

    @JsonProperty ("tipo_esito_committente")
    public void setTipoEsitoCommittente(String tipoEsitoCommittente) {
        this.tipoEsitoCommittente = tipoEsitoCommittente;
    }

    @JsonProperty ("tipo_protocollo")
    public String getTipoProtocollo() {
        return tipoProtocollo;
    }

    @JsonProperty ("tipo_protocollo")
    public void setTipoProtocollo(String tipoProtocollo) {
        this.tipoProtocollo = tipoProtocollo;
    }
}
