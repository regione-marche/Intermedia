package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "ENTE_PALEO_CA") //, uniqueConstraints= @UniqueConstraint(columnNames = {"ID_FISCALE_COMMITTENTE", "CODICE_UFFICIO"}))
public class EntePaleoCaEntity implements IEntity, Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID_ENTE_PALEO_CA")
    private BigInteger idEntePaleoCa;

    @Column(name="USER_ID_WS", nullable = false, length = 255)
    private String userIdWs;

    @Column(name="PASSWORD_WS", nullable = false, length = 255)
    private String passwordWs;

    @Column(name="CODICE_AMM", nullable = false, length = 50)
    private String codiceAMM;

    @Column(name="PROTOCOLLISTA_NOME", nullable = false, length = 50)
    private String protocollistaNome;

    @Column(name="PROTOCOLLISTA_COGNOME", nullable = false, length = 50)
    private String protocollistaCognome;

    @Column(name="PROTOCOLLISTA_RUOLO", nullable = false, length = 50)
    private String protocollistaRuolo;

    @Column(name="PROTOCOLLISTA_UO", nullable = false, length = 50)
    private String protocollistaUo;

    @Column(name="RESPONSABILE_PROCEDIMENTO_NOME", nullable = false, length = 50)
    private String responsabileProcedimentoNome;

    @Column(name="RESPONSABILE_PROCEDIMENTO_COGNOME", nullable = false, length = 50)
    private String responsabileProcedimentoCognome;

    @Column(name="RESPONSABILE_PROCEDIMENTO_RUOLO", nullable = false, length = 50)
    private String responsabileProcedimentoRuolo;

    @Column(name="RESPONSABILE_PROCEDIMENTO_UO", nullable = false, length = 50)
    private String responsabileProcedimentoUo;

    @Column(name="CODICE_REGISTRO", nullable = false, length = 50)
    private String codiceRegistro;

    public EntePaleoCaEntity(){
        //needed from jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigInteger getIdEntePaleoCa() {
        return idEntePaleoCa;
    }

    public void setIdEntePaleoCa(BigInteger idEntePaleoCa) {
        this.idEntePaleoCa = idEntePaleoCa;
    }

    public String getPasswordWs() {
        return passwordWs;
    }

    public void setPasswordWs(String passwordWs) {
        this.passwordWs = passwordWs;
    }

    public String getProtocollistaNome() {
        return protocollistaNome;
    }

    public void setProtocollistaNome(String protocollistaNome) {
        this.protocollistaNome = protocollistaNome;
    }

    public String getProtocollistaCognome() {
        return protocollistaCognome;
    }

    public void setProtocollistaCognome(String protocollistaCognome) {
        this.protocollistaCognome = protocollistaCognome;
    }

    public String getProtocollistaRuolo() {
        return protocollistaRuolo;
    }

    public void setProtocollistaRuolo(String protocollistaRuolo) {
        this.protocollistaRuolo = protocollistaRuolo;
    }

    public String getProtocollistaUo() {
        return protocollistaUo;
    }

    public void setProtocollistaUo(String protocollistaUo) {
        this.protocollistaUo = protocollistaUo;
    }

    public String getResponsabileProcedimentoNome() {
        return responsabileProcedimentoNome;
    }

    public void setResponsabileProcedimentoNome(String responsabileProcedimentoNome) {
        this.responsabileProcedimentoNome = responsabileProcedimentoNome;
    }

    public String getResponsabileProcedimentoCognome() {
        return responsabileProcedimentoCognome;
    }

    public void setResponsabileProcedimentoCognome(String responsabileProcedimentoCognome) {
        this.responsabileProcedimentoCognome = responsabileProcedimentoCognome;
    }

    public String getResponsabileProcedimentoRuolo() {
        return responsabileProcedimentoRuolo;
    }

    public void setResponsabileProcedimentoRuolo(String responsabileProcedimentoRuolo) {
        this.responsabileProcedimentoRuolo = responsabileProcedimentoRuolo;
    }

    public String getResponsabileProcedimentoUo() {
        return responsabileProcedimentoUo;
    }

    public void setResponsabileProcedimentoUo(String responsabileProcedimentoUo) {
        this.responsabileProcedimentoUo = responsabileProcedimentoUo;
    }

    public String getCodiceRegistro() {
        return codiceRegistro;
    }

    public void setCodiceRegistro(String codiceRegistro) {
        this.codiceRegistro = codiceRegistro;
    }

    public String getUserIdWs() {
        return userIdWs;
    }

    public void setUserIdWs(String userIdWs) {
        this.userIdWs = userIdWs;
    }

    public String getCodiceAMM() {
        return codiceAMM;
    }

    public void setCodiceAMM(String codiceAMM) {
        this.codiceAMM = codiceAMM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntePaleoCaEntity that = (EntePaleoCaEntity) o;
        return Objects.equals(idEntePaleoCa, that.idEntePaleoCa) &&
                Objects.equals(userIdWs, that.userIdWs) &&
                Objects.equals(passwordWs, that.passwordWs) &&
                Objects.equals(codiceAMM, that.codiceAMM) &&
                Objects.equals(protocollistaNome, that.protocollistaNome) &&
                Objects.equals(protocollistaCognome, that.protocollistaCognome) &&
                Objects.equals(protocollistaRuolo, that.protocollistaRuolo) &&
                Objects.equals(protocollistaUo, that.protocollistaUo) &&
                Objects.equals(responsabileProcedimentoNome, that.responsabileProcedimentoNome) &&
                Objects.equals(responsabileProcedimentoCognome, that.responsabileProcedimentoCognome) &&
                Objects.equals(responsabileProcedimentoRuolo, that.responsabileProcedimentoRuolo) &&
                Objects.equals(responsabileProcedimentoUo, that.responsabileProcedimentoUo) &&
                Objects.equals(codiceRegistro, that.codiceRegistro);
    }

    @Override
    public int hashCode() {

        return Objects.hash(idEntePaleoCa, userIdWs, passwordWs, codiceAMM, protocollistaNome, protocollistaCognome, protocollistaRuolo, protocollistaUo, responsabileProcedimentoNome, responsabileProcedimentoCognome, responsabileProcedimentoRuolo, responsabileProcedimentoUo, codiceRegistro);
    }

}