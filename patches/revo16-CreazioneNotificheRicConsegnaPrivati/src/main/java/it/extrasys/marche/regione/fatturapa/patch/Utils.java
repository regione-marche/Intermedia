package it.extrasys.marche.regione.fatturapa.patch;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class Utils {

    private static String convertByteArrayToHexString(byte[] arrayBytes) {

        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }

        return stringBuffer.toString();
    }

    public static String hashString(byte[] fatturaOriginale, String algorithm) throws FatturaPAException {

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            //byte[] hashedBytes = digest.digest(fatturaOriginale.getBytes("UTF-8"));
            byte[] hashedBytes = digest.digest(fatturaOriginale);

            return convertByteArrayToHexString(hashedBytes);
        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        }
    }

    public static String hashFile(String pathFile, String algorithm) throws FatturaPAException {

        try {

            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            FileInputStream inputStream = new FileInputStream(pathFile);

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);

        } catch (Exception e) {
            throw new FatturaPAException(e.getMessage(), e);
        }
    }

    public static String getNomeFileRC(String nomeFileFattura){

        String nomeFileRC = nomeFileFattura.replaceAll(".xml" , "");
        nomeFileRC = nomeFileRC.replaceAll(".p7m" , "");
        nomeFileRC += "_RC_001.xml";

        return nomeFileRC;
    }
}
