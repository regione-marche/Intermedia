package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "count"
})
public class WarningStatiFatturaCountResponse {

    @JsonProperty("count")
    @JsonPropertyDescription("")
    private Integer count;

    @JsonProperty("count")
    @JsonPropertyDescription("")
    public Integer getCount() {
        return count;
    }

    @JsonProperty("count")
    @JsonPropertyDescription("")
    public void setCount(Integer count) {
        this.count = count;
    }
}
