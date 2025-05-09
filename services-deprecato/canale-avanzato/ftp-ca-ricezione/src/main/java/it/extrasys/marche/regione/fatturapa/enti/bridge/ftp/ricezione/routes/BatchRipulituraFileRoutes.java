package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.filter.FilterDirectoryZipFile;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Date;

public class BatchRipulituraFileRoutes extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(BatchRipulituraFileRoutes.class);


    @Override
    public void configure() throws Exception {

        from("{{fatturapa.ftp.quartz.ripulitura.file}}")
                .routeId(FtpConstants.RIPULITURA_FILE_FTP_QUARTZ_ROUTE)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")
                .to(FtpConstants.RIPULITURA_FILE_FTP_ENDPOINT)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");


        from("{{fatturapa.ftp.jetty.ripulitura.file}}")
                .routeId(FtpConstants.RIPULITURA_FILE_FTP_JETTY_ROUTE)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")
                .to(FtpConstants.RIPULITURA_FILE_FTP_ENDPOINT)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");


        from(FtpConstants.RIPULITURA_FILE_FTP_ENDPOINT)
                .routeId(FtpConstants.RIPULITURA_FILE_FTP_ROUTE)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")

                .setHeader(FtpConstants.GIORNI, simple("{{fatturapa.ftp.ripulitara.day}}"))
                .setHeader(FtpConstants.DIR_ZIP, simple("{{fatturapa.dir.zip}}"))

                .process(exchange -> {
                    Date thresholdDate = (new DateTime().minusDays(Integer.parseInt((String) exchange.getIn().getHeader(FtpConstants.GIORNI)))).toDate();
                    File dirZip = new File((String) exchange.getIn().getHeader(FtpConstants.DIR_ZIP));
                    //Recupera tutte le directory degli enti
                    File[] directoryEnti = dirZip.listFiles(new FilterDirectoryZipFile());

                    for (File dir : directoryEnti) {
                        Collection<File> fileDaEliminare = FileUtils.listFiles(new File(dir.getPath().concat(File.separator).concat(FtpConstants.DIR_ELABORATI)), new AgeFileFilter(thresholdDate), new RegexFileFilter("^.*done"));

                        for (File f : fileDaEliminare.toArray(new File[fileDaEliminare.size()])) {
                            LOG.info("FTP CA BATCH RIPULITURA: Eliminato il file : " + dir + "/" + f.getName());
                            FileUtils.forceDelete(f);
                        }
                    }
                })

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");
    }
}
