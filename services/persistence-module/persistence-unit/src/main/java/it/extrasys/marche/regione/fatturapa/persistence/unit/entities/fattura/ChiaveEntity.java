package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "CHIAVE")
public class ChiaveEntity implements IEntity {

    private static final long serialVersionUID = -423234L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_CHIAVE")
    private BigInteger idChiave;

    @Column(name = "CHIAVE")
    private String chiave;

    public ChiaveEntity(){
        //needed from jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getChiave() {
        return chiave;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public BigInteger getIdChiave() {
        return idChiave;
    }

    public void setIdChiave(BigInteger idChiave) {
        this.idChiave = idChiave;
    }
}