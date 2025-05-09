package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.EsitoTrasferimentoFTPType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAMaxSizeException;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeFattura;
import it.extrasys.marche.regione.fatturapa.core.utils.validator.ValidatoreNomeNotificaEsitoCommittente;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor.ValidateQuadratura;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.validation.SchemaValidationException;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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

                    //Update record tabella FTP_REPORT_ST
                    .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO))
                    .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(true))
                    .setHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, simple("${property." + Exchange.EXCEPTION_CAUGHT + "}"))
                    .process("ftpReportStProcessor")
                    //.removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)
                    .removeHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION)

                    .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                    .inOnly(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                    .handled(true)
                .end()

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Processo il file di quadratura ${property." + FtpConstants.FILE_QUADRATURA + "} della directory: ${headers." + FtpConstants.DIR_UNZIP + "}")

                .removeHeaders("*", FtpConstants.FILE_NAME_ZIP, FtpConstants.DIR_UNZIP, FtpConstants.ESITO_FTP, FtpConstants.ORA_RICEZIONE, FtpConstants.COD_FISCALE_ENTE,FtpConstants.FTP,FtpConstants.ENDPOINT_FTP,FtpConstants.DIR_ROOT,FtpConstants.DIR_OUT
                ,FtpConstants.USERNAME,FtpConstants.PASSWORD)
                .removeProperties("*", FtpConstants.NUMERO_FILE_TOTALE, FtpConstants.FILE_QUADRATURA, FtpConstants.NOME_FILE_FATTURA, FtpConstants.ID_FTP_REPORT_ST_FI)

                .setBody(simple("${property." + FtpConstants.FILE_QUADRATURA + "}"))

                // @formatter:off
                //Non c'è il file di quadratura
                .choice()
                    .when(simple("${body.size()} != 1"))
                        .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Errore: trovati ${property." + FtpConstants.FILE_QUADRATURA +".size()} file di quadratura")

                        //Update record tabella FTP_REPORT_ST
                        .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO))
                        .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(true))
                        .setHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, simple("Trovati ${property." + FtpConstants.FILE_QUADRATURA + ".size()} file di quadratura"))
                        .process("ftpReportStProcessor")
                        //.removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)
                        .removeHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION)

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

                                //Update record tabella FTP_REPORT_ST
                                .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO))
                                .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(true))
                                .setHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, simple("File di quadratura NON valido"))
                                .process("ftpReportStProcessor")
                                //.removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)
                                .removeHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION)

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

                    //Update record tabella FTP_REPORT_ST
                    .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO))
                    .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(true))
                    .setHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, simple("${property." + Exchange.EXCEPTION_CAUGHT + "}"))
                    .process("ftpReportStProcessor")
                    //.removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)
                    .removeHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION)

                    .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                    .inOnly(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                    .handled(true)
                .end()

                .onException(FatturaPAMaxSizeException.class)
                    .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Superata dimensione massima per il file ${headers.CamelFileName}")
                    .log("FTP CA RICEZIONE: STACKTRACE: ${property." + Exchange.EXCEPTION_CAUGHT + "}")

                    //Update record tabella FTP_REPORT_ST
                    .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO))
                    .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(true))
                    .setHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, simple("${property." + Exchange.EXCEPTION_CAUGHT + "}"))
                    .process("ftpReportStProcessor")
                    //.removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)
                    .removeHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION)

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
                        File f = new File(((String) exchange.getIn().getHeader(FtpConstants.DIR_UNZIP)).concat(File.separator).concat((String) exchange.getIn().getBody()));
                        LOG.info("Path File: " + f.getAbsolutePath());
                        String s = FileUtils.readFileToString(f);

                        /*
                        *** Gestione Doppia Cartella al momento non attivo ***
                        String s = "";
                        try{
                            //s = FileUtils.readFileToString(new File(((String) exchange.getIn().getHeader(FtpConstants.DIR_UNZIP)).concat(File.separator).concat((String) exchange.getIn().getBody())));
                            String pathFile = exchange.getIn().getHeader(FtpConstants.DIR_UNZIP) + File.separator + exchange.getIn().getBody();
                            s = FileUtils.readFileToString(new File(pathFile));
                        }catch (Exception e){
                            //Caso della doppia cartella
                            String[] pathSplit = exchange.getIn().getHeader(FtpConstants.DIR_UNZIP, String.class).split(File.separator);
                            String pathFile = exchange.getIn().getHeader(FtpConstants.DIR_UNZIP) + File.separator + pathSplit[pathSplit.length -1] + File.separator + exchange.getIn().getBody();
                            s = FileUtils.readFileToString(new File(pathFile));
                        }
                        */

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
                        exchange.setProperty(FtpConstants.ABSOLUTE_PATH_FILE_FATTURA, f);
                    })

                    .choice()
                        .when(simple("${headers." + FtpConstants.TIPO_FILE + "} == 'FA'"))
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] ${headers." + Exchange.FILE_NAME + "} ricevuto FA")

                            // Nel body ho la fattura che devo validare. Devo estrarre l'eventuale firma e dunque validare il contenuto
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {

                                    Message msg = exchange.getIn();

                                    String nomeFile = msg.getHeader(Exchange.FILE_NAME, String.class);

                                    String absolutePath = (exchange.getProperty(FtpConstants.ABSOLUTE_PATH_FILE_FATTURA, File.class)).getAbsolutePath();
                                    File f = new File(absolutePath);

                                    byte[] content = org.apache.commons.io.FileUtils.readFileToByteArray(f);

                                    //Controllo dimensione Fattura Attiva
                                    double maxMB = Double.valueOf(exchange.getContext().resolvePropertyPlaceholders("{{enti.bridge.ftp.ca.fatturazione.attiva.max.size}}"));
                                    String unit = exchange.getContext().resolvePropertyPlaceholders("{{enti.bridge.ftp.ca.fatturazione.attiva.size.unit}}");

                                    String sizeFattura = CommonUtils.convertToStringRepresentation(content.length, unit);

                                    double sizeFattAttiva = Double.parseDouble(sizeFattura.replaceAll(" " + unit, ""));

                                    if(sizeFattAttiva > maxMB){
                                        LOG.error("Ricevuta Fattura Attiva " + nomeFile + " con size " + sizeFattura + " (Max Size " + maxMB + " " + unit + ")");
                                        throw new FatturaPAMaxSizeException();
                                    }

                                    if(nomeFile.toLowerCase().endsWith(".p7m")){

                                        LOG.info("FTP CA RICEZIONE: [ROUTE ${routeId}] Gestione Fattura Firmata (.p7m)");

                                        /* Messo fuori dall'if
                                        String absolutePath = (exchange.getProperty(FtpConstants.ABSOLUTE_PATH_FILE_FATTURA, File.class)).getAbsolutePath();
                                        File f = new File(absolutePath);

                                        byte[] content = org.apache.commons.io.FileUtils.readFileToByteArray(f);
                                        */

                                        byte[] contentUnsigned = removeP7MCodes(nomeFile, content);

                                        if (contentUnsigned != null) {
                                            LOG.debug(new String(contentUnsigned));
                                        }

                                        String fatturaElettronica = new String(contentUnsigned);

                                        FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);

                                        String fileFatturaString = JaxBUtils.getFatturaElettronicaAsString(fatturaElettronicaType);

                                        msg.setBody(fileFatturaString);

                                        exchange.removeProperty(FtpConstants.ABSOLUTE_PATH_FILE_FATTURA);

                                    }else {
                                        LOG.info("FTP CA RICEZIONE: [ROUTE ${routeId}] Gestione Fattura Non Firmata (.xml)");
                                    }
                                }

                                public byte[] removeP7MCodes(final String fileName, byte[] p7bytes) throws FatturaPAException {

                                    LOG.info("FTP CA RICEZIONE: [ROUTE ${routeId}] Gestione Fattura Firmata - Estrazione firma per la validazione START");

                                    try {

                                        if (p7bytes == null) {
                                            return p7bytes;
                                        }

                                        if (!fileName.toUpperCase().endsWith(".P7M")) {
                                            return p7bytes;
                                        }

                                        try {
                                            p7bytes = org.bouncycastle.util.encoders.Base64.decode(p7bytes);
                                        } catch (Exception e) {
                                            LOG.debug("File P7m not in base64, use content standard" + e.getMessage());
                                        }

                                        // This is where I get the exception
                                        CMSSignedData cms = new CMSSignedData(p7bytes);

                                        if (cms.getSignedContent() == null) {
                                            LOG.error("Unable to find signed Content during decoding from P7M for file: " + fileName);
                                            throw new FatturaPAException("Errore durante la fase di estrazione della firma");
                                        }

                                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                                        cms.getSignedContent().write(out);

                                        LOG.info("FTP CA RICEZIONE: [ROUTE ${routeId}] Gestione Fattura Firmata - Estrazione firma per la validazione END");

                                        return out.toByteArray();

                                    } catch (CMSException e) {
                                        LOG.error("CMSException from P7M for file: " + fileName, e);
                                        throw new FatturaPAException("Errore durante la fase di estrazione della firma. Msg: " + e.getMessage());
                                    } catch (IOException e) {
                                        LOG.error("IOException from P7M for file: " + fileName, e);
                                        throw new FatturaPAException("Errore durante la fase di estrazione della firma. Msg: " + e.getMessage());
                                    }
                                    //return null;
                                }
                            })

                            .to("validator:xml/FatturazioneElettronica.xsd")

                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] ${headers." + Exchange.FILE_NAME + "} valido")

                        .when(simple("${headers." + FtpConstants.TIPO_FILE + "} == 'EC'"))
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] ${headers." + Exchange.FILE_NAME + "} ricevuto EC")
                            .to("validator:xml/MessaggiTypes_v1.1.xsd")
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] ${headers." + Exchange.FILE_NAME + "} valido")

                        .otherwise()
                            .log("FTP CA RICEZIONE: [ROUTE ${routeId}] File ${headers." + Exchange.FILE_NAME + "} sconosciuto")
                    .end()
                .end()
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Tutti i file sono stati validati")

                //Update record tabella FTP_REPORT_ST
                .setHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, simple(FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO))
                .setHeader(FtpConstants.FTP_REPORT_ST_ERRORE, constant(false))
                .process("ftpReportStProcessor")
                //.removeProperty(FtpConstants.ID_FTP_REPORT_ST_FI)
                .removeHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION)

                //Invia il file di esito e ripulisce i file
                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_01.value()))
                .to(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)

                // @formatter:on
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");
    }
}