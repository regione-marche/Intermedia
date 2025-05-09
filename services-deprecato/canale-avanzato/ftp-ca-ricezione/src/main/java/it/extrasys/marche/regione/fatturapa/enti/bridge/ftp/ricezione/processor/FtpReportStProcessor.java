package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
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

        String tipoOperazione = msg.getHeader(FtpConstants.FTP_REPORT_ST_TIPO_OPERZIONE, String.class);
        String nomeSupporto = msg.getHeader(FtpConstants.FILE_NAME_ZIP, String.class);
        String codiceEnte = msg.getHeader(FtpConstants.FTP_REPORT_ST_ENTE, String.class);
        Boolean errore = msg.getHeader(FtpConstants.FTP_REPORT_ST_ERRORE, Boolean.class);
        String descErrore = msg.getHeader(FtpConstants.FTP_REPORT_ST_EXCEPTION, String.class);

        switch (tipoOperazione){

            case FtpConstants.REPORT_ST_INSERT_FI:

                FtpReportStEntity ftpReportStEntityFI = ftpReportStManager.salvaFI(codiceEnte, nomeSupporto);
                exchange.setProperty(FtpConstants.ID_FTP_REPORT_ST_FI, ftpReportStEntityFI.getId());

                break;

            case FtpConstants.REPORT_ST_UPDATE_FI_SUPPORTO:

                BigInteger idFtpReportStFISupp = exchange.getProperty(FtpConstants.ID_FTP_REPORT_ST_FI, BigInteger.class);
                ftpReportStManager.updateStatusFISupporto(idFtpReportStFISupp, errore, descErrore);

                break;

            case FtpConstants.REPORT_ST_UPDATE_FI_ESITO:

                BigInteger idFtpReportStFIEsito = exchange.getProperty(FtpConstants.ID_FTP_REPORT_ST_FI, BigInteger.class);
                ftpReportStManager.updateStatusFIEsito(idFtpReportStFIEsito, errore, descErrore);

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