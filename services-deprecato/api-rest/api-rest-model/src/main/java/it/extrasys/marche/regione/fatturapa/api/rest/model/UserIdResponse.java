
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;


/**
 * Anagrafica Utente: Response recupero Identificativo dell'utente
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id"
})
public class UserIdResponse {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    private BigInteger id;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("id")
    public void setId(BigInteger id) {
        this.id = id;
    }

}
