package it.extrasys.marche.regione.fatturapa.storicizzazione.processor;

import it.extrasys.marche.regione.fatturapa.persistence.camel.model.ReportFatturazioneStorico;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazioneAttivaStoricizzataManager;
import it.extrasys.marche.regione.fatturapa.storicizzazione.utils.StoricoConstant;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SalvaCancellaFatturaAttivaStoricizzataProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(SalvaCancellaFatturaAttivaStoricizzataProcessor.class);

    private FatturazioneAttivaStoricizzataManager fatturazioneAttivaStoricizzataManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Object[] fatturaAttiva = (Object[]) exchange.getIn().getBody();

        List<ReportFatturazioneStorico> reportFatturazioneStorico = fatturazioneAttivaStoricizzataManager.storicizzaFatturaAttivaPerIdentificativoSdi(fatturaAttiva);

        exchange.setProperty(StoricoConstant.REPORT_LIST_PROP, reportFatturazioneStorico);
    }





    public FatturazioneAttivaStoricizzataManager getFatturazioneAttivaStoricizzataManager() {
        return fatturazioneAttivaStoricizzataManager;
    }

    public void setFatturazioneAttivaStoricizzataManager(FatturazioneAttivaStoricizzataManager fatturazioneAttivaStoricizzataManager) {
        this.fatturazioneAttivaStoricizzataManager = fatturazioneAttivaStoricizzataManager;
    }
}