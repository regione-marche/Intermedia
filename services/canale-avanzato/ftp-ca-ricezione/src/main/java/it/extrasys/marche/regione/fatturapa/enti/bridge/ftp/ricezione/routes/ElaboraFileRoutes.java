package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class ElaboraFileRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        //from(FtpConstants.FTP_ELABORA_ESITO_COMMITTENTE_ENDPOINT)
        from( "{{fatturapa.ftp.elabora.notifica.queue}}")
                .routeId(FtpConstants.FTP_ELABORA_ESITO_COMMITTENTE_ROUTE)
                .log("[ROUTE ${routeId}] STARTED")
                .log("FTP RICEZIONE: Invio messaggio alla coda di sdi outbound")
                .log(LoggingLevel.DEBUG,"FTP RICEZIONE: Invio messaggio alla coda di sdi outbound: ${body}")

                .transacted("XA_TX_REQUIRED").id("transactionId")

                .setHeader(FtpConstants.TIPO_MESSAGGIO, constant(FtpConstants.NOTIFICA_ESITO_COMMITTENTE))
                .setHeader(FtpConstants.CANALE_AVANZATO, constant(true))

                .convertBodyTo(String.class)

                .setProperty("fileOriginale", simple("${body}"))

                .unmarshal("esitoCommittenteJaxb")

                .process("salvaEsitoCommittenteProcessor")

                .convertBodyTo(String.class)

                .inOnly("{{sdi.outbound.invio.notifica.send.queue}}").id("idInoltraECToSdiFromFtp")

                .log("[ROUTE ${routeId}] FINISHED");


        from( "{{fatturapa.ftp.elabora.fattura.queue}}")
                .routeId(FtpConstants.FTP_ELABORA_FATTURA_ELETTRONICA_ROUTE)

                .log("[ROUTE ${routeId}] STARTED")
                .log(LoggingLevel.DEBUG, "FTP RICEZIONE: Invio messaggio alla coda di sdi outbound: ${body}")
                .log("FTP RICEZIONE: Invio messaggio alla coda di sdi outbound")

                .transacted("XA_TX_REQUIRED").id("transactionId")

                .setHeader(FtpConstants.NOME_FILE, simple("${headers." + Exchange.FILE_NAME_ONLY + "}"))
                .setHeader(FtpConstants.CANALE_AVANZATO, constant(true))

                /*
                .convertBodyTo(String.class)
                .setProperty("fileOriginale", simple("${body}"))
                .unmarshal("fatturaElettronicaJaxb")
                .process("salvaFatturaProcessor")
                .convertBodyTo(String.class)
                */

                .process("salvaFatturaProcessor")

                .inOnly("{{fatturazione.attiva.inoltra.fatture.queue}}").id("idInoltraFattureToSdiFromFtp")

                .log("[ROUTE ${routeId}] FINISHED");
    }
}
