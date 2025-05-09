package it.extrasys.marche.regione.fatturapa.persistence.unit.entities.storico;

import it.extrasys.marche.regione.fatturapa.core.api.persistence.IEntity;
import org.apache.openjpa.persistence.ExternalValues;
import org.apache.openjpa.persistence.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "FATTURA_ATTIVA_STORICIZZATA")

public class FatturaAttivaStoricizzataEntity implements IEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_FATTURA_ATTIVA_STORICIZZATA")
	private BigInteger idFatturaAttivaStoricizzata;

	@Column(name = "NOME_FILE_FATTURA", nullable = false)
	private String nomeFileFattura;

	@Column(name = "CODICE_UFFICIO_MITTENTE", nullable = false)
	private String codiceUfficioMittente;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_RICEZIONE", nullable = false)
	private Date dataRicezione;

	@Column(name = "IDENTIFICATIVO_SDI", nullable = false)
	private BigInteger identificativoSdI;

	@ExternalValues({"true=T","false=F"})
	@Column(name = "FATTURAZIONE_INTERNA", nullable = false)
	@Type(String.class)
	private Boolean fatturazioneInterna;

	@Column(name = "CODICE_DESTINATARIO")
	private String codiceDestinatario;

	@Column(name = "PEC_DESTINATARIO")
	private String pecDestinatario;

	@Column(name = "RICEVUTA_COMUNICAZIONE")
	private String ricevutaComunicazione;

	@Column(name = "NOTIFICHE_FATTURA")
	private String notificheFattura;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INSERIMENTO", nullable = false)
	private Date dataInserimento;

	public FatturaAttivaStoricizzataEntity() {
	}


	public BigInteger getIdFatturaAttivaStoricizzata() {
		return idFatturaAttivaStoricizzata;
	}

	public void setIdFatturaAttivaStoricizzata(BigInteger idFatturaAttivaStoricizzata) {
		this.idFatturaAttivaStoricizzata = idFatturaAttivaStoricizzata;
	}

	public String getNomeFileFattura() {
		return nomeFileFattura;
	}

	public void setNomeFileFattura(String nomeFileFattura) {
		this.nomeFileFattura = nomeFileFattura;
	}

	public String getCodiceUfficioMittente() {
		return codiceUfficioMittente;
	}

	public void setCodiceUfficioMittente(String codiceUfficioMittente) {
		this.codiceUfficioMittente = codiceUfficioMittente;
	}

	public Date getDataRicezione() {
		return dataRicezione;
	}

	public void setDataRicezione(Date dataRicezione) {
		this.dataRicezione = dataRicezione;
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

	public String getCodiceDestinatario() {
		return codiceDestinatario;
	}

	public void setCodiceDestinatario(String codiceDestinatario) {
		this.codiceDestinatario = codiceDestinatario;
	}

	public String getPecDestinatario() {
		return pecDestinatario;
	}

	public void setPecDestinatario(String pecDestinatario) {
		this.pecDestinatario = pecDestinatario;
	}

	public String getRicevutaComunicazione() {
		return ricevutaComunicazione;
	}

	public void setRicevutaComunicazione(String ricevutaComunicazione) {
		this.ricevutaComunicazione = ricevutaComunicazione;
	}

	public String getNotificheFattura() {
		return notificheFattura;
	}

	public void setNotificheFattura(String notificheFattura) {
		this.notificheFattura = notificheFattura;
	}

	public Date getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Date dataInserimento) {
		this.dataInserimento = dataInserimento;
	}
}
