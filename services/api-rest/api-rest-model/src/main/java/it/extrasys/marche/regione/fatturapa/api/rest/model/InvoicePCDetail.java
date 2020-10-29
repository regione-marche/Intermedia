
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Ricerca Fatture Ciclo Passivo: Response di dettaglio della fattura ricercata
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identificativo_sdi",
    "nome_file_fattura",
    "segnatura_protocollo",
    "codice_ufficio_destinatario",
    "data_ricezione",
    "data_decorrenza_termini",
    "flusso_semplificato",
    "tipo_canale",
    "InvoiceFlow"
})
public class InvoicePCDetail {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("identificativo_sdi")
    @JsonPropertyDescription("")
    private String identificativoSdi;
    /**
     * 
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio_destinatario")
    @JsonPropertyDescription("")
    private String codiceUfficioDestinatario;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_ricezione")
    @JsonPropertyDescription("")
    private Date dataRicezione;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_decorrenza_termini")
    @JsonPropertyDescription("")
    private Date dataDecorrenzaTermini;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("flusso_semplificato")
    @JsonPropertyDescription("")
    private Boolean flussoSemplificato;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale")
    @JsonPropertyDescription("")
    private String tipoCanale;
    /**
     * 
     * 
     */
    @JsonProperty("InvoiceFlow")
    @JsonPropertyDescription("")
    private List<InvoiceFlowPC> invoiceFlow = new ArrayList<InvoiceFlowPC>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("identificativo_sdi")
    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("identificativo_sdi")
    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nome_file_fattura")
    public String getNomeFileFattura() {
        return nomeFileFattura;
    }

    /**
     * 
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio_destinatario")
    public String getCodiceUfficioDestinatario() {
        return codiceUfficioDestinatario;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio_destinatario")
    public void setCodiceUfficioDestinatario(String codiceUfficioDestinatario) {
        this.codiceUfficioDestinatario = codiceUfficioDestinatario;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_ricezione")
    public Date getDataRicezione() {
        return dataRicezione;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_ricezione")
    public void setDataRicezione(Date dataRicezione) {
        this.dataRicezione = dataRicezione;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_decorrenza_termini")
    public Date getDataDecorrenzaTermini() {
        return dataDecorrenzaTermini;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_decorrenza_termini")
    public void setDataDecorrenzaTermini(Date dataDecorrenzaTermini) {
        this.dataDecorrenzaTermini = dataDecorrenzaTermini;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("flusso_semplificato")
    public Boolean getFlussoSemplificato() {
        return flussoSemplificato;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("flusso_semplificato")
    public void setFlussoSemplificato(Boolean flussoSemplificato) {
        this.flussoSemplificato = flussoSemplificato;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale")
    public String getTipoCanale() {
        return tipoCanale;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale")
    public void setTipoCanale(String tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    /**
     * 
     * 
     */
    @JsonProperty("InvoiceFlow")
    public List<InvoiceFlowPC> getInvoiceFlow() {
        return invoiceFlow;
    }

    /**
     * 
     * 
     */
    @JsonProperty("InvoiceFlow")
    public void setInvoiceFlow(List<InvoiceFlowPC> invoiceFlow) {
        this.invoiceFlow = invoiceFlow;
    }

}
