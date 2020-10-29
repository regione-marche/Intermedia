package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.EsitoFTPType;
import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.EsitoTrasferimentoFTPType;
import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.ObjectFactory;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class CreaFileEsito implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaFileEsito.class);

    /*
    Nell'header, l'esito della validazione
     */

    @Override
    public void process(Exchange exchange) throws Exception {

        String unzipDir = (String) exchange.getIn().getHeader(FtpConstants.DIR_UNZIP);
        String nomeFileZip = (String) exchange.getIn().getHeader(FtpConstants.FILE_NAME_ZIP);
        String nomeFileEsito = "E".concat(nomeFileZip.substring(1));
        String replace = nomeFileEsito.replace(FtpConstants.EXT_ZIP_DONE, ".xml");

        //Crea il file di esito
        ObjectFactory factory = new ObjectFactory();

        EsitoFTPType esitoFTPType = factory.createEsitoFTPType();
        esitoFTPType.setIdentificativoNodo((String) exchange.getIn().getHeader(FtpConstants.COD_FISCALE_ENTE));
        esitoFTPType.setDataOraEsito(DateUtils.DateToXMLGregorianCalendar(new Date()));
        esitoFTPType.setDataOraRicezione(DateUtils.DateToXMLGregorianCalendar((Date) exchange.getIn().getHeader(FtpConstants.ORA_RICEZIONE)));
        esitoFTPType.setNomeSupporto(replace);
        esitoFTPType.setEsito(EsitoTrasferimentoFTPType.fromValue((String) exchange.getIn().getHeader(FtpConstants.ESITO_FTP)));
        esitoFTPType.setVersione(null);

        exchange.getIn().setHeader(Exchange.FILE_NAME, replace);
        exchange.getIn().setBody(esitoFTPType);


        //In caso di errore e se il file è stato già unzippato, cancella la cartella unzippata e sposto lo zip
        if (EsitoTrasferimentoFTPType.ET_02.value().equalsIgnoreCase((String) exchange.getIn().getHeader(FtpConstants.ESITO_FTP)) && StringUtils.isNotEmpty(unzipDir)) {
            cancellaCartellaUnzipAndMoveFileErrore(unzipDir);
        } else {
            cancellaCartellaUnzipAndMoveFileValidi(unzipDir, nomeFileZip);
        }
    }


    //Sposta i file validi da elaborare scartando il file di quadratura
    private static void cancellaCartellaUnzipAndMoveFileValidi(String unzipDir, String zipFile) throws IOException {
        File dir = new File(unzipDir);
        File dirDest = new File(dir.getParent().concat(File.separator).concat(FtpConstants.DIR_DA_ELABORARE).concat(File.separator).concat(dir.getName()));

        Arrays.stream(dir.listFiles()).forEach(f -> {
            if (!f.getName().startsWith("EO.")) {
                try {
                    FileUtils.moveFileToDirectory(f, dirDest, true);
                } catch (IOException e) {
                    LOG.info("FTP CA RICEZIONE: [ROUTE ${routeId}] ECCEZIONE: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        //cancella la directory dei file unzippati e sposta lo zip
        FileUtils.forceDelete(dir);
        try {
            //Se già esiste in elaborati lo cancello prima di spostarlo
            FileUtils.moveFileToDirectory(new File(dir.getParent().concat(File.separator).concat(zipFile)), new File(dir.getParent().concat(File.separator).concat(FtpConstants.DIR_ELABORATI)), true);
        } catch (FileExistsException e) {
            LOG.info("FTP CA RICEZIONE: Il file " + dir.getParent().concat(File.separator).concat(zipFile) + " esiste già nella cartella DA_ELABORARE");
            FileUtils.forceDelete(new File(dir.getParent().concat(File.separator).concat(FtpConstants.DIR_ELABORATI).concat(File.separator).concat(zipFile)));
            FileUtils.moveFileToDirectory(new File(dir.getParent().concat(File.separator).concat(zipFile)), new File(dir.getParent().concat(File.separator).concat(FtpConstants.DIR_ELABORATI)), true);
        }

    }


    //Sposta il file .zip in errore nella cartella 'SOSPESI'
    private static void cancellaCartellaUnzipAndMoveFileErrore(String unzipDir) throws IOException {
        File unzipDirFile = new File(unzipDir);
        if (unzipDirFile.exists()) {
            FileUtils.forceDelete(unzipDirFile);
        }

        File zipFile = new File(unzipDir + FtpConstants.EXT_ZIP_DONE);
        try {
            FileUtils.moveFileToDirectory(zipFile, new File(zipFile.getParent().concat(File.separator).concat(FtpConstants.DIR_SOSPESI).concat(File.separator).concat(zipFile.getName())), true);
        } catch (FileExistsException e) {
            //Il file esiste già nei file in sospeso: lo elimino e poi sposto il nuovo
            FileUtils.forceDelete(new File(zipFile.getParent().concat(File.separator).concat(FtpConstants.DIR_SOSPESI).concat(File.separator).concat(zipFile.getName())));
            FileUtils.moveFileToDirectory(zipFile, new File(zipFile.getParent().concat(File.separator).concat(FtpConstants.DIR_SOSPESI).concat(File.separator).concat(zipFile.getName())), true);
        }
    }
}
