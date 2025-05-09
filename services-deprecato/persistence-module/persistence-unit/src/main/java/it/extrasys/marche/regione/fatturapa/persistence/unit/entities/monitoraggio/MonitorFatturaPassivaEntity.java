package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "MONITOR_FATTURA_PASSIVA")
public class MonitorFatturaPassivaEntity implements IEntity {

    private static final long serialVersionUID = 6205397931773868619L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_MONITOR_FATTURA_PASSIVA")
    private BigInteger idMonitorFatturaPassiva;

    @Column(name = "IDENTIFICATIVO_SDI")
    private BigInteger identificativoSdi;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_ULTIMO_STATO")
    private Timestamp dataUltimoStato;

    @Column(name = "CODICE_UFFICIO_DESTINATARIO")
    private String codiceUfficioDestinatario;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_CREAZIONE")
    private Timestamp dataCreazione;

    @Column(name = "TIPO_CANALE")
    private String tipoCanale;

    @Column(name = "STATO")
    private String stato;

    @ManyToOne
    @JoinColumn(name = "CODICE_FLAG_WARNING")
    private CodificaFlagWarningEntity flagWarning;

    @Column(name = "ID_DATI_FATTURA")
    private BigInteger idDatiFattura;

    @Column(name = "nome_file")
    private String nomeFile;

    @Column(name = "NOTE")
    private String note;

    @Transient
    private CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag;

    public BigInteger getIdMonitorFatturaPassiva() {
        return idMonitorFatturaPassiva;
    }

    public void setIdMonitorFatturaPassiva(BigInteger idMonitorFatturaPassiva) {
        this.idMonitorFatturaPassiva = idMonitorFatturaPassiva;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public String getCodiceUfficioDestinatario() {
        return codiceUfficioDestinatario;
    }

    public void setCodiceUfficioDestinatario(String codiceUfficioDestinatario) {
        this.codiceUfficioDestinatario = codiceUfficioDestinatario;
    }

    public Timestamp getDataUltimoStato() {
        return dataUltimoStato;
    }

    public void setDataUltimoStato(Timestamp dataUltimoStato) {
        this.dataUltimoStato = dataUltimoStato;
    }

    public Timestamp getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Timestamp dataCreazione) {
        this.dataCreazione = dataCreazione;
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

    public CodificaFlagWarningEntity getFlagWarning() {
        return flagWarning;
    }

    public void setFlagWarning(CodificaFlagWarningEntity flagWarning) {
        this.flagWarning = flagWarning;
    }

    public BigInteger getIdDatiFattura() {
        return idDatiFattura;
    }

    public void setIdDatiFattura(BigInteger idDatiFattura) {
        this.idDatiFattura = idDatiFattura;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public CodificaFlagWarningEntity.CODICI_FLAG_WARNING getFlag() {
        return flag;
    }

    public void setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag) {
        this.flag = flag;
    }
}
