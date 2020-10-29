package it.extrasys.marche.regione.fatturapa.core.utils.signature;

import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 15/03/15.
 */
public class XAdESUnwrapper {

    public static String unwrap(byte[] signedFileContent) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String originalDataString;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(signedFileContent));
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//*[local-name() = 'FatturaElettronica']/*[local-name() = 'Signature']");
            Node signatureNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            if(signatureNode != null){
                signatureNode.getParentNode().removeChild(signatureNode);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.transform(new DOMSource(document), new StreamResult(baos));
                originalDataString =  new String(baos.toByteArray(), Charset.forName("UTF-8"));
                originalDataString = XmlStringSanitizer.sanitizeFatturaPA(originalDataString);

                //REVO-17
                originalDataString = new String(originalDataString.getBytes(), CommonUtils.getCharsetName(originalDataString.getBytes()));

                return originalDataString;
            }
            originalDataString = new String(signedFileContent, Charset.forName("UTF-8"));
            originalDataString = XmlStringSanitizer.sanitizeFatturaPA(originalDataString);

            //REVO-17
            originalDataString = new String(originalDataString.getBytes(), CommonUtils.getCharsetName(originalDataString.getBytes()));

            return originalDataString;
        } finally {
            baos.flush();
            baos.close();
        }
    }
}