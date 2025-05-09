package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CanaleCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "ENDPOINT_ATTIVA_CA")
public class EndpointAttivaCaEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_ENDPOINT_CA")
    private BigInteger idEndpointCa;

    @OneToOne
    @JoinColumn(name="ID_CANALE_CA", nullable = false)
    private CanaleCaEntity canaleCa;

    @Column(name = "endpoint", length = 500, nullable = false)
    private String endpoint;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "path", length = 255)
    private String path;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "CERTIFICATO", nullable = false)
    @Type(String.class)
    private Boolean certificato;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_ULTIMA_MODIFICA", nullable = false)
    private Date dataUltimaModifica;

    @OneToOne
    @JoinColumn(name = "ID_UTENTE_MODIFICA", nullable = true)
    private UtenteEntity utenteModifica;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public EndpointAttivaCaEntity(){
        //needed from jpa
    }

    public BigInteger getIdEndpointCa() {
        return idEndpointCa;
    }

    public void setIdEndpointCa(BigInteger idEndpointCa) {
        this.idEndpointCa = idEndpointCa;
    }

    public CanaleCaEntity getCanaleCa() {
        return canaleCa;
    }

    public void setCanaleCa(CanaleCaEntity canaleCa) {
        this.canaleCa = canaleCa;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getCertificato() {
        return certificato;
    }

    public void setCertificato(Boolean certificato) {
        this.certificato = certificato;
    }

    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    public void setDataUltimaModifica(Date dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }

    public UtenteEntity getUtenteModifica() {
        return utenteModifica;
    }

    public void setUtenteModifica(UtenteEntity utenteModifica) {
        this.utenteModifica = utenteModifica;
    }
}