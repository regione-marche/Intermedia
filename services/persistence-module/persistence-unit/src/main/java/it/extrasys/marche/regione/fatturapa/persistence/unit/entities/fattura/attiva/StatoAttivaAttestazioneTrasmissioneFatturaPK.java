package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigInteger;

public class StatoAttivaAttestazioneTrasmissioneFatturaPK implements Serializable{

    private BigInteger attestazioneTrasmissioneFatturaEntity;

    private String stato;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getAttestazioneTrasmissioneFatturaEntity() {
        return attestazioneTrasmissioneFatturaEntity;
    }

    public void setAttestazioneTrasmissioneFatturaEntity(BigInteger attestazioneTrasmissioneFatturaEntity) {
        this.attestazioneTrasmissioneFatturaEntity = attestazioneTrasmissioneFatturaEntity;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public boolean equals(Object o){
        if(! attestazioneTrasmissioneFatturaEntity.equals(((StatoAttivaAttestazioneTrasmissioneFatturaPK)o).getAttestazioneTrasmissioneFatturaEntity())) {
            return false;
        }
        if(! stato.equals(((StatoAttivaAttestazioneTrasmissioneFatturaPK)o).getStato())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int h = 0;

        if (attestazioneTrasmissioneFatturaEntity != null) {
            h=+ attestazioneTrasmissioneFatturaEntity.hashCode();
        }
        if (stato != null) {
            h=+ super.hashCode();
        }
        return h;
    }


}
