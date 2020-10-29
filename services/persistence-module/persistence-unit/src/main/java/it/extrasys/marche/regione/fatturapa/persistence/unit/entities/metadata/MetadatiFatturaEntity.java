package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 26/01/15.
 */
@Entity
@Table(name = "METADATI_FATTURA")
public class MetadatiFatturaEntity  implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="IDENTIFICATIVO_SDI")
    private BigInteger identificativoSdI;

    @Column(name="NOME_FILE")
    private String nomeFile;

    @Column(name="NOME_FILE_METADATI")
    private String nomeFileMetadati;

    @Column(name="CODICE_DESTINATARIO")
    private String codiceDestinatario;

    @Column(name="FORMATO")
    private String formato;

    @Column(name="TENTATIVI_INVIO")
    private BigInteger tentativiInvio;

    @Column(name="MESSAGE_ID")
    private String messageId;

    @Column(name="NOTE")
    private String note;

    @Column(name="CONTENUTO_FILE")
    private byte[] contenutoFile;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_RICEZIONE_SDI")
    private Timestamp dataRicezioneSdi;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "FATTURAZIONE_TEST", nullable = false)
    @Type(String.class)
    private Boolean fatturazioneTest;


    @PrePersist
    void createdAt() {

        if(this.dataRicezioneSdi == null){
            this.dataRicezioneSdi = new Timestamp(new Date().getTime());
        }
    }


    public MetadatiFatturaEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(BigInteger identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getCodiceDestinatario() {
        return codiceDestinatario;
    }

    public void setCodiceDestinatario(String codiceDestinatario) {
        this.codiceDestinatario = codiceDestinatario;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public BigInteger getTentativiInvio() {
        return tentativiInvio;
    }

    public void setTentativiInvio(BigInteger tentativiInvio) {
        this.tentativiInvio = tentativiInvio;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getDataRicezioneSdi() {
        return dataRicezioneSdi;
    }

    public void setDataRicezioneSdi(Timestamp dataRicezioneSdi) {
        this.dataRicezioneSdi = dataRicezioneSdi;
    }

    public byte[] getContenutoFile() {
        return contenutoFile;
    }

    public void setContenutoFile(byte[] contenutoFile) {
        this.contenutoFile = contenutoFile;
    }

    public String getNomeFileMetadati() {
        return nomeFileMetadati;
    }

    public void setNomeFileMetadati(String nomeFileMetadati) {
        this.nomeFileMetadati = nomeFileMetadati;
    }

    public Boolean getFatturazioneTest() {
        return fatturazioneTest;
    }

    public void setFatturazioneTest(Boolean fatturazioneTest) {
        this.fatturazioneTest = fatturazioneTest;
    }
}
