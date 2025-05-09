package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor.CreaFileEsito;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor.FtpReportStProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.io.File;

public class InviaEsitoRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                .routeId(FtpConstants.INVIA_FILE_ESITO_ROUTE)

                .onException(Exception.class)
                    .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Exception")
                    .log("FTP CA RICEZIONE: STACKTRACE: ${property." + Exchange.EXCEPTION_CAUGHT + "}")

                    //Update record tabella FTP_REPORT_ST
                    .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_ESITO))
                    .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(true))
                    .setHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, simple("${property." + Exchange.EXCEPTION_CAUGHT + "}"))
                    .process("ftpReportStProcessor")
                    .removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)

                    .handled(true)
                .end()

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")

                .process(new CreaFileEsito())
                .marshal("fileEsitoQuadratura")

                //.toD(componentFtp + enteEntity.getEndpointFromFtpCa().getEndpoint() + dirRoot + File.separator + dirOut + "?username=" + enteEntity.getEndpointFromFtpCa().getUsernameWrite() + "&password=" + enteEntity.getEndpointFromFtpCa().getPasswordWrite() + "&passiveMode=true")
                .process(exchange -> {
                    String urlFtp = (((String) exchange.getIn().getHeader(FtpConstants.ENDPOINT_FTP)).concat(File.separator).concat((String) exchange.getIn().getHeader(FtpConstants.DIR_ROOT))
                            .concat(File.separator).concat((String) exchange.getIn().getHeader(FtpConstants.DIR_OUT)).concat("?username=").concat((String) exchange.getIn().getHeader(FtpConstants.USERNAME)).concat("&password=").concat((String) exchange.getIn().getHeader(FtpConstants.PASSWORD)).concat("&passiveMode=true"));
                    exchange.getIn().setHeader("urlFtpInvio", urlFtp);
                })

                .toD("${headers.urlFtpInvio}")

                //Update record tabella FTP_REPORT_ST
                .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_ESITO))
                .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(false))
                .process("ftpReportStProcessor")
                .removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");
    }
}