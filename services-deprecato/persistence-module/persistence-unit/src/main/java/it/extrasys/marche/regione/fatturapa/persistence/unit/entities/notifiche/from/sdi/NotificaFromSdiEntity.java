package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by agosteeno on 03/03/15.
 */
@Entity
@Table(name = "NOTIFICHE_FROM_SDI")
public class NotificaFromSdiEntity implements IEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID_NOTIFICA_FROM_SDI")
    private BigInteger idNotificaFromSdi;

    @OneToOne
    @JoinColumn(name="ID_COMUNICAZIONE", nullable = true)
    private NotificaFromEntiEntity notificaFromEntiEntity;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_RICEZIONE_RISPOSTA_SDI", nullable = true)
    private Date dataRicezioneRispostaSDI;

    @Column(name = "ESITO", nullable = false)
    private String esito;

    @Column(name = "DESCRIZIONE", nullable = false)
    private String descrizione;

    @Column(name="NOME_FILE_SCARTO", nullable = true)
    private String nomeFileScarto;

    @Lob
    @Column(name = "CONTENUTO_FILE", nullable = true)
    private String contenutoFile;

    @Column(name = "IDENTIFICATIVO_SDI", nullable = true)
    private BigInteger identificativoSdI;

    @Column(name = "NUMERO_PROTOCOLLO_NOTIFICA", nullable = true, length = 255)
    private String numeroProtocolloNotifica;

    @PrePersist
    void createdAt() {
        this.dataRicezioneRispostaSDI = new Timestamp(new Date().getTime());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdNotificaFromSdi() {
        return idNotificaFromSdi;
    }

    public void setIdNotificaFromSdi(BigInteger idNotificaFromSdi) {
        this.idNotificaFromSdi = idNotificaFromSdi;
    }

    public NotificaFromEntiEntity getNotificaFromEntiEntity() {
        return notificaFromEntiEntity;
    }

    public void setNotificaFromEntiEntity(NotificaFromEntiEntity notificaFromEntiEntity) {
        this.notificaFromEntiEntity = notificaFromEntiEntity;
    }

    public Date getDataRicezioneRispostaSDI() {
        return dataRicezioneRispostaSDI;
    }

    public void setDataRicezioneRispostaSDI(Date dataRicezioneRispostaSDI) {
        this.dataRicezioneRispostaSDI = dataRicezioneRispostaSDI;
    }

    public EsitoNotificaType getEsito() {
        return EsitoNotificaType.fromValue(esito);
    }

    public void setEsito(EsitoNotificaType esito) {
        this.esito = esito.value();
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNomeFileScarto() {
        return nomeFileScarto;
    }

    public void setNomeFileScarto(String nomeFileScarto) {
        this.nomeFileScarto = nomeFileScarto;
    }

    public String getContenutoFile() {
        return contenutoFile;
    }

    public void setContenutoFile(String contenutoFile) {
        this.contenutoFile = contenutoFile;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(BigInteger identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public String getNumeroProtocolloNotifica() {
        return numeroProtocolloNotifica;
    }

    public void setNumeroProtocolloNotifica(String numeroProtocolloNotifica) {
        this.numeroProtocolloNotifica = numeroProtocolloNotifica;
    }
}