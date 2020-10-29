
package it.extrasys.marche.regione.fatturapa.mock.sdi.setup;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.extrasys.marche.regione.fatturapa.mock.sdi.setup package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetNotificaImpostata_QNAME = new QName("http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", "getNotificaImpostata");
    private final static QName _GetNotificaImpostataResponse_QNAME = new QName("http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", "getNotificaImpostataResponse");
    private final static QName _SetNotificaImpostata_QNAME = new QName("http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", "setNotificaImpostata");
    private final static QName _SetNotificaImpostataResponse_QNAME = new QName("http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", "setNotificaImpostataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.extrasys.marche.regione.fatturapa.mock.sdi.setup
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetNotificaImpostata }
     * 
     */
    public GetNotificaImpostata createGetNotificaImpostata() {
        return new GetNotificaImpostata();
    }

    /**
     * Create an instance of {@link GetNotificaImpostataResponse }
     * 
     */
    public GetNotificaImpostataResponse createGetNotificaImpostataResponse() {
        return new GetNotificaImpostataResponse();
    }

    /**
     * Create an instance of {@link SetNotificaImpostata }
     * 
     */
    public SetNotificaImpostata createSetNotificaImpostata() {
        return new SetNotificaImpostata();
    }

    /**
     * Create an instance of {@link SetNotificaImpostataResponse }
     * 
     */
    public SetNotificaImpostataResponse createSetNotificaImpostataResponse() {
        return new SetNotificaImpostataResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificaImpostata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", name = "getNotificaImpostata")
    public JAXBElement<GetNotificaImpostata> createGetNotificaImpostata(GetNotificaImpostata value) {
        return new JAXBElement<GetNotificaImpostata>(_GetNotificaImpostata_QNAME, GetNotificaImpostata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificaImpostataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", name = "getNotificaImpostataResponse")
    public JAXBElement<GetNotificaImpostataResponse> createGetNotificaImpostataResponse(GetNotificaImpostataResponse value) {
        return new JAXBElement<GetNotificaImpostataResponse>(_GetNotificaImpostataResponse_QNAME, GetNotificaImpostataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetNotificaImpostata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", name = "setNotificaImpostata")
    public JAXBElement<SetNotificaImpostata> createSetNotificaImpostata(SetNotificaImpostata value) {
        return new JAXBElement<SetNotificaImpostata>(_SetNotificaImpostata_QNAME, SetNotificaImpostata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetNotificaImpostataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://setup.sdi.mock.fatturapa.regione.marche.extrasys.it/", name = "setNotificaImpostataResponse")
    public JAXBElement<SetNotificaImpostataResponse> createSetNotificaImpostataResponse(SetNotificaImpostataResponse value) {
        return new JAXBElement<SetNotificaImpostataResponse>(_SetNotificaImpostataResponse_QNAME, SetNotificaImpostataResponse.class, null, value);
    }

}
