package it.extrasys.marche.regione.fatturapa.persistence.camel.model;

import java.math.BigInteger;
import java.util.List;

public class NotificheFatturaAttivaModel {

    private BigInteger idNotifica;

    private List<StatiNotificheAttivaModel> stato;

    private String tipoNotifica;


    public List<StatiNotificheAttivaModel> getStato() {
        return stato;
    }

    public void setStato(List<StatiNotificheAttivaModel> stato) {
        this.stato = stato;
    }

    public BigInteger getIdNotifica() {
        return idNotifica;
    }

    public void setIdNotifica(BigInteger idNotifica) {
        this.idNotifica = idNotifica;
    }

    public String getTipoNotifica() {
        return tipoNotifica;
    }

    public void setTipoNotifica(String tipoNotifica) {
        this.tipoNotifica = tipoNotifica;
    }


    public enum TIPO_NOTIFICA_FROM_SDI_SHORT {
        RC("001"),
        MC("002"),
        NS("003"),
        NE("004"),
        DT("005"),
        AT("006");

        private String value;

        TIPO_NOTIFICA_FROM_SDI_SHORT(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TIPO_NOTIFICA_FROM_SDI_SHORT parse(String codTipoCanale) {
            TIPO_NOTIFICA_FROM_SDI_SHORT tipoNotificaFromSdiTmp = null; // Default
            for (TIPO_NOTIFICA_FROM_SDI_SHORT temp : TIPO_NOTIFICA_FROM_SDI_SHORT.values()) {
                if (temp.getValue().equals(codTipoCanale)) {
                    tipoNotificaFromSdiTmp = temp;
                    break;
                }
            }
            return tipoNotificaFromSdiTmp;
        }
    }
}
