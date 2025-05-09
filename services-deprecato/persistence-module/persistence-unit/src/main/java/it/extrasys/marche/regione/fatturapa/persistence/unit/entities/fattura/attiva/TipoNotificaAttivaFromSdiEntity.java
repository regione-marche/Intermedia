package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by agosteeno on 21/03/15.
 */
@Entity
@Table(name = "TIPO_NOTIFICA_ATTIVA_FROM_SDI")
public class TipoNotificaAttivaFromSdiEntity {

    private static final long serialVersionUID = 1L;


    public enum TIPO_NOTIFICA_FROM_SDI {

        RICEVUTA_CONSEGNA("001"),
        NOTIFICA_MANCATA_CONSEGNA("002"),
        NOTIFICA_SCARTO("003"),
        NOTIFICA_ESITO("004"),
        NOTIFICA_DECORRENZA_TERMINI("005"),
        ATTESTAZIONE_TRASMISSIONE_FATTURA("006");

        private String value;

        TIPO_NOTIFICA_FROM_SDI(String value){this.value=value;}

        public String getValue() { return value; }

        public static TIPO_NOTIFICA_FROM_SDI parse(String codTipoCanale) {
            TIPO_NOTIFICA_FROM_SDI tipoNotificaFromSdiTmp = null; // Default
            for (TIPO_NOTIFICA_FROM_SDI temp : TIPO_NOTIFICA_FROM_SDI.values()) {
                if (temp.getValue().equals(codTipoCanale)) {
                    tipoNotificaFromSdiTmp = temp;
                    break;
                }
            }
            return tipoNotificaFromSdiTmp;
        }

    }

    @Id
    @Column(name="COD_TIPO_NOTIFICA_FROM_SDI", nullable = false)
    private String codTipoNotificaFromSdi;

    @Column(name="DESC_TIPO_NOTIFICA_FROM_SDI", nullable = false)
    private String descTipoNotificaFromSdi;

    public TipoNotificaAttivaFromSdiEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public TIPO_NOTIFICA_FROM_SDI getCodTipoNotificaFromSdi() {
        return TIPO_NOTIFICA_FROM_SDI.parse(codTipoNotificaFromSdi);
    }

    public void setCodTipoNotificaFromSdi(TIPO_NOTIFICA_FROM_SDI codTipoNotificaFromSdi) {
        this.codTipoNotificaFromSdi = codTipoNotificaFromSdi.getValue();
    }

    public String getDescTipoNotificaFromSdi() {
        return descTipoNotificaFromSdi;
    }

    public void setDescTipoNotificaFromSdi(String descTipoNotificaFromSdi) {
        this.descTipoNotificaFromSdi = descTipoNotificaFromSdi;
    }
}
