package it.extrasys.marche.regione.fatturapa.mock.sdi.ricevi.notifiche;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.FileSdIBaseType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.RispostaSdINotificaEsitoType;
import org.apache.camel.*;
import org.apache.cxf.message.MessageContentsList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

public class GeneraRispostaAccettazioneScartoECProcessor implements Processor {

    private static final String TIPO_RISPOSTA_HEADER = "tipoRisposta";

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String tipoRispSdI = message.getHeader(TIPO_RISPOSTA_HEADER, String.class);

        MessageContentsList messageContentsList = message.getBody(MessageContentsList.class);

        FileSdIType fileSdIType = (FileSdIType) messageContentsList.get(0);

        String identificativoSdI = fileSdIType.getIdentificativoSdI().toString();
        String nomeFileEC = fileSdIType.getNomeFile();
        String[] splitnomeFileEC = nomeFileEC.split("_");

        RispostaSdINotificaEsitoType returned = null;

        switch (tipoRispSdI){

            case "accettazione":

                returned = new RispostaSdINotificaEsitoType();
                returned.setEsito(EsitoNotificaType.ES_01);

                break;

            case "scarto":

                returned = new RispostaSdINotificaEsitoType();
                returned.setEsito(EsitoNotificaType.ES_00);

                ProducerTemplate template = exchange.getContext().createProducerTemplate();

                String scartoEsitoCommittente = template.sendBodyAndHeader("velocity:velocity/Accettazione_Scarto.vm", ExchangePattern.InOut, message.getHeaders(), "", "").toString();
                scartoEsitoCommittente = scartoEsitoCommittente.replaceAll("%identificativoSdI%", identificativoSdI);

                byte[] scartoNotificaEC = scartoEsitoCommittente.getBytes();

                FileSdIBaseType fileSdIBaseType = new FileSdIBaseType();

                DataSource notificaScartoECDs = new ByteArrayDataSource(scartoNotificaEC, "text/plain; charset=UTF-8");

                fileSdIBaseType.setFile(new DataHandler(notificaScartoECDs));
                String[] split = splitnomeFileEC[3].split("\\.");
                fileSdIBaseType.setNomeFile(splitnomeFileEC[0] + "_" + splitnomeFileEC[1] + "_SE_" + split[0] + ".xml");

                returned.setScartoEsito(fileSdIBaseType);

                break;
        }

        message.setBody(returned, RispostaSdINotificaEsitoType.class);
    }
}