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
@Table(name = "TIPO_CANALE")
public class TipoCanaleEntity implements IEntity {

    private static final long serialVersionUID = 1L;


    public enum TIPO_CANALE {

        PEC("001"),
        WS("002"),
        MAIL("003"),
        CA("004"),
        FTP("005");

        private String value;

        TIPO_CANALE(String value){this.value=value;}

        public String getValue() { return value; }

        public static TIPO_CANALE parse(String codTipoCanale) {
            TIPO_CANALE codTipoCanaleTmp = null; // Default
            for (TIPO_CANALE temp : TIPO_CANALE.values()) {
                if (temp.getValue().equals(codTipoCanale)) {
                    codTipoCanaleTmp = temp;
                    break;
                }
            }
            return codTipoCanaleTmp;
        }


    };

    @Id
    @Column(name="COD_TIPO_CANALE", nullable = false)
    private String codTipoCanale;

    @Column(name="DESC_CANALE", nullable = false)
    private String descCanale;

    public TipoCanaleEntity(){
        //needed fro jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public TIPO_CANALE getCodStato() {
        return TIPO_CANALE.parse(codTipoCanale);
    }

    public void setCodTipoCanale(TIPO_CANALE codTipoCanale) {
        this.codTipoCanale = codTipoCanale.getValue();
    }

    public String getDescCanale() {
        return descCanale;
    }

    public void setDescCanale(String descCanale) {
        this.descCanale = descCanale;
    }

    public String getCodTipoCanale() {
        return codTipoCanale;
    }

    public void setCodTipoCanale(String codTipoCanale) {
        this.codTipoCanale = codTipoCanale;
    }
}
