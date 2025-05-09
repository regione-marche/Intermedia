package it.extrasys.marche.regione.fatturapa.persistence.unit.converters;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CessionarioCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaBodyType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaHeaderType;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 29/01/15.
 */

public class FatturaElettronicaToStatoFatturaEntityConverter {

    private static final Logger LOG = LoggerFactory.getLogger(FatturaElettronicaToStatoFatturaEntityConverter.class);


    public StatoFatturaEntity convert(BigInteger identificativoSdI, String nomeFile, int posizioneFattura, FatturaElettronicaHeaderType fatturaElettronicaHeader, FatturaElettronicaBodyType fatturaElettronicaBody, boolean isFatturaInterna, Boolean isTest) {

        if (fatturaElettronicaBody == null || fatturaElettronicaHeader == null || identificativoSdI == null || identificativoSdI == null) {
            return null;
        }

        StatoFatturaEntity statoFatturaEntity = new StatoFatturaEntity();

        statoFatturaEntity.setStato(new CodificaStatiEntity());

        statoFatturaEntity.setDatiFattura(new DatiFatturaEntity());

        statoFatturaEntity.getDatiFattura().setPosizioneFattura(new Integer(posizioneFattura));

        statoFatturaEntity.getDatiFattura().setCedenteIdFiscaleIVA(cfOrPiva(fatturaElettronicaHeader.getCedentePrestatore()));

        statoFatturaEntity.getDatiFattura().setCommittenteIdFiscaleIVA(cfOrPiva(fatturaElettronicaHeader.getCessionarioCommittente()));

        statoFatturaEntity.getDatiFattura().setIdentificativoSdI(identificativoSdI);

        statoFatturaEntity.getDatiFattura().setNomeFile(nomeFile);

        statoFatturaEntity.getDatiFattura().setNumeroFattura(fatturaElettronicaBody.getDatiGenerali().getDatiGeneraliDocumento().getNumero().trim());

        statoFatturaEntity.getDatiFattura().setDataFattura(DateUtils.XMLGregorianCalendarToDate(fatturaElettronicaBody.getDatiGenerali().getDatiGeneraliDocumento().getData()));

        statoFatturaEntity.getDatiFattura().setCodiceDestinatario(fatturaElettronicaHeader.getDatiTrasmissione().getCodiceDestinatario());
        if (isTest) {
            statoFatturaEntity.getDatiFattura().setFatturazioneTest(Boolean.TRUE);
        } else {
            statoFatturaEntity.getDatiFattura().setFatturazioneTest(Boolean.FALSE);
        }
        //aggiunto per regma 112
        if (isFatturaInterna) {
            statoFatturaEntity.getDatiFattura().setFatturazioneInterna(true);
        } else {

            statoFatturaEntity.getDatiFattura().setFatturazioneInterna(false);
        }

        /*
           aggiunto per regma 141: verificare se nell'anagrafica del cedente prestatore e' presente il campo Denonimazione oppure i valori nome e cognome
         */
        if (fatturaElettronicaHeader.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione() == null || "".equals(fatturaElettronicaHeader.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione())) {
            //devono essere valorizzati i campi nome e cognome
            statoFatturaEntity.getDatiFattura().setNomeCedentePrestatore(fatturaElettronicaHeader.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome() + " " + fatturaElettronicaHeader.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome());
        } else {
            statoFatturaEntity.getDatiFattura().setNomeCedentePrestatore(fatturaElettronicaHeader.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione());
        }

        return statoFatturaEntity;

    }

    public static String cfOrPiva(Object actor) {

        if (actor instanceof CessionarioCommittenteType) {
            CessionarioCommittenteType committente = (CessionarioCommittenteType) actor;
            return committente.getDatiAnagrafici().getIdFiscaleIVA() != null ? committente.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() + committente.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() : committente.getDatiAnagrafici().getCodiceFiscale();
        }
        if (actor instanceof CedentePrestatoreType) {
            CedentePrestatoreType prestatore = (CedentePrestatoreType) actor;
            return prestatore.getDatiAnagrafici().getIdFiscaleIVA() != null ? prestatore.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() + prestatore.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() : prestatore.getDatiAnagrafici().getCodiceFiscale();
        }
        return null;
    }


}
