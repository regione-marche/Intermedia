package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.processors;

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
 */
public class PecCAScansioneCasellaCheckSubjectProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecCAScansioneCasellaCheckSubjectProcessor.class);

    private static final String SUBJECT_PEC = "subject";

    private static final String ACCETTAZIONE_PART = "ACCETTAZIONE:";
    private static final String TIPO_ACCETTAZIONE_FATTURA = "accettazioneFattura";
    private static final String TIPO_ACCETTAZIONE_DECORRENZA = "accettazioneDecorrenza";
    private static final String TIPO_ACCETTAZIONE_EC = "accettazioneEC";
    private static final String TIPO_ACCETTAZIONE_SCARTO_EC = "accettazioneScartoEC";


    private static final String CONSEGNA_PART = "CONSEGNA:";
    private static final String TIPO_CONSEGNA_FATTURA = "consegnaFattura";
    private static final String TIPO_CONSEGNA_DECORRENZA = "consegnaDecorrenza";
    private static final String TIPO_CONSEGNA_EC = "consegnaEC";
    private static final String TIPO_CONSEGNA_SCARTO_EC = "consegnaScartoEC";

    private static final String ACCETTAZIONE_PRESA_IN_CARICO_PART = "ACCETTAZIONE: Presa in carico IntermediaMarche";
    private static final String CONSEGNA_PRESA_IN_CARICO_PART = "CONSEGNA: Presa in carico IntermediaMarche";

    private static final String ACCETTAZIONE_ERRORE_PART = "ACCETTAZIONE: Errore";
    private static final String CONSEGNA_ERRORE_PART = "CONSEGNA: Errore";

    private static final String AVVISO_NON_ACCETTAZIONE_PART = "AVVISO DI NON ACCETTAZIONE";
    private static final String AVVISO_MANCATA_CONSEGNA_PART = "AVVISO DI MANCATA CONSEGNA";

    private static final String TIPO_NON_ACCETTAZIONE_O_MANCATA_CONSEGNA = "notificaMancataConsegnaNonAccettazione";

    private static final String FILE_DEC_TERMINI = "_DT_";
    private static final String FILE_ESITO_COMM = "_EC_";
    private static final String FILE_SCARTO_ESITO_COMM = "_SE_";

    private static final String TIPO_SUBJECT_HEADER = "tipoSubject";

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String subject = (String) message.getHeader(SUBJECT_PEC);

        subject.trim();

        LOG.info("PecCAScansioneCasellaCheckSubjectProcessor: subject prima decode = [" + subject + "]");

        subject = javax.mail.internet.MimeUtility.decodeText(subject);

        LOG.info("PecCAScansioneCasellaCheckSubjectProcessor: subject dopo decode = [" + subject + "]");

        subject = subject.replaceAll("(\r\n|\r|\n|\n\r)", "");
        message.setHeader(SUBJECT_PEC, subject);

        String[] split = subject.split("-");

        //String fileTipoMex = split[split.length - 1].trim();
        String fileTipoMex = "";
        if(split.length < 2){

            LOG.info("PecCAScansioneCasellaCheckSubjectProcessor: *** Subject non conforme! ***");
            fileTipoMex = split[split.length - 1].trim();

        }else{
            
            fileTipoMex = split[1].trim();
        }

        if(subject.startsWith(ACCETTAZIONE_PART)) {

            if(!subject.startsWith(ACCETTAZIONE_PRESA_IN_CARICO_PART) && !subject.startsWith(ACCETTAZIONE_ERRORE_PART)) {

                LOG.info("PecCAScansioneCasellaCheckSubjectProcessor: ACCETTAZIONE");

                String tipoMex = "";

                if (fileTipoMex.contains(FILE_DEC_TERMINI)) {
                    tipoMex = TIPO_ACCETTAZIONE_DECORRENZA;

                } else if (fileTipoMex.contains(FILE_ESITO_COMM)) {
                    tipoMex = TIPO_ACCETTAZIONE_EC;
                } else if (fileTipoMex.contains(FILE_SCARTO_ESITO_COMM)) {
                    tipoMex = TIPO_ACCETTAZIONE_SCARTO_EC;
                }

                if ("".equals(tipoMex) && fileTipoMex.contains(".xml"))
                    tipoMex = TIPO_ACCETTAZIONE_FATTURA;

                message.setHeader(TIPO_SUBJECT_HEADER, tipoMex);
            }
        }

        else if(subject.startsWith(CONSEGNA_PART)) {

            if(!subject.startsWith(CONSEGNA_PRESA_IN_CARICO_PART) && !subject.startsWith(CONSEGNA_ERRORE_PART)) {

                LOG.info("PecCAScansioneCasellaCheckSubjectProcessor: CONSEGNA");

                String tipoMex = "";

                if (fileTipoMex.contains(FILE_DEC_TERMINI)) {
                    tipoMex = TIPO_CONSEGNA_DECORRENZA;

                } else if (fileTipoMex.contains(FILE_ESITO_COMM)) {
                    tipoMex = TIPO_CONSEGNA_EC;
                } else if (fileTipoMex.contains(FILE_SCARTO_ESITO_COMM)) {
                    tipoMex = TIPO_CONSEGNA_SCARTO_EC;
                }

                if ("".equals(tipoMex) && fileTipoMex.contains(".xml"))
                    tipoMex = TIPO_CONSEGNA_FATTURA;

                message.setHeader(TIPO_SUBJECT_HEADER, tipoMex);
            }
        }

        else if(subject.startsWith(AVVISO_NON_ACCETTAZIONE_PART) || subject.startsWith(AVVISO_MANCATA_CONSEGNA_PART)){

            LOG.info("PecCAScansioneCasellaCheckSubjectProcessor: caso NON ACCETTAZIONE / MANCATA CONSEGNA");

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
        //FIXME manca la gestione esplicita del caso in cui arrivi un subject diverso da tutti questi e non ! In quel caso si deve produrre un errore al mittente direttamente da qua

    }
}