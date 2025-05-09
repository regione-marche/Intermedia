package it.extrasys.marche.regione.fatturapa.core.utils.file;

import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.CAdESUnwraper;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.cms.CMSException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 10/03/15.
 */
public class FileUtils {

    public static String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public static String removeFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        while(filename.lastIndexOf(".") != -1){
            int index = filename.lastIndexOf(".");
            filename = filename.substring(0, index);
        }

        return filename;
    }

    public static String getNomeFileFatturaFromNomeFileNotificaRicevutaConsegna(String nomeFile) {

        if(nomeFile == null || "".equals(nomeFile)){
            return null;
        }

        String temp = FileUtils.removeFileExtension(nomeFile);

        String nomeMetadati = "";

        int index = temp.indexOf("_RC_");
        nomeMetadati = temp.substring(0, index);

        nomeMetadati = nomeMetadati + ".xml";

        return nomeMetadati;
    }

    public static String getNomeRicevutaConsegnaFromNomeFileFattura(String nomeFileFattura){
        if(nomeFileFattura == null || "".equals(nomeFileFattura)){
            return null;
        }

        String temp = FileUtils.removeFileExtension(nomeFileFattura);

        String nomeRicevutaConsegna = temp + "_RC_001.xml";

        return nomeRicevutaConsegna;
    }

    public static String getNomeMetadatiFromNomeFattura(String nomeFileFattura) {

        if(nomeFileFattura == null || "".equals(nomeFileFattura)){
            return null;
        }

        String temp = FileUtils.removeFileExtension(nomeFileFattura);

        String nomeMetadati = temp + "_MT_001.xml";

        return nomeMetadati;
    }

    public static String getNomeDecorrenzaTerminiFromNomeFattura(String nomeFileFattura) {

        if(nomeFileFattura == null || "".equals(nomeFileFattura)){
            return null;
        }

        String temp = FileUtils.removeFileExtension(nomeFileFattura);

        String nomeMetadati = temp + "_DT_001.xml";

        return nomeMetadati;
    }

    /**
     * Restituisce il nome originale relativo ad una dlq. Se la coda non e' una dlq restituisce null
     *
     * @param dlqName il nome della dlq per il quale si vuole ottenere il nome originale
     * @return
     */
    public static String estraiNomeOriginaleDaDlq(String dlqName){

        if(!dlqName.startsWith("DLQ.")){
            //errore, la coda deve essere una DLQ

            return null;
        }

        String result = dlqName.substring(dlqName.indexOf(".") + 1);

        return result;
    }

    public static String getFatturaElettonicaSenzaFirma(String nomeFile, byte[] fileFattureOriginale) throws IOException, CMSException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        //Controllo se è in base64
        if (Base64Utils.isBase64(fileFattureOriginale)) {
            fileFattureOriginale = Base64.decodeBase64(fileFattureOriginale);
        }

        /// Rimuovo la firma
        String fatturaElettronica = "";

        if (nomeFile.toLowerCase().endsWith(".xml.p7m")) {
            fatturaElettronica = CAdESUnwraper.unwrap(fileFattureOriginale);
        } else {
            fatturaElettronica = XAdESUnwrapper.unwrap(fileFattureOriginale);
        }
        // fine rimozione firma

        return fatturaElettronica;
    }
}