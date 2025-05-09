
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Ricerca Fatture Ciclo Attivo: Response di dettaglio della fattura ricercata
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identificativo_sdi",
    "nome_file_fattura",
    "data_decorrenza_termini",
    "flusso_semplificato",
    "tipo_canale",
    "codice_ufficio_mittente",
    "data_inoltro",
    "formato_trasmissione",
    "InvoiceFlow"
})
public class InvoiceACDetail {

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
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio_mittente")
    @JsonPropertyDescription("")
    private String codiceUfficioMittente;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_inoltro")
    @JsonPropertyDescription("")
    private Date dataInoltro;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("formato_trasmissione")
    @JsonPropertyDescription("")
    private String formatoTrasmissione;
    /**
     * 
     * 
     */
    @JsonProperty("InvoiceFlow")
    @JsonPropertyDescription("")
    private List<InvoiceFlow> invoiceFlow = new ArrayList<InvoiceFlow>();

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
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio_mittente")
    public String getCodiceUfficioMittente() {
        return codiceUfficioMittente;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("codice_ufficio_mittente")
    public void setCodiceUfficioMittente(String codiceUfficioMittente) {
        this.codiceUfficioMittente = codiceUfficioMittente;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_inoltro")
    public Date getDataInoltro() {
        return dataInoltro;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data_inoltro")
    public void setDataInoltro(Date dataInoltro) {
        this.dataInoltro = dataInoltro;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("formato_trasmissione")
    public String getFormatoTrasmissione() {
        return formatoTrasmissione;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("formato_trasmissione")
    public void setFormatoTrasmissione(String formatoTrasmissione) {
        this.formatoTrasmissione = formatoTrasmissione;
    }

    /**
     * 
     * 
     */
    @JsonProperty("InvoiceFlow")
    public List<InvoiceFlow> getInvoiceFlow() {
        return invoiceFlow;
    }

    /**
     * 
     * 
     */
    @JsonProperty("InvoiceFlow")
    public void setInvoiceFlow(List<InvoiceFlow> invoiceFlow) {
        this.invoiceFlow = invoiceFlow;
    }

}
