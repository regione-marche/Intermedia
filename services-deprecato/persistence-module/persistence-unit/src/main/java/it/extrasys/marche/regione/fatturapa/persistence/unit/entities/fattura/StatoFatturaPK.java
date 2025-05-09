package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 28/01/15.
 */
public class StatoFatturaPK implements Serializable{

    private BigInteger datiFattura;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getDatiFattura() {
        return datiFattura;
    }

    public void setDatiFattura(BigInteger datiFattura) {
        this.datiFattura = datiFattura;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }


    @Override
    public boolean equals(Object o){
        if(! datiFattura.equals(((StatoFatturaPK)o).getDatiFattura())) {
            return false;
        }
        if(! stato.equals(((StatoFatturaPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (datiFattura != null) {
            h=+ datiFattura.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
