package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by agosteeno on 21/03/15.
 */
@Entity
@Table(name = "NOTIFICHE_ATTIVA_FROM_SDI")
public class NotificheAttivaFromSdiEntity implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID_NOTIFICA_ATTIVA_FROM_SDI")
    private BigInteger idNotificaAttivaFromSdi;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_RICEZIONE_RISPOSTA_SDI", nullable = true)
    private Date dataRicezioneRispostaSDI;

    @Column(name="identificativoSdi", nullable = false)
    private BigInteger identificativoSdi;

    @Column(name="NOME_FILE", nullable = true)
    private String nomeFile;

    @Lob
    @Column(name = "ORIGINAL_MESSAGE", nullable = false)
    private String originalMessage;

    @OneToOne
    @JoinColumn(name="ID_TIPO_NOTIFICA_ATTIVA_FROM_SDI", nullable = false)
    private TipoNotificaAttivaFromSdiEntity tipoNotificaAttivaFromSdiEntity;

    @Column(name="RICEVUTA_COMUNICAZIONE", nullable = true)
    private String ricevutaComunicazione;

    //Mi serve solo per farlo restituire dalla query che fa la join con 'fattura_attiva'
    @Transient
    private BigInteger idFatturaAttiva;

    public NotificheAttivaFromSdiEntity(){

    }

    @PrePersist
    void createdAt() {
        this.dataRicezioneRispostaSDI = new Timestamp(new Date().getTime());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdNotificaAttivaFromSdi() {
        return idNotificaAttivaFromSdi;
    }

    public void setIdNotificaAttivaFromSdi(BigInteger idNotificaAttivaFromSdi) {
        this.idNotificaAttivaFromSdi = idNotificaAttivaFromSdi;
    }

    public Date getDataRicezioneRispostaSDI() {
        return dataRicezioneRispostaSDI;
    }

    public void setDataRicezioneRispostaSDI(Date dataRicezioneRispostaSDI) {
        this.dataRicezioneRispostaSDI = dataRicezioneRispostaSDI;
    }

    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    public void setIdentificativoSdi(BigInteger identificativoSdi) {
        this.identificativoSdi = identificativoSdi;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public TipoNotificaAttivaFromSdiEntity getTipoNotificaAttivaFromSdiEntity() {
        return tipoNotificaAttivaFromSdiEntity;
    }

    public void setTipoNotificaAttivaFromSdiEntity(TipoNotificaAttivaFromSdiEntity tipoNotificaAttivaFromSdiEntity) {
        this.tipoNotificaAttivaFromSdiEntity = tipoNotificaAttivaFromSdiEntity;
    }

    public String getRicevutaComunicazione() {
        return ricevutaComunicazione;
    }

    public void setRicevutaComunicazione(String ricevutaComunicazione) {
        this.ricevutaComunicazione = ricevutaComunicazione;
    }

    public BigInteger getIdFatturaAttiva() {
        return idFatturaAttiva;
    }

    public void setIdFatturaAttiva(BigInteger idFatturaAttiva) {
        this.idFatturaAttiva = idFatturaAttiva;
    }
}
