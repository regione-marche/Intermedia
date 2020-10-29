package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.EndpointAttivaCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by agosteeno on 24/02/15.
 */

@Entity
@Table(name = "ENTE", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_FISCALE_COMMITTENTE", "CODICE_UFFICIO"}))
public class EnteEntity implements IEntity {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_ENTE")
    private BigInteger idEnte;

    @Column(name = "ID_FISCALE_COMMITTENTE", nullable = false, length = 50)
    private String idFiscaleCommittente;

    @Column(name = "CODICE_UFFICIO", nullable = false, length = 50)
    private String codiceUfficio;

    @Column(name = "NOME", nullable = false, length = 50)
    private String nome;

    @Column(name = "coda_gestionale_ws_custom_avanzato", nullable = true, length = 50)
    private String codaGestionaleWsCustomAvazato;

    @Column(name = "EMAIL_PEC", nullable = true, length = 250)
    private String emailPec;

    @Column(name = "CODA_INVIO_ENTE", nullable = true, length = 100)
    private String codaInvioEnte;

    @Column(name = "CODA_INVIO_ENTE_ATTIVA", nullable = true, length = 100)
    private String codaInvioEnteAttiva;

    @Column(name = "CODA_PROTOCOLLO_CA", nullable = true, length = 100)
    private String codaProtocolloCa;

    @Column(name = "CODA_GESTIONALE_CA", nullable = true, length = 100)
    private String codaGestionaleCa;

    @OneToOne
    @JoinColumn(name = "ID_TIPO_CANALE", nullable = false)
    private TipoCanaleEntity tipoCanale;

    //PER CANALE AVANZATO
    @OneToOne
    @JoinColumn(name = "ID_ENDPOINT_PROTOCOLLO_CA", nullable = true)
    private EndpointCaEntity endpointProtocolloCa;

    //PER CANALE AVANZATO
    @OneToOne
    @JoinColumn(name = "ID_ENDPOINT_REGISTRAZIONE_CA", nullable = true)
    private EndpointCaEntity endpointRegistrazioneCa;

    //PER CANALE AVANZATO
    @OneToOne
    @JoinColumn(name = "ID_ENDPOINT_ESITO_COMMITTENTE_CA", nullable = true)
    private EndpointCaEntity endpointEsitoCommittenteCa;

    //PER CANALE AVANZATO
    @OneToOne
    @JoinColumn(name = "ID_ENTE_PALEO_CA", nullable = true)
    private EntePaleoCaEntity entePaleoCaEntity;

    //PER CANALE AVANZATO
    @OneToOne
    @JoinColumn(name = "ID_ENDPOINT_FATTURE_ATTIVA_CA", nullable = true)
    private EndpointAttivaCaEntity endpointFattureAttivaCa;

    //PER CANALE AVANZATO
    @OneToOne
    @JoinColumn(name = "ID_ENDPOINT_NOTIFICHE_ATTIVA_CA", nullable = true)
    private EndpointAttivaCaEntity endpointNotificheAttivaCa;


    //introdotto per canale avanzato, valido per tutti
    @Column(name = "DENOMINAZIONE_ENTE", nullable = true, length = 255)
    private String denominazioneEnte;

    //introdotto per canale avanzato, valido per tutti
    @Column(name = "CONTATTI", nullable = true, length = 255)
    private String contatti;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "CICLO_ATTIVO", nullable = false)
    @Type(String.class)
    private Boolean cicloAttivo;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "CICLO_PASSIVO", nullable = false)
    @Type(String.class)
    private Boolean cicloPassivo;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "CAMPI_OPZIONALI", nullable = false)
    @Type(String.class)
    private Boolean campiOpzionali;

    @Column(name = "AMBIENTE_CICLOATTIVO", nullable = true, length = 200)
    private String ambienteCicloAttivo;

    @Column(name = "AMBIENTE_CICLOPASSIVO", nullable = true, length = 200)
    private String ambienteCicloPassivo;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_ULTIMA_MODIFICA", nullable = false)
    private Date dataUltimaModifica;

    @OneToOne
    @JoinColumn(name = "ID_UTENTE_MODIFICA", referencedColumnName = "ID_UTENTE", nullable = true)
    private UtenteEntity utenteModifica;

    @ExternalValues({"true=T","false=F"})
    @Column(name = "INVIO_UNICO", nullable = true)
    @Type(String.class)
    private Boolean invioUnico;

    public EnteEntity() {
        //needed from jpa
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigInteger getIdEnte() {
        return idEnte;
    }

    public void setIdEnte(BigInteger idEnte) {
        this.idEnte = idEnte;
    }

    public String getIdFiscaleCommittente() {
        return idFiscaleCommittente;
    }

    public void setIdFiscaleCommittente(String idFiscaleCommittente) {
        this.idFiscaleCommittente = idFiscaleCommittente;
    }

    public String getCodiceUfficio() {
        return codiceUfficio;
    }

    public void setCodiceUfficio(String codiceUfficio) {
        this.codiceUfficio = codiceUfficio;
    }

    public String getCodaInvioEnteAttiva() {
        return codaInvioEnteAttiva;
    }

    public void setCodaInvioEnteAttiva(String codaInvioEnteAttiva) {
        this.codaInvioEnteAttiva = codaInvioEnteAttiva;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodaGestionaleWsCustomAvazato() {
        return codaGestionaleWsCustomAvazato;
    }

    public void setCodaGestionaleWsCustomAvazato(String codaGestionaleWsCustomAvazato) {
        this.codaGestionaleWsCustomAvazato = codaGestionaleWsCustomAvazato;
    }

    public String getEmailPec() {
        return emailPec;
    }

    public void setEmailPec(String emailPec) {
        this.emailPec = emailPec;
    }

    public String getCodaInvioEnte() {
        return codaInvioEnte;
    }

    public void setCodaInvioEnte(String codaInvioEnte) {
        this.codaInvioEnte = codaInvioEnte;
    }

    public String getCodaProtocolloCa() {
        return codaProtocolloCa;
    }

    public void setCodaProtocolloCa(String codaProtocolloCa) {
        this.codaProtocolloCa = codaProtocolloCa;
    }

    public String getCodaGestionaleCa() {
        return codaGestionaleCa;
    }

    public void setCodaGestionaleCa(String codaGestionaleCa) {
        this.codaGestionaleCa = codaGestionaleCa;
    }

    public TipoCanaleEntity getTipoCanale() {
        return tipoCanale;
    }

    public void setTipoCanale(TipoCanaleEntity tipoCanale) {
        this.tipoCanale = tipoCanale;
    }

    public EndpointCaEntity getEndpointProtocolloCa() {
        return endpointProtocolloCa;
    }

    public void setEndpointProtocolloCa(EndpointCaEntity endpointProtocolloCa) {
        this.endpointProtocolloCa = endpointProtocolloCa;
    }

    public EndpointCaEntity getEndpointRegistrazioneCa() {
        return endpointRegistrazioneCa;
    }

    public void setEndpointRegistrazioneCa(EndpointCaEntity endpointRegistrazioneCa) {
        this.endpointRegistrazioneCa = endpointRegistrazioneCa;
    }

    public EndpointCaEntity getEndpointEsitoCommittenteCa() {
        return endpointEsitoCommittenteCa;
    }

    public void setEndpointEsitoCommittenteCa(EndpointCaEntity endpointEsitoCommittenteCa) {
        this.endpointEsitoCommittenteCa = endpointEsitoCommittenteCa;
    }

    public EntePaleoCaEntity getEntePaleoCaEntity() {
        return entePaleoCaEntity;
    }

    public void setEntePaleoCaEntity(EntePaleoCaEntity entePaleoCaEntity) {
        this.entePaleoCaEntity = entePaleoCaEntity;
    }


    public void setEndpointFattureAttivaCa(EndpointAttivaCaEntity endpointFattureAttivaCa) {
        this.endpointFattureAttivaCa = endpointFattureAttivaCa;
    }

    public EndpointAttivaCaEntity getEndpointNotificheAttivaCa() {
        return endpointNotificheAttivaCa;
    }

    public String getDenominazioneEnte() {
        return denominazioneEnte;
    }

    public void setDenominazioneEnte(String denominazioneEnte) {
        this.denominazioneEnte = denominazioneEnte;
    }

    public String getContatti() {
        return contatti;
    }

    public void setContatti(String contatti) {
        this.contatti = contatti;
    }

    public void setEndpointNotificheAttivaCa(EndpointAttivaCaEntity endpointNotificheAttivaCa) {
        this.endpointNotificheAttivaCa = endpointNotificheAttivaCa;
    }

    public EndpointAttivaCaEntity getEndpointFattureAttivaCa() {
        return endpointFattureAttivaCa;
    }



    public Boolean getCicloPassivo() {
        return cicloPassivo;
    }

    public void setCicloPassivo(Boolean cicloPassivo) {
        this.cicloPassivo = cicloPassivo;
    }

    public Boolean getCampiOpzionali() {
        return campiOpzionali;
    }

    public void setCampiOpzionali(Boolean campiOpzionali) {
        this.campiOpzionali = campiOpzionali;
    }

    public String getAmbienteCicloAttivo() {
        return ambienteCicloAttivo;
    }

    public void setAmbienteCicloAttivo(String ambienteCicloAttivo) {
        this.ambienteCicloAttivo = ambienteCicloAttivo;
    }

    public String getAmbienteCicloPassivo() {
        return ambienteCicloPassivo;
    }

    public void setAmbienteCicloPassivo(String ambienteCicloPassivo) {
        this.ambienteCicloPassivo = ambienteCicloPassivo;
    }

    public Boolean isCicloAttivo() {
        return cicloAttivo;
    }

    public void setCicloAttivo(Boolean cicloAttivo) {
        this.cicloAttivo = cicloAttivo;
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

    public Boolean getInvioUnico() {
        return invioUnico;
    }

    public void setInvioUnico(Boolean invioUnico) {
        this.invioUnico = invioUnico;
    }
}
