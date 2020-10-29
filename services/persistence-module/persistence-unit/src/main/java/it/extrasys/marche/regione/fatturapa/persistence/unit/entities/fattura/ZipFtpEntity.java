package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "ZIP_FTP")
public class ZipFtpEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_ZIP_FTP")
    private BigInteger idZipFtp;

    @Column(name = "NOME_FILE_ZIP", nullable = false, length = 100)
    private String nomeFileZip;

    @Column(name = "DATA_INVIO")
    private Date dataInvio;

    @Column(name = "DATA_RICEZIONE")
    private Date dataRicezione;

    @Column(name = "FTP_IN")
    private Boolean ftpIn;

    @Column(name = "FTP_OUT")
    private Boolean ftpOut;

    @Column(name = "CODICE_ENTE")
    private String codiceEnte;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    public ZipFtpEntity() {
    }

    public BigInteger getIdZipFtp() {
        return idZipFtp;
    }

    public void setIdZipFtp(BigInteger idZipFtp) {
        this.idZipFtp = idZipFtp;
    }

    public String getNomeFileZip() {
        return nomeFileZip;
    }

    public void setNomeFileZip(String nomeFileZip) {
        this.nomeFileZip = nomeFileZip;
    }

    public Date getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(Date dataInvio) {
        this.dataInvio = dataInvio;
    }

    public Date getDataRicezione() {
        return dataRicezione;
    }

    public void setDataRicezione(Date dataRicezione) {
        this.dataRicezione = dataRicezione;
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

    public String getCodiceEnte() {
        return codiceEnte;
    }

    public void setCodiceEnte(String codiceEnte) {
        this.codiceEnte = codiceEnte;
    }
}
