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
        if (((String) newExchange.getIn().getHeader(Exchange.FILE_NAME)).startsWith("EO.") ||
                ((String) newExchange.getIn().getHeader(Exchange.FILE_NAME)).startsWith("FI.")) {

            if(newExchange.getIn().getBody() instanceof byte[]){
                fileQuadratura.add(new String(newExchange.getIn().getBody(byte[].class)));
            }else{
                fileQuadratura.add((String) newExchange.getIn().getBody());
            }

        } else {
            fileFatture.add((String) newExchange.getIn().getHeader(Exchange.FILE_NAME));
        }

        /*
        *** Da utilizzare nel caso in cui si vuole gestire la doppia cartella ***
        String fileName = getFileName((String) newExchange.getIn().getHeader(Exchange.FILE_NAME));
        newExchange.getIn().setHeader(Exchange.FILE_NAME, fileName);

        if ((fileName).startsWith("EO.") ||
                (fileName).startsWith("FI.")) {
            fileQuadratura.add((String) newExchange.getIn().getBody());
        } else {
            fileFatture.add(fileName);
        }
        */

        newExchange.setProperty(FtpConstants.FILE_QUADRATURA, fileQuadratura);
        newExchange.setProperty(FtpConstants.NUMERO_FILE_TOTALE, countZipFile);
        newExchange.setProperty(FtpConstants.NOME_FILE_FATTURA, fileFatture);

        return newExchange;
    }

    /*
    *** Da utilizzare nel caso in cui si vuole gestire la doppia cartella ***
    private String getFileName (String exchangeFileName){

        String fileName = "";

        if(StringUtils.isNotEmpty(exchangeFileName) && exchangeFileName.contains(File.separator)){

            String[] split = exchangeFileName.split(File.separator);

            if(split.length > 1){
                fileName = split[split.length -1];
            }else{
                fileName = split[0];
            }

            return fileName;

        }else{
            return exchangeFileName;
        }
    }
    */
}