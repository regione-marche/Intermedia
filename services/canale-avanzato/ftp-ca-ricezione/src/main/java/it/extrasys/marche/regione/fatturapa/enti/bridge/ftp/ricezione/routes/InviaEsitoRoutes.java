package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor.CreaFileEsito;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.builder.RouteBuilder;

import java.io.File;

public class InviaEsitoRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {


        from(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                .routeId(FtpConstants.INVIA_FILE_ESITO_ROUTE)
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

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");
    }
}
