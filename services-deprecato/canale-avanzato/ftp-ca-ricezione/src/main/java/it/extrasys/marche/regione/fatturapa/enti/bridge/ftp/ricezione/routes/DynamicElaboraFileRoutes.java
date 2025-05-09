package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeFattura;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor.SalvaEsitoCommittenteProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/*
Una rotta per ogni ente che legge i file dalla cartella 'DA_ELABORARE' e li invia allo SDI
 */

public class DynamicElaboraFileRoutes extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicElaboraFileRoutes.class);


    private EnteEntity enteEntity;
    private String dirZip;

    public DynamicElaboraFileRoutes(EnteEntity enteEntity, String dirZip) {
        this.enteEntity = enteEntity;
        this.dirZip = dirZip;
    }


    @Override
    public void configure() throws Exception {

        from("file://".concat(dirZip).concat(enteEntity.getCodiceUfficio()).concat(File.separator).concat(FtpConstants.DIR_DA_ELABORARE) + "?move=.done&recursive=true&moveFailed=.failed")
                .routeId(FtpConstants.ELABORA_FILE_ROUTE + enteEntity.getCodiceUfficio())
                .log("[ROUTE ${routeId}] STARTED")

                .process(exchange -> {
                    exchange.getIn().setHeader("codiceUfficio", enteEntity.getCodiceUfficio());
                })

                .process(exchange -> {
                    String fileNamePath = (String) exchange.getIn().getHeader(Exchange.FILE_NAME); //nell forma dir/file.xml
                    String dirFile = fileNamePath.split(File.separator)[0];
                    String fileName = (String) exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY);

                    String tipoFile = "NN";

                    boolean validateEc = ValidatoreNomeNotificaEsitoCommittente.validate(fileName);
                    if (!validateEc) {
                        boolean validateFA = ValidatoreNomeFattura.validate(fileName);
                        if (validateFA) {
                            tipoFile = "FA";
                        }
                    } else {
                        tipoFile = "EC";
                    }

                    //Il nome del file zip che conteneva il file. Serve per salvarlo sul database
                    exchange.getIn().setHeader(FtpConstants.FILE_NAME_ZIP, dirFile.concat(FtpConstants.EXT_ZIP_DONE));
                    exchange.getIn().setHeader(FtpConstants.TIPO_FILE, tipoFile);
                })
                .log("[ROUTE ${routeId}] Tipo file ${headers." + FtpConstants.TIPO_FILE + "} - ${headers." + Exchange.FILE_NAME_ONLY + "} contenuto nel file zip ${headers." + FtpConstants.FILE_NAME_ZIP + "}")
                // @formatter:off
                .choice()
                    //ESITO COMMITTENTE
                    .when(simple("${headers." + FtpConstants.TIPO_FILE + "} == 'EC'"))
                        //.to(FtpConstants.FTP_ELABORA_ESITO_COMMITTENTE_ENDPOINT)
                        .inOnly("{{fatturapa.ftp.elabora.notifica.queue}}")
                    //FATTURA ATTIVA
                    .when(simple("${headers." + FtpConstants.TIPO_FILE + "} == 'FA'"))
                        //.to(FtpConstants.FTP_ELABORA_FATTURA_ELETTRONICA_ENDPOINT)
                        .inOnly("{{fatturapa.ftp.elabora.fattura.queue}}")

                .otherwise()
                        .log("[ROUTE ${routeId}] TIpo file ${headers." + FtpConstants.TIPO_FILE + "} sconosciuto!")
                .end()
                // @formatter:on
                .log("[ROUTE ${routeId}] FINISHED");

    }

    public EnteEntity getEnteEntity() {
        return enteEntity;
    }

    public void setEnteEntity(EnteEntity enteEntity) {
        this.enteEntity = enteEntity;
    }

    public String getDirZip() {
        return dirZip;
    }

    public void setDirZip(String dirZip) {
        this.dirZip = dirZip;
    }
}
