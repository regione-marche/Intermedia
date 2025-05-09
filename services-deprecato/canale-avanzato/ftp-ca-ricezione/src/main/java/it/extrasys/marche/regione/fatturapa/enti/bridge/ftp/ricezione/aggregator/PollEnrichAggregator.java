package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.aggregator;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class PollEnrichAggregator implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        oldExchange.getIn().removeHeaders("*", FtpConstants.FILE_NAME_ZIP, FtpConstants.DIR_UNZIP, FtpConstants.ESITO_FTP,  FtpConstants.ORA_RICEZIONE, FtpConstants.COD_FISCALE_ENTE);
        oldExchange.removeProperties("*", FtpConstants.NUMERO_FILE_TOTALE, FtpConstants.NOME_FILE_FATTURA,FtpConstants.FTP,FtpConstants.ENDPOINT_FTP,FtpConstants.DIR_ROOT,FtpConstants.DIR_OUT
                ,FtpConstants.USERNAME,FtpConstants.PASSWORD);

        if (newExchange != null) {
            newExchange.getIn().getHeaders().putAll(oldExchange.getIn().getHeaders());
            newExchange.getProperties().putAll(oldExchange.getProperties());
        }
        return newExchange;
    }
}
