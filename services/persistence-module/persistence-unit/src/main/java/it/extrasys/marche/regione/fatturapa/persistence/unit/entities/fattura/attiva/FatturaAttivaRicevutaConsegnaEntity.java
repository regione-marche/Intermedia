package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Created by agosteeno on 22/03/15.
 */
@Entity
@Table(name = "FATTURA_ATTIVA_RICEVUTA_CONSEGNA")

public class FatturaAttivaRicevutaConsegnaEntity implements IEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID_RICEVUTA_CONSEGNA")
    private BigInteger idRicevutaConsegna;

    @OneToOne
    @JoinColumn(name="ID_FATTURA_ATTIVA", nullable = false)
    private FatturaAttivaEntity fatturaAttiva;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA", updatable=false)
    private Timestamp data;

    public FatturaAttivaRicevutaConsegnaEntity(){

    }

    public BigInteger getIdRicevutaConsegna() {
        return idRicevutaConsegna;
    }

    public void setIdRicevutaConsegna(BigInteger idRicevutaConsegna) {
        this.idRicevutaConsegna = idRicevutaConsegna;
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
