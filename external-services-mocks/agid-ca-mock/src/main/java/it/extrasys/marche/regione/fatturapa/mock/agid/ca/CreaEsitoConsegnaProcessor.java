package it.extrasys.marche.regione.fatturapa.mock.agid.ca;

import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.EsitoType;
import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.ObjectFactory;
import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.Segnatura;
import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.SegnaturaEnvelopeType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreaEsitoConsegnaProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(CreaEsitoConsegnaProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList mcl = (MessageContentsList) exchange.getIn().getBody();

        SegnaturaEnvelopeType set = (SegnaturaEnvelopeType) mcl.get(0);

        LOG.info("MOCK AGID - Ricevuta: \n" + set.toString());

        ObjectFactory factory = new ObjectFactory();

        exchange.getIn().setBody(EsitoType.OK);
    }
}

