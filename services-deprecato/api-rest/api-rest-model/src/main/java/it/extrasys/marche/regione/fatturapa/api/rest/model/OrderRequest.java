package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "orderBy",
        "ordering",
        "numberOfElements",
        "pageNumber"
})
public class OrderRequest {

    @JsonProperty("orderBy")
    @JsonPropertyDescription("")
    private String orderBy;

    @JsonProperty("ordering")
    @JsonPropertyDescription("")
    private String ordering;

    @JsonProperty("numberOfElements")
    @JsonPropertyDescription("")
    private Integer numberOfElements;

    @JsonProperty("pageNumber")
    @JsonPropertyDescription("")
    private Integer pageNumber;

    @JsonProperty("orderBy")
    @JsonPropertyDescription("")
    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @JsonProperty("ordering")
    @JsonPropertyDescription("")
    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    @JsonProperty("numberOfElements")
    @JsonPropertyDescription("")
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    @JsonProperty("pageNumber")
    @JsonPropertyDescription("")
    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
