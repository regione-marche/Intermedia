package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "MONITOR_FATTURA_ATTIVA")
public class MonitorFatturaAttivaEntity implements IEntity {

    private static final long serialVersionUID = 6205397931773868619L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_MONITOR_FATTURA_ATTIVA")
    private BigInteger idMonitorFatturaAttiva;

    @Column(name = "IDENTIFICATIVO_SDI")
    private BigInteger identificativoSdi;

    @Column(name = "CODICE_UFFICIO_MITTENTE")
    private String codiceUfficioMittente;

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

    @Column(name = "ID_FATTURA_ATTIVA")
    private BigInteger idFatturaAttiva;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @Column(name = "nome_file")
    private String nomeFile;

    @Transient
    private CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag;

    public BigInteger getIdMonitorFatturaAttiva() {
        return idMonitorFatturaAttiva;
    }

    public void setIdMonitorFatturaAttiva(BigInteger idMonitorFatturaAttiva) {
        this.idMonitorFatturaAttiva = idMonitorFatturaAttiva;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
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

    public BigInteger getIdFatturaAttiva() {
        return idFatturaAttiva;
    }

    public void setIdFatturaAttiva(BigInteger idFatturaAttiva) {
        this.idFatturaAttiva = idFatturaAttiva;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getCodiceUfficioMittente() {
        return codiceUfficioMittente;
    }

    public void setCodiceUfficioMittente(String codiceUfficioMittente) {
        this.codiceUfficioMittente = codiceUfficioMittente;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public CodificaFlagWarningEntity.CODICI_FLAG_WARNING getFlag() {
        return flag;
    }

    public void setFlag(CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag) {
        this.flag = flag;
    }
}
