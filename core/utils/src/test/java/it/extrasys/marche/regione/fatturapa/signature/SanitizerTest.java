package it.extrasys.marche.regione.fatturapa.signature;

import it.extrasys.marche.regione.fatturapa.core.utils.signature.XmlStringSanitizer;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 12/06/15.
 */
public class SanitizerTest {

    @Test
    public void removeHeaderAndFooter() throws IOException {


        String path = this.getClass().getResource("/p7ms/FileWithHeaderAndFooter.xml.p7m").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        String pathExpected = this.getClass().getResource("/expected/FileWithHeaderAndFooterExpected-decoded.xml.p7m").getPath();

        String fatturaExpected = FileUtils.readFileToString(new File(pathExpected));

        String fatturaActual = new String(XmlStringSanitizer.removeHeaderAndFooter(fattura));

        Assert.assertEquals(fatturaExpected, fatturaActual);

    }
}
