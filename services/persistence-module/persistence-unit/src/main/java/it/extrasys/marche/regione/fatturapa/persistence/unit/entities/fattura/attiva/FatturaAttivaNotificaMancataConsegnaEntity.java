package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Created by agosteeno on 22/03/15.
 */
@Entity
@Table(name = "FATTURA_ATTIVA_NOTIFICA_MANCATA_CONSEGNA")

public class FatturaAttivaNotificaMancataConsegnaEntity implements IEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID_NOTIFICA_MANCATA_CONSEGNA")
    private BigInteger idNotificaMancataConsegna;

    @OneToOne
    @JoinColumn(name="ID_FATTURA_ATTIVA", nullable = false)
    private FatturaAttivaEntity fatturaAttiva;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA", updatable=false)
    private Timestamp data;

    public FatturaAttivaNotificaMancataConsegnaEntity(){

    }

    public BigInteger getIdNotificaMancataConsegna() {
        return idNotificaMancataConsegna;
    }

    public void setIdNotificaMancataConsegna(BigInteger idNotificaMancataConsegna) {
        this.idNotificaMancataConsegna = idNotificaMancataConsegna;
    }

    public FatturaAttivaEntity getFatturaAttiva() {
        return fatturaAttiva;
    }

    public void setFatturaAttiva(FatturaAttivaEntity fatturaAttiva) {
        this.fatturaAttiva = fatturaAttiva;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }
}
