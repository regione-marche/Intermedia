package it.extrasys.marche.regione.fatturapa.api.rest.models;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.util.Date;

@CsvRecord(separator = ";", generateHeaderColumns = true )
public class FatturaBindy {

    @DataField(pos = 1)
    private String identificativoSdi;

    @DataField(pos = 2)
    private String dataUltimoStato;

    @DataField(pos = 3)
    private String codiceUfficio;

    @DataField(pos = 4)
    private String dataCreazione;

    @DataField(pos = 5)
    private String nomeFile;

    @DataField(pos = 6)
    private String tipoCanale;

    @DataField(pos = 7)
    private String stato;

    @DataField(pos = 8)
    private String flag;

    public String getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(String identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public String getCodiceUfficio() {
        return codiceUfficio;
    }

    public void setCodiceUfficio(String codiceUfficio) {
        this.codiceUfficio = codiceUfficio;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getTipoCanale() {
        return tipoCanale;
    }

    public void setTipoCanale(String tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDataUltimoStato() {
        return dataUltimoStato;
    }

    public void setDataUltimoStato(String dataUltimoStato) {
        this.dataUltimoStato = dataUltimoStato;
    }

    public String getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(String dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}
