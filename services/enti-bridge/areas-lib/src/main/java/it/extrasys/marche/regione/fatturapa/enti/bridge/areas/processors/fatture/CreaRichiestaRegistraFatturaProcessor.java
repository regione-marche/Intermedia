package it.extrasys.marche.regione.fatturapa.enti.bridge.areas.processors.fatture;


import it.extrasys.marche.regione.fatturapa.contracts.areas.documento.beans.DocumentoSDIType;
import it.extrasys.marche.regione.fatturapa.contracts.areas.documento.beans.InputType;
import it.extrasys.marche.regione.fatturapa.contracts.areas.documento.beans.ObjectFactory;
import it.extrasys.marche.regione.fatturapa.contracts.areas.documento.beans.XMLType;
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
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/03/15.
 */
public class CreaRichiestaRegistraFatturaProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {

        String fileFattura = exchange.getIn().getBody(String.class);

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        String  segnaturaProtocollo = exchange.getIn().getHeader("numeroProtocollo", String.class);

       // String [] segnaturaProtocolloSplitted = segnaturaProtocollo.split("\\|");

        //String numeroProtocollo = segnaturaProtocolloSplitted[0];

        //String [] dataProtocolloSplitted = segnaturaProtocolloSplitted[1].split("\\/");

        //String  dataProtocollo = dataProtocolloSplitted[2]+"-"+dataProtocolloSplitted[1]+"-"+dataProtocolloSplitted[0];

        Date dataProtocolloDate = exchange.getIn().getHeader("dataProtocollo", Date.class);

        if(dataProtocolloDate == null){
            dataProtocolloDate = new Date();
        }

        String dataProtocolloString = DateUtils.getDateAsString("yyyy-MM-dd", dataProtocolloDate);

        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);

        String sessionID = exchange.getIn().getHeader("session-token", String.class);

        ObjectFactory objectFactory = new ObjectFactory();

        InputType inputType = objectFactory.createInputType();

        inputType.setSessionID(sessionID);

        DocumentoSDIType documentoSDIType = objectFactory.createDocumentoSDIType();

        documentoSDIType.setDataProtocollo(dataProtocolloString);

        documentoSDIType.setNumeroProtocollo(segnaturaProtocollo);

        documentoSDIType.setIdentificativoSDI(identificativoSdI);

        XMLType xmlType = objectFactory.createXMLType();

        xmlType.setNomeFile(nomeFile);

        xmlType.setValue(fileFattura.getBytes());

        documentoSDIType.setXML(xmlType);

        inputType.setDocumentoSDI(documentoSDIType);

        String inputTypeString = marshallAreasInputType(inputType);

        exchange.getIn().setBody(inputTypeString,String.class);

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
