package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp.FtpReportStEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FtpReportStManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.math.BigInteger;

public class FtpReportStProcessor implements Processor {

    private FtpReportStManager ftpReportStManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String tipoOperazione = msg.getHeader("ftpReportStTipoOperazione", String.class);
        String nomeSupporto = msg.getHeader("nomeFileZip", String.class);
        String codiceEnte = msg.getHeader("ente", String.class);
        Boolean errore = msg.getHeader("ftpReportStErrore", Boolean.class);
        String descErrore = msg.getHeader("exception", String.class);

        switch (tipoOperazione){

            case "insertFO":

                FtpReportStEntity ftpReportStEntityFO = ftpReportStManager.salvaFO(codiceEnte, nomeSupporto);
                exchange.setProperty("idFtpReportStFO", ftpReportStEntityFO.getId());

                break;

            case "updateFO":

                BigInteger idFtpReportStFO = exchange.getProperty("idFtpReportStFO", BigInteger.class);
                ftpReportStManager.updateStatusFOSupporto(idFtpReportStFO, errore, descErrore);

                break;
        }
    }

    public FtpReportStManager getFtpReportStManager() {
        return ftpReportStManager;
    }

    public void setFtpReportStManager(FtpReportStManager ftpReportStManager) {
        this.ftpReportStManager = ftpReportStManager;
    }
}