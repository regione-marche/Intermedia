package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "warning_stati_fattura_response_list"
})
public class WarningStatiFatturaResponseList {

    /**
     * (Required)
     */
    @JsonProperty("warning_stati_fattura_response")
    @JsonPropertyDescription("")
    List<WarningStatiFattureResponse> warningStatiFattureResponseList;

    @JsonProperty("warning_stati_fattura_response")
    public List<WarningStatiFattureResponse> getWarningStatiFattureResponseList() {
        return warningStatiFattureResponseList;
    }

    @JsonProperty("warning_stati_fattura_response")
    public void setWarningStatiFattureResponseList(List<WarningStatiFattureResponse> warningStatiFattureResponseList) {
        this.warningStatiFattureResponseList = warningStatiFattureResponseList;
    }
}
