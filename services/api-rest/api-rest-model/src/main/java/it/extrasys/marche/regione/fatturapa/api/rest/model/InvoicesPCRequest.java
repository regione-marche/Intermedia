
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.Date;


/**
 * Ricerca Fatture Ciclo Passivo: Request della ricerca
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identificativo_sdi",
    "nome_file_fattura",
    "segnatura_protocollo",
    "codice_ufficio_destinatario",
    "data_ricezione_da",
    "data_ricezione_a",
    "order_by",
    "ordering",
    "number_of_elements",
    "page_number"
})
public class InvoicesPCRequest {

    /**
     * 
     * 
     */
    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    private String identificativoSdi;
    /**
     * 
     * 
     */
    @JsonProperty("nome_file_fattura")
    @JsonPropertyDescription("")
    private String nomeFileFattura;
    /**
     * 
     * 
     */
    @JsonProperty("segnatura_protocollo")
    @JsonPropertyDescription("")
    private String segnaturaProtocollo;
    /**
     * 
     * 
     */
    @JsonProperty("codice_ufficio_destinatario")
    @JsonPropertyDescription("")
    private String codiceUfficioDestinatario;
    /**
     * 
     * 
     */
    @JsonProperty("data_ricezione_da")
    @JsonPropertyDescription("")
    private Date dataRicezioneDa;
    /**
     * 
     * 
     */
    @JsonProperty("data_ricezione_a")
    @JsonPropertyDescription("")
    private Date dataRicezioneA;

    /**
     *
     *
     */
    @JsonProperty("order_by")
    @JsonPropertyDescription("")
    private String orderBy;

    /**
     *
     *
     */
    @JsonProperty("ordering")
    @JsonPropertyDescription("")
    private String ordering;

    /**
     *
     *
     */
    @JsonProperty("number_of_elements")
    @JsonPropertyDescription("")
    private Integer numberOfElements;

    /**
     *
     *
     */
    @JsonProperty("page_number")
    @JsonPropertyDescription("")
    private Integer pageNumber;

    /**
     * 
     * 
     */
    @JsonProperty("identificativo_sdi")
    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    /**
     * 
     * 
     */
    @JsonProperty("identificativo_sdi")
    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    /**
     * 
     * 
     */
    @JsonProperty("nome_file_fattura")
    public String getNomeFileFattura() {
        return nomeFileFattura;
    }

    /**
     * 
     * 
     */
    @JsonProperty("nome_file_fattura")
    public void setNomeFileFattura(String nomeFileFattura) {
        this.nomeFileFattura = nomeFileFattura;
    }

    /**
     * 
     * 
     */
    @JsonProperty("segnatura_protocollo")
    public String getSegnaturaProtocollo() {
        return segnaturaProtocollo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("segnatura_protocollo")
    public void setSegnaturaProtocollo(String segnaturaProtocollo) {
        this.segnaturaProtocollo = segnaturaProtocollo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_ufficio_destinatario")
    public String getCodiceUfficioDestinatario() {
        return codiceUfficioDestinatario;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_ufficio_destinatario")
    public void setCodiceUfficioDestinatario(String codiceUfficioDestinatario) {
        this.codiceUfficioDestinatario = codiceUfficioDestinatario;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_ricezione_da")
    public Date getDataRicezioneDa() {
        return dataRicezioneDa;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_ricezione_da")
    public void setDataRicezioneDa(Date dataRicezioneDa) {
        this.dataRicezioneDa = dataRicezioneDa;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_ricezione_a")
    public Date getDataRicezioneA() {
        return dataRicezioneA;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_ricezione_a")
    public void setDataRicezioneA(Date dataRicezioneA) {
        this.dataRicezioneA = dataRicezioneA;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
