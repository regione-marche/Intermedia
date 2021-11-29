package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.utils;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.NumeroFileType;
import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.ObjectFactory;
import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.QuadraturaFTPType;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class FtpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FtpUtils.class);


    public void creaFileZip(Exchange exchange) throws IOException, JAXBException {

        List<FatturaFtpModel> fatture = (List<FatturaFtpModel>) exchange.getIn().getBody();
        Map<String, List<FatturaFtpModel>> zipFileMap = new HashMap<>();
        String tipoInvio = (String) exchange.getIn().getHeader("tipoFile");

        double maxSizeZip = Double.parseDouble((String) exchange.getIn().getHeader("maxSizeZip")) * 1024 * 1024;
        String dirZip = ((String) exchange.getIn().getHeader("dirZip")).concat((String) exchange.getIn().getHeader("ente")).concat(File.separator);

       // DateTime now = new DateTime();
        ZoneId idz = ZoneId.of("Europe/Rome");
        Instant now = Instant.now();
        String codFiscale = fatture.get(0).getIdFiscaleEnte();

        //Crea il file zip e le relative dir
        String fileNameZip = FtpUtils.generateFileName(now, codFiscale, dirZip, FtpConstants.FO_FILE, "001", ".zip.done");
        File fileZip = new File(fileNameZip);
        fileZip.getParentFile().mkdirs();

        //Aggiunge il path del primo zip da inviare
        zipFileMap.put(fileZip.getName(), new ArrayList<>());

        //Genera il file xml di esito
        //String fileNameEsito = FtpUtils.generateFileName(now, codFiscale, dirZip, FtpConstants.EO_FILE, "001", ".xml");
        String fileNameEsito = FtpUtils.generateFileName(now, codFiscale, dirZip, FtpConstants.FO_FILE, "001", ".xml");

        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(fileZip));
        ZipEntry zipEntry = null;
        ZipEntry zipEntryMetadati = null;
        double currentSize = 0;

        for (FatturaFtpModel fm : fatture) {
            boolean fileDuplicato = false;
            zipEntry = new ZipEntry(fm.getNomeFile());
            //Se c'è il file dei metadati li aggiungo allo zip
            if (StringUtils.isNotEmpty(fm.getNomeFileMetadati())) {
                zipEntryMetadati = new ZipEntry(fm.getNomeFileMetadati());
            }
            if (currentSize >= maxSizeZip) {
                //Genera il file di quadratura e lo aggiunge allo zip
                QuadraturaFTPType quadraturaFTPType = generateFileEsitoFtp(fileNameEsito, fileZip.getName(), codFiscale, zipFileMap.get(fileZip.getName()), tipoInvio);
                //Aggiunge il file di esito allo zip
                double zipEntrySize = addFileEsitoToZip(new File(fileNameEsito), zipEntry, zipOut);

                zipOut.close();

                if (fileZip.exists()) {
                    fileNameZip = FtpUtils.incrementFileName(fileZip);
                    fileZip = new File(fileNameZip);
                    fileNameEsito = FtpUtils.fromFileZipToEsito(fileZip);
                }

                FileOutputStream fos = new FileOutputStream(fileZip);
                zipOut = new ZipOutputStream(fos);
                zipFileMap.put(fileZip.getName(), new ArrayList<>());

                currentSize = 0;
            }

            //Aggiunge file fattura
            try {
                zipOut.putNextEntry(zipEntry);

            } catch (ZipException e) {
                //Generata nel caso si cerca di inserire un file già presente nello zip.
                //Il file non viene aggiunto e sarà aggiunto al prossimo passaggio del batch
                LOG.info("BATCH FTP CA - INVIO FATTURA ATTIVA: il file " + fm.getNomeFile() + " è già presente nello zip " + fileZip.getName());
                fileDuplicato = true;
            }
            zipOut.write(fm.getContenutoFattura(), 0, fm.getContenutoFattura().length);
            zipOut.closeEntry();

            //Aggiunge file metadati
            if (StringUtils.isNotEmpty(fm.getNomeFileMetadati())) {
                try {
                    zipOut.putNextEntry(zipEntryMetadati);
                } catch (ZipException e) {
                    //Generata nel caso si cerca di inserire un file già presente nello zip.
                    //Il file non viene aggiunto e sarà aggiunto al prossimo passaggio del batch
                    LOG.info("BATCH FTP CA - INVIO FATTURA ATTIVA: il file metadati " + fm.getNomeFileMetadati() + " è già presente nello zip " + fileZip.getName());
                    fileDuplicato = true;
                }
                zipOut.write(fm.getContenutoMetadati(), 0, fm.getContenutoMetadati().length);
                zipOut.closeEntry();
            }
            //Aggiorna la mappa solo se non è un duplicato
            if (!fileDuplicato) {
                zipFileMap.get(fileZip.getName()).add(fm);
            }

            currentSize += zipEntry.getCompressedSize();
        }
        //Aggiunge l'ultimo file di quadratura allo zip
        QuadraturaFTPType quadraturaFTPType = generateFileEsitoFtp(fileNameEsito, fileZip.getName(), codFiscale, zipFileMap.get(fileZip.getName()), tipoInvio);
        addFileEsitoToZip(new File(fileNameEsito), zipEntry, zipOut);
        zipOut.close();

        //mi salvo nel body i nome dei file .zip, presenti nella mappa
        List<String> zipFiles = zipFileMap.entrySet().stream()
                .map(f -> f.getKey())
                .collect(Collectors.toList());

        exchange.getIn().setHeader("dirZip", dirZip);
        exchange.setProperty("mapFattureModel", zipFileMap);
        exchange.getIn().setBody(zipFiles);
    }


    public void deleteFile(Exchange exchange) throws IOException {
        String file = (String) exchange.getIn().getHeader(Exchange.FILE_PATH);
        FileUtils.forceDelete(new File(file));
    }


    private static double addFileEsitoToZip(File file, ZipEntry zipEntry, ZipOutputStream zipOut) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);
            IOUtils.copy(fis, zipOut);
        } catch (FileNotFoundException e) {
            LOG.info("File " + file + " not found");
            throw e;
        }

        FileUtils.forceDelete(file);
        return zipEntry.getCompressedSize();
    }


    private static QuadraturaFTPType generateFileEsitoFtp(String fileNameEsito, String nomeSupporto, String codFiscale, List<FatturaFtpModel> fatture, String tipoInvio) throws JAXBException {
        ObjectFactory factory = new ObjectFactory();
        QuadraturaFTPType esito = new QuadraturaFTPType();
        esito.setIdentificativoNodo(codFiscale);
        esito.setDataOraCreazione(DateUtils.DateToXMLGregorianCalendar(new Date()));
        esito.setNomeSupporto(nomeSupporto.replace(".done", ""));
        esito.setVersione(null);

        if ("FATTURA_ATTIVA".equalsIgnoreCase(tipoInvio)) {
            esito.setNumeroFile(calcolaNumeroTipoFattureAttive(fatture));
        } else if ("FATTURA_PASSIVA".equalsIgnoreCase(tipoInvio)){
            esito.setNumeroFile(new NumeroFileType());
            esito.getNumeroFile().setFatture(BigInteger.valueOf(fatture.size()));
            esito.getNumeroFile().setMetadatiInvioFile(BigInteger.valueOf(fatture.size()));
        }  else if ("DEC_TERMINI".equalsIgnoreCase(tipoInvio)){
            esito.setNumeroFile(new NumeroFileType());
            esito.getNumeroFile().setNotificaDecorrenzaTermini(BigInteger.valueOf(fatture.size()));
        }  else if ("SCARTO_ESITO".equalsIgnoreCase(tipoInvio)){
            esito.setNumeroFile(new NumeroFileType());
            //esito.getNumeroFile().setNotificaScarto(BigInteger.valueOf(fatture.size()));
            esito.getNumeroFile().setScartoEsitoCommittente(BigInteger.valueOf(fatture.size()));
        }

        JAXBElement<QuadraturaFTPType> fileEsitoFTP = factory.createFileQuadraturaFTP(esito);

        JAXBContext jaxbContext = JAXBContext.newInstance(QuadraturaFTPType.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(fileEsitoFTP, new File(fileNameEsito));

        return esito;
    }


    private static NumeroFileType calcolaNumeroTipoFattureAttive(List<FatturaFtpModel> fatture) {
        NumeroFileType numeroFileType = new NumeroFileType();

        long countRC = fatture.parallelStream()
                .filter(f -> TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA.name().equalsIgnoreCase(f.getTipoFattura()))
                .count();

        long countNMC = fatture.parallelStream()
                .filter(f -> TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA.name().equalsIgnoreCase(f.getTipoFattura()))
                .count();

        long countNS = fatture.parallelStream()
                .filter(f -> TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO.name().equalsIgnoreCase(f.getTipoFattura()))
                .count();

        long countNE = fatture.parallelStream()
                .filter(f -> TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.name().equalsIgnoreCase(f.getTipoFattura()))
                .count();

        long countNDC = fatture.parallelStream()
                .filter(f -> TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.name().equalsIgnoreCase(f.getTipoFattura()))
                .count();

        long countATF = fatture.parallelStream()
                .filter(f -> TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.name().equalsIgnoreCase(f.getTipoFattura()))
                .count();

        numeroFileType.setRicevutaConsegna(BigInteger.valueOf(countRC));
        numeroFileType.setNotificaMancataConsegna(BigInteger.valueOf(countNMC));
        numeroFileType.setNotificaScarto(BigInteger.valueOf(countNS));
        numeroFileType.setNotificaEsito(BigInteger.valueOf(countNE));
        numeroFileType.setNotificaDecorrenzaTermini(BigInteger.valueOf(countNDC));
        numeroFileType.setAttestazioneTrasmissioneFattura(BigInteger.valueOf(countATF));

        return numeroFileType;
    }


    private static String generateFileName(Instant instant, String codFiscale, String dir, String tipoFile, String numberDoc, String extension) {

        int ddd = instant.atZone(ZoneOffset.UTC).get(ChronoField.DAY_OF_YEAR);
        int aaaa = instant.atZone(ZoneOffset.UTC).get(ChronoField.YEAR);
        int hh = instant.atZone(ZoneOffset.UTC).get(ChronoField.HOUR_OF_DAY);
        int mm = instant.atZone(ZoneOffset.UTC).get(ChronoField.MINUTE_OF_HOUR);

        StringBuilder sb = new StringBuilder();
        sb.append(dir);
        sb.append(File.separator);
        sb.append(tipoFile).append(".");

        if(extension.contains(".zip.done")){
            sb.append(codFiscale.replaceAll("IT", "")).append(".");
        }else{
            if((tipoFile.contains("FO") || tipoFile.contains("FI")) && extension.contains(".xml")){
                sb.append(codFiscale.replaceAll("IT", "")).append(".");
            }else{
                sb.append(codFiscale).append(".");
            }
        }

        sb.append(aaaa).append(ddd).append(".");
        sb.append(hh).append(mm).append(".");
        sb.append(numberDoc);
        sb.append(extension);

        return sb.toString();
    }

    private static String incrementFileName(File file) {
        String numberDoc = file.getName().split("\\.")[4];
        String newNumberDoc = StringUtils.leftPad((Integer.parseInt(numberDoc) + 1) + "", 3, "0");

        String newFileName = file.getParent().concat(File.separator).concat(file.getName().replace(numberDoc, newNumberDoc));
        return newFileName;
    }


    private static String fromFileZipToEsito(File file) {
        String s1 = file.getName().replace(FtpConstants.FO_FILE, FtpConstants.EO_FILE);
        return s1.replace(".zip.done", ".xml");
    }
}