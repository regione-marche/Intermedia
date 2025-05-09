package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "contenuto",
        "nomeReport"
})
public class MonitoraggioReportResponse {

    @JsonProperty("contenuto")
    @JsonPropertyDescription("")
    private String contenuto;

    @JsonProperty("nomeReport")
    @JsonPropertyDescription("")
    private String nomeReport;

    @JsonProperty("contenuto")
    @JsonPropertyDescription("")
    public String getContenuto() {
        return contenuto;
    }

    @JsonProperty("contenuto")
    @JsonPropertyDescription("")
    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    @JsonProperty("nomeReport")
    @JsonPropertyDescription("")
    public String getNomeReport() {
        return nomeReport;
    }

    @JsonProperty("nomeReport")
    @JsonPropertyDescription("")
    public void setNomeReport(String nomeReport) {
        this.nomeReport = nomeReport;
    }
}
