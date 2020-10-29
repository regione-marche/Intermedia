package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by agosteeno on 06/03/15.
 */

@Entity
@Table(name = "NOTIFICA_DECORRENZA_TERMINI")
public class NotificaDecorrenzaTerminiEntity implements IEntity {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_NOTIFICA_DECORRENZA_TERMINI")
    private BigInteger idNotificaDecorrenzaTermini;

    @Column(name = "IDENTIFICATIVO_SDI", nullable = false)
    private BigInteger identificativoSdi;

    @Column(name = "ID_FISCALE_COMMITTENTE", nullable = false)
    private String idFiscaleCommittente;

    @Column(name = "NOME_FILE", nullable = false)
    private String nomeFile;

    @Column(name = "CODICE_UFFICIO", nullable = false, length = 50)
    private String codiceUfficio;

    @Lob
    @Column(name = "CONTENUTO_FILE", length = 10485760, nullable = false)
    private byte[] contenutoFile;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_RICEZIONE", nullable = false)
    private Date dataRicezione;

    @Column(name = "NUMERO_PROTOCOLLO", nullable = true, length = 255)
    private String numeroProtocollo;

    @Transient
    private String nomeCedentePrestatore;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @PrePersist
    void createdAt() {
        this.dataRicezione = new Timestamp(new Date().getTime());
    }

    public BigInteger getIdNotificaDecorrenzaTermini() {
        return idNotificaDecorrenzaTermini;
    }

    public void setIdNotificaDecorrenzaTermini(BigInteger idNotificaDecorrenzaTermini) {
        this.idNotificaDecorrenzaTermini = idNotificaDecorrenzaTermini;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public String getIdFiscaleCommittente() {
        return idFiscaleCommittente;
    }

    public void setIdFiscaleCommittente(String idFiscaleCommittente) {
        this.idFiscaleCommittente = idFiscaleCommittente;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
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

    public String getCodiceUfficio() {
        return codiceUfficio;
    }

    public void setCodiceUfficio(String codiceUfficio) {
        this.codiceUfficio = codiceUfficio;
    }

    public void setDataRicezione(Date dataRicezione) {
        this.dataRicezione = dataRicezione;
    }

    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    public void setNumeroProtocollo(String numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
    }

    public String getNomeCedentePrestatore() {
        return nomeCedentePrestatore;
    }

    public void setNomeCedentePrestatore(String nomeCedentePrestatore) {
        this.nomeCedentePrestatore = nomeCedentePrestatore;
    }
}