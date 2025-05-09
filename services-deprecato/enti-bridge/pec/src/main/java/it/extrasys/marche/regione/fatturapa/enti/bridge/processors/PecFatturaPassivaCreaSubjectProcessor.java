package it.extrasys.marche.regione.fatturapa.enti.bridge.processors;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agosteeno on 30/03/16.
 */
public class PecFatturaPassivaCreaSubjectProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecFatturaPassivaCreaSubjectProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private static final String SUBJECT_FIRST_PART_HEADER = "ente.pec.subject.fattura";

    private static final String SUBJECT_HEADER = "subject";

    private DatiFatturaManager datiFatturaManager;

    private String firstPartSubject;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String fixedPartSubject = "- Identificativo SDI: ";

        String identificativoSdi = (String)message.getHeader(IDENTIFICATIVO_SDI_HEADER);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdi));

        String nomeCedentePrestatore = datiFatturaEntityList.get(0).getNomeCedentePrestatore();

        //numero fattura per ora in stand by
        //String numeroFattura

        Date dataCreazione = datiFatturaEntityList.get(0).getDataCreazione();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dataFormattata = formatter.format(dataCreazione);

        String subject = firstPartSubject + " " + nomeCedentePrestatore + ", data ricezione dallo SdI " + dataFormattata + " " + fixedPartSubject + identificativoSdi;

        LOG.info("PecFatturaPassivaCreaSubjectProcessor - subject: " + subject);

        message.setHeader(SUBJECT_HEADER, subject);
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public String getFirstPartSubject() {
        return firstPartSubject;
    }

    public void setFirstPartSubject(String firstPartSubject) {
        this.firstPartSubject = firstPartSubject;
    }
}
