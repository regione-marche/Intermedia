package it.extrasys.marche.regione.fatturapa.core.utils.cxf.interceptors;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AddNonceAndCreatedOutputInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private List<PhaseInterceptor<? extends Message>> extras = new ArrayList<PhaseInterceptor<? extends Message>>(1);

    private XPath xpath = XPathFactory.newInstance().newXPath();

    public AddNonceAndCreatedOutputInterceptor() {
        super(Phase.POST_PROTOCOL);
        extras.add(new SAAJOutInterceptor());
    }

    public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
        return extras;
    }

    public void handleMessage(SoapMessage message) throws Fault {
        SOAPMessage msg = message.getContent(SOAPMessage.class);
        try {
            removeNodes("idJustification", msg.getSOAPBody());
            removeNodes("indicRdv", msg.getSOAPBody());
        } catch (Exception e) {
            throw new Fault(e);
        }
    }

    private synchronized void removeNodes(String path, Element el) throws
            XPathExpressionException {
        NodeList l = (NodeList) xpath.evaluate("//" + path, el, XPathConstants.NODESET);
        if (l != null) {
            for (int x = 0; x < l.getLength(); x++) {
                Element el2 = (Element) l.item(0);
                el2.getParentNode().removeChild(el2);
            }
        }
    }
}
