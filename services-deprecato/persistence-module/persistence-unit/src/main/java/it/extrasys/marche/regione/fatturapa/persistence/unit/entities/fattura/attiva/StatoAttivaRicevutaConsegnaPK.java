package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoAttivaRicevutaConsegnaPK implements Serializable{

    private BigInteger ricevutaConsegnaEntity;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getRicevutaConsegnaEntity() {
        return ricevutaConsegnaEntity;
    }

    public void setRicevutaConsegnaEntity(BigInteger ricevutaConsegnaEntity) {
        this.ricevutaConsegnaEntity = ricevutaConsegnaEntity;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! ricevutaConsegnaEntity.equals(((StatoAttivaRicevutaConsegnaPK)o).getRicevutaConsegnaEntity())) {
            return false;
        }
        if(! stato.equals(((StatoAttivaRicevutaConsegnaPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (ricevutaConsegnaEntity != null) {
            h=+ ricevutaConsegnaEntity.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
