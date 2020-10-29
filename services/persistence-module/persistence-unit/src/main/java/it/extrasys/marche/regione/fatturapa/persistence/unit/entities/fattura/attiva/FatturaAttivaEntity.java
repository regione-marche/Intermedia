package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by agosteeno on 15/03/15.
 */
@Entity
@Table(name = "FATTURA_ATTIVA", uniqueConstraints = @UniqueConstraint(columnNames = {"IDENTIFICATIVO_SDI"}))
public class FatturaAttivaEntity implements IEntity{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID_FATTURA_ATTIVA")
    private BigInteger idFatturaAttiva;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_RICEZIONE_FROM_ENTI", nullable = true)
    private Date dataRicezioneFromEnti;

    @Column(name = "NOME_FILE", nullable = false)
    private String nomeFile;

    @ManyToOne
    @Column(name = "ENTE", nullable = false)
    private EnteEntity ente;

    @Lob
    @Column(name = "FILE_FATTURA_ORIGINALE", nullable = false)
    private byte[] fileFatturaOriginale;

    @Column(name = "IDENTIFICATIVO_SDI", nullable = true)
    private BigInteger identificativoSdi;

    //REGMA 112: aggiunta per flusso semplificato
    @ExternalValues({"true=T","false=F"})
    @Column(name = "FATTURAZIONE_INTERNA", nullable = false)
    @Type(String.class)
    private Boolean fatturazioneInterna;

    //REVO-3 per fatturazione verso privati
    @Column(name = "FORMATO_TRASMISSIONE", length = 5)
    private String formatoTrasmissione;

    //REVO-3 per fatturazione verso privati
    @Column(name = "CODICE_DESTINATARIO", length = 7)
    private String codiceDestinatario;

    //REVO-3 per fatturazione verso privati
    @Column(name = "PEC_DESTINATARIO", length = 256)
    private String pecDestinatario;

    @Column(name = "RICEVUTA_COMUNICAZIONE", length = 69)
    private String ricevutaComunicazione;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "FATTURAZIONE_TEST", nullable = false)
    @Type(String.class)
    private Boolean fatturazioneTest;

    public FatturaAttivaEntity() {

    }

    // CREATION DATE MANAGEMENT
    @PrePersist
    void createdAt() {
        this.dataRicezioneFromEnti = new Date();
    }


    public BigInteger getIdFatturaAttiva() {
        return idFatturaAttiva;
    }

    public void setIdFatturaAttiva(BigInteger idFatturaAttiva) {
        this.idFatturaAttiva = idFatturaAttiva;
    }

    public Date getDataRicezioneFromEnti() {
        return dataRicezioneFromEnti;
    }

    public void setDataRicezioneFromEnti(Date dataRicezioneFromEnti) {
        this.dataRicezioneFromEnti = dataRicezioneFromEnti;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public EnteEntity getEnte() {
        return ente;
    }

    public void setEnte(EnteEntity ente) {
        this.ente = ente;
    }

    public byte[] getFileFatturaOriginale() {
        return fileFatturaOriginale;
    }

    public void setFileFatturaOriginale(byte[] fileFatturaOriginale) {
        this.fileFatturaOriginale = fileFatturaOriginale;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public boolean isFatturazioneInterna() {
        return fatturazioneInterna;
    }

    public void setFatturazioneInterna(boolean fatturazioneInterna) {
        this.fatturazioneInterna = fatturazioneInterna;
    }

    public String getFormatoTrasmissione() {
        return formatoTrasmissione;
    }

    public void setFormatoTrasmissione(String formatoTrasmissione) {
        this.formatoTrasmissione = formatoTrasmissione;
    }

    public String getCodiceDestinatario() {
        return codiceDestinatario;
    }

    public void setCodiceDestinatario(String codiceDestinatario) {
        this.codiceDestinatario = codiceDestinatario;
    }

    public String getPecDestinatario() {
        return pecDestinatario;
    }

    public void setPecDestinatario(String pecDestinatario) {
        this.pecDestinatario = pecDestinatario;
    }

    public String getRicevutaComunicazione() {
        return ricevutaComunicazione;
    }

    public void setRicevutaComunicazione(String ricevutaComunicazione) {
        this.ricevutaComunicazione = ricevutaComunicazione;
    }

    public Boolean getFatturazioneInterna() {
        return fatturazioneInterna;
    }

    public void setFatturazioneInterna(Boolean fatturazioneInterna) {
        this.fatturazioneInterna = fatturazioneInterna;
    }

    public Boolean getFatturazioneTest() {
        return fatturazioneTest;
    }

    public void setFatturazioneTest(Boolean fatturazioneTest) {
        this.fatturazioneTest = fatturazioneTest;
    }
}