package it.extrasys.marche.regione.fatturapa.patch.bean;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigInteger;

/**
 * Created by Antonio on 30/07/2015.
 */
@CsvRecord(separator = ";")
public class Ente {

    @DataField(pos = 1)
    private BigInteger identificativoSdi;
    @DataField(pos = 2)
    private String data;
    @DataField(pos = 3)
    private String cedenteIdFiscaleIva;
    @DataField(pos = 4)
    private String codiceDestinatario;
    @DataField(pos = 5)
    private String dataFattura;
    @DataField(pos = 6)
    private String nomeFile;
    @DataField(pos = 7)
    private String numeroFattura;
    @DataField(pos = 8)
    private String numeroProtocollo;
    @DataField(pos = 9)
    private String descStato;

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCedenteIdFiscaleIva() {
        return cedenteIdFiscaleIva;
    }

    public void setCedenteIdFiscaleIva(String cedenteIdFiscaleIva) {
        this.cedenteIdFiscaleIva = cedenteIdFiscaleIva;
    }

    public String getDataFattura() {
        return dataFattura;
    }

    public void setDataFattura(String dataFattura) {
        this.dataFattura = dataFattura;
    }

    public String getNumeroFattura() {
        return numeroFattura;
    }

    public void setNumeroFattura(String numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    public void setNumeroProtocollo(String numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
    }

    public String getDescStato() {
        return descStato;
    }

    public void setDescStato(String descStato) {
        this.descStato = descStato;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getCodiceDestinatario() {
        return codiceDestinatario;
    }

    public void setCodiceDestinatario(String codiceDestinatario) {
        this.codiceDestinatario = codiceDestinatario;
    }
}
