
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;


/**
 * Ricerca Fatture Ciclo Attivo: dettaglio del flusso del file fattura/notifiche
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "stato",
    "notifica"
})
public class InvoiceFlow {

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
    @JsonProperty("notifica")
    @JsonPropertyDescription("")
    private String notifica;

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
    @JsonProperty("notifica")
    public String getNotifica() {
        return notifica;
    }

    /**
     * 
     * 
     */
    @JsonProperty("notifica")
    public void setNotifica(String notifica) {
        this.notifica = notifica;
    }

}
