package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class RecuperaSegnaturaProtocolloFatturaProcessor implements Processor {

    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;


    @Override
    public void process(Exchange exchange) throws Exception {


        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        String segnaturaProtocolloFattura = fatturazionePassivaFatturaManager.getSegnaturaProtocolloByIdentificativoSdI(identificativoSdI);

        if (segnaturaProtocolloFattura == null || segnaturaProtocolloFattura.trim().isEmpty()) {
            throw new FatturaPAException("Nessun Numero Protocollo della Fattura/Lotto Fatture trovato avente identificativo SDI: " + identificativoSdI);
        }

        exchange.getIn().setBody(segnaturaProtocolloFattura, String.class);

    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }
}
