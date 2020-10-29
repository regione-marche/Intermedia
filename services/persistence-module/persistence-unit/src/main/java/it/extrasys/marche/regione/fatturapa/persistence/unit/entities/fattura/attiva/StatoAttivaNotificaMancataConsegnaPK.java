package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoAttivaNotificaMancataConsegnaPK implements Serializable{

    private BigInteger notificaMancataConsegnaEntity;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getNotificaMancataConsegnaEntity() {
        return notificaMancataConsegnaEntity;
    }

    public void setNotificaMancataConsegnaEntity(BigInteger notificaMancataConsegnaEntity) {
        this.notificaMancataConsegnaEntity = notificaMancataConsegnaEntity;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! notificaMancataConsegnaEntity.equals(((StatoAttivaNotificaMancataConsegnaPK)o).getNotificaMancataConsegnaEntity())) {
            return false;
        }
        if(! stato.equals(((StatoAttivaNotificaMancataConsegnaPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (notificaMancataConsegnaEntity != null) {
            h=+ notificaMancataConsegnaEntity.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
