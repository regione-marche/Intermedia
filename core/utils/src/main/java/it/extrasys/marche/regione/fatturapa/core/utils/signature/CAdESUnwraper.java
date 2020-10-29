package it.extrasys.marche.regione.fatturapa.core.utils.signature;

import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 14/03/15.
 */
public class CAdESUnwraper {

    public static String unwrap(byte[] signedFileContent) throws CMSException, IOException {

        signedFileContent = XmlStringSanitizer.removeHeaderAndFooter(signedFileContent);

        ByteArrayInputStream inStream = new ByteArrayInputStream(signedFileContent);

        InputStream is = new ByteArrayInputStream(new byte[1024]);

        try {
            CMSSignedData sdp = new CMSSignedData(inStream);

            is = new ASN1InputStream(inStream);

            CMSProcessableByteArray cmsp = (CMSProcessableByteArray) sdp.getSignedContent();

            byte[] originalDataByteArray = (byte[]) cmsp.getContent();

            String originalDataString = new String(originalDataByteArray);

            originalDataString = XmlStringSanitizer.sanitizeFatturaPA(originalDataString);

            //REVO-17
            originalDataString = new String(originalDataString.getBytes(), CommonUtils.getCharsetName(originalDataString.getBytes()));

            return originalDataString;

        } finally {
            is.reset();
            is.close();
            inStream.reset();
            inStream.close();
        }
    }
}
