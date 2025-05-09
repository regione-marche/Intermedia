package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/02/15.
 */


@Entity
@Table(name = "MESSAGGI_SDI")
public class MessaggiSDIEntity implements IEntity {

    @Id
    @Column(name="IDENTIFICATIVO_SDI", nullable = false)
    private BigInteger identificativoSdI;

    @Lob
    @Column(name="MESSAGGIO_SDI", nullable = false)
    private String messaggioSdI;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_RICEZIONE", nullable = false)
    private Timestamp dataRicezione;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @PrePersist
    void createdAt() {
        this.dataRicezione = new Timestamp(new Date().getTime());
    }
    
    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    public void setIdentificativoSdI(BigInteger identificativoSdI) {
        this.identificativoSdI = identificativoSdI;
    }

    public String getMessaggioSdI() {
        return messaggioSdI;
    }

    public void setMessaggioSdI(String messaggioSdI) {
        this.messaggioSdI = messaggioSdI;
    }

    public Timestamp getDataRicezione() {
        return dataRicezione;
    }

}