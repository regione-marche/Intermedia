package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/03/15.
 */

public class RecuperaDataRicezioneFatturaProcessor implements Processor {


    FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        Date dataRicezione = fatturazionePassivaFatturaManager.recuperaDataRicezioneByIdentificativoSdI(identificativoSdI);

        exchange.getIn().setBody(dataRicezione);

    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }
}
