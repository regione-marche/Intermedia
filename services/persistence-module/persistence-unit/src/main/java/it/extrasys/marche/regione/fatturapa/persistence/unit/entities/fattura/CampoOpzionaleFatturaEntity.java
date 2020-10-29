package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "CAMPO_OPZIONALE_FATTURA")
public class CampoOpzionaleFatturaEntity implements IEntity {

    private static final long serialVersionUID = -4232314L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_CAMPO")
    private BigInteger idCampo;

    @Column(name = "CAMPO")
    private String campo;

    @Column(name = "ID_TAG")
    private String idTag;

    public CampoOpzionaleFatturaEntity(){
        //needed from jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(BigInteger idCampo) {
        this.idCampo = idCampo;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }
}