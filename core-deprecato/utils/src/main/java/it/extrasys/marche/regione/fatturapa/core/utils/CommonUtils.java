package it.extrasys.marche.regione.fatturapa.core.utils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import javax.activation.DataHandler;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommonUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Metodo che ci permette di capire il tipo di Charset utilizzato
     *
     * @param bytes
     * @return CharsetName
     */
    public static String getCharsetName(byte[] bytes) {

        CharsetDetector detector = new CharsetDetector();
        detector.setText(bytes);
        CharsetMatch match = detector.detect();
        return match.getName();
    }


    public static List<String> transformFromListLong(List<Long> listLong) {

        List<String> stringList = null;

        if (listLong != null && listLong.size() > 0) {
            stringList = listLong.stream().map(Object::toString).collect(Collectors.toList());
        }

        return stringList;
    }
    /**
     * Metodo che genera una codice utile al canale avanzato
     *
     * @return
     */
    public static String createRicevutaComunicazione() {

        String prefisso = "IntermediaMarche";
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String dateTime = sdf.format(new Date());

        return prefisso + "_" + uuid + "_" + dateTime;
    }

    /**
     * Metodo che converte DataHandler in stringa xml
     *
     * @param dh
     * @return
     * @throws IOException
     */
    public static String getXmlFromDataHandler(DataHandler dh) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] mex;

        try {

            dh.writeTo(outputStream);
            mex = outputStream.toByteArray();

            return new String(mex);

        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }

    public static String encryptPassword(String psw, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        // Create key and cipher
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        // encrypt the psw
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = cipher.doFinal(psw.getBytes());


        return new String(Base64.getEncoder().encode(encrypted));
    }

    public static String decryptPassword(String enc, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        byte[] decrypted = cipher.doFinal((Base64.getDecoder().decode(enc.getBytes())));

        return new String(decrypted);
    }

    //Rappresentazione
    private static final long B = 1;
    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final long GB = MB * KB;
    private static final long TB = GB * KB;

    public static String convertToStringRepresentation(final long value, final String unit){

        switch (unit){

            case "B":

                final double resultB = B > 1 ? (double) value / (double) B : (double) value;
                return new DecimalFormat("#,##0.#").format(resultB) + " " + unit;

            case "KB":

                final double resultKB = KB > 1 ? (double) value / (double) KB : (double) value;
                return new DecimalFormat("#,##0.#").format(resultKB) + " " + unit;

            case "MB":

                final double resultMB = MB > 1 ? (double) value / (double) MB : (double) value;
                return new DecimalFormat("#,##0.#").format(resultMB) + " " + unit;

            case "GB":

                final double resultGB = GB > 1 ? (double) value / (double) GB : (double) value;
                return new DecimalFormat("#,##0.#").format(resultGB) + " " + unit;

            case "TB":

                final double resultTB = TB > 1 ? (double) value / (double) TB : (double) value;
                return new DecimalFormat("#,##0.#").format(resultTB) + " " + unit;

        }

        return "0,0";
    }
}