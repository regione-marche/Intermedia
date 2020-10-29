package it.extrasys.marche.regione.fatturapa.services.common;

import javax.activation.DataHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by agosteeno on 15/04/15.
 */
public class SdiBridgeOutboundCommon {

    public static byte[] getFileSdiTypeBytesArray(DataHandler dh) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] fileSdiTypeBytesArray;
        try {
            dh.writeTo(outputStream);
            fileSdiTypeBytesArray = outputStream.toByteArray();
            return fileSdiTypeBytesArray;
        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }

    public static byte[] getMessaggioAsBytesArray(DataHandler dh) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] messaggioBytes;
        try {
            dh.writeTo(outputStream);
            messaggioBytes = outputStream.toByteArray();
            return messaggioBytes;
        } finally {
            outputStream.reset();
            outputStream.close();
        }
    }
}
