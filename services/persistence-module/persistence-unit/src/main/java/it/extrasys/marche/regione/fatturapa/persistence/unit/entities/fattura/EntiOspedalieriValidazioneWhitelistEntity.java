package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by agosteeno on 24/11/15.
 */
@Entity
@Table(name = "ENTI_OSPEDALIERI_VALIDAZIONE_WHITELIST")
public class EntiOspedalieriValidazioneWhitelistEntity implements IEntity {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID_ENTI_OSPEDALIERI_VALIDAZIONE_WHITELIST", nullable = false)
    private BigInteger idEntiOspedalieriValidazioneWhitelist;

    /**
     * puo' essere la partita iva o il codice fiscale del mittente
     */
    @Column(name="ID_FISCALE_CEDENTE", nullable = false, length = 50)
    private String idFiscaleCedente;

    @Column(name="NOME_ENTE", nullable = false, length = 50)
    private String nomeEnte;


    public EntiOspedalieriValidazioneWhitelistEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdEntiOspedalieriValidazioneWhitelist() {
        return idEntiOspedalieriValidazioneWhitelist;
    }

    public void setIdEntiOspedalieriValidazioneWhitelist(BigInteger idEntiOspedalieriValidazioneWhitelist) {
        this.idEntiOspedalieriValidazioneWhitelist = idEntiOspedalieriValidazioneWhitelist;
    }

    public String getIdFiscaleCedente() {
        return idFiscaleCedente;
    }

    public void setIdFiscaleCedente(String idFiscaleCedente) {
        this.idFiscaleCedente = idFiscaleCedente;
    }

    public String getNomeEnte() {
        return nomeEnte;
    }

    public void setNomeEnte(String nomeEnte) {
        this.nomeEnte = nomeEnte;
    }
}
