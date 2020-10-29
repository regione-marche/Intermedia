package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.ZipFtpEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "STATO_ATTIVA_RICEVUTA_CONSEGNA")
@IdClass(StatoAttivaRicevutaConsegnaPK.class)
public class StatoAttivaRicevutaConsegnaEntity implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_RICEVUTA_CONSEGNA", nullable = false)
    private FatturaAttivaRicevutaConsegnaEntity ricevutaConsegnaEntity;

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_COD_STATO", nullable = false)
    private CodificaStatiAttivaEntity stato;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA", updatable = false)
    private Timestamp data;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_ZIP_FTP")
    private ZipFtpEntity zipFtpEntity;


    public StatoAttivaRicevutaConsegnaEntity() {
        //needed from jpa
    }

    // CREATION DATE MANAGEMENT
    @PrePersist
    void createdAt() {
        this.data = new Timestamp(new Date().getTime());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public FatturaAttivaRicevutaConsegnaEntity getRicevutaConsegnaEntity() {
        return ricevutaConsegnaEntity;
    }

    public void setRicevutaConsegnaEntity(FatturaAttivaRicevutaConsegnaEntity ricevutaConsegnaEntity) {
        this.ricevutaConsegnaEntity = ricevutaConsegnaEntity;
    }

    public CodificaStatiAttivaEntity getStato() {
        return stato;
    }

    public void setStato(CodificaStatiAttivaEntity stato) {
        this.stato = stato;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }

    public ZipFtpEntity getZipFtpEntity() {
        return zipFtpEntity;
    }

    public void setZipFtpEntity(ZipFtpEntity zipFtpEntity) {
        this.zipFtpEntity = zipFtpEntity;
    }
}
