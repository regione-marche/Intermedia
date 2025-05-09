package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 15/03/15.
 */
@Entity
@Table(name = "FILE_FATTURA", uniqueConstraints = @UniqueConstraint(columnNames = {"IDENTIFICATIVO_SDI"}))
public class FileFatturaEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_FILE_FATTURA")
    private BigInteger idFileFattura;

    @Column(name = "NOME_FILE_FATTURA", length = 50, nullable = false)
    private String nomeFileFattura;

    @Column(name = "IDENTIFICATIVO_SDI", length = 12, nullable = false)
    private BigInteger identificativoSdI;

    @Lob
    @Column(name = "CONTENUTO_FILE", length = 31457280, nullable = false)
    private byte[] contenutoFile;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_RICEZIONE", nullable = true)
    private Date dataRicezione;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "FATTURAZIONE_TEST", nullable = false)
    @Type(String.class)
    private Boolean fatturazioneTest;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @PrePersist
    void createdAt() {

        if(this.dataRicezione == null) {
            this.dataRicezione = new Timestamp(new Date().getTime());
        }
    }

    public BigInteger getIdFileFattura() {
        return idFileFattura;
    }

    public void setIdFileFattura(BigInteger idFileFattura) {
        this.idFileFattura = idFileFattura;
    }

    public String getNomeFileFattura() {
        return nomeFileFattura;
    }

    public void setNomeFileFattura(String nomeFileFattura) {
        this.nomeFileFattura = nomeFileFattura;
    }

    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(BigInteger identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public byte[] getContenutoFile() {
        return contenutoFile;
    }

    public void setContenutoFile(byte[] contenutoFile) {
        this.contenutoFile = contenutoFile;
    }

    public Date getDataRicezione() {
        return dataRicezione;
    }

    public void setDataRicezione(Date dataRicezione) {
        this.dataRicezione = dataRicezione;
    }

    public Boolean getFatturazioneTest() {
        return fatturazioneTest;
    }

    public void setFatturazioneTest(Boolean fatturazioneTest) {
        this.fatturazioneTest = fatturazioneTest;
    }
}
