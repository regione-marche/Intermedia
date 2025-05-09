package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZippedMessageProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(UnZippedMessageProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        //Integer countZipFile = 0;
        List<Integer> countZipFile = new ArrayList<>();
        countZipFile.add(0);
        List<String> fileQuadratura = new ArrayList<>();
        List<String> fileFatture = new ArrayList<>();

        Message msg = exchange.getIn();

        try {

            String destDir = msg.getHeader(FtpConstants.DIR_UNZIP, String.class);
            String zipFilePath = destDir + ".zip.done";

            FileInputStream fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);

            byte[] buffer = new byte[1024];

            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                String fileName = zipEntry.getName();

                File newFile = new File(destDir + File.separator + fileName);
                LOG.info("Unzipping to " + newFile.getAbsolutePath());
            /*
            //create directories for sub directories in zip
            new File(newFile.getParent()).mkdirs();
            */
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                zipEntry = zis.getNextEntry();

                aggregator(fileName, newFile, exchange, countZipFile, fileQuadratura, fileFatture);
            }

            zis.closeEntry();
            zis.close();
            fis.close();
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }

    private void aggregator(String fileNameUnzipped, File fileUnzipped, Exchange ex, List<Integer> countZipFile,
                            List<String> fileQuadratura, List<String> fileFatture) throws IOException {

        Integer countZF = countZipFile.get(0).intValue() + 1;
        countZipFile.add(0, countZF);

        //Salvo direttamente il file di quadratura, in modo da non andare a rileggerlo in seguito
        if ((fileNameUnzipped).startsWith("EO.") || (fileNameUnzipped).startsWith("FI.")) {

            byte[] fileQuadraturaBytes = FileUtils.readFileToByteArray(fileUnzipped);
            fileQuadratura.add(new String(fileQuadraturaBytes));

        } else {
            fileFatture.add(fileNameUnzipped);
        }

        ex.setProperty(FtpConstants.FILE_QUADRATURA, fileQuadratura);
        //ex.setProperty(FtpConstants.NUMERO_FILE_TOTALE, countZipFile);
        ex.setProperty(FtpConstants.NUMERO_FILE_TOTALE, countZF);
        ex.setProperty(FtpConstants.NOME_FILE_FATTURA, fileFatture);
    }
}