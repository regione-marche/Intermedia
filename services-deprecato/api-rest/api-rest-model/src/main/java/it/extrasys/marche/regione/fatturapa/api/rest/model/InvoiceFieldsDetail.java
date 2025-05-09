
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Fattura PA: Campi file fattura
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id_campo",
    "nome_tag_xml"
})
public class InvoiceFieldsDetail {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_campo")
    @JsonPropertyDescription("")
    private Integer idCampo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_tag_xml")
    @JsonPropertyDescription("")
    private String nomeTagXml;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_campo")
    public Integer getIdCampo() {
        return idCampo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id_campo")
    public void setIdCampo(Integer idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_tag_xml")
    public String getNomeTagXml() {
        return nomeTagXml;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_tag_xml")
    public void setNomeTagXml(String nomeTagXml) {
        this.nomeTagXml = nomeTagXml;
    }

}
