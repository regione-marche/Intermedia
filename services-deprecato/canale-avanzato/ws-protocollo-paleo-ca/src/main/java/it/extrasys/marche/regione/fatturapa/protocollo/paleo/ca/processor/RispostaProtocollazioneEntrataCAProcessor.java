package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.marche.regione.paleo.services.RespProtocolloArrivo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class RispostaProtocollazioneEntrataCAProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(RispostaProtocollazioneEntrataCAProcessor.class);

    private final String PALEO_ERROR_HEADER = "PALEO_ERROR_MESSAGE";
    private final String PALEO_RESULT_HEADER = "paleoResult";
    private final String PALEO_RESULT_DESC_HEADER = "paleoResultDesc";

    @Override
    public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        LOG.info("RispostaProtocollazioneEntrataCAProcessor: START - IDENTIFICATIVO SDI " + identificativoSdI);

        MessageContentsList contentsList = exchange.getIn().getBody(MessageContentsList.class);

        RespProtocolloArrivo respProtocolloArrivo = (RespProtocolloArrivo) contentsList.get(0);

        String paleoResult = respProtocolloArrivo.getMessaggioRisultato().getValue().getTipoRisultato().value().trim();
        String paleoResultDesc = respProtocolloArrivo.getMessaggioRisultato().getValue().getDescrizione().getValue();
        if(paleoResultDesc != null)
            paleoResultDesc = paleoResultDesc.trim();

        exchange.getIn().setHeader(PALEO_RESULT_HEADER, paleoResult);
        exchange.getIn().setHeader(PALEO_RESULT_DESC_HEADER, paleoResultDesc);

        if ("Error".equalsIgnoreCase(paleoResult)) {

            exchange.getIn().setHeader(PALEO_ERROR_HEADER, paleoResultDesc);

            LOG.info("RispostaProtocollazioneEntrataCAProcessor: RILEVATO ERRORE NELLA RISPOSTA DI PALEO CA: '" + paleoResultDesc + "' - IDENTIFICATIVO SDI " + identificativoSdI);

            return;
        }

        Date dataProtocollazione;

        XMLGregorianCalendar gregorianCalendar = respProtocolloArrivo.getDataProtocollazione();

        if (gregorianCalendar != null) {
            dataProtocollazione = DateUtils.XMLGregorianCalendarToDate(gregorianCalendar);
        } else {
            dataProtocollazione = new Date();
        }

        exchange.getIn().setHeader("dataProtocollazione", dataProtocollazione);

        String segnatura = respProtocolloArrivo.getSegnatura();

        exchange.getIn().setBody(segnatura, String.class);

        LOG.info("RispostaProtocollazioneEntrataCAProcessor: END - IDENTIFICATIVO SDI " + identificativoSdI);
    }
}