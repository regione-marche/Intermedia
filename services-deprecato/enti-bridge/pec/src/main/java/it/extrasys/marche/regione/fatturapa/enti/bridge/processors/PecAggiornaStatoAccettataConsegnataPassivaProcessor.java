package it.extrasys.marche.regione.fatturapa.enti.bridge.processors;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatalException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by agosteeno on 09/09/15.
 */
public class PecAggiornaStatoAccettataConsegnataPassivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecAggiornaStatoAccettataConsegnataPassivaProcessor.class);

    private static final String TIPO_ACCETTAZIONE_FATTURA = "accettazioneFattura";
    private static final String TIPO_CONSEGNA_FATTURA = "consegnaFattura";
    private static final String TIPO_ACCETTAZIONE_DECORRENZA = "accettazioneDecorrenza";
    private static final String TIPO_CONSEGNA_DECORRENZA = "consegnaDecorrenza";

    private static final String TIPO_SUBJECT_HEADER = "tipoSubject";
    private static final String SUBJECT_HEADER = "subject";

    private DatiFatturaManager datiFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        /*
        quello che devo fare e' controllare quale sia l'ultimo stato della fattura in questione e in base a questo fare
        i seguenti check in base al tipo di messaggio che ho ricevuto (ACCETTAZIONE o CONSEGNA):

        - PEC_CONSEGNATA: non devo fare nulla.
        - PEC ACCETTATA:
            - se messaggio CONSEGNA: aggiornare lo stato a PEC_CONSEGNATA
            - se messaggio ACCETTATA: non devo fare nulla
        - INOLTRATA_MAIL:
            - se messaggio CONSEGNA: anche se sembra che non dovrebbe succedere, perche' ci dovrebbe essere prima l'accettazione
                puo'succedere perche' l'ordine con il quale viene scansionata la casella non e' deterministico, per via
                del fatto che le PEC ricevute/inviate che si trovano nella casellanon sono sotto il nostro controllo. Se
                siamo in questo caso implicitamente la pec e' stata anche accettata e quando verra' scansionata la corrispettiva
                pec di accettazione verra' semplicemente scartata. Devo dunque aggiornare lo stato prima a PEC_ACCETTATA
                e dunque PEC_CONSEGNATA
            - se messaggio ACCETTATA: aggiornare lo stato a PEC_ACCETTATA


        PS in maniera analoga (e indipendente) si effettua la stessa cosa sulla decorrenza termini
         */

        Message message = exchange.getIn();

        //estraggo dal subject l'identificativo SdI
        String subject = (String) message.getHeader(SUBJECT_HEADER);
        LOG.info("PecAggiornaStatoAccettataConsegnataPassivaProcessor - Subject: [" + subject + "]");

        String[] subjectArray = subject.split(":");
        //String identificativoSdI = subjectArray[2].trim();
        String identificativoSdI = subjectArray[subjectArray.length -1].trim();

        //Serve per creare un log "Dinamico"
        String logStr = "PecAggiornaStatoAccettataConsegnataPassivaProcessor - Subject splittato: ";

        int index = 0;
        for(String splittata : subjectArray){
            index += 1;
            logStr += "["+index+"] : {" + splittata + "}    ";
        }

        //LOG.info("PecAggiornaStatoAccettataConsegnataPassivaProcessor - Subject splittato: primo [" + subjectArray [0] + "], secondo [" + subjectArray [1] + "], terzo[" + subjectArray [2] + "] ");
        LOG.info(logStr);

        //recupero le fatture in questione
        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        String tipoMessaggio = (String) message.getHeader(TIPO_SUBJECT_HEADER);

        if(tipoMessaggio == null || "".equals(tipoMessaggio)){

            throw new FatturaPAFatalException("PecAggiornaStatoAccettataConsegnataPassivaProcessor - Header tipo messaggio non trovato!");

        }

        for(DatiFatturaEntity datiFatturaEntity : datiFatturaEntityList) {

            BigInteger idDatiFattura = datiFatturaEntity.getIdDatiFattura();

            if (tipoMessaggio.equalsIgnoreCase(TIPO_ACCETTAZIONE_FATTURA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePec(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue());

            } else if (tipoMessaggio.equalsIgnoreCase(TIPO_CONSEGNA_FATTURA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePec(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_CONSEGNATA.getValue());

            } if (tipoMessaggio.equalsIgnoreCase(TIPO_ACCETTAZIONE_DECORRENZA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePec(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_ACCETTATA.getValue());

            } else if (tipoMessaggio.equalsIgnoreCase(TIPO_CONSEGNA_DECORRENZA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePec(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_CONSEGNATA.getValue());

            }
        }
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}