package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.notifiche;

import it.extrasys.marche.regione.fatturapa.contracts.areas.notifica.beans.*;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/03/15.
 */
public class CreaRichiestaRegistraNotificaDecorrenzaTerminiProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {


        String fileNotificaDecorrenzaTermini = exchange.getIn().getBody(String.class);

        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);

        String sessionID = exchange.getIn().getHeader("session-token", String.class);

        String tipoNotifica = exchange.getIn().getHeader("tipo-notifica", String.class);

        String noteEsito =  exchange.getIn().getHeader("esito-note", String.class);

        ObjectFactory objectFactory = new ObjectFactory();

        InputType inputType = objectFactory.createInputType();

        inputType.setSessionID(sessionID);

        NotificaSDIType notificaSDIType = objectFactory.createNotificaSDIType();

        EsitoType esitoType = objectFactory.createEsitoType();

        esitoType.setCodice(tipoNotifica);

        esitoType.setData(DateUtils.getDateAsString("yyyy-MM-dd HH:mm:ss",new Date()));

        esitoType.setNote(noteEsito);

        notificaSDIType.setEsito(esitoType);

        XMLType xmlType = objectFactory.createXMLType();

        xmlType.setNomeFile(nomeFile);

        xmlType.setValue(fileNotificaDecorrenzaTermini.getBytes());

        notificaSDIType.setXML(xmlType);

        inputType.setNotificaSDI(notificaSDIType);

        String inputTypeString = marshallAreasInputType(inputType);

        exchange.getIn().setBody(inputTypeString, String.class);

    }


    private String marshallAreasInputType(InputType inputType) throws JAXBException {

        StringWriter stringWriter = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance(InputType.class);

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // format the XML output
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);

        QName qName = new QName("", "Input");

        JAXBElement<InputType> root = new JAXBElement<InputType>(qName, InputType.class, inputType);

        jaxbMarshaller.marshal(root, stringWriter);

        String result = stringWriter.toString();

        return result;
    }
}

