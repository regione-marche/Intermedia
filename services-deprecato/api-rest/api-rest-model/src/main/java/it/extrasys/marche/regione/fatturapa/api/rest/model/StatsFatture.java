package it.extrasys.marche.regione.fatturapa.api.rest.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "ws_base",
        "pec_base",
        "ws_ca",
        "pec_ca",
        "ftp_ca",
        "ca_non_invio_unico"
})
public class StatsFatture {

    @JsonProperty ("ws_base")
    @JsonPropertyDescription ("")
    private int wsBase;

    @JsonProperty ("pec_base")
    @JsonPropertyDescription ("")
    private int pecBase;

    @JsonProperty ("ws_ca")
    @JsonPropertyDescription ("")
    private int wsCa;

    @JsonProperty ("pec_ca")
    @JsonPropertyDescription ("")
    private int pecCa;

    @JsonProperty ("ftp_ca")
    @JsonPropertyDescription ("")
    private int ftpCa;

    @JsonProperty ("ca_non_invio_unico")
    @JsonPropertyDescription ("")
    private int caNonInvioUnico;

    public StatsFatture(){
        wsBase = 0;
        pecBase = 0;
        wsCa = 0;
        pecCa = 0;
        ftpCa = 0;
        caNonInvioUnico = 0;
    }

    @JsonProperty ("ws_base")
    public int getWsBase() {
        return wsBase;
    }

    @JsonProperty ("ws_base")
    public void setWsBase(int wsBase) {
        this.wsBase = wsBase;
    }

    @JsonProperty ("pec_base")
    public int getPecBase() {
        return pecBase;
    }

    @JsonProperty ("pec_base")
    public void setPecBase(int pecBase) {
        this.pecBase = pecBase;
    }

    @JsonProperty ("ws_ca")
    public int getWsCa() {
        return wsCa;
    }

    @JsonProperty ("ws_ca")
    public void setWsCa(int wsCa) {
        this.wsCa = wsCa;
    }

    @JsonProperty ("pec_ca")
    public int getPecCa() {
        return pecCa;
    }

    @JsonProperty ("pec_ca")
    public void setPecCa(int pecCa) {
        this.pecCa = pecCa;
    }

    @JsonProperty ("ftp_ca")
    public int getFtpCa() {
        return ftpCa;
    }

    @JsonProperty ("ftp_ca")
    public void setFtpCa(int ftpCa) {
        this.ftpCa = ftpCa;
    }

    @JsonProperty ("ca_non_invio_unico")
    public int getCaNonInvioUnico() {
        return caNonInvioUnico;
    }

    @JsonProperty ("ca_non_invio_unico")
    public void setCaNonInvioUnico(int caNonInvioUnico) {
        this.caNonInvioUnico = caNonInvioUnico;
    }
}
