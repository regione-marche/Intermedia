package it.extrasys.marche.regione.fatturapa.persistence.unit.converters;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.CodificaStatiAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.StatoFatturaAttivaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 29/01/15.
 */

public class FatturaElettronicaAttivaToStatoFatturaAttivaEntityConverter {

    private static final Logger LOG = LoggerFactory.getLogger(FatturaElettronicaAttivaToStatoFatturaAttivaEntityConverter.class);


    public StatoFatturaAttivaEntity convert(String nomeFile, String formatoTrasmissione, String codiceDestinatario, String pecDestinatario,
                                            byte[] fileOriginale, EnteEntity enteEntity, Boolean isTest, boolean isFlussoSemplificato)  {

        if (nomeFile == null || fileOriginale == null ){
            return null;
        }

        StatoFatturaAttivaEntity statoFatturaAttivaEntity = new StatoFatturaAttivaEntity();

        statoFatturaAttivaEntity.setStato(new CodificaStatiAttivaEntity());

        statoFatturaAttivaEntity.setFatturaAttiva(new FatturaAttivaEntity());

        statoFatturaAttivaEntity.getFatturaAttiva().setNomeFile(nomeFile);

        statoFatturaAttivaEntity.getFatturaAttiva().setFileFatturaOriginale(fileOriginale);

        statoFatturaAttivaEntity.getFatturaAttiva().setEnte(enteEntity);

        //REGMA-21
        //statoFatturaAttivaEntity.getFatturaAttiva().setFatturazioneInterna(false);
        statoFatturaAttivaEntity.getFatturaAttiva().setFatturazioneInterna(isFlussoSemplificato);

        statoFatturaAttivaEntity.getFatturaAttiva().setFatturazioneTest(isTest);

        //REVO-3 provo ad inserirli solo se sono valorizzati
        if(formatoTrasmissione != null && !"".equals(formatoTrasmissione)){
            statoFatturaAttivaEntity.getFatturaAttiva().setFormatoTrasmissione(formatoTrasmissione);
        }

        if(codiceDestinatario != null && !"".equals(codiceDestinatario)){
            statoFatturaAttivaEntity.getFatturaAttiva().setCodiceDestinatario(codiceDestinatario);
        }

        if(pecDestinatario != null && !"".equals(pecDestinatario)){
            statoFatturaAttivaEntity.getFatturaAttiva().setPecDestinatario(pecDestinatario);
        }

        return statoFatturaAttivaEntity;

    }
}
