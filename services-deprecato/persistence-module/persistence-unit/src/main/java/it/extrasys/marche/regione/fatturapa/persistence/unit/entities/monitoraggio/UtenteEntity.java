package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.EndpointAttivaCaEntity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "UTENTI")
public class UtenteEntity implements IEntity {

    private static final long serialVersionUID = -4346527455596642067L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_UTENTE")
    private BigInteger idUtente;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "ruolo")
    private String ruolo;

    @Column(name = "TOKEN_AUTH")
    private String tokenAuth;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

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

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getTokenAuth() {
        return tokenAuth;
    }

    public void setTokenAuth(String tokenAuth) {
        this.tokenAuth = tokenAuth;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
