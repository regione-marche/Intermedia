package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "UTENTI_SERVIZIO")
public class UtenteServizioEntity implements IEntity {

    private static final long serialVersionUID = -4234L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_UTENTE")
    private BigInteger idUtente;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    public UtenteServizioEntity(){}

    public BigInteger getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(BigInteger idUtente) {
        this.idUtente = idUtente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}