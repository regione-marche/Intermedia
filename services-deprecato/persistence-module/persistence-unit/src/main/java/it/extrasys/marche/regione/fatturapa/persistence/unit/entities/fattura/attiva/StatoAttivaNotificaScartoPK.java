package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoAttivaNotificaScartoPK implements Serializable{

    private BigInteger notificaScartoEntity;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getNotificaScartoEntity() {
        return notificaScartoEntity;
    }

    public void setNotificaScartoEntity(BigInteger notificaScartoEntity) {
        this.notificaScartoEntity = notificaScartoEntity;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! notificaScartoEntity.equals(((StatoAttivaNotificaScartoPK)o).getNotificaScartoEntity())) {
            return false;
        }
        if(! stato.equals(((StatoAttivaNotificaScartoPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (notificaScartoEntity != null) {
            h=+ notificaScartoEntity.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
