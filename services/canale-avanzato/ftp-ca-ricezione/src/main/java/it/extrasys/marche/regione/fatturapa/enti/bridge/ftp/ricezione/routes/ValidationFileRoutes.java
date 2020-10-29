package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.EsitoTrasferimentoFTPType;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeFattura;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor.ValidateQuadratura;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.validation.SchemaValidationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ValidationFileRoutes extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationFileRoutes.class);

    @Override
    public void configure() throws Exception {

        /*
        Valida il file di quadratura e se ok, valida tutti gli altri file .xml
        Se validazione è OK, sposta i file delle fatture nella cartella ELABORATI
         */

        from(FtpConstants.VALIDA_FILE_QUADRATURA_ENDPOINT)
                .routeId(FtpConstants.VALIDA_FILE_QUADRATURA_ROUTE)
                .onException(SchemaValidationException.class)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Validazione fallita per il file dui quadratura ${headers.CamelFileName}")
                .log("FTP CA RICEZIONE: STACKTRACE: ${property." + Exchange.EXCEPTION_CAUGHT + "}")
                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                .inOnly(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                .handled(true)
                .end()

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Processo il file di quadratura ${property." + FtpConstants.FILE_QUADRATURA + "} della directory: ${headers." + FtpConstants.DIR_UNZIP + "}")

                .removeHeaders("*", FtpConstants.FILE_NAME_ZIP, FtpConstants.DIR_UNZIP, FtpConstants.ESITO_FTP, FtpConstants.ORA_RICEZIONE, FtpConstants.COD_FISCALE_ENTE,FtpConstants.FTP,FtpConstants.ENDPOINT_FTP,FtpConstants.DIR_ROOT,FtpConstants.DIR_OUT
                ,FtpConstants.USERNAME,FtpConstants.PASSWORD)
                .removeProperties("*", FtpConstants.NUMERO_FILE_TOTALE, FtpConstants.FILE_QUADRATURA, FtpConstants.NOME_FILE_FATTURA)

                .setBody(simple("${property." + FtpConstants.FILE_QUADRATURA + "}"))

                // @formatter:off
                //Non c'è il file di quadratura
                .choice()
                    .when(simple("${body.size()} != 1"))
                        .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Errore: trovati ${property." + FtpConstants.FILE_QUADRATURA +".size()} file di quadratura")
                        .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                        .to(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)

                    .otherwise()
                        .setBody(simple("${body[0]}"))
                        //Valida il file di quadratura
                        .to("validator:xml/view-source_https___www.fatturapa.gov.it_export_fatturazione_sdi_ftp_v1.3_FtpTypes_v1.2.xsd")
                        .log("FTP CA RICEZIONE: [ROUTE ${routeId}] File Quadratura valido")

                        .unmarshal("fileEsitoQuadratura")
                        .process(new ValidateQuadratura())

                        .choice()
                            .when(simple("${headers." + FtpConstants.VALIDATED + "} == 'true'"))
                                .to(FtpConstants.VALIDA_FILE_FATTURE_ENDPOINT)
                            .otherwise()
                                //file di quadratura non valido
                                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Errore: file di quadratura NON valido")
                                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                                .to(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                        .end()
                .endChoice()
                // @formatter:on
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");


        from(FtpConstants.VALIDA_FILE_FATTURE_ENDPOINT)
                .routeId(FtpConstants.VALIDA_FILE_FATTURE_ROUTE)
                .onException(SchemaValidationException.class)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Validazione fallita per il file ${headers.CamelFileName}")
                .log("FTP CA RICEZIONE: STACKTRACE: ${property." + Exchange.EXCEPTION_CAUGHT + "}")
                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                .inOnly(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                .handled(true)
                .end()

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")

                // @formatter:off
                //Non è possibile usare il PollEnrich per leggere i file da validare perchè in caso di eccezione continua comunque l'esecuzione creando messaggi inflight
                .split().simple("${property." + FtpConstants.NOME_FILE_FATTURA + "}").stopOnException()
                    //Ricavo il tipo di file: -FA File Fattura Elettronica PA o B2B   -EC Notifica di esito committente
                    .process(exchange -> {
                        String s = FileUtils.readFileToString(new File(((String) exchange.getIn().getHeader(FtpConstants.DIR_UNZIP)).concat(File.separator).concat((String) exchange.getIn().getBody())));
                        //String tipoFile = ((String) exchange.getIn().getBody()).split("_")[2];
                        String tipoFile = "NN";

                        boolean validateEc = ValidatoreNomeNotificaEsitoCommittente.validate((String) exchange.getIn().getBody());
                        if (!validateEc) {
                            boolean validateFA = ValidatoreNomeFattura.validate((String) exchange.getIn().getBody());
                            if (validateFA) {
                                tipoFile = "FA";
                            }
                        } else {
                            tipoFile = "EC";
                        }

                        exchange.getIn().setHeader(Exchange.FILE_NAME, (String) exchange.getIn().getBody());
                        exchange.getIn().setHeader(FtpConstants.TIPO_FILE, tipoFile);
                        exchange.getIn().setBody(s);
                    })

                    .choice()
                        .when(simple("${headers." + FtpConstants.TIPO_FILE + "} == 'FA'"))
                            .to("validator:xml/FatturazioneElettronica.xsd")
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] ${headers." + Exchange.FILE_NAME + "} valido")

                        .when(simple("${headers." + FtpConstants.TIPO_FILE + "} == 'EC'"))
                            .to("validator:xml/MessaggiTypes_v1.1.xsd")
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] ${headers." + Exchange.FILE_NAME + "} valido")

                        .otherwise()
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] File ${headers." + Exchange.FILE_NAME + "}  sconosciuto")
                    .end()
                .end()
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Tutti i file sono stati validati")


                //Invia il file di esito e ripulisce i file
                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_01.value()))
                .to(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)

                // @formatter:on
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");

    }
}
