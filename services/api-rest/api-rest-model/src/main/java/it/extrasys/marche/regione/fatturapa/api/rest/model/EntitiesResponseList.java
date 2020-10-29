package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "entities_response_list"
})
public class EntitiesResponseList {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("entities_response_list")
    @JsonPropertyDescription("")
    private List<EntitiesResponse> entitiesResponseList;

    public EntitiesResponseList(){
        this.entitiesResponseList = new ArrayList<>();
    }

    @JsonProperty("entities_response_list")
    public List<EntitiesResponse> getEntitiesResponseList() {
        return entitiesResponseList;
    }

    @JsonProperty("entities_response_list")
    public void setEntitiesResponseList(List<EntitiesResponse> entitiesResponseList) {
        this.entitiesResponseList = entitiesResponseList;
    }

    public void addElement2List(EntitiesResponse entitiesResponse){
        this.entitiesResponseList.add(entitiesResponse);
    }
}
