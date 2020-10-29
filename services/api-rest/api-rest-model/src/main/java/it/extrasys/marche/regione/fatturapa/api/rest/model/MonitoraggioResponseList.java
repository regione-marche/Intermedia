package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "monitoraggio_response_list"
})
public class MonitoraggioResponseList {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("monitoraggio_response_list")
    @JsonPropertyDescription("")
    List<MonitoraggioResponse> monitoraggioResponseList;

    @JsonProperty("monitoraggio_response_list")
    public List<MonitoraggioResponse> getMonitoraggioResponseList() {
        return monitoraggioResponseList;
    }

    @JsonProperty("monitoraggio_response_list")
    public void setMonitoraggioResponseList(List<MonitoraggioResponse> monitoraggioResponseList) {
        this.monitoraggioResponseList = monitoraggioResponseList;
    }
}
