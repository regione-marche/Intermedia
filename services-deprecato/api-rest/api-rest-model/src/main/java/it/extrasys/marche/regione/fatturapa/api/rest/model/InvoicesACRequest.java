
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;


/**
 * Ricerca Fatture Ciclo Attivo: Request della ricerca
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identificativo_sdi",
    "nome_file_fattura",
    "codice_ufficio_mittente",
    "data_inoltro_a",
    "data_inoltro_da"
})
public class InvoicesACRequest {

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
    @JsonProperty("codice_ufficio_mittente")
    @JsonPropertyDescription("")
    private String codiceUfficioMittente;
    /**
     * 
     * 
     */
    @JsonProperty("data_inoltro_a")
    @JsonPropertyDescription("")
    private Date dataInoltroA;
    /**
     * 
     * 
     */
    @JsonProperty("data_inoltro_da")
    @JsonPropertyDescription("")
    private Date dataInoltroDa;

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
    @JsonProperty("codice_ufficio_mittente")
    public String getCodiceUfficioMittente() {
        return codiceUfficioMittente;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_ufficio_mittente")
    public void setCodiceUfficioMittente(String codiceUfficioMittente) {
        this.codiceUfficioMittente = codiceUfficioMittente;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_inoltro_a")
    public Date getDataInoltroA() {
        return dataInoltroA;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_inoltro_a")
    public void setDataInoltroA(Date dataInoltroA) {
        this.dataInoltroA = dataInoltroA;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_inoltro_da")
    public Date getDataInoltroDa() {
        return dataInoltroDa;
    }

    /**
     * 
     * 
     */
    @JsonProperty("data_inoltro_da")
    public void setDataInoltroDa(Date dataInoltroDa) {
        this.dataInoltroDa = dataInoltroDa;
    }

}
