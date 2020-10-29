package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;


/**
 * Ricerca Fatture Ciclo Passivo: dettaglio del flusso del file fattura/notifiche
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "stato",
        "numero_fattura"
})
public class InvoiceFlowPC {

    /**
     *
     *
     */
    @JsonProperty("data")
    @JsonPropertyDescription("")
    private Date data;
    /**
     *
     *
     */
    @JsonProperty("stato")
    @JsonPropertyDescription("")
    private String stato;
    /**
     *
     *
     */
    @JsonProperty("numero_fattura")
    @JsonPropertyDescription("")
    private String numeroFattura;

    /**
     *
     *
     */
    @JsonProperty("data")
    public Date getData() {
        return data;
    }

    /**
     *
     *
     */
    @JsonProperty("data")
    public void setData(Date data) {
        this.data = data;
    }

    /**
     *
     *
     */
    @JsonProperty("stato")
    public String getStato() {
        return stato;
    }

    /**
     *
     *
     */
    @JsonProperty("stato")
    public void setStato(String stato) {
        this.stato = stato;
    }

    /**
     *
     *
     */
    @JsonProperty("numero_fattura")
    public String getNumeroFattura() {
        return numeroFattura;
    }

    /**
     *
     *
     */
    @JsonProperty("numero_fattura")
    public void setNumeroFattura(String numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

}
