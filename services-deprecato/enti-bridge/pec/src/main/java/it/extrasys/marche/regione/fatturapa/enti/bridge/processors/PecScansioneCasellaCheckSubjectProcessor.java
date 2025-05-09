package it.extrasys.marche.regione.fatturapa.enti.bridge.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Multipart;
import java.io.ByteArrayOutputStream;

/**
 *
 * Controlla il subject della pec e ne verifica il tipo, distinguendo tra i casi accettazione/consegna e altro
 *
 * Created by agosteeno on 09/09/15.
 */
public class PecScansioneCasellaCheckSubjectProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecScansioneCasellaCheckSubjectProcessor.class);

    private static final String SUBJECT_PEC = "subject";

    private static final String ACCETTAZIONE_FATTURA_PART = "ACCETTAZIONE: Fattura";
    private static final String TIPO_ACCETTAZIONE_FATTURA = "accettazioneFattura";
    private static final String CONSEGNA_FATTURA_PART = "CONSEGNA: Fattura";
    private static final String TIPO_CONSEGNA_FATTURA = "consegnaFattura";

    private static final String ACCETTAZIONE_DECORRENZA_PART = "ACCETTAZIONE: Decorrenza Termini";
    private static final String TIPO_ACCETTAZIONE_DECORRENZA = "accettazioneDecorrenza";
    private static final String CONSEGNA_DECORRENZA_PART = "CONSEGNA: Decorrenza Termini";
    private static final String TIPO_CONSEGNA_DECORRENZA = "consegnaDecorrenza";

    private static final String AVVISO_NON_ACCETTAZIONE_PART = "AVVISO DI NON ACCETTAZIONE";
    private static final String AVVISO_MANCATA_CONSEGNA_PART = "AVVISO DI MANCATA CONSEGNA";

    private static final String TIPO_SUBJECT_HEADER = "tipoSubject";
    private static final String TIPO_NON_ACCETTAZIONE_O_MANCATA_CONSEGNA = "notificaMancataConsegnaNonAccettazione";

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String subject = (String) message.getHeader(SUBJECT_PEC);

        subject.trim();

        LOG.info("PecScansioneCasellaCheckSubjectProcessor: subject prima decode = [" + subject + "]");

        subject = javax.mail.internet.MimeUtility.decodeText(subject);

        LOG.info("PecScansioneCasellaCheckSubjectProcessor: subject dopo decode = [" + subject + "]");

        message.setHeader(SUBJECT_PEC, subject);

        if(subject.startsWith(ACCETTAZIONE_FATTURA_PART)){

            LOG.info("PecScansioneCasellaCheckSubjectProcessor: fattura caso ACCETTAZIONE");

            message.setHeader(TIPO_SUBJECT_HEADER, TIPO_ACCETTAZIONE_FATTURA);

        } else if(subject.startsWith(CONSEGNA_FATTURA_PART)) {

            LOG.info("PecScansioneCasellaCheckSubjectProcessor: fattura caso CONSEGNA");

            message.setHeader(TIPO_SUBJECT_HEADER, TIPO_CONSEGNA_FATTURA);

        } else if(subject.startsWith(ACCETTAZIONE_DECORRENZA_PART)){

            LOG.info("PecScansioneCasellaCheckSubjectProcessor: decorrenza caso ACCETTAZIONE");

            message.setHeader(TIPO_SUBJECT_HEADER, TIPO_ACCETTAZIONE_DECORRENZA);

        } else if(subject.startsWith(CONSEGNA_DECORRENZA_PART)) {

            LOG.info("PecScansioneCasellaCheckSubjectProcessor: decorrenza caso CONSEGNA");

            message.setHeader(TIPO_SUBJECT_HEADER, TIPO_CONSEGNA_DECORRENZA);

        } else if(subject.startsWith(AVVISO_NON_ACCETTAZIONE_PART) || subject.startsWith(AVVISO_MANCATA_CONSEGNA_PART)){

            LOG.info("PecScansioneCasellaCheckSubjectProcessor: caso NON ACCETTAZIONE / MANCATA CONSEGNA");

            message.setHeader(TIPO_SUBJECT_HEADER, TIPO_NON_ACCETTAZIONE_O_MANCATA_CONSEGNA);

            /*
            siccome il body del messaggio e' un Multipart, non puo' essere infilato in un messaggio JMS (per poterlo fare deve essere serializable,
            cosa che non e'). Lo trasformo in stringa e dunque lo metto nel body
             */

            String corpoMessaggioBody = "";

            Multipart multipart = (Multipart) message.getBody();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            multipart.writeTo(out);
            out.close();

            corpoMessaggioBody = out.toString();

            message.setBody(corpoMessaggioBody, String.class);

        }
    }
}
