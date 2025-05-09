package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 26/01/15.
 */

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "DATI_FATTURA", uniqueConstraints = @UniqueConstraint(columnNames = { "IDENTIFICATIVO_SDI", "NUMERO_FATTURA", "DATA_FATTURA" }))
public class  DatiFatturaEntity implements IEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_DATI_FATTURA")
	private BigInteger idDatiFattura;

	@Column(name = "POSIZIONE_FATTURA", nullable = false)
	private Integer posizioneFattura;

	@Column(name = "CEDENTE_ID_FISCALE_IVA", nullable = false)
	private String cedenteIdFiscaleIVA;

	@Column(name = "COMMITTENTE_ID_FISCALE_IVA", nullable = false)
	private String committenteIdFiscaleIVA;

	@Column(name = "CODICE_DESTINATARIO", nullable = false, length = 50)
	private String codiceDestinatario;

	@Column(name = "NUMERO_PROTOCOLLO")
	private String numeroProtocollo;

	@Column(name = "IDENTIFICATIVO_SDI", nullable = false)
	private BigInteger identificativoSdI;

	@Column(name = "NUMERO_FATTURA", nullable = false)
	private String numeroFattura;

	@Column(name = "NOME_FILE", nullable = false)
	private String nomeFile;

	@Column(name = "PROGRESSIVO_INVIO_NOTIFICA", nullable = true)
	private String progressivoInvioNotifica;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FATTURA", nullable = false)
	private Date dataFattura;

	//REGMA 112: aggiunta per flusso semplificato
	@ExternalValues({"true=T","false=F"})
	@Column(name = "FATTURAZIONE_INTERNA", nullable = false)
	@Type(String.class)
	private Boolean fatturazioneInterna;

	//REGMA 141: aggiunta per infilarlo nell'oggetto delle PEC
	@Column(name = "NOME_CEDENTE_PRESTATORE", nullable = true)
	private String nomeCedentePrestatore;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_DECORRENZA_TERMINI_PREVISTA")
	private Date dataDecorrenzaTerminiPrevista;

/*
    // *** Rimosso a causa di un bug su OpenJPA, si veda REGMA-101 per maggiori info ***

	@OneToMany(mappedBy = "datiFattura", fetch = FetchType.EAGER)
	private List<StatoFatturaEntity> stati;
*/

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_CREAZIONE", nullable = false)
	private Date dataCreazione;

	@ManyToOne
	@JoinColumn(name = "ID_FILE_FATTURA", nullable = false)
	private FileFatturaEntity fileFatturaEntity;

	@ExternalValues({"true=T","false=F"})
	@Column(name = "FATTURAZIONE_TEST", nullable = false)
	@Type(String.class)
	private Boolean fatturazioneTest;


	public DatiFatturaEntity() {
		// needed from jpa
	}

	// CREATION DATE MANAGEMENT
	@PrePersist
	void createdAt() {
		if(this.dataCreazione == null) {
			this.dataCreazione = new Date();
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public BigInteger getIdDatiFattura() {
		return idDatiFattura;
	}

	public void setIdDatiFattura(BigInteger idDatiFattura) {
		this.idDatiFattura = idDatiFattura;
	}

	public BigInteger getIdentificativoSdI() {
		return identificativoSdI;
	}

	public void setIdentificativoSdI(BigInteger identificativoSdI) {
		this.identificativoSdI = identificativoSdI;
	}

	public String getNumeroFattura() {
		return numeroFattura;
	}

	public void setNumeroFattura(String numeroFattura) {
		this.numeroFattura = numeroFattura;
	}

	public Date getDataFattura() {
		return dataFattura;
	}

	public void setDataFattura(Date dataFattura) {
		this.dataFattura = dataFattura;
	}

	public Integer getPosizioneFattura() {
		return posizioneFattura;
	}

	public void setPosizioneFattura(Integer posizioneFattura) {
		this.posizioneFattura = posizioneFattura;
	}

	public String getCedenteIdFiscaleIVA() {
		return cedenteIdFiscaleIVA;
	}

	public void setCedenteIdFiscaleIVA(String cedenteIdFiscaleIVA) {
		this.cedenteIdFiscaleIVA = cedenteIdFiscaleIVA;
	}

	public String getCommittenteIdFiscaleIVA() {
		return committenteIdFiscaleIVA;
	}

	public void setCommittenteIdFiscaleIVA(String committenteIdFiscaleIVA) {
		this.committenteIdFiscaleIVA = committenteIdFiscaleIVA;
	}

	public String getCodiceDestinatario() {
		return codiceDestinatario;
	}

	public void setCodiceDestinatario(String codiceDestinatario) {
		this.codiceDestinatario = codiceDestinatario;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public String getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public String getProgressivoInvioNotifica() {
		return progressivoInvioNotifica;
	}

	public void setProgressivoInvioNotifica(String progressivoInvioNotifica) {
		this.progressivoInvioNotifica = progressivoInvioNotifica;
	}

	public Boolean getFatturazioneTest() {
		return fatturazioneTest;
	}

	public void setFatturazioneTest(Boolean fatturazioneTest) {
		this.fatturazioneTest = fatturazioneTest;
	}

	// *** Rimosso a causa di un bug su OpenJPA, si veda REGMA-101 per maggiori info ***

    /*public List<StatoFatturaEntity> getStati() {
        return stati;
    }

    public StatoFatturaEntity getLastStato() {

        StatoFatturaEntity returned = stati.get(0);
        for (StatoFatturaEntity stato : stati) {
            if (stato.getData().getTime() >= returned.getData().getTime())
                returned = stato;
        }

        return returned;
    }

    public void setStati(List<StatoFatturaEntity> stati) {
        this.stati = stati;
    }
*/
    public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public FileFatturaEntity getFileFatturaEntity() {
		return fileFatturaEntity;
	}

	public void setFileFatturaEntity(FileFatturaEntity fileFatturaEntity) {
		this.fileFatturaEntity = fileFatturaEntity;
	}

	public Boolean getFatturazioneInterna() {
		return fatturazioneInterna;
	}

	public void setFatturazioneInterna(Boolean fatturazioneInterna) {
		this.fatturazioneInterna = fatturazioneInterna;
	}

	public String getNomeCedentePrestatore() {
		return nomeCedentePrestatore;
	}

	public void setNomeCedentePrestatore(String nomeCedentePrestatore) {
		this.nomeCedentePrestatore = nomeCedentePrestatore;
	}

	public Date getDataDecorrenzaTerminiPrevista() {
		return dataDecorrenzaTerminiPrevista;
	}

	public void setDataDecorrenzaTerminiPrevista(Date dataDecorrenzaTerminiPrevista) {
		this.dataDecorrenzaTerminiPrevista = dataDecorrenzaTerminiPrevista;
	}
}