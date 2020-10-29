package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "CODE")
public class CodeEntity implements IEntity {

    private static final long serialVersionUID = -4025110597543801056L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_CODE")
    private BigInteger idCode;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "LABEL")
    private String label;

    @Column(name = "TIPO_CANALE")
    private String tipoCanale;

    @Column(name = "CICLO")
    private String ciclo;


    public BigInteger getIdCode() {
        return idCode;
    }

    public void setIdCode(BigInteger idCode) {
        this.idCode = idCode;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTipoCanale() {
        return tipoCanale;
    }

    public void setTipoCanale(String tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
}
