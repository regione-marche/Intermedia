package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by agosteeno on 14/03/15.
 */

@Entity
@Table(name = "MAIL_IN_TO_ENTI_ATTIVA", uniqueConstraints= @UniqueConstraint(columnNames = {"EMAIL", "ID_ENTE"}))
public class MailInToEntiAttivaEntity implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID_MAIL_IN_TO_ENTI_ATTIVA")
    private BigInteger idMailToEntiAttiva;


    @Column (name="EMAIL", nullable = false, length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name="ID_ENTE", nullable = false)
    private EnteEntity enteEntity;


    public MailInToEntiAttivaEntity(){

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public EnteEntity getEnteEntity() {
        return enteEntity;
    }

    public void setEnteEntity(EnteEntity enteEntity) {
        this.enteEntity = enteEntity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigInteger getIdMailToEntiAttiva() {
        return idMailToEntiAttiva;
    }

    public void setIdMailToEntiAttiva(BigInteger idMailToEntiAttiva) {
        this.idMailToEntiAttiva = idMailToEntiAttiva;
    }
}
