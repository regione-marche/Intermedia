package it.extrasys.marche.regione.fatturapa.core.utils.signature;

import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 02/04/15.
 */

public class XmlStringSanitizer {

    private static final Logger LOG = LoggerFactory.getLogger(XmlStringSanitizer.class);


    // private static String regexp ="^.*(<\\?xml.*<\\/.*?:*FatturaElettronica>).*";

    private static String regexp ="^.*(<.*?:*FatturaElettronica>.*<\\/.*?:*FatturaElettronica>).*";

    private static Pattern xmlStringSanitizerRegExp=Pattern.compile(regexp, Pattern.DOTALL);

    private static Pattern removeStyleSheet = Pattern.compile("<\\?\\s*?xml-style.*?\\?>", Pattern.DOTALL);

    private static Pattern matchHeader = Pattern.compile("-+\\s*BEGIN.*-+\\s*", Pattern.CASE_INSENSITIVE);

    private static Pattern matchFooter = Pattern.compile("-+\\s*END.*-+", Pattern.CASE_INSENSITIVE);


    public static String sanitizeFatturaPA(String fatturaStringXml) {

        String encodType = CommonUtils.getCharsetName(fatturaStringXml.getBytes());

        String fatturaStringXmlWhithoutStyleSheet = removeStyleSheet(fatturaStringXml);

        if(encodType.equalsIgnoreCase("UTF-8") || encodType.equalsIgnoreCase("ISO-8859-1")) {
            String fatturaStringXmlClean = removeDirt(fatturaStringXmlWhithoutStyleSheet);

            return fatturaStringXmlClean;
        }else{
            return fatturaStringXmlWhithoutStyleSheet;
        }
    }

    public static String removeDirt(String fatturaStringXml){

        if(fatturaStringXml.indexOf("<")>=1)
            fatturaStringXml = fatturaStringXml.substring(fatturaStringXml.indexOf("<"), fatturaStringXml.length());

        if(fatturaStringXml.lastIndexOf(">")<fatturaStringXml.length()-1)
            fatturaStringXml = fatturaStringXml.substring(0, fatturaStringXml.lastIndexOf(">")+1);

        return fatturaStringXml;
    }

    public static String removeStyleSheet(String fatturaStringXml){

        String sanitizedXmlString="";

        Matcher matcher = removeStyleSheet.matcher(fatturaStringXml);
        sanitizedXmlString = matcher.replaceAll("");

        if(sanitizedXmlString ==null ||  sanitizedXmlString.trim().isEmpty()){
            return  fatturaStringXml;
        }

        return sanitizedXmlString;
    }


    public static byte[] removeHeaderAndFooter(byte[] signedFileContent) throws UnsupportedEncodingException {

/*
        //Modifica fatta per REVO-17 ma non è necessaria e in alcuni rari casi la libreria CharsetDetector non riesce a capire l'encoding corretto ....
        String encodType = CommonUtils.getCharsetName(signedFileContent);
        String signedFileContentString = new String(signedFileContent, encodType);
*/
        String signedFileContentString = new String(signedFileContent, "UTF-8");

        String firstLine = signedFileContentString.substring(0, signedFileContentString.indexOf("\n"));

        Matcher matcherHeader = matchHeader.matcher(firstLine);

        if (matcherHeader.matches()) {
            signedFileContentString = signedFileContentString.substring(signedFileContentString.indexOf("\n") + 1);

            LOG.info("XmlStringSanitizer: FATTURA CON: BEGIN ED END");

            String lastLine = signedFileContentString.substring(signedFileContentString.lastIndexOf("\n") + 1);

            Matcher matcherFooter = matchFooter.matcher(lastLine);

            if (matcherFooter.matches()) {
                signedFileContentString = signedFileContentString.substring(0, signedFileContentString.lastIndexOf("\n"));
            }

            signedFileContent = signedFileContentString.getBytes();

            if (Base64Utils.isBase64(signedFileContent)) {
                signedFileContent = Base64.decodeBase64(signedFileContent);
            }
        }
        return signedFileContent;
    }
}