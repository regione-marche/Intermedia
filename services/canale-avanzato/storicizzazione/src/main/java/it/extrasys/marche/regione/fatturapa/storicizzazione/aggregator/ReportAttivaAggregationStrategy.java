package it.extrasys.marche.regione.fatturapa.storicizzazione.aggregator;

import it.extrasys.marche.regione.fatturapa.persistence.camel.model.ReportFatturazioneStorico;
import it.extrasys.marche.regione.fatturapa.storicizzazione.utils.StoricoConstant;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.List;

public class ReportAttivaAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        List<ReportFatturazioneStorico> reportFinale = null;

        if (oldExchange == null) {
            reportFinale = new ArrayList<>();

        } else {
            reportFinale = (List<ReportFatturazioneStorico>) oldExchange.getProperty(StoricoConstant.REPORT_LIST_PROP_FINALE);
        }
        if ((List<ReportFatturazioneStorico>) newExchange.getProperty(StoricoConstant.REPORT_LIST_PROP) != null) {
            reportFinale.addAll((List<ReportFatturazioneStorico>) newExchange.getProperty(StoricoConstant.REPORT_LIST_PROP));
        }
        newExchange.setProperty(StoricoConstant.REPORT_LIST_PROP_FINALE, reportFinale);
        return newExchange;
    }


}
