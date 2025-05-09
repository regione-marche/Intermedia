package it.extrasys.marche.regione.fatturapa.enti.bridge.giunta.processors.fatto;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaMetadatiPaleo;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 05/03/15.
 */
public class FattoRupHeaderProcessor implements Processor {

	private String codiceFiscaleRUP = "N.A.";
	private String nomeRUP;
	private String cognomeRUP;
	private String codiceUORUP;
	private String ruoloRUP;

	private static final Logger LOG = LoggerFactory.getLogger(FattoRupHeaderProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		String numeroFattura = exchange.getIn().getHeader("numeroFattura", String.class);
		FatturaElettronicaWrapper fattoOriginalBody = exchange.getIn().getHeader("fattoOriginalBody", FatturaElettronicaWrapper.class);
		Boolean rupAssegnato = (Boolean) exchange.getIn().getHeader("rupAssegnato");

		FatturaElettronicaMetadatiPaleo fatturaElettronicaMetadatiPaleo = fattoOriginalBody.getFatturaMetadatiMap().get(numeroFattura);

		if(fatturaElettronicaMetadatiPaleo != null) {
			// FIXME
			if (rupAssegnato == null) {
				if (fatturaElettronicaMetadatiPaleo.getRuoloRUP() == null || fatturaElettronicaMetadatiPaleo.getRuoloRUP().equals("")) {
					rupAssegnato = false;
					exchange.getIn().setHeader("rupAssegnato", rupAssegnato);
				} else {
					rupAssegnato = true;
					exchange.getIn().setHeader("rupAssegnato", rupAssegnato);
				}
			}

			if (rupAssegnato) {

				fillValues(fatturaElettronicaMetadatiPaleo);
			}
		}else{
			codiceFiscaleRUP = "N.A.";
			nomeRUP = "N.A.";
			cognomeRUP = "N.A.";
			codiceUORUP = "N.A.";
			ruoloRUP = "N.A.";
		}

		LOG.info("setting up values for fattura: " + numeroFattura);
		LOG.info("setting up values for rupCf: " + codiceFiscaleRUP);
		LOG.info("setting up values for rupNome: " + nomeRUP);
		LOG.info("setting up values for rupCognome: " + cognomeRUP);
		LOG.info("setting up values for rupUoCodiceDescr: " + codiceUORUP);
		LOG.info("setting up values for rupruolo: " + ruoloRUP);

		exchange.getIn().setHeader("rupCf", codiceFiscaleRUP);
		exchange.getIn().setHeader("rupNome", nomeRUP);
		exchange.getIn().setHeader("rupCognome", cognomeRUP);
		exchange.getIn().setHeader("rupUoCodiceDescr", codiceUORUP);
		exchange.getIn().setHeader("rupruolo", ruoloRUP);

	}

	private void fillValues(FatturaElettronicaMetadatiPaleo fatturaElettronicaMetadatiPaleo) {
		codiceFiscaleRUP = fatturaElettronicaMetadatiPaleo.getCodiceFiscaleRUP();
		nomeRUP = fatturaElettronicaMetadatiPaleo.getNomeRUP();
		cognomeRUP = fatturaElettronicaMetadatiPaleo.getCognomeRUP();
		codiceUORUP = fatturaElettronicaMetadatiPaleo.getCodiceUORUP();
		ruoloRUP = fatturaElettronicaMetadatiPaleo.getRuoloRUP();

		if (codiceFiscaleRUP == null || "".compareTo(codiceFiscaleRUP) == 0) {
			codiceFiscaleRUP = "N.A.";
		}
		if (nomeRUP == null || "".compareTo(nomeRUP) == 0) {
			nomeRUP = "N.A.";
		}
		if (cognomeRUP == null || "".compareTo(cognomeRUP) == 0) {
			cognomeRUP = "N.A.";
		}
		if (codiceUORUP == null || "".compareTo(codiceUORUP) == 0) {
			codiceUORUP = "N.A.";
		}
		if (ruoloRUP == null || "".compareTo(ruoloRUP) == 0) {
			ruoloRUP = "N.A.";
		}
	}

	public String getCodiceFiscaleRUP() {
		return codiceFiscaleRUP;
	}

	public void setCodiceFiscaleRUP(String codiceFiscaleRUP) {
		this.codiceFiscaleRUP = codiceFiscaleRUP;
	}

	public String getNomeRUP() {
		return nomeRUP;
	}

	public void setNomeRUP(String nomeRUP) {
		this.nomeRUP = nomeRUP;
	}

	public String getCognomeRUP() {
		return cognomeRUP;
	}

	public void setCognomeRUP(String cognomeRUP) {
		this.cognomeRUP = cognomeRUP;
	}

	public String getCodiceUORUP() {
		return codiceUORUP;
	}

	public void setCodiceUORUP(String codiceUORUP) {
		this.codiceUORUP = codiceUORUP;
	}

	public String getRuoloRUP() {
		return ruoloRUP;
	}

	public void setRuoloRUP(String ruoloRUP) {
		this.ruoloRUP = ruoloRUP;
	}

}
