package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Operatore;
import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class CreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor implements Processor {


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;
    private String codiceRegistro; //default : GRM
    private String codiceUOOperatoriTrasmissione; //default SGG

    @Override
    public void process(Exchange exchange) throws Exception {

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = exchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        ObjectFactory objectFactory = new ObjectFactory();

        ReqProtocolloArrivo reqProtocolloArrivo = objectFactory.createReqProtocolloArrivo();

        //RICHIEDENTE
        OperatorePaleo richiedente = getRichiedente(objectFactory);
        reqProtocolloArrivo.setOperatore(richiedente);

        //FINE RICHIEDENTE

        //CODICE REGISTRO
        reqProtocolloArrivo.setCodiceRegistro(codiceRegistro);

        String identificativoSdI = (String) exchange.getIn().getHeader("identificativoSdI");
        String oggetto = "DECORRENZA TERMINI FATTURA - Identificativo SdI: " + identificativoSdI;
        reqProtocolloArrivo.setOggetto(oggetto);

        // PRIVATO: FALSE
        reqProtocolloArrivo.setPrivato(false);


        // FILE NOTIFICA
        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);
        String estensione = FileUtils.getFileExtension(nomeFile);

        File fileFattura = getFileNotificaDecorrenzaTermini(notificaDecorrenzaTerminiWrapper, nomeFile, estensione, objectFactory);
        reqProtocolloArrivo.setDocumentoPrincipale(objectFactory.createReqDocumentoDocumentoPrincipale(fileFattura));

        // ACQUISITO INTEGRALMENTE
        reqProtocolloArrivo.setDocumentoPrincipaleAcquisitoIntegralmente(true);

        // DATA ARRIVO
        //Date dataArrivo = exchange.getIn().getHeader("dataRicezione", Date.class);
        reqProtocolloArrivo.setDataArrivo(DateUtils.DateToXMLGregorianCalendar(new Date()));


        //Canale avanzato: caso seconda chiamata dopo che la prima ha restituito errore
        String paleoErrorMessage = exchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);
        if(paleoErrorMessage != null) {
            //NOTE
            String note = getNote(notificaDecorrenzaTerminiWrapper.getClassificazioniList());
            reqProtocolloArrivo.setNote(objectFactory.createReqDocumentoNote(note));
        }

        //CLASSIFICAZIONI
        if (notificaDecorrenzaTerminiWrapper.getClassificazioniList() != null && notificaDecorrenzaTerminiWrapper.getClassificazioniList().size() > 0) {
            ArrayOfClassificazione arrayOfClassificazione = getClassificazioni(notificaDecorrenzaTerminiWrapper.getClassificazioniList(), objectFactory, paleoErrorMessage);
            reqProtocolloArrivo.setClassificazioni(objectFactory.createReqDocumentoClassificazioni(arrayOfClassificazione));
        }
        //MITTENTE
        Corrispondente corrispondente = objectFactory.createCorrispondente();

        DatiCorrispondente datiCorrispondenteOccasionale = objectFactory.createDatiCorrispondente();

        datiCorrispondenteOccasionale.setCognome("SISTEMA DI INTERSCAMBIO");

        corrispondente.setCorrispondenteOccasionale(objectFactory.createCorrispondenteCorrispondenteOccasionale(datiCorrispondenteOccasionale));

        reqProtocolloArrivo.setMittente(corrispondente);
        //TIPO DOCUMENTO PRINCIPALE ORIGINALE
        reqProtocolloArrivo.setDocumentoPrincipaleOriginale(TipoOriginale.DIGITALE);

        // Tipo Documento
        reqProtocolloArrivo.setTipoDocumento(objectFactory.createReqDocumentoTipoDocumento("FATTURAPA"));

        String isCanaleAvanzato = (String) exchange.getIn().getHeader("canaleAvanzato");

        if(isCanaleAvanzato != null && !"".equals(isCanaleAvanzato) && "true".equals(isCanaleAvanzato)){

            //Trasmissione
            List<Operatore> operatoreList = new ArrayList<>();
            Operatore operatoreDestinatario = new Operatore();
            operatoreDestinatario.setUo(notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoUo());
            operatoreDestinatario.setCognome(notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoCognome());
            operatoreDestinatario.setNome(notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoNome());
            operatoreDestinatario.setRuolo(notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoRuolo());
            operatoreList.add(operatoreDestinatario);

            Trasmissione trasmissione = getTrasmissione(operatoreList, objectFactory);
            reqProtocolloArrivo.setTrasmissione(objectFactory.createReqDocumentoTrasmissione(trasmissione));

        }else{

            String isEnteGiuntaHeader = (String) exchange.getIn().getHeader("isEnteGiunta");

            if(isEnteGiuntaHeader != null && !"".equals(isEnteGiuntaHeader)) {

                //REGMA 140: va' effettuato solo per l'ente GIUNTA cerco l'header isEnteGiunta

                if (isEnteGiuntaHeader != null && !"".equals(isEnteGiuntaHeader) && "true".equals(isEnteGiuntaHeader)) {

                    List<Operatore> operatoreList = null;

                    if(notificaDecorrenzaTerminiWrapper.getOperatoreList() != null){
                        operatoreList = notificaDecorrenzaTerminiWrapper.getOperatoreList();
                    }else{
                        operatoreList = new ArrayList<>();
                    }

                    Trasmissione trasmissione = getTrasmissione(operatoreList, objectFactory);
                    reqProtocolloArrivo.setTrasmissione(objectFactory.createReqDocumentoTrasmissione(trasmissione));
                }
            }
        }

        //SEND TO ENDPOINT
        exchange.getIn().setBody(reqProtocolloArrivo);
    }


    /**
     * @param objectFactory
     * @return
     */
    private OperatorePaleo getRichiedente(ObjectFactory objectFactory) {
        OperatorePaleo operatorePaleo = objectFactory.createOperatorePaleo();

        //  CREO L'OPERATORE  PALEO RICHIEDENTE
        operatorePaleo.setCodiceFiscale(objectFactory.createOperatorePaleoCodiceFiscale(null));
        operatorePaleo.setCodiceRuolo(objectFactory.createOperatorePaleoCodiceRuolo(null));
        operatorePaleo.setCodiceUO(codiceUO);
        operatorePaleo.setCodiceUOSicurezza(objectFactory.createOperatorePaleoCodiceUOSicurezza(null));
        operatorePaleo.setCognome(cognome);
        operatorePaleo.setNome(objectFactory.createOperatorePaleoNome(nome));
        operatorePaleo.setRuolo(ruolo);
        /*
        operatorePaleo.setUO(objectFactory.createOperatorePaleoUO(null));
        operatorePaleo.setUO(objectFactory.createOperatorePaleoUO(uo));
        */
        /// FINE OPERATORE  PALEO

        return operatorePaleo;

    }

    /**
     * @param notificaDecorrenzaTerminiWrapper
     * @param objectFactory
     * @return
     */
    private File getFileNotificaDecorrenzaTermini(NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper, String nomeFile, String estensione, ObjectFactory objectFactory) {

        File fileNotificaDecorrenzaTermini = objectFactory.createFile();

        fileNotificaDecorrenzaTermini.setEstensione(objectFactory.createFileEstensione(estensione));

        fileNotificaDecorrenzaTermini.setNome(nomeFile);

        fileNotificaDecorrenzaTermini.setMimeType(objectFactory.createFileMimeType("application/octet-stream"));

        fileNotificaDecorrenzaTermini.setStream(notificaDecorrenzaTerminiWrapper.getNotificaDecorrenzaTermini().getBytes());

        return fileNotificaDecorrenzaTermini;

        // FINE FILE FATTURA
    }

    private ArrayOfClassificazione getClassificazioni(List<String> listaClassificazioni, ObjectFactory objectFactory, String paleoErrorMessage) {

        ArrayOfClassificazione arrayOfClassificazione = objectFactory.createArrayOfClassificazione();

        for (String classificazioneString : listaClassificazioni) {

            Classificazione classificazione = objectFactory.createClassificazione();

            if(paleoErrorMessage == null || "".equals(paleoErrorMessage)) {
                classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(classificazioneString));
            }else{
                classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(""));
            }

            //classificazione.setNuovoFascicolo(objectFactory.createClassificazioneNuovoFascicolo(null));
            arrayOfClassificazione.getClassificazione().add(classificazione);
        }
        return arrayOfClassificazione;

    }

    //REGMA 140 aggiunta per gli utenti nuovi
    private Trasmissione getTrasmissione(List<Operatore> operatoreList, ObjectFactory objectFactory){

        // TRASMISSIONE
        Trasmissione trasmissione = objectFactory.createTrasmissione();

        ArrayOfTrasmissioneUtente arrayOfTrasmissioneUtente = objectFactory.createArrayOfTrasmissioneUtente();

        trasmissione.setInvioOriginaleCartaceo(false);

        for(Operatore op : operatoreList) {

            TrasmissioneUtente  trasmissioneUtente = null;

            trasmissioneUtente = objectFactory.createTrasmissioneUtente();

            OperatorePaleo operatorePaleo = new OperatorePaleo();

            //operatorePaleo.setCodiceFiscale(objectFactory.createOperatorePaleoCodiceFiscale(op.getCodiceFiscale()));
            operatorePaleo.setNome(objectFactory.createOperatorePaleoNome(op.getNome()));
            operatorePaleo.setCognome(op.getCognome());
            operatorePaleo.setCodiceUO(codiceUOOperatoriTrasmissione);
            operatorePaleo.setRuolo(op.getRuolo());

            trasmissioneUtente.setOperatoreDestinatario(operatorePaleo);
            trasmissioneUtente.setRagione("Assegnazione");
            trasmissioneUtente.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Decorrenza Termini"));

            arrayOfTrasmissioneUtente.getTrasmissioneUtente().add(trasmissioneUtente);
        }

        if (arrayOfTrasmissioneUtente.getTrasmissioneUtente().size() > 0) {
            trasmissione.setTrasmissioniUtente(objectFactory.createTrasmissioneTrasmissioniUtente(arrayOfTrasmissioneUtente));
        }

        return trasmissione;
    }

    private String getNote(List<String> classificazioniList){

        String note = "";

        for(String codiceFascicolo : classificazioniList){

            note += "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo + ";";
        }

        return note;
    }

    public String getCodiceUO() {
        return codiceUO;
    }

    public void setCodiceUO(String codiceUO) {
        this.codiceUO = codiceUO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getUo() {
        return uo;
    }

    public void setUo(String uo) {
        this.uo = uo;
    }

    public String getCodiceRegistro() {
        return codiceRegistro;
    }

    public void setCodiceRegistro(String codiceRegistro) {
        this.codiceRegistro = codiceRegistro;
    }

    public String getCodiceUOOperatoriTrasmissione() {
        return codiceUOOperatoriTrasmissione;
    }

    public void setCodiceUOOperatoriTrasmissione(String codiceUOOperatoriTrasmissione) {
        this.codiceUOOperatoriTrasmissione = codiceUOOperatoriTrasmissione;
    }
}

