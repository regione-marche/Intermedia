package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.processor;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.ftp.FtpReportStEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class CreateReportSTProcessor implements Processor{

    private EnteManager enteManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String headerCsv = msg.getHeader("headerReportST", String.class);
        String ambiente = msg.getHeader("ambienteReportST", String.class);
        String tipoReportSt = msg.getHeader("tipoReportST", String.class);
        String pathTmpFileCsv = msg.getHeader("pathTmpFileCsv", String.class);
        String extFileReportSt = msg.getHeader("extFileReportSt", String.class);

        LinkedList<FtpReportStEntity> ftpReportStEntities = (LinkedList<FtpReportStEntity>) msg.getBody();

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(ftpReportStEntities.get(0).getCodiceEnte());

        StringBuffer csvHeader = new StringBuffer(headerCsv + "\n");

        switch (tipoReportSt){

            case "FO":

                String nomeFileFO = "ST." + enteEntity.getIdFiscaleCommittente().replaceAll("IT", "") + ".FO." + getDateTime(new Timestamp(System.currentTimeMillis()), "file") + extFileReportSt;
                String pathFileFO = pathTmpFileCsv + "/FO" + "/" + nomeFileFO;

                StringBuffer csvDataFO = getCsvDataFO(ambiente, ftpReportStEntities);
                writeCsv(enteEntity.getDenominazioneEnte(), csvHeader, csvDataFO, pathFileFO);

                msg.setBody(nomeFileFO);

                break;

            case "FI":

                String nomeFileFI = "ST." + enteEntity.getIdFiscaleCommittente().replaceAll("IT", "") + ".EO." + getDateTime(new Timestamp(System.currentTimeMillis()), "file") + extFileReportSt;
                String pathFileFI = pathTmpFileCsv + "/FI" + "/" + nomeFileFI;

                StringBuffer csvDataFI = getCsvDataFI(ambiente, ftpReportStEntities);
                writeCsv(enteEntity.getDenominazioneEnte(), csvHeader, csvDataFI, pathFileFI);

                msg.setBody(nomeFileFI);

                break;
        }
    }

    private StringBuffer getCsvDataFO(String ambiente, LinkedList<FtpReportStEntity> ftpReportStEntities){

        StringBuffer csvData = new StringBuffer("");

        for(FtpReportStEntity ftpReportStEntity : ftpReportStEntities){

            csvData.append(ftpReportStEntity.getNomeSupportoZip() + ";" + getDateTime(ftpReportStEntity.getTimestampInvio(), "csv") + ";" + ambiente + "\n");
        }

        return csvData;
    }

    private StringBuffer getCsvDataFI(String ambiente, LinkedList<FtpReportStEntity> ftpReportStEntities){

        StringBuffer csvData = new StringBuffer("");

        for(FtpReportStEntity ftpReportStEntity : ftpReportStEntities){

            String nomeFileInviato = ftpReportStEntity.getNomeSupportoZip().replaceAll("zip", "xml").replaceAll("FI", "EO");

            csvData.append(ftpReportStEntity.getNomeSupportoZip() + ";" + getDateTime(ftpReportStEntity.getTimestampRicezione(), "csv") + ";" + nomeFileInviato + ";"
                    + getDateTime(ftpReportStEntity.getTimestampInvio(), "csv") + ";" + ambiente + ";" + ftpReportStEntity.getStatoSupporto().getDescStato() + " - "
                    + ftpReportStEntity.getStatoEsito().getDescStato() + "\n");
        }

        return csvData;
    }

    private void writeCsv(String denominazioneEnte, StringBuffer csvHeader, StringBuffer csvData, String pathFile) throws FatturaPAException {

        PrintWriter pw;

        try{

            pw = new PrintWriter(pathFile);

            pw.write(denominazioneEnte + "\n\n");
            pw.write(csvHeader.toString());
            pw.write(csvData.toString());

            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            throw new FatturaPAException("Errore scrittura file csv. Msg: " + e.getMessage());
        }
    }

    private String getDateTime(Timestamp timestamp, String tipoConvesione){

        String dateTime = "";

        try{

            switch (tipoConvesione){

                case "file":

                    Date dateFile = new Date(System.currentTimeMillis());

                    SimpleDateFormat sdfDateFile = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sdfTimeFile = new SimpleDateFormat("HHmm");

                    //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                    dateTime = sdfDateFile.format(dateFile) + "." + sdfTimeFile.format(dateFile);

                    break;

                case "csv":

                    Date dateCsv = new Date(timestamp.getTime());

                    SimpleDateFormat sdfDateCsv = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdfTimeCsv = new SimpleDateFormat("HH:mm:ss");

                    //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                    dateTime = sdfDateCsv.format(dateCsv) + ";" + sdfTimeCsv.format(dateCsv);

                    break;
            }
        }catch (Exception e) {
            switch (tipoConvesione) {
                case "file":
                    dateTime = "date.time";
                    break;
                case "csv":
                    dateTime = "errore data consegna;errore time consegna";
                    break;
            }
        }

        return dateTime;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }
}