package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table (name = "UTENTE_ENTE")
public class UtenteEnteEntity {

    private static final long serialVersionUID = -42331234L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_UTENTE_ENTE")
    private BigInteger idUtenteEnte;

    @ManyToOne
    @JoinColumn(name = "ID_UTENTE")
    private UtenteEntity utente;

    @ManyToOne
    @JoinColumn(name = "ID_ENTE")
    private EnteEntity ente;

    public UtenteEntity getUtente() {
        return utente;
    }

    public void setUtente(UtenteEntity utente) {
        this.utente = utente;
    }

    public EnteEntity getEnte() {
        return ente;
    }

    public void setEnte(EnteEntity ente) {
        this.ente = ente;
    }

    public BigInteger getIdUtenteEnte() {
        return idUtenteEnte;
    }

    public void setIdUtenteEnte(BigInteger idUtenteEnte) {
        this.idUtenteEnte = idUtenteEnte;
    }
}
