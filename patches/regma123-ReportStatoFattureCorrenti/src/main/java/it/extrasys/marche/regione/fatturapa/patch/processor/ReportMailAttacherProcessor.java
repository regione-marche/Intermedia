package it.extrasys.marche.regione.fatturapa.patch.processor;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Created by agosteeno on 31/07/15.
 */
public class ReportMailAttacherProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ReportMailAttacherProcessor.class);

    private String pathReportFileFolder;

    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn();

        String nomeFileReport = (String)msg.getHeader("fileNameReport");

        LOG.info("ReportMailAttacherProcessor - nome file report: "+ nomeFileReport);

        if(nomeFileReport == null || "".equals(nomeFileReport)){
            throw new FatturaPAException("REGMA123 - ReportMailAttacherProcessor: nome file report vuoto o null!!!");
        }

        msg.addAttachment(nomeFileReport, new DataHandler(new FileDataSource(pathReportFileFolder + nomeFileReport)));

    }

    public String getPathReportFileFolder() {
        return pathReportFileFolder;
    }

    public void setPathReportFileFolder(String pathReportFileFolder) {
        this.pathReportFileFolder = pathReportFileFolder;
    }
}
