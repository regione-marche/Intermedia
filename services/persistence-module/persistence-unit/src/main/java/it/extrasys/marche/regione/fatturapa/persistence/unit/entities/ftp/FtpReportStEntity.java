package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "FTP_REPORT_ST")
public class FtpReportStEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "CODICE_ENTE")
    private String codiceEnte;

    @Column(name = "NOME_SUPPORTO_ZIP", nullable = false, length = 100)
    private String nomeSupportoZip;

    @Column(name = "FTP_IN", nullable = false)
    private Boolean ftpIn;

    @Column(name = "FTP_OUT", nullable = false)
    private Boolean ftpOut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIMESTAMP_INVIO", updatable=false)
    private Timestamp timestampInvio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIMESTAMP_RICEZIONE", updatable=false)
    private Timestamp timestampRicezione;

    @ManyToOne
    @JoinColumn(name = "STATO_SUPPORTO")
    private FtpCodificaStatiSupportoEntity statoSupporto;

    @ManyToOne
    @JoinColumn(name = "STATO_ESITO")
    private FtpCodificaStatiEsitoEntity statoEsito;

    @Column(name = "DESC_ERRORE_SUPPORTO")
    private String descErroreSupporto;

    @Column(name = "DESC_ERRORE_ESITO")
    private String descErroreEsito;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getCodiceEnte() {
        return codiceEnte;
    }

    public void setCodiceEnte(String codiceEnte) {
        this.codiceEnte = codiceEnte;
    }

    public String getNomeSupportoZip() {
        return nomeSupportoZip;
    }

    public void setNomeSupportoZip(String nomeSupportoZip) {
        this.nomeSupportoZip = nomeSupportoZip;
    }

    public Boolean getFtpIn() {
        return ftpIn;
    }

    public void setFtpIn(Boolean ftpIn) {
        this.ftpIn = ftpIn;
    }

    public Boolean getFtpOut() {
        return ftpOut;
    }

    public void setFtpOut(Boolean ftpOut) {
        this.ftpOut = ftpOut;
    }

    public Timestamp getTimestampInvio() {
        return timestampInvio;
    }

    public void setTimestampInvio(Timestamp timestampInvio) {
        this.timestampInvio = timestampInvio;
    }

    public Timestamp getTimestampRicezione() {
        return timestampRicezione;
    }

    public void setTimestampRicezione(Timestamp timestampRicezione) {
        this.timestampRicezione = timestampRicezione;
    }

    public FtpCodificaStatiSupportoEntity getStatoSupporto() {
        return statoSupporto;
    }

    public void setStatoSupporto(FtpCodificaStatiSupportoEntity statoSupporto) {
        this.statoSupporto = statoSupporto;
    }

    public FtpCodificaStatiEsitoEntity getStatoEsito() {
        return statoEsito;
    }

    public void setStatoEsito(FtpCodificaStatiEsitoEntity statoEsito) {
        this.statoEsito = statoEsito;
    }

    public String getDescErroreSupporto() {
        return descErroreSupporto;
    }

    public void setDescErroreSupporto(String descErroreSupporto) {
        this.descErroreSupporto = descErroreSupporto;
    }

    public String getDescErroreEsito() {
        return descErroreEsito;
    }

    public void setDescErroreEsito(String descErroreEsito) {
        this.descErroreEsito = descErroreEsito;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FtpReportStEntity that = (FtpReportStEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(codiceEnte, that.codiceEnte) && Objects.equals(nomeSupportoZip, that.nomeSupportoZip) && Objects.equals(ftpIn, that.ftpIn) && Objects.equals(ftpOut, that.ftpOut) && Objects.equals(timestampInvio, that.timestampInvio) && Objects.equals(timestampRicezione, that.timestampRicezione) && Objects.equals(statoSupporto, that.statoSupporto) && Objects.equals(statoEsito, that.statoEsito) && Objects.equals(descErroreSupporto, that.descErroreSupporto) && Objects.equals(descErroreEsito, that.descErroreEsito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codiceEnte, nomeSupportoZip, ftpIn, ftpOut, timestampInvio, timestampRicezione, statoSupporto, statoEsito, descErroreSupporto, descErroreEsito);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}