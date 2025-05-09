package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

public class DeleteReportSTProcessor implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(DeleteReportSTProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String dirFO = msg.getHeader("dirReportSt", String.class) + File.separator + "FO";

        Collection<File> fileDaEliminareFO = FileUtils.listFiles(new File(dirFO), null, false);

        for (File f : fileDaEliminareFO.toArray(new File[fileDaEliminareFO.size()])) {
            LOG.info("BATCH FTP CA - INVIO REPORT ST: Eliminato il file nella cartella FO : " + dirFO + "/" + f.getName() + " prima di iniziare");
            FileUtils.forceDelete(f);
        }

        String dirFI = msg.getHeader("dirReportSt", String.class) + File.separator + "FI";

        Collection<File> fileDaEliminareFI = FileUtils.listFiles(new File(dirFI), null, false);

        for (File f : fileDaEliminareFI.toArray(new File[fileDaEliminareFI.size()])) {
            LOG.info("BATCH FTP CA - INVIO REPORT ST: Eliminato il file nella cartella FI : " + dirFO + "/" + f.getName() + " prima di iniziare");
            FileUtils.forceDelete(f);
        }
    }
}