package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.processors;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.sdi.NotificaFromSdiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromEntiManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.NotificaFromSdiManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PecCAFatturaPassivaCreaSubjectProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecCAFatturaPassivaCreaSubjectProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";

    private static final String SUBJECT_HEADER = "subject";

    private DatiFatturaManager datiFatturaManager;
    private FatturazionePassivaNotificaDecorrenzaTerminiManager notificaDecorrenzaTerminiManager;
    private NotificaFromSdiManager notificaFromSdiManager;
    private NotificaFromEntiManager notificaFromEntiManager;

    private String firstPartSubject;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdi = (String) message.getHeader(IDENTIFICATIVO_SDI_HEADER);
        String tipoMessaggio = (String) message.getHeader(TIPO_MESSAGGIO_HEADER);

        String subject = "";

        LOG.info("PecCAFatturaPassivaCreaSubjectProcessor - identificativoSdI [" + identificativoSdi + "], tipo messaggio [" + tipoMessaggio + "]" );

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdi));
        DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);

        //Informazione che serve per la composizione del Subject
        String nomeCedentePrestatore = datiFatturaEntityList.get(0).getNomeCedentePrestatore();

        switch (tipoMessaggio) {

            case "FatturaElettronica":

                String nomeFileFattura = datiFatturaEntity.getNomeFile();
                Date dataCreazione = datiFatturaEntityList.get(0).getDataCreazione();
                String dataCreazioneFormattata = sdf.format(dataCreazione);

                subject = identificativoSdi + " - " + nomeFileFattura + " - Fattura " + nomeCedentePrestatore + " - Data Ricezione SDI " + dataCreazioneFormattata;

                break;

            case "NotificaDecorrenzaTermini":

                NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = notificaDecorrenzaTerminiManager.getNotificaDecorrenzaTerminiByIdentificativoSDI(new BigInteger(identificativoSdi));

                String nomeFileDecTermini = message.getHeader("nomeFile", String.class);
                Date dataRicezioneDecTermini = notificaDecorrenzaTerminiEntity.getDataRicezione();
                String dataRicezioneDecTerminiFormattata = sdf.format(dataRicezioneDecTermini);

                subject = identificativoSdi + " - " + nomeFileDecTermini + " - Decorrenza Termini " + nomeCedentePrestatore + " - Data Ricezione SDI " + dataRicezioneDecTerminiFormattata;

                break;

            case "NotificaEsitoCommittente":

                NotificaFromEntiEntity notificaFromEntiEntity = notificaFromEntiManager.getNotificaECFromIdentificativoSdi(new BigInteger(identificativoSdi));

                String nomeFileEC = message.getHeader("nomeFile", String.class);
                Date dataRicezioneEC = notificaFromEntiEntity.getDataRicezioneFromEnte();
                String dataRicezioneECFormattata = sdf.format(dataRicezioneEC);

                subject = identificativoSdi + " - " + nomeFileEC + " - Notifica Esito " + nomeCedentePrestatore + " - Data Ricezione Ente " + dataRicezioneECFormattata;

                break;

            case "NotificaScartoEsito":

                /*
                NotificaFromSdiEntity notificaFromSdiEntity = notificaFromSdiManager.getScartoEsitoFromSdI(new BigInteger(identificativoSdi));

                String nomeFileScarto = message.getHeader("nomeFile", String.class);
                Date dataRicezioneScarto = notificaFromSdiEntity.getDataRicezioneRispostaSDI();
                String dataRicezioneScartoFormattata = sdf.format(dataRicezioneScarto);
                */

                String nomeFileScarto = message.getHeader("nomeFile", String.class);
                //String nomeFileScarto = message.getHeader("nomeFileScartoEsito", String.class);
                Date dataRicezioneScarto = new Date();
                String dataRicezioneScartoFormattata = sdf.format(dataRicezioneScarto);

                subject = identificativoSdi + " - " + nomeFileScarto + " - Notifica Scarto " + nomeCedentePrestatore + " - Data Ricezione SDI " + dataRicezioneScartoFormattata;

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

    public NotificaFromEntiManager getNotificaFromEntiManager() {
        return notificaFromEntiManager;
    }

    public void setNotificaFromEntiManager(NotificaFromEntiManager notificaFromEntiManager) {
        this.notificaFromEntiManager = notificaFromEntiManager;
    }
}