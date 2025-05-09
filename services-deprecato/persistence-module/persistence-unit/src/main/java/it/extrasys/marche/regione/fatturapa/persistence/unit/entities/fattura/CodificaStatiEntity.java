package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 26/01/15.
 */

@Entity
@Table(name = "CODIFICA_STATI")
public class CodificaStatiEntity implements IEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="COD_STATO", nullable = false)
    private String codStato;
    @Column(name="DESC_STATO", nullable = false)
    private String descStato;


    public CodificaStatiEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public CODICI_STATO_FATTURA getCodStato() {
        return CODICI_STATO_FATTURA.parse(codStato);
    }

    public void setCodStato(CODICI_STATO_FATTURA codStato) {
        this.codStato = codStato.getValue();
    }

    public String getDescStato() {
        return descStato;
    }

    public void setDescStato(String descStato) {
        this.descStato = descStato;
    }

    public enum CODICI_STATO_FATTURA {

        RICEVUTA("001"),
        PROTOCOLLATA("002"),
        REGISTRATA("003"),
        RICEVUTA_ACCETTAZIONE("004"),
        RICEVUTO_RIFIUTO("005"),
        INVIATA_ACCETTAZIONE("006"),
        INVIATO_RIFIUTO("007"),
        ACCETTATA_PER_DECORRENZA_TERMINI("008"),
        INOLTRATA_MAIL("009"),
        NOTIFICA_ACCETTATA("010"),
        NOTIFICA_SCARTATA("011"),
        RIFIUTATA_PER_VALIDAZIONE_FALLITA("012"),
        PEC_FATTURA_ACCETTATA("013"),
        PEC_FATTURA_CONSEGNATA("014"),
        PEC_DECORRENZA_ACCETTATA("015"),
        PEC_DECORRENZA_CONSEGNATA("016"),
        NOTIFICA_SCARTATA_CONSEGNATA("017"),
        NOTIFICA_SCARTATA_PER_REINVIO("018"),
        RICEVUTA_DECORRENZA_TERMINI("019"),

        //Pec CA
        //Fattura
        PEC_CA_FATTURA_INVIO_UNICO("020"),
        PEC_CA_FATTURA_INOLTRATA_PROTOCOLLO("021"),
        PEC_CA_FATTURA_INOLTRATA_REGISTRAZIONE("022"),
        PEC_CA_FATTURA_ACCETTATA("023"),
        PEC_CA_FATTURA_CONSEGNATA("024"),
        //Dec Termini
        PEC_CA_DEC_TERMINI_INVIO_UNICO("025"),
        PEC_CA_DEC_TERMINI_INOLTRATA_PROTOCOLLO("026"),
        PEC_CA_DEC_TERMINI_INOLTRATA_REGISTRAZIONE("027"),
        PEC_CA_DEC_TERMINI_ACCETTATA("028"),
        PEC_CA_DEC_TERMINI_CONSEGNATA("029"),
        //Notifica EC
        PEC_CA_EC_INOLTRATA_PROTOCOLLO("030"),
        PEC_CA_EC_ACCETTATA("031"),
        PEC_CA_EC_CONSEGNATA("032"),
        //Notifica Scarto EC
        PEC_CA_SCARTO_EC_INVIO_UNICO("033"),
        PEC_CA_SCARTO_EC_INOLTRATA_PROTOCOLLO("034"),
        PEC_CA_SCARTO_EC_INOLTRATA_REGISTRAZIONE("035"),
        PEC_CA_SCARTO_EC_ACCETTATA("036"),
        PEC_CA_SCARTO_EC_CONSEGNATA("037"),
        //Notifica EC Ricezione Ente
        PEC_CA_EC_RICEVUTA_ACCETTAZIONE("038"),
        PEC_CA_EC_RICEVUTO_RIFIUTO("039"),

        //WS CA
        //Fattura
        WS_CA_FATTURA_INVIO_UNICO("040"),
        WS_CA_FATTURA_PROTOCOLLATA("041"),
        WS_CA_FATTURA_REGISTRATA("042"),
        //Dec Termini
        WS_CA_DEC_TERMINI_INVIO_UNICO("043"),
        WS_CA_DEC_TERMINI_PROTOCOLLATA("044"),
        WS_CA_DEC_TERMINI_REGISTRATA("045"),
        //Notifica EC
        WS_CA_EC_PROTOCOLLATA("046"),
        //Notifica Scarto EC
        WS_CA_SCARTO_EC_INVIO_UNICO("047"),
        WS_CA_SCARTO_EC_PROTOCOLLATA("048"),
        WS_CA_SCARTO_EC_REGISTRATA("049"),
        //Notifica EC Ricezione Ente
        WS_CA_EC_RICEVUTA_ACCETTAZIONE("050"),
        WS_CA_EC_RICEVUTO_RIFIUTO("051"),

        //FTP
        FTP_CA_FATTURA_INVIO_UNICO("060"),
        FTP_CA_FATTURA_INVIO_PROTOCOLLO("061"),
        FTP_CA_FATTURA_INVIO_REGISTRAZIONE("062"),
        FTP_CA_DEC_TERMINI_INVIO_UNICO("063"),
        FTP_CA_DEC_TERMINI_INVIO_PROTOCOLLO("064"),
        FTP_CA_DEC_TERMINI_INVIO_REGISTRAZIONE("065"),
        FTP_CA_SCARTO_EC_INVIO_UNICO("066"),
        FTP_CA_SCARTO_EC_INVIO_PROTOCOLLO("067"),
        FTP_CA_SCARTO_EC_INVIO_REGISTRAZIONE("068"),
        FTP_CA_NOTIFICA_ESITO_COMMITTENTE_INVIO_PROTOCOLLO("069"),
        FTP_CA_EC_RICEVUTA_ACCETTAZIONE("070"),
        FTP_CA_EC_RICEVUTO_RIFIUTO("071"),
        ;

        private String value;

        CODICI_STATO_FATTURA(String value) {
            this.value = value;
        }

        public static CODICI_STATO_FATTURA parse(String codStato) {
            CODICI_STATO_FATTURA codStatoFattura = null; // Default
            for (CODICI_STATO_FATTURA temp : CODICI_STATO_FATTURA.values()) {
                if (temp.getValue().equals(codStato)) {
                    codStatoFattura = temp;
                    break;
                }
            }
            return codStatoFattura;
        }

        public String getValue() {
            return value;
        }
    }
}