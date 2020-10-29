
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;


/**
 * Ricerca Fatture Ciclo Passivo: Dettaglio risultati della response
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identificativo_sdi",
    "nome_file_fattura",
    "codice_ufficio_destinatario",
    "data_ricezione",
    "data_decorrenza_termini"
})
public class InvoicesPCResult {

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

}
