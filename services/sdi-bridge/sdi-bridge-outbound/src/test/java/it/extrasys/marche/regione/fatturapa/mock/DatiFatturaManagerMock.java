package it.extrasys.marche.regione.fatturapa.mock;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DatiFatturaManagerMock {

	private List<DatiFatturaEntity> returned = new ArrayList<DatiFatturaEntity>();

	public List<DatiFatturaEntity> getFatturaByNumeroProtocolloIdFiscaleCommittenteAndNumeroFattura(String numeroProtocollo, String idFiscaleCommittente, String numeroFattura) {
		return returned;
	}

	public void aggiornaStatoFatturaEsito(BigInteger idFattura, String codificaStatoEntity) throws FatturaPAException {
	}

	public List<DatiFatturaEntity> getReturned() {
		return returned;
	}

	public void addReturned(DatiFatturaEntity fattura) {
		this.returned.add(fattura);
	}

	public void aggiornaNumeroProgressivo(List<DatiFatturaEntity> datiFatturaEntityList, String numeroProgressivo) 	 {
	}

}
