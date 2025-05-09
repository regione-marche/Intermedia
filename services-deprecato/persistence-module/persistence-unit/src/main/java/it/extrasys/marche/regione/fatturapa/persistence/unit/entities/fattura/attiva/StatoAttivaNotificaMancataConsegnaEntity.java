package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.ZipFtpEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "STATO_ATTIVA_NOTIFICA_MANCATA_CONSEGNA")
@IdClass(StatoAttivaNotificaMancataConsegnaPK.class)
public class StatoAttivaNotificaMancataConsegnaEntity implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_NOTIFICA_MANCATA_CONSEGNA", nullable = false)
    private FatturaAttivaNotificaMancataConsegnaEntity notificaMancataConsegnaEntity;

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


    public StatoAttivaNotificaMancataConsegnaEntity() {
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

    public FatturaAttivaNotificaMancataConsegnaEntity getNotificaMancataConsegnaEntity() {
        return notificaMancataConsegnaEntity;
    }

    public void setNotificaMancataConsegnaEntity(FatturaAttivaNotificaMancataConsegnaEntity notificaMancataConsegnaEntity) {
        this.notificaMancataConsegnaEntity = notificaMancataConsegnaEntity;
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
