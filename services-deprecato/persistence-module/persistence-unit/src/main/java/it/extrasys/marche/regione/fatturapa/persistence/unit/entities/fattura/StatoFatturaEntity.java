package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 26/01/15.
 */

@Entity
@Table(name = "STATO_FATTURA")
@IdClass(StatoFatturaPK.class)
public class StatoFatturaEntity implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="ID_DATI_FATTURA", nullable = false)
    private DatiFatturaEntity datiFattura;

    @Id
    @ManyToOne
    @JoinColumn(name="ID_COD_STATO", nullable = false )
    private CodificaStatiEntity stato;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA", updatable=false)
    private Timestamp data;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_ZIP_FTP")
    private ZipFtpEntity zipFtpEntity;

    public StatoFatturaEntity(){
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

    public DatiFatturaEntity getDatiFattura() {
        return datiFattura;
    }

    public void setDatiFattura(DatiFatturaEntity datiFattura) {
        this.datiFattura = datiFattura;
    }

    public CodificaStatiEntity getStato() {
        return stato;
    }

    public void setStato(CodificaStatiEntity stato) {
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
