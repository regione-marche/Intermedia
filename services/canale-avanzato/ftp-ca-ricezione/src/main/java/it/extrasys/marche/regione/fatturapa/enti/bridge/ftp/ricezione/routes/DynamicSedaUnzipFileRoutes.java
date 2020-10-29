package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.EsitoTrasferimentoFTPType;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.aggregator.UnzipZipFileAggregator;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class DynamicSedaUnzipFileRoutes extends RouteBuilder {

    private EnteEntity enteEntity;

    private String componentFtp;

    private String dirRoot;

    private String dirOut;

    public DynamicSedaUnzipFileRoutes(EnteEntity enteEntity, String componentFtp, String dirRoot, String dirOut) {
        this.enteEntity = enteEntity;
        this.componentFtp = componentFtp;
        this.dirOut = dirOut;
        this.dirRoot = dirRoot;
    }

    @Override
    public void configure() throws Exception {

        /*crea dinamicamente le code SEDA: Una rotta per ogni ente
        - unzippa il file in una sotto-cartella con nome del file .zip

    - Properties: -nomeFileQuadratura: il nome del fiel di quadratura
                  -numeroFileTotale: il numero totale dei files dello zip
    */
        from(FtpConstants.UNZIP_FILE_ENDPOINT.concat("-").concat(enteEntity.getCodiceUfficio()))
                .routeId(FtpConstants.UNZIP_FILE_ROUTE.concat(enteEntity.getCodiceUfficio()))
                .onException(Exception.class)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Exception")
                .log("FTP CA RICEZIONE: STACKTRACE: ${property." + Exchange.EXCEPTION_CAUGHT + "}")
                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                .to(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                .handled(true)
                .end()

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")

                .process(exchange -> {
                    String fileNameZip = (String) exchange.getIn().getHeader(Exchange.FILE_NAME_PRODUCED);
                    String dirUnzip = fileNameZip.replace(FtpConstants.EXT_ZIP_DONE, "");
                    FileUtils.forceMkdir(new File(dirUnzip));
                    exchange.getIn().setHeader(FtpConstants.FILE_NAME_ZIP, (String) exchange.getIn().getHeader(Exchange.FILE_NAME));
                    exchange.getIn().setHeader(FtpConstants.DIR_UNZIP, dirUnzip);
                })
                // @formatter:off
                .split(new ZipSplitter()).streaming()
                    .aggregationStrategy(new UnzipZipFileAggregator())
                    .convertBodyTo(String.class)
                    .toD("file://${headers." + FtpConstants.DIR_UNZIP + "}")
                .end()

                // @formatter:on
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Unzippato il file: ${headers" + Exchange.FILE_NAME + "} nella directory ${headers." + FtpConstants.DIR_UNZIP + "}")

                .to(FtpConstants.VALIDA_FILE_QUADRATURA_ENDPOINT)

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");

    }
}
