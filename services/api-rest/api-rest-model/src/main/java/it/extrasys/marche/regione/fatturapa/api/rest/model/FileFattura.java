package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "fattura",
        "nome"
})
public class FileFattura {

    @JsonProperty ("fattura")
    @JsonPropertyDescription ("")
    private byte[] fattura;

    @JsonProperty ("nome")
    @JsonPropertyDescription ("")
    private String nome;

    @JsonProperty ("fattura")
    public byte[] getFattura() {
        return fattura;
    }

    @JsonProperty ("fattura")
    public void setFattura(byte[] fattura) {
        this.fattura = fattura;
    }

    @JsonProperty ("nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty ("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    public FileFattura(){

    }
}
