package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.aggregator;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.List;

public class UnzipZipFileAggregator implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Integer countZipFile = 0;
        List<String> fileQuadratura = new ArrayList<>();
        List<String> fileFatture = new ArrayList<>();

        if (oldExchange != null) {
            countZipFile = (Integer) oldExchange.getProperty(FtpConstants.NUMERO_FILE_TOTALE);
            fileQuadratura = (List<String>) oldExchange.getProperty(FtpConstants.FILE_QUADRATURA);
            fileFatture = (List<String>) oldExchange.getProperty(FtpConstants.NOME_FILE_FATTURA);
        }
        countZipFile = countZipFile + 1;

        //Salvo direttamente il file di quadratura, in modo da non andare a rileggerlo in seguito
        if (((String) newExchange.getIn().getHeader(Exchange.FILE_NAME)).startsWith("EO.")) {
            fileQuadratura.add((String) newExchange.getIn().getBody());
        } else {
            fileFatture.add((String) newExchange.getIn().getHeader(Exchange.FILE_NAME));
        }

        newExchange.setProperty(FtpConstants.FILE_QUADRATURA, fileQuadratura);
        newExchange.setProperty(FtpConstants.NUMERO_FILE_TOTALE, countZipFile);
        newExchange.setProperty(FtpConstants.NOME_FILE_FATTURA, fileFatture);

        return newExchange;
    }
}
