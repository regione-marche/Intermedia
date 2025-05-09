package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
* Entity Creata per problema riscontrato nel recupero della descrizione stato dell'entity originale
* */

@Entity
@Table (name = "CODIFICA_STATI")
public class CodificaStati2Entity implements IEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Column (name="COD_STATO", nullable = false)
    private String codStato;
    @Column(name="DESC_STATO", nullable = false)
    private String descStato;


    public CodificaStati2Entity(){
        //needed fro jpa
    }

    public String getCodStato() {
        return codStato;
    }

    public void setCodStato(String codStato) {
        this.codStato = codStato;
    }

    public String getDescStato() {
        return descStato;
    }

    public void setDescStato(String descStato) {
        this.descStato = descStato;
    }
}