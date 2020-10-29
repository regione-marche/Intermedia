package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoFatturaAttivaPK implements Serializable{

    private BigInteger fatturaAttiva;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getFatturaAttiva() {
        return fatturaAttiva;
    }

    public void setFatturaAttiva(BigInteger fatturaAttiva) {
        this.fatturaAttiva = fatturaAttiva;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! fatturaAttiva.equals(((StatoFatturaAttivaPK)o).getFatturaAttiva())) {
            return false;
        }
        if(! stato.equals(((StatoFatturaAttivaPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (fatturaAttiva != null) {
            h=+ fatturaAttiva.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
