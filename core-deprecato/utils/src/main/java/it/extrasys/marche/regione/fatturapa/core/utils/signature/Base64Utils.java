package it.extrasys.marche.regione.fatturapa.core.utils.signature;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Luigi De Masi <ldemasi@redhat.com> on 23/07/15.
 */
public class Base64Utils {

    private static Pattern matchHeader = Pattern.compile("-+\\s*BEGIN.*-+\\s*", Pattern.CASE_INSENSITIVE);


    public static boolean isBase64(byte[] arrayOctet) throws UnsupportedEncodingException {

        for (int i = 0; i < arrayOctet.length; i++) {
            if (!Base64.isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
                return false;
            }
        }

        String signedFileContentString = new String(arrayOctet, "UTF-8");

        int indexOfNewLine = signedFileContentString.indexOf("\n");

        if(indexOfNewLine < 0){
            return true;
        }

        String firstLine = signedFileContentString.substring(0, indexOfNewLine);

        Matcher matcherHeader = matchHeader.matcher(firstLine);

        if (matcherHeader.matches()) {
            return false;
        }

        return true;
    }




    /**
     * Checks if a byte value is whitespace or not.
     * Whitespace is taken to mean: space, tab, CR, LF
     *
     * @param byteToCheck the byte to check
     * @return true if byte is whitespace, false otherwise
     */
    protected static boolean isWhiteSpace(byte byteToCheck) {
        switch (byteToCheck) {
            case ' ':
            case '\n':
            case '\r':
            case '\t':
                return true;
            default:
                return false;
        }
    }
}
