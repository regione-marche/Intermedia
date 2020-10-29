package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.processors;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

public class PecCAFatturaPassivaCreaSubjectProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecCAFatturaPassivaCreaSubjectProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";

    private static final String SUBJECT_HEADER = "subject";

    private DatiFatturaManager datiFatturaManager;
    private FatturazionePassivaNotificaDecorrenzaTerminiManager notificaDecorrenzaTerminiManager;
    private NotificaFromSdiManager notificaFromSdiManager;

    private String firstPartSubject;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdi = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoMessaggio = (String) message.getHeader(TIPO_MESSAGGIO_HEADER);

        String subject = "";

        LOG.info("PecCAFatturaPassivaCreaSubjectProcessor - identificativoSdI [" + identificativoSdi + "], tipo messaggio [" + tipoMessaggio + "]" );

        switch (tipoMessaggio) {

            case "FatturaElettronica":

                List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdi));

                DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);

                subject = identificativoSdi + " - " + datiFatturaEntity.getNomeFile();

                break;

            case "NotificaDecorrenzaTermini":

                String nomeFileDetTermini = message.getHeader("nomeFile", String.class);
                subject = identificativoSdi + " - " + nomeFileDetTermini;

                break;

            case "NotificaEsitoCommittente":

                String nomeFileEC = message.getHeader("nomeFile", String.class);
                subject = identificativoSdi + " - " + nomeFileEC;

                break;

            case "NotificaScartoEsito":

                String nomeFileScarto = message.getHeader("nomeFile", String.class);
                subject = identificativoSdi + " - " + nomeFileScarto;

                break;
        }

        LOG.info("PecCAFatturaPassivaCreaSubjectProcessor - Subject: " + subject);

        message.setHeader(SUBJECT_HEADER, subject);
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getNotificaDecorrenzaTerminiManager() {
        return notificaDecorrenzaTerminiManager;
    }

    public void setNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager notificaDecorrenzaTerminiManager) {
        this.notificaDecorrenzaTerminiManager = notificaDecorrenzaTerminiManager;
    }

    public String getFirstPartSubject() {
        return firstPartSubject;
    }

    public void setFirstPartSubject(String firstPartSubject) {
        this.firstPartSubject = firstPartSubject;
    }

    public NotificaFromSdiManager getNotificaFromSdiManager() {
        return notificaFromSdiManager;
    }

    public void setNotificaFromSdiManager(NotificaFromSdiManager notificaFromSdiManager) {
        this.notificaFromSdiManager = notificaFromSdiManager;
    }
}