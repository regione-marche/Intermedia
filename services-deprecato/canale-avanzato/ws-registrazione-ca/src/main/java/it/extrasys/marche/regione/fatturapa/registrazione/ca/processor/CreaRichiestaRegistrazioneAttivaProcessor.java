package it.extrasys.marche.regione.fatturapa.registrazione.ca.processor;

import it.extrasys.marche.regione.fatturapa.contracts.inoltro.notifiche.fatturazione.attiva.ca.beans.*;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

public class CreaRichiestaRegistrazioneAttivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaRichiestaRegistrazioneAttivaProcessor.class);

    private static final String TIPO_NOTIFICA_HEADER = "tipoMessaggio";
    private static final String NOTIFICA_IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String NOTIFICA_NOTE_HEADER = "notificaNote";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String RICEVUTA_COMUNICAZIONE_HEADER = "ricevutaComunicazione";

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String tipoNotifica = message.getHeader(TIPO_NOTIFICA_HEADER, String.class);
        String notificaIdentificativoSdI = message.getHeader(NOTIFICA_IDENTIFICATIVO_SDI_HEADER, String.class);
        String notificaNote = message.getHeader(NOTIFICA_NOTE_HEADER, String.class);
        String nomeFile = message.getHeader(NOME_FILE_HEADER, String.class);

        String ricevutaComunicazione = CommonUtils.createRicevutaComunicazione();

        LOG.info("Ricevuta dallo SdI la notifica tipo " + tipoNotifica + " per la fattura attiva con IdentificativoSdI = " + notificaIdentificativoSdI);

        String body = message.getBody(String.class);

        LOG.debug("Body: " + body);

        NotificaRequestType notificaRequestType = new NotificaRequestType();

        NotificaType notificaType = new NotificaType();
        notificaType.setIdentificativoSDI(notificaIdentificativoSdI);

        notificaType.setEsito(getEsitoType(tipoNotifica, notificaNote));
        notificaType.setRicevutaComunicazione(ricevutaComunicazione);

        XMLType notificaXmlType = new XMLType();
        notificaXmlType.setNomeFile(nomeFile);
        notificaXmlType.setValue(body.getBytes());

        notificaType.setXML(notificaXmlType);

        notificaRequestType.setRegistraNotifica(notificaType);

        message.setHeader(RICEVUTA_COMUNICAZIONE_HEADER, ricevutaComunicazione);
        message.setBody(notificaRequestType);
    }

    private EsitoType getEsitoType(String tipoNotifica, String notificaNote) throws ParseException {

        NotificaAttivaRequestCodeType notificaAttivaRequestCodeType = null;

        TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI tipoNotificaFromSdi = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.parse(tipoNotifica);

        switch (tipoNotificaFromSdi){

            case RICEVUTA_CONSEGNA:

                LOG.info("Tipo Notifica " + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.RICEVUTA_CONSEGNA);
                notificaAttivaRequestCodeType = NotificaAttivaRequestCodeType.RC;

                break;

            case NOTIFICA_MANCATA_CONSEGNA:

                LOG.info("Tipo Notifica " + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_MANCATA_CONSEGNA);
                notificaAttivaRequestCodeType = NotificaAttivaRequestCodeType.MC;

                break;

            case NOTIFICA_SCARTO:

                LOG.info("Tipo Notifica " + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_SCARTO);
                notificaAttivaRequestCodeType = NotificaAttivaRequestCodeType.NS;

                break;

            case NOTIFICA_ESITO:

                LOG.info("Tipo Notifica " + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO);
                notificaAttivaRequestCodeType = NotificaAttivaRequestCodeType.NE;

                break;

            case NOTIFICA_DECORRENZA_TERMINI:

                LOG.info("Tipo Notifica " + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI);
                notificaAttivaRequestCodeType = NotificaAttivaRequestCodeType.DT;

                break;

            case ATTESTAZIONE_TRASMISSIONE_FATTURA:

                LOG.info("Tipo Notifica " + TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA);
                notificaAttivaRequestCodeType = NotificaAttivaRequestCodeType.AT;

                break;
        }

        EsitoType esitoType = new EsitoType();
        esitoType.setCodice(notificaAttivaRequestCodeType);
        esitoType.setData(DateUtils.getDateAsString("yyyy-MM-dd HH:mm:ss", new Date()));
        esitoType.setNote(notificaNote);

        return esitoType;
    }
}