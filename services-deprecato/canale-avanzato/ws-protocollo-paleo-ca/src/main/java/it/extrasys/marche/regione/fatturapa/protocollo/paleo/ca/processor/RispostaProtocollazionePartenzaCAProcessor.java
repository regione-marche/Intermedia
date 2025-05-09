package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.marche.regione.paleo.services.RespProtocolloPartenza;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class RispostaProtocollazionePartenzaCAProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(RispostaProtocollazionePartenzaCAProcessor.class);

    private final String PALEO_ERROR_HEADER = "PALEO_ERROR_MESSAGE";
    private final String PALEO_RESULT_HEADER = "paleoResult";
    private final String PALEO_RESULT_DESC_HEADER = "paleoResultDesc";

    @Override
    public void process(Exchange exchange) throws Exception {

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        LOG.info("RispostaProtocollazionePartenzaCAProcessor: START - IDENTIFICATIVO SDI " + identificativoSdI);

        MessageContentsList contentsList = exchange.getIn().getBody(MessageContentsList.class);

        RespProtocolloPartenza respProtocolloPartenza = (RespProtocolloPartenza) contentsList.get(0);

        String paleoResult = respProtocolloPartenza.getMessaggioRisultato().getValue().getTipoRisultato().value().trim();
        String paleoResultDesc = respProtocolloPartenza.getMessaggioRisultato().getValue().getDescrizione().getValue();
        if(paleoResultDesc != null)
            paleoResultDesc = paleoResultDesc.trim();

        exchange.getIn().setHeader(PALEO_RESULT_HEADER, paleoResult);
        exchange.getIn().setHeader(PALEO_RESULT_DESC_HEADER, paleoResultDesc);

        if ("Error".equalsIgnoreCase(paleoResult)) {

            exchange.getIn().setHeader(PALEO_ERROR_HEADER, paleoResultDesc);

            LOG.info("RispostaProtocollazionePartenzaCAProcessor: RILEVATO ERRORE NELLA RISPOSTA DI PALEO CA: '" + paleoResultDesc + "' - IDENTIFICATIVO SDI " + identificativoSdI);

            return;
        }

        Date dataProtocollazione;

        XMLGregorianCalendar gregorianCalendar = respProtocolloPartenza.getDataProtocollazione();

        if (gregorianCalendar != null) {
            dataProtocollazione = DateUtils.XMLGregorianCalendarToDate(gregorianCalendar);
        } else {
            dataProtocollazione = new Date();
        }

        exchange.getIn().setHeader("dataProtocollazione", dataProtocollazione);

        String segnatura = respProtocolloPartenza.getSegnatura();

        exchange.getIn().setBody(segnatura, String.class);

        LOG.info("RispostaProtocollazionePartenzaCAProcessor: END - IDENTIFICATIVO SDI " + identificativoSdI);
    }
}