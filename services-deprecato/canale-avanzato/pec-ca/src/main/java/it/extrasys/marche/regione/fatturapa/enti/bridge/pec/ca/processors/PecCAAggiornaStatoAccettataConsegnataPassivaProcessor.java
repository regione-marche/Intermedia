package it.extrasys.marche.regione.fatturapa.enti.bridge.pec.ca.processors;

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

public class PecCAAggiornaStatoAccettataConsegnataPassivaProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PecCAAggiornaStatoAccettataConsegnataPassivaProcessor.class);

    private static final String TIPO_ACCETTAZIONE_FATTURA = "accettazioneFattura";
    private static final String TIPO_CONSEGNA_FATTURA = "consegnaFattura";
    private static final String TIPO_ACCETTAZIONE_DECORRENZA = "accettazioneDecorrenza";
    private static final String TIPO_CONSEGNA_DECORRENZA = "consegnaDecorrenza";
    private static final String TIPO_ACCETTAZIONE_EC = "accettazioneEC";
    private static final String TIPO_ACCETTAZIONE_SCARTO_EC = "accettazioneScartoEC";
    private static final String TIPO_CONSEGNA_EC = "consegnaEC";
    private static final String TIPO_CONSEGNA_SCARTO_EC = "consegnaScartoEC";

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
        LOG.info("PecCAAggiornaStatoAccettataConsegnataPassivaProcessor - Subject: [" + subject + "]");

        String[] subjectArray = subject.split(":");
        //String identificativoSdI = subjectArray[2].trim();
        String identificativoSdI = subjectArray[subjectArray.length -1].trim();
        String[] split = identificativoSdI.split("-");
        identificativoSdI = split[0].trim();

        //Serve per creare un log "Dinamico"
        String logStr = "PecCAAggiornaStatoAccettataConsegnataPassivaProcessor - Subject splittato: ";

        int index = 0;
        for(String splittata : subjectArray){
            index += 1;
            logStr += "["+index+"] : {" + splittata + "}    ";
        }

        LOG.info(logStr);

        //recupero le fatture in questione
        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdI));

        String tipoMessaggio = (String) message.getHeader(TIPO_SUBJECT_HEADER);

        if(tipoMessaggio == null || "".equals(tipoMessaggio)){

            throw new FatturaPAFatalException("PecCAAggiornaStatoAccettataConsegnataPassivaProcessor - Header tipo messaggio non trovato!");

        }

        for(DatiFatturaEntity datiFatturaEntity : datiFatturaEntityList) {

            BigInteger idDatiFattura = datiFatturaEntity.getIdDatiFattura();

            //Fattura
            if (tipoMessaggio.equalsIgnoreCase(TIPO_ACCETTAZIONE_FATTURA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_ACCETTATA.getValue());

            } else if (tipoMessaggio.equalsIgnoreCase(TIPO_CONSEGNA_FATTURA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_CONSEGNATA.getValue());
            }

            //Dec Termini
            if (tipoMessaggio.equalsIgnoreCase(TIPO_ACCETTAZIONE_DECORRENZA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_ACCETTATA.getValue());

            } else if (tipoMessaggio.equalsIgnoreCase(TIPO_CONSEGNA_DECORRENZA)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_CONSEGNATA.getValue());
            }

            //Esito Commmittente
            if (tipoMessaggio.equalsIgnoreCase(TIPO_ACCETTAZIONE_EC)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_ACCETTATA.getValue());

            } else if (tipoMessaggio.equalsIgnoreCase(TIPO_CONSEGNA_EC)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_CONSEGNATA.getValue());
            }

            //Scarto Esito Commmittente
            if (tipoMessaggio.equalsIgnoreCase(TIPO_ACCETTAZIONE_SCARTO_EC)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_ACCETTATA.getValue());

            } else if (tipoMessaggio.equalsIgnoreCase(TIPO_CONSEGNA_SCARTO_EC)) {

                datiFatturaManager.aggiornaStatoFatturaSpedizionePecCA(idDatiFattura, CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_CONSEGNATA.getValue());
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