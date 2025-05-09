package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FTP_CODIFICA_STATI_ESITO")
public class FtpCodificaStatiEsitoEntity implements IEntity {

    @Id
    @Column(name="COD_STATO", nullable = false)
    private String codStato;

    @Column(name="DESC_STATO", nullable = false)
    private String descStato;


    public FtpCodificaStatiEsitoEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public FTP_CODICI_STATO_ESITO getCodStato() {
        return FTP_CODICI_STATO_ESITO.parse(codStato);
    }

    public void setCodStato(FTP_CODICI_STATO_ESITO codStato) {
        this.codStato = codStato.getValue();
    }

    public String getDescStato() {
        return descStato;
    }

    public void setDescStato(String descStato) {
        this.descStato = descStato;
    }

    public enum FTP_CODICI_STATO_ESITO {

        EO_INVIATO("001"),
        EO_ERRORE_INVIO("002")
        ;

        private String value;

        FTP_CODICI_STATO_ESITO(String value) {
            this.value = value;
        }

        public static FTP_CODICI_STATO_ESITO parse(String codStato) {
            FTP_CODICI_STATO_ESITO codStatoFattura = null; // Default
            for (FTP_CODICI_STATO_ESITO temp : FTP_CODICI_STATO_ESITO.values()) {
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