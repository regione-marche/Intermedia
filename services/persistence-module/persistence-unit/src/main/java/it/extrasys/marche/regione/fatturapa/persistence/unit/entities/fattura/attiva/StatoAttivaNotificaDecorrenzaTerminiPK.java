package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoAttivaNotificaDecorrenzaTerminiPK implements Serializable{

    private BigInteger notificaDecorrenzaTerminiEntity;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getNotificaDecorrenzaTerminiEntity() {
        return notificaDecorrenzaTerminiEntity;
    }

    public void setNotificaDecorrenzaTerminiEntity(BigInteger notificaDecorrenzaTerminiEntity) {
        this.notificaDecorrenzaTerminiEntity = notificaDecorrenzaTerminiEntity;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! notificaDecorrenzaTerminiEntity.equals(((StatoAttivaNotificaDecorrenzaTerminiPK)o).getNotificaDecorrenzaTerminiEntity())) {
            return false;
        }
        if(! stato.equals(((StatoAttivaNotificaDecorrenzaTerminiPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (notificaDecorrenzaTerminiEntity != null) {
            h=+ notificaDecorrenzaTerminiEntity.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
