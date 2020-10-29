
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;


/**
 * Anagrafica Ente: Response recupero Identificativo dell'ente
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id_ente"
})
public class EntitiesIdResponse {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_ente")
    @JsonPropertyDescription("")
    private BigInteger idEnte;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_ente")
    public BigInteger getIdEnte() {
        return idEnte;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_ente")
    public void setIdEnte(BigInteger idEnte) {
        this.idEnte = idEnte;
    }

}
