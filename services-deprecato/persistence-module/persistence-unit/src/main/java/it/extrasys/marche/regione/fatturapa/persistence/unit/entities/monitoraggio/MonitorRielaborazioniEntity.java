package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "MONITOR_RIELABORAZIONI")
public class MonitorRielaborazioniEntity implements IEntity {

    private static final long serialVersionUID = 1170810144218429803L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_MONITOR_RIELABORAZIONI")
    private BigInteger idMonitorRielaborazioni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_INIZIO_RIELABORAZIONE", nullable = false)
    private Timestamp dataInizioRielaborazione;

    @OneToOne
    @JoinColumn(name = "ID_UTENTE", nullable = false)
    private UtenteEntity idUtente;

    @OneToOne
    @JoinColumn(name = "ID_CODE", nullable = false)
    private CodeEntity idCoda;

    @Column(name = "NUMERO_RIELABORAZIONI", nullable = false)
    private Integer numeroRielaborazioni;

    @Column(name = "NOME_REPORT", nullable = false, length = 50)
    private String nomeReport;

    @Column(name = "stacktrace")
    private byte[] stacktrace;

    @Column(name = "identificativo_sdi")
    private BigInteger identificativoSdi;

    @Column(name = "numero_tentativi")
    private Integer numeroTentativi;

    public BigInteger getIdMonitorRielaborazioni() {
        return idMonitorRielaborazioni;
    }

    public void setIdMonitorRielaborazioni(BigInteger idMonitorRielaborazioni) {
        this.idMonitorRielaborazioni = idMonitorRielaborazioni;
    }

    public Timestamp getDataInizioRielaborazione() {
        return dataInizioRielaborazione;
    }

    public void setDataInizioRielaborazione(Timestamp dataInizioRielaborazione) {
        this.dataInizioRielaborazione = dataInizioRielaborazione;
    }

    public UtenteEntity getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(UtenteEntity idUtente) {
        this.idUtente = idUtente;
    }

    public CodeEntity getIdCoda() {
        return idCoda;
    }

    public void setIdCoda(CodeEntity idCoda) {
        this.idCoda = idCoda;
    }

    public Integer getNumeroRielaborazioni() {
        return numeroRielaborazioni;
    }

    public void setNumeroRielaborazioni(Integer numeroRielaborazioni) {
        this.numeroRielaborazioni = numeroRielaborazioni;
    }

    public String getNomeReport() {
        return nomeReport;
    }

    public void setNomeReport(String nomeReport) {
        this.nomeReport = nomeReport;
    }

    public byte[] getStacktrace() {

        return stacktrace;
    }

    public void setStacktrace(byte[] stacktrace) {
        this.stacktrace = stacktrace;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public Integer getNumeroTentativi() {
        return numeroTentativi;
    }

    public void setNumeroTentativi(Integer numeroTentativi) {
        this.numeroTentativi = numeroTentativi;
    }
}
