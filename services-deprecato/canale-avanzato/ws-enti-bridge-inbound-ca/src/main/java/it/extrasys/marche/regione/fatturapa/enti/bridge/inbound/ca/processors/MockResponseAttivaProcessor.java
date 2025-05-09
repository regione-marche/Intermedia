package it.extrasys.marche.regione.fatturapa.enti.bridge.inbound.ca.processors;

import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.CodInvioCAType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.DescInvioCAType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.fatturazione.attiva.ca.beans.RispostaRiceviFileCAType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MockResponseAttivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(MockResponseAttivaProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOG.info("Start processor");

        Message inMessage = exchange.getIn();
        String operationName = inMessage.getHeader(CxfConstants.OPERATION_NAME, String.class);

        LOG.info("operationName = " + operationName);

        RispostaRiceviFileCAType rispostaRiceviFileType = new RispostaRiceviFileCAType();
        rispostaRiceviFileType.setCodice(CodInvioCAType.FA_00);
        rispostaRiceviFileType.setDescrizione(DescInvioCAType.RICEZIONE_AVVENUTA_CON_SUCCESSO);
        rispostaRiceviFileType.setRicevutaComunicazione("REGMA-123456789");

        Instant instant = new Instant();
        String dateTimeString = instant.toString();
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
        rispostaRiceviFileType.setDataOraRicezione(xmlGregorianCalendar);

        inMessage.setBody(rispostaRiceviFileType);

        LOG.info("End processor");
    }
}