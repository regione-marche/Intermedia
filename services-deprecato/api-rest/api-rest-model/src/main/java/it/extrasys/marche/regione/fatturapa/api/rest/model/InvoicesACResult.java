
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;


/**
 * Ricerca Fatture Ciclo Attivo: Dettaglio risultati della response
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identificativo_sdi",
    "nome_file_fattura",
    "data_decorrenza_termini",
    "codice_ufficio_mittente",
    "data_inoltro"
})
public class InvoicesACResult {

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

}
