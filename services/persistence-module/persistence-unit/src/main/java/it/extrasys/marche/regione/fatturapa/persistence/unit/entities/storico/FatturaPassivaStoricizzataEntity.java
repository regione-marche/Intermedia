package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.storico;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "FATTURA_PASSIVA_STORICIZZATA")

public class FatturaPassivaStoricizzataEntity implements IEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_FATTURA_PASSIVA_STORICIZZATA")
	private BigInteger idFatturaPassivaStoricizzata;

	@Column(name = "NOME_FILE_FATTURA", nullable = false)
	private String nomeFileFattura;

	@Column(name = "CODICE_UFFICIO", nullable = false)
	private String codiceUfficio;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_RICEZIONE_SDI", nullable = false)
	private Date dataRicezioneSdi;

	@Column(name = "IDENTIFICATIVO_SDI", nullable = false)
	private BigInteger identificativoSdI;

	@ExternalValues({"true=T","false=F"})
	@Column(name = "FATTURAZIONE_INTERNA", nullable = false)
	@Type(String.class)
	private Boolean fatturazioneInterna;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FATTURA")
	private Date dataFattura;

	@Column(name = "NUMERO_FATTURA")
	private String numeroFattura;

	@Column(name = "NUMERO_PROTOCOLLO")
	private String numeroProtocollo;

	@Column(name = "CEDENTE_ID_FISCALE_IVA")
	private String cedenteIdFiscaleIva;

	@Column(name = "COMMITTENTE_ID_FISCALE_IVA")
	private String committenteIdFiscaleIva;

	@Column(name = "STATO_FATTURA")
	private String statoFattura;

	@Column(name = "TIPO_CANALE")
	private String tipoCanale;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INSERIMENTO", nullable = false)
	private Date dataInserimento;


	public FatturaPassivaStoricizzataEntity() {
	}

	// CREATION DATE MANAGEMENT
	@PrePersist
	void createdAt() {
		if(this.dataInserimento == null) {
			this.dataInserimento = new Date();
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public BigInteger getIdFatturaPassivaStoricizzata() {
		return idFatturaPassivaStoricizzata;
	}

	public void setIdFatturaPassivaStoricizzata(BigInteger idFatturaPassivaStoricizzata) {
		this.idFatturaPassivaStoricizzata = idFatturaPassivaStoricizzata;
	}

	public String getNomeFileFattura() {
		return nomeFileFattura;
	}

	public void setNomeFileFattura(String nomeFileFattura) {
		this.nomeFileFattura = nomeFileFattura;
	}

	public String getCodiceUfficio() {
		return codiceUfficio;
	}

	public void setCodiceUfficio(String codiceUfficio) {
		this.codiceUfficio = codiceUfficio;
	}

	public Date getDataRicezioneSdi() {
		return dataRicezioneSdi;
	}

	public void setDataRicezioneSdi(Date dataRicezioneSdi) {
		this.dataRicezioneSdi = dataRicezioneSdi;
	}

	public BigInteger getIdentificativoSdI() {
		return identificativoSdI;
	}

	public void setIdentificativoSdI(BigInteger identificativoSdI) {
		this.identificativoSdI = identificativoSdI;
	}

	public Boolean getFatturazioneInterna() {
		return fatturazioneInterna;
	}

	public void setFatturazioneInterna(Boolean fatturazioneInterna) {
		this.fatturazioneInterna = fatturazioneInterna;
	}

	public Date getDataFattura() {
		return dataFattura;
	}

	public void setDataFattura(Date dataFattura) {
		this.dataFattura = dataFattura;
	}

	public String getNumeroFattura() {
		return numeroFattura;
	}

	public void setNumeroFattura(String numeroFattura) {
		this.numeroFattura = numeroFattura;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public String getCedenteIdFiscaleIva() {
		return cedenteIdFiscaleIva;
	}

	public void setCedenteIdFiscaleIva(String cedenteIdFiscaleIva) {
		this.cedenteIdFiscaleIva = cedenteIdFiscaleIva;
	}

	public String getCommittenteIdFiscaleIva() {
		return committenteIdFiscaleIva;
	}

	public void setCommittenteIdFiscaleIva(String committenteIdFiscaleIva) {
		this.committenteIdFiscaleIva = committenteIdFiscaleIva;
	}

	public String getStatoFattura() {
		return statoFattura;
	}

	public void setStatoFattura(String statoFattura) {
		this.statoFattura = statoFattura;
	}

	public String getTipoCanale() {
		return tipoCanale;
	}

	public void setTipoCanale(String tipoCanale) {
		this.tipoCanale = tipoCanale;
	}

	public Date getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Date dataInserimento) {
		this.dataInserimento = dataInserimento;
	}
}
