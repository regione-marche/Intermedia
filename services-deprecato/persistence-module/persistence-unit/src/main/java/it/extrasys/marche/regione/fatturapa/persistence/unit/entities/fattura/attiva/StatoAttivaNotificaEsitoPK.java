package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoAttivaNotificaEsitoPK implements Serializable{

    private BigInteger notificaEsitoEntity;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getNotificaEsitoEntity() {
        return notificaEsitoEntity;
    }

    public void setNotificaEsitoEntity(BigInteger notificaEsitoEntity) {
        this.notificaEsitoEntity = notificaEsitoEntity;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! notificaEsitoEntity.equals(((StatoAttivaNotificaEsitoPK)o).getNotificaEsitoEntity())) {
            return false;
        }
        if(! stato.equals(((StatoAttivaNotificaEsitoPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (notificaEsitoEntity != null) {
            h=+ notificaEsitoEntity.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
