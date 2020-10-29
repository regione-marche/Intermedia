package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

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
@Table(name = "CODIFICA_STATI_ATTIVA")
public class CodificaStatiAttivaEntity implements IEntity {

    private static final long serialVersionUID = 1L;


    public enum CODICI_STATO_FATTURA_ATTIVA {

        RICEVUTA("001"),
        INVIATA("002");


        private String value;

        CODICI_STATO_FATTURA_ATTIVA(String value){this.value=value;}

        public String getValue() { return value; }

        public static CODICI_STATO_FATTURA_ATTIVA parse(String codStato) {
            CODICI_STATO_FATTURA_ATTIVA codStatoFattura = null; // Default
            for (CODICI_STATO_FATTURA_ATTIVA temp : CODICI_STATO_FATTURA_ATTIVA.values()) {
                if (temp.getValue().equals(codStato)) {
                    codStatoFattura = temp;
                    break;
                }
            }
            return codStatoFattura;
        }


    }

    @Id
    @Column(name="COD_STATO", nullable = false)
    private String codStato;

    @Column(name="DESC_STATO", nullable = false)
    private String descStato;

    public CodificaStatiAttivaEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public CODICI_STATO_FATTURA_ATTIVA getCodStato() {
        return CODICI_STATO_FATTURA_ATTIVA.parse(codStato);
    }

    public void setCodStato(CODICI_STATO_FATTURA_ATTIVA codStato) {
        this.codStato = codStato.getValue();
    }

    public String getDescStato() {
        return descStato;
    }

    public void setDescStato(String descStato) {
        this.descStato = descStato;
    }
}
