
package it.extrasys.marche.regione.fatturapa.api.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Ciclo Passivo: Request configurazione dell'integrazione Sistema di Protocollo
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tipo_canale_ca",
    "tipo_ws",
    "invio_unico",
    "ambiente_cicloPassivo",
    "endpoint",
    "porta",
    "user",
    "password",
    "protocollista_nome",
    "protocollista_cognome",
    "protocollista_uo",
    "protocollista_ruolo",
    "responsabile_procedimento_nome",
    "responsabile_procedimento_cognome",
    "responsabile_procedimento_uo",
    "responsabile_procedimento_ruolo",
    "codice_registro",
    "codice_amm",
    "certificato"
})
public class PCprotocolResponse {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale_ca")
    @JsonPropertyDescription("")
    private String tipoCanaleCa;
    /**
     * 
     * 
     */
    @JsonProperty("tipo_ws")
    @JsonPropertyDescription("")
    private String tipoWs;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("invio_unico")
    @JsonPropertyDescription("")
    private Boolean invioUnico;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloPassivo")
    @JsonPropertyDescription("")
    private String ambienteCicloPassivo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpoint")
    @JsonPropertyDescription("")
    private String endpoint;
    /**
     * 
     * 
     */
    @JsonProperty("porta")
    @JsonPropertyDescription("")
    private Integer porta;
    /**
     * 
     * 
     */
    @JsonProperty("user")
    @JsonPropertyDescription("")
    private String user;
    /**
     * 
     * 
     */
    @JsonProperty("password")
    @JsonPropertyDescription("")
    private String password;
    /**
     * 
     * 
     */
    @JsonProperty("protocollista_nome")
    @JsonPropertyDescription("")
    private String protocollistaNome;
    /**
     * 
     * 
     */
    @JsonProperty("protocollista_cognome")
    @JsonPropertyDescription("")
    private String protocollistaCognome;
    /**
     * 
     * 
     */
    @JsonProperty("protocollista_uo")
    @JsonPropertyDescription("")
    private String protocollistaUo;
    /**
     * 
     * 
     */
    @JsonProperty("protocollista_ruolo")
    @JsonPropertyDescription("")
    private String protocollistaRuolo;
    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_nome")
    @JsonPropertyDescription("")
    private String responsabileProcedimentoNome;
    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_cognome")
    @JsonPropertyDescription("")
    private String responsabileProcedimentoCognome;
    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_uo")
    @JsonPropertyDescription("")
    private String responsabileProcedimentoUo;
    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_ruolo")
    @JsonPropertyDescription("")
    private String responsabileProcedimentoRuolo;
    /**
     * 
     * 
     */
    @JsonProperty("codice_registro")
    @JsonPropertyDescription("")
    private String codiceRegistro;
    /**
     * 
     * 
     */
    @JsonProperty("codice_amm")
    @JsonPropertyDescription("")
    private String codiceAmm;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("certificato")
    @JsonPropertyDescription("")
    private Boolean certificato;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale_ca")
    public String getTipoCanaleCa() {
        return tipoCanaleCa;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tipo_canale_ca")
    public void setTipoCanaleCa(String tipoCanaleCa) {
        this.tipoCanaleCa = tipoCanaleCa;
    }

    /**
     * 
     * 
     */
    @JsonProperty("tipo_ws")
    public String getTipoWs() {
        return tipoWs;
    }

    /**
     * 
     * 
     */
    @JsonProperty("tipo_ws")
    public void setTipoWs(String tipoWs) {
        this.tipoWs = tipoWs;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("invio_unico")
    public Boolean getInvioUnico() {
        return invioUnico;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("invio_unico")
    public void setInvioUnico(Boolean invioUnico) {
        this.invioUnico = invioUnico;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloPassivo")
    public String getAmbienteCicloPassivo() {
        return ambienteCicloPassivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ambiente_cicloPassivo")
    public void setAmbienteCicloPassivo(String ambienteCicloPassivo) {
        this.ambienteCicloPassivo = ambienteCicloPassivo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("endpoint")
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 
     * 
     */
    @JsonProperty("porta")
    public Integer getPorta() {
        return porta;
    }

    /**
     * 
     * 
     */
    @JsonProperty("porta")
    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    /**
     * 
     * 
     */
    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    /**
     * 
     * 
     */
    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 
     * 
     */
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    /**
     * 
     * 
     */
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_nome")
    public String getProtocollistaNome() {
        return protocollistaNome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_nome")
    public void setProtocollistaNome(String protocollistaNome) {
        this.protocollistaNome = protocollistaNome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_cognome")
    public String getProtocollistaCognome() {
        return protocollistaCognome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_cognome")
    public void setProtocollistaCognome(String protocollistaCognome) {
        this.protocollistaCognome = protocollistaCognome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_uo")
    public String getProtocollistaUo() {
        return protocollistaUo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_uo")
    public void setProtocollistaUo(String protocollistaUo) {
        this.protocollistaUo = protocollistaUo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_ruolo")
    public String getProtocollistaRuolo() {
        return protocollistaRuolo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("protocollista_ruolo")
    public void setProtocollistaRuolo(String protocollistaRuolo) {
        this.protocollistaRuolo = protocollistaRuolo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_nome")
    public String getResponsabileProcedimentoNome() {
        return responsabileProcedimentoNome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_nome")
    public void setResponsabileProcedimentoNome(String responsabileProcedimentoNome) {
        this.responsabileProcedimentoNome = responsabileProcedimentoNome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_cognome")
    public String getResponsabileProcedimentoCognome() {
        return responsabileProcedimentoCognome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_cognome")
    public void setResponsabileProcedimentoCognome(String responsabileProcedimentoCognome) {
        this.responsabileProcedimentoCognome = responsabileProcedimentoCognome;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_uo")
    public String getResponsabileProcedimentoUo() {
        return responsabileProcedimentoUo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_uo")
    public void setResponsabileProcedimentoUo(String responsabileProcedimentoUo) {
        this.responsabileProcedimentoUo = responsabileProcedimentoUo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_ruolo")
    public String getResponsabileProcedimentoRuolo() {
        return responsabileProcedimentoRuolo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("responsabile_procedimento_ruolo")
    public void setResponsabileProcedimentoRuolo(String responsabileProcedimentoRuolo) {
        this.responsabileProcedimentoRuolo = responsabileProcedimentoRuolo;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_registro")
    public String getCodiceRegistro() {
        return codiceRegistro;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_registro")
    public void setCodiceRegistro(String codiceRegistro) {
        this.codiceRegistro = codiceRegistro;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_amm")
    public String getCodiceAmm() {
        return codiceAmm;
    }

    /**
     * 
     * 
     */
    @JsonProperty("codice_amm")
    public void setCodiceAmm(String codiceAmm) {
        this.codiceAmm = codiceAmm;
    }

    public boolean isCertificato() {
        return certificato;
    }

    public void setCertificato(Boolean certificato) {
        this.certificato = certificato;
    }
}
