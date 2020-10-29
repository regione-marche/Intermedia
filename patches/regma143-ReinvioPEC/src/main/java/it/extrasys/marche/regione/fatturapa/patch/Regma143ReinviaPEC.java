package it.extrasys.marche.regione.fatturapa.patch;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Regma143ReinviaPEC {

    private static final Logger log = LoggerFactory.getLogger(Regma143ReinviaPEC.class);

    private ArrayList<Integer> elencoFattureSDIdaReinviare;
    private ArrayList<Integer> elencoDecTerminiSDIdaReinviare;

    private ConsumerTemplate consumer;
    private ProducerTemplate producer;

    private static final String QUEUE_NAME_HEADER = "queueName";
    private static final String QUEUE_SIZE_HEADER = "queueSize";

    private static final String SUBJ_FATTURA_MANC_CONSEGNA_HEADER = "subjFatturaMancConsegna";
    private static final String SUBJ_FATTURA_MANC_ACCETTAZIONE_HEADER = "subjFatturaMancAccettazione";
    private static final String SUBJ_DEC_TERMINI_MANC_CONSEGNA_HEADER = "subjDecTerminiMancConsegna";
    private static final String SUBJ_DEC_TERMINI_MANC_ACCETTAZIONE_HEADER = "subjDecTerminiMancAccettazione";
    private static final String SUBJ_END_HEADER = "subjEnd";

    private String SUBJ_FATTURA_MANC_CONSEGNA;
    private String SUBJ_FATTURA_MANC_ACCETTAZIONE;
    private String SUBJ_END;
    private String SUBJ_DEC_TERMINI_MANC_CONSEGNA;
    private String SUBJ_DEC_TERMINI_MANC_ACCETTAZIONE;

    //di solito e' sempre activemq, ma nei test si cambia in vm
    private String activeMqComponentName;

    private EntityManagerFactory entityManagerFactory;
    private StatoFatturaDao statoFatturaDao;

    private ArrayList<SDIReportLog> listaSdiRep;

    public void reinviaPEC(Exchange exchange) throws Exception {

        listaSdiRep = new ArrayList<SDIReportLog>();

        elencoFattureSDIdaReinviare = new ArrayList<Integer>();
        elencoDecTerminiSDIdaReinviare = new ArrayList<Integer>();

        int messaggiScodati = 0;

        Message messageMain = exchange.getIn();

        String queueName = (String) messageMain.getHeader(QUEUE_NAME_HEADER);
        int queueSize = (int)exchange.getIn().getHeader(QUEUE_SIZE_HEADER);


        SUBJ_FATTURA_MANC_CONSEGNA = (String) messageMain.getHeader(SUBJ_FATTURA_MANC_CONSEGNA_HEADER);
        SUBJ_FATTURA_MANC_ACCETTAZIONE = (String) messageMain.getHeader(SUBJ_FATTURA_MANC_ACCETTAZIONE_HEADER);
        SUBJ_END = (String) messageMain.getHeader(SUBJ_END_HEADER);
        SUBJ_DEC_TERMINI_MANC_CONSEGNA = (String) messageMain.getHeader(SUBJ_DEC_TERMINI_MANC_CONSEGNA_HEADER);
        SUBJ_DEC_TERMINI_MANC_ACCETTAZIONE = (String) messageMain.getHeader(SUBJ_DEC_TERMINI_MANC_ACCETTAZIONE_HEADER);


        if(queueName == null || "".equals(queueName)){

            log.warn("REGMA 143 nome coda nullo o vuoto");

            return;
        }

        log.info("REGMA 143 - INIZIO SCODA");

        for (int i = 0; i < queueSize; i++){

            Exchange exchangeTmp = consumer.receive(activeMqComponentName + ":" + queueName);

            messaggiScodati = i + 1;

            Message message = exchangeTmp.getIn();
            String subject = message.getHeader("subject").toString();

            log.info("REGMA 143 Subject: " + "[" + subject + "]");

            if(subject != null && !"".equals(subject)){

                boolean[] infoPec = isStandardProblem(subject);

                if(infoPec[0]){

                    Integer sdi = getSDI(subject);

                    if(sdi.equals(-1)){
                        producer.send(activeMqComponentName + ":" + queueName, exchangeTmp);

                        continue;
                    }else{
                        gestioneDB(sdi, subject, infoPec[1], infoPec[2]);
                    }

                }else{

                    producer.send(activeMqComponentName + ":" + queueName, exchangeTmp);

                    continue;
                }
            } else{
                log.warn("REGMA 143 subject nullo o vuoto");
            }
        }

        String elencoSDIFatt = "";
        String elencoSDIDecTer = "";

        if(elencoFattureSDIdaReinviare.isEmpty()){
            //log.warn("REGMA 143 Fatture: non è stato possibile reinviare nessuna pec");
        }else{

            for(int i = 0; i < elencoFattureSDIdaReinviare.size(); i++){
                if(i == 0){
                    elencoSDIFatt += elencoFattureSDIdaReinviare.get(i);
                }else{
                    elencoSDIFatt += "," + elencoFattureSDIdaReinviare.get(i);
                }
            }
        }

        if(elencoDecTerminiSDIdaReinviare.isEmpty()){
            //log.warn("REGMA 143 Dec Termini: non è stato possibile reinviare nessuna pec");
        }else{

            for(int i = 0; i < elencoDecTerminiSDIdaReinviare.size(); i++){
                if(i == 0){
                    elencoSDIDecTer += elencoDecTerminiSDIdaReinviare.get(i);
                }else{
                    elencoSDIDecTer += "," + elencoDecTerminiSDIdaReinviare.get(i);
                }
            }
        }

        exchange.getIn().setHeader("elencoSDIFatture", elencoSDIFatt);
        exchange.getIn().setHeader("elencoSDIDecTermini", elencoSDIDecTer);

        log.info("REGMA 143 coda " + queueName + ", totale messaggi scodati: " + messaggiScodati );
        log.info("REGMA 143 eleco SDI Fatture : " + "[" + elencoSDIFatt + "] ");
        log.info("REGMA 143 eleco SDI Dec Termini : " + "[" + elencoSDIDecTer + "] ");

        if (!listaSdiRep.isEmpty()){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            log.warn("###############################################\n\n");
            log.warn("Regma 143 report del " + sdf.format(new Date()) + " \n\n");

            for(int i = 0; i < listaSdiRep.size(); i++){
                log.warn(listaSdiRep.get(i).toString());
            }

            log.warn("###############################################\n\n");
        }

        log.info("REGMA 143 - FINE SCODA");
    }

    private boolean[] isStandardProblem(String subject){

        //Array di 3 elementi:
        // 0 : isStandardProblem
        // 1 : isFattura
        // 2 : isDecTermini
        boolean[] array = {false, false, false};

        //boolean isStandardProblem = false;

        String subjUpper = subject.toUpperCase();

        //Fattura
        if(subjUpper.startsWith(SUBJ_FATTURA_MANC_CONSEGNA) || subjUpper.startsWith(SUBJ_FATTURA_MANC_ACCETTAZIONE)){

            if(subjUpper.contains(SUBJ_END)){
                //isStandardProblem = true;
                array[0] = true;
                array[1] = true;
                array[2] = false;
            }else {
                array[0] = false;
                array[1] = true;
                array[2] = false;
            }
            return array;
        }

        //Dec Termini
        if(subjUpper.startsWith(SUBJ_DEC_TERMINI_MANC_CONSEGNA) || subjUpper.startsWith(SUBJ_DEC_TERMINI_MANC_ACCETTAZIONE)){

            if(subjUpper.contains(SUBJ_END)){
                //isStandardProblem = true;
                array[0] = true;
                array[1] = false;
                array[2] = true;
            }else {
                array[0] = false;
                array[1] = false;
                array[2] = true;
            }
            return array;
        }

        //return isStandardProblem;
        listaSdiRep.add(new SDIReportLog(0, "", subject, "Impossibile rilevare se si tratta di fattura o dec. termini"));
        log.warn("REGMA 143 impossibile rilevare se si tratta di fattura o dec. termini");
        return array;
    }

    private Integer getSDI(String subject){

        Integer sdi = -1;

        String subjUpper = subject.toUpperCase();

        String[] splittata = subjUpper.split(SUBJ_END);

        if(splittata != null && splittata.length == 2){

            try{
                sdi = Integer.valueOf(splittata[1].trim());
                log.info("REGMA 143 estratto SDI = [" + sdi + "]");
            }catch (Exception e){
                listaSdiRep.add(new SDIReportLog(0, "", subject, "Errore estrazione Identificativo SDI"));
                log.warn("REGMA 143 errore estrazione Identificativo SDI");
            }
        }

        return sdi;
    }

    private void gestioneDB(Integer SDI, String subject, boolean isFattura, boolean isDecTermini) throws FatturaPAException {

        EntityManager entityManager = null;

        try {

            entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            List<StatoFatturaEntity> statoFatturaEntityList = statoFatturaDao.getStatoFattureByIdentificativoSdI(new BigInteger(SDI.toString()), entityManager);

            if(statoFatturaEntityList == null || statoFatturaEntityList.isEmpty()){
                if (isFattura){
                    listaSdiRep.add(new SDIReportLog(SDI, "Fattura", subject, "Nessuno Stato trovato per lo SDI sul DB"));
                }else{
                    listaSdiRep.add(new SDIReportLog(SDI, "Dec. Termini", subject, "Nessuno Stato trovato per lo SDI sul DB"));
                }
                log.warn("REGMA 143 situazione anomala: Nessuno stato trovato per lo SDI = [" + SDI + "] ");
            }else{
                if(isFattura){
                    if(possoCancellareStatiFattura(statoFatturaEntityList, SDI, subject)){
                        for(int i = 0; i < statoFatturaEntityList.size(); i++){
                            StatoFatturaEntity statoFatturaEntity = statoFatturaEntityList.get(i);
                            if(statoFatturaEntity.getStato().getCodStato().getValue().equals("009")
                                    || statoFatturaEntity.getStato().getCodStato().getValue().equals("013")){
                                statoFatturaDao.delete(statoFatturaEntity, entityManager);

                                if(!elencoFattureSDIdaReinviare.contains(SDI)){
                                    elencoFattureSDIdaReinviare.add(SDI);
                                }
                            }
                        }
                    }else{
                        log.warn("REGMA 143 situazione anomala --> SDI = [" + SDI + "] ");
                    }
                }else{
                    if(possoCancellareStatiDecTermini(statoFatturaEntityList, SDI, subject)){
                        for(int i = 0; i < statoFatturaEntityList.size(); i++){
                            StatoFatturaEntity statoFatturaEntity = statoFatturaEntityList.get(i);
                            if(statoFatturaEntity.getStato().getCodStato().getValue().equals("008")
                                    || statoFatturaEntity.getStato().getCodStato().getValue().equals("015")){
                                statoFatturaDao.delete(statoFatturaEntity, entityManager);
                                if(!elencoDecTerminiSDIdaReinviare.contains(SDI)){
                                    elencoDecTerminiSDIdaReinviare.add(SDI);
                                }
                            }
                        }
                    }else{
                        log.warn("REGMA 143 situazione anomala --> SDI = [" + SDI + "] ");
                    }
                }
            }

            entityManager.getTransaction().commit();

        }catch (Exception e) {
            throw new FatturaPAException("Impossibile recuperare gli stati per l'identificativo SDI : [" + SDI + "] " + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private boolean possoCancellareStatiFattura(List<StatoFatturaEntity> statoFatturaEntityList, Integer SDI, String subject){

        ArrayList<String> listaStatiFattura = new ArrayList<String>();

        for(int i = 0; i < statoFatturaEntityList.size(); i++){
           StatoFatturaEntity statoFatturaEntity = statoFatturaEntityList.get(i);

           listaStatiFattura.add(statoFatturaEntity.getStato().getCodStato().getValue());

        }

        if(listaStatiFattura.isEmpty()){
            listaSdiRep.add(new SDIReportLog(SDI, "Fattura", subject, "Errore creazine lista codici stati fattura"));
            log.warn("REGMA 143 SDI = [" + SDI + "] : Errore creazine lista codici stati fattura");
            return false;
        }

        //Controllo se è presente il codice 014
        if(listaStatiFattura.contains("014")){
            listaSdiRep.add(new SDIReportLog(SDI, "Fattura", subject, "Stato 'PEC_FATTURA_CONSEGNATA'"));
            log.warn("REGMA 143 la fattura con SDI = " + SDI + " è in stato 'PEC_FATTURA_CONSEGNATA'");
            return false;
        }

        if((listaStatiFattura.contains("001") && listaStatiFattura.contains("009")) ||
                (listaStatiFattura.contains("001") && listaStatiFattura.contains("009") && listaStatiFattura.contains("013"))){
            return true;
        }

        return false;
    }

    private boolean possoCancellareStatiDecTermini(List<StatoFatturaEntity> statoFatturaEntityList, Integer SDI, String subject){

        ArrayList<String> listaStatiFattura = new ArrayList<String>();

        for(int i = 0; i < statoFatturaEntityList.size(); i++){
            StatoFatturaEntity statoFatturaEntity = statoFatturaEntityList.get(i);

            listaStatiFattura.add(statoFatturaEntity.getStato().getCodStato().getValue());

        }

        if(listaStatiFattura.isEmpty()){
            listaSdiRep.add(new SDIReportLog(SDI, "Dec. Termini", subject, "Errore creazine lista codici stati fattura"));
            log.warn("REGMA 143 SDI = [" + SDI + "] : Errore creazione lista codici stati fattura");
            return false;
        }

        //Controllo se è presente il codice 016
        if(listaStatiFattura.contains("016")){
            listaSdiRep.add(new SDIReportLog(SDI, "Dec. Termini", subject, "Stato 'PEC_DECORRENZA_CONSEGNATA'"));
            log.warn("REGMA 143 la fattura con SDI = " + SDI + " è in stato 'PEC_DECORRENZA_CONSEGNATA'");
            return false;
        }

        if((listaStatiFattura.contains("001") && listaStatiFattura.contains("008")) ||
                (listaStatiFattura.contains("001") && listaStatiFattura.contains("008") && listaStatiFattura.contains("015"))){
            return true;
        }

        return false;
    }

    public ConsumerTemplate getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerTemplate consumer) {
        this.consumer = consumer;
    }

    public ProducerTemplate getProducer() {
        return producer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    public String getActiveMqComponentName() {
        return activeMqComponentName;
    }

    public void setActiveMqComponentName(String activeMqComponentName) {
        this.activeMqComponentName = activeMqComponentName;
    }


    //DB
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public StatoFatturaDao getStatoFatturaDao() {
        return statoFatturaDao;
    }

    public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
        this.statoFatturaDao = statoFatturaDao;
    }
}