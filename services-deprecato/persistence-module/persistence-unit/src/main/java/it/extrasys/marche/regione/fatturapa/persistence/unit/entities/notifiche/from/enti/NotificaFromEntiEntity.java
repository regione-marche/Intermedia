package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti;


import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 13/02/15.
 */

@Entity
@Table(name = "NOTIFICHE_FROM_ENTI")
public class NotificaFromEntiEntity implements IEntity {

    @Column(name = "MESSAGE_ID_COMMITTENTE", nullable = false, length = 14)
    String messageIdCommittente;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "ID_NOTIFICA")
    private BigInteger idNotifica;
    @Column(name = "IDENTIFICATIVO_SDI", nullable = false)
    private BigInteger identificativoSdI;

    @Column(name = "ID_FISCALE_COMMITTENTE", nullable = false, length = 30)
    private String idFiscaleCommittente;

    @Column(name = "CODICE_UFFICIO", nullable = false, length = 6)
    private String codUfficio;

    @Column(name = "NUMERO_PROTOCOLLO", nullable = false, length = 100)
    private String numeroProtocollo;

    @Column(name = "NUMERO_FATTURA", nullable = false, length = 20)
    private String numeroFattura;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_RICEZIONE_FROM_ENTE", nullable = false)
    private Date dataRicezioneFromEnte;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_FATTAURA", nullable = true)
    private Date dataFattura;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_INVIO_SDI", nullable = true)
    private Date dataInvioSDI;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_RICEZIONE_RISPOSTA_SDI", nullable = true)
    private Date dataRicezioneRispostaSDI;

    @Column(name = "MESSAGGIO_FROM_ENTI", nullable = true)
    private String messaggioFromEnti;

    @Column(name = "MESSAGGIO_FROM_SDI", nullable = true)
    private String messaggioFromSDI;

    @Column(name = "ESITO", nullable = false)
    private String esito;

    @Column(name = "DESCRIZIONE", nullable = false)
    private String descrizione;

    @Column(name = "ID_COMMUNICAZIONE", nullable = false, length = 100)
    private String idComunicazione;

    @Column(name = "NOME_FILE")
    private String nomeFile;

    @Lob
    @Column(name = "ORIGINAL_MESSAGE", nullable = true)
    private String originalMessage;

    @Transient
    private String nomeCedentePrestatore;

    @PrePersist
    void createdAt() {
        this.dataRicezioneFromEnte = new Timestamp(new Date().getTime());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdNotifica() {
        return idNotifica;
    }

    public void setIdNotifica(BigInteger idNotifica) {
        this.idNotifica = idNotifica;
    }

    public String getIdComunicazione() {
        return idComunicazione;
    }

    public void setIdComunicazione(String idComunicazione) {
        this.idComunicazione = idComunicazione;
    }

    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(BigInteger identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public Date getDataRicezioneFromEnte() {
        return dataRicezioneFromEnte;
    }

    public void setDataRicezioneFromEnte(Date dataRicezioneFromEnte) {
        this.dataRicezioneFromEnte = dataRicezioneFromEnte;
    }

    public Date getDataInvioSDI() {
        return dataInvioSDI;
    }

    public void setDataInvioSDI(Date dataInvioSDI) {
        this.dataInvioSDI = dataInvioSDI;
    }

    public Date getDataRicezioneRispostaSDI() {
        return dataRicezioneRispostaSDI;
    }

    public void setDataRicezioneRispostaSDI(Date dataRicezioneRispostaSDI) {
        this.dataRicezioneRispostaSDI = dataRicezioneRispostaSDI;
    }

    public String getMessaggioFromEnti() {
        return messaggioFromEnti;
    }

    public void setMessaggioFromEnti(String messaggioFromEnti) {
        this.messaggioFromEnti = messaggioFromEnti;
    }

    public String getMessaggioFromSDI() {
        return messaggioFromSDI;
    }

    public void setMessaggioFromSDI(String messaggioFromSDI) {
        this.messaggioFromSDI = messaggioFromSDI;
    }

    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    public void setNumeroProtocollo(String numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
    }

    public String getIdFiscaleCommittente() {
        return idFiscaleCommittente;
    }

    public void setIdFiscaleCommittente(String idFiscaleCommittente) {
        this.idFiscaleCommittente = idFiscaleCommittente;
    }

    public Date getDataFattura() {
        return dataFattura;
    }

    public void setDataFattura(Date dataFattura) {
        this.dataFattura = dataFattura;
    }

    public String getNumeroFattura() {
        return numeroFattura;
    }

    public void setNumeroFattura(String numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public CODICI_ESITO_NOTIFICA getEsito() {
        return CODICI_ESITO_NOTIFICA.parse(esito);
    }

    public void setEsito(CODICI_ESITO_NOTIFICA esito) {
        this.esito = esito.getValue();
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getCodUfficio() {
        return codUfficio;
    }

    public void setCodUfficio(String codUfficio) {
        this.codUfficio = codUfficio;
    }

    public String getMessageIdCommittente() {
        return messageIdCommittente;
    }

    public void setMessageIdCommittente(String messageIdCommittente) {
        this.messageIdCommittente = messageIdCommittente;
    }

    public String getNomeCedentePrestatore() {
        return nomeCedentePrestatore;
    }

    public void setNomeCedentePrestatore(String nomeCedentePrestatore) {
        this.nomeCedentePrestatore = nomeCedentePrestatore;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public enum CODICI_ESITO_NOTIFICA {

        ACCETTATA("EC01"),
        RIFIUTATA("EC02");

        private String value;

        CODICI_ESITO_NOTIFICA(String value) {
            this.value = value;
        }

        public static CODICI_ESITO_NOTIFICA parse(String codStato) {
            CODICI_ESITO_NOTIFICA codesitoNotifica = null; // Default
            for (CODICI_ESITO_NOTIFICA temp : CODICI_ESITO_NOTIFICA.values()) {
                if (temp.getValue().equals(codStato)) {
                    codesitoNotifica = temp;
                    break;
                }
            }
            return codesitoNotifica;
        }

        public String getValue() {
            return value;
        }
    }
}
