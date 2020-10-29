package it.extrasys.marche.regione.fatturapa.signature;

import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.CAdESUnwraper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

/**
 * Created by Luigi De Masi <ldemasi@redhat.com> on 23/07/15.
 */
@RunWith(Parameterized.class)
public class InvoiceWithHeaderAndFooter {

    private final String fileName;

    public InvoiceWithHeaderAndFooter(String fileName) {
        this.fileName = fileName;
    }

    @Parameterized.Parameters(name = "{index}: {0}) ")
    public static Iterable<String> data() {

        String reqPath = InvoiceWithHeaderAndFooter.class.getResource("/headersAndFooters").getPath();

        File folder = new File(reqPath);
        File[] listOfFiles = folder.listFiles();
        String[] names = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                names[i] = listOfFiles[i].getName();
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        return Arrays.asList(names);
    }

    @Test
    public void invoiceWithHeaderAndFooterTest() throws Exception{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String path = this.getClass().getResource("/headersAndFooters/" + fileName).getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        if (Base64Utils.isBase64(fattura)) {
            fattura = Base64.decodeBase64(fattura);
        }

        String actual = CAdESUnwraper.unwrap(fattura);


    }

}
