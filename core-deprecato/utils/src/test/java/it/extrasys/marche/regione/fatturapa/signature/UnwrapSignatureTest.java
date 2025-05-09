package it.extrasys.marche.regione.fatturapa.signature;


import it.extrasys.marche.regione.fatturapa.core.utils.signature.CAdESUnwraper;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 14/03/15.
 */
public class UnwrapSignatureTest {

    @Test
    @Ignore
    public void unwrapCaDESTest() throws IOException, CMSException, SAXException {

        String input = IOUtils.toString(this.getClass().getResourceAsStream("/p7ms/IT80073490155_08661.xml.p7m"));

        String path = this.getClass().getResource("/p7ms/IT80008630420_11111.xml.p7m").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        String expectedPath = this.getClass().getResource("/p7ms/attesoIT80008630420_11111.xml").getPath();

        String expected = new String(FileUtils.readFileToByteArray(new File(expectedPath)));
        String actual = CAdESUnwraper.unwrap(fattura);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Diff compare = XMLUnit.compareXML(expected, actual);
        Assert.assertTrue(compare.identical());

    }

    @Test
    public void unwrapXaDESTest() throws IOException, CMSException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        String path = this.getClass().getResource("/xades/IT01234567890_11003.xml").getPath();

        String expectedPath = this.getClass().getResource("/xades/IT01234567890_11003.expected.xml").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        byte[] fatturaExpectedBA = FileUtils.readFileToByteArray(new File(expectedPath));

        String expected = new String(fatturaExpectedBA, Charset.forName("UTF-8"));

        String actual = XAdESUnwrapper.unwrap(fattura);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Diff compare = XMLUnit.compareXML(expected, actual);

        Assert.assertTrue(compare.identical());

    }

    @Test
    @Ignore
    public void unwrapCaDESDataHandlerTest() throws IOException, CMSException, SAXException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String input = IOUtils.toString(this.getClass().getResourceAsStream("/p7ms/IT80073490155_08661.xml.p7m"));

        String path = this.getClass().getResource("/p7ms/IT80008630420_11111.xml.p7m").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        DataHandler dataHandler = new DataHandler(new FileDataSource(new File(path)));


        InputStream inStream = dataHandler.getInputStream();
        long length = inStream.available();

        int offset = 0;
        int numRead = 0;
        byte [] bytes = new byte[(int) length];
        while (offset < bytes.length && (numRead = inStream.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        inStream.close();

       // dataHandler.writeTo(byteArrayOutputStream);

        String expectedPath = this.getClass().getResource("/p7ms/attesoIT80008630420_11111.xml").getPath();

        String expected = new String(FileUtils.readFileToByteArray(new File(expectedPath)));

        //String actual = CAdESUnwraper.unwrap(byteArrayOutputStream.toByteArray());
        String actual = CAdESUnwraper.unwrap(bytes);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Diff compare = XMLUnit.compareXML(expected, actual);

        Assert.assertTrue(compare.identical());

    }

    @Test
    public void unwrapCaDESRiccardoFileTest() throws IOException, CMSException, SAXException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String input = IOUtils.toString(this.getClass().getResourceAsStream("/p7ms/provaFattura.xml.p7m"));

        String path = this.getClass().getResource("/p7ms/provaFattura.xml.p7m").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        DataHandler dataHandler = new DataHandler(new FileDataSource(new File(path)));


        InputStream inStream = dataHandler.getInputStream();
        long length = inStream.available();

        int offset = 0;
        int numRead = 0;
        byte [] bytes = new byte[(int) length];
        while (offset < bytes.length && (numRead = inStream.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        inStream.close();

        // dataHandler.writeTo(byteArrayOutputStream);

        String expectedPath = this.getClass().getResource("/p7ms/provaFattura.xml").getPath();

        String expected = new String(FileUtils.readFileToByteArray(new File(expectedPath)));

        //String actual = CAdESUnwraper.unwrap(byteArrayOutputStream.toByteArray());
        String actual = CAdESUnwraper.unwrap(bytes);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Diff compare = XMLUnit.compareXML(expected, actual);

        Assert.assertTrue(compare.identical());
    }


    @Test
    public void unwrapCaDESStefanoFileTest() throws IOException, CMSException, SAXException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String input = IOUtils.toString(this.getClass().getResourceAsStream("/p7ms/StefanoFileFattura.xml.p7m"));

        String path = this.getClass().getResource("/p7ms/StefanoFileFattura.xml.p7m").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        DataHandler dataHandler = new DataHandler(new FileDataSource(new File(path)));


        InputStream inStream = dataHandler.getInputStream();
        long length = inStream.available();

        int offset = 0;
        int numRead = 0;
        byte [] bytes = new byte[(int) length];
        while (offset < bytes.length && (numRead = inStream.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        inStream.close();

        // dataHandler.writeTo(byteArrayOutputStream);

        String expectedPath = this.getClass().getResource("/p7ms/StefanoFileFattura.xml").getPath();

        String expected = new String(FileUtils.readFileToByteArray(new File(expectedPath)));

        //String actual = CAdESUnwraper.unwrap(byteArrayOutputStream.toByteArray());
        String actual = CAdESUnwraper.unwrap(bytes);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Diff compare = XMLUnit.compareXML(expected, actual);

        Assert.assertTrue(compare.identical());
    }


    @Test
    public void unwrapCaDESHeaderAndFooterFileTest() throws IOException, CMSException, SAXException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String path = this.getClass().getResource("/p7ms/FileWithHeaderAndFooter.xml.p7m").getPath();

        byte[] fattura = FileUtils.readFileToByteArray(new File(path));

        // dataHandler.writeTo(byteArrayOutputStream);

        String expectedPath = this.getClass().getResource("/expected/FileWithHeaderAndFooter-unwrapped.xml").getPath();

        String expected = new String(FileUtils.readFileToByteArray(new File(expectedPath)));

        //String actual = CAdESUnwraper.unwrap(byteArrayOutputStream.toByteArray());
        String actual = CAdESUnwraper.unwrap(fattura);

        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);

        Diff compare = XMLUnit.compareXML(expected, actual);

        Assert.assertTrue(compare.identical());
    }





}
