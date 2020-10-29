package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Cedente;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaMetadatiPaleo;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */
public class CreaRichiestaProtocollazioneEntrataProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaRichiestaProtocollazioneEntrataProcessor.class);

    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;   //default SGG - Segreteria Generale
    private String codiceRegistro; // default: GRM

    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    public void process(Exchange exchange) throws Exception {

        ObjectFactory objectFactory = new ObjectFactory();

        FatturaElettronicaWrapper fatturaElettronicaWrapper = exchange.getIn().getBody(FatturaElettronicaWrapper.class);

        Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati = fatturaElettronicaWrapper.getFatturaMetadatiMap();

        ReqProtocolloArrivo reqProtocolloArrivo = objectFactory.createReqProtocolloArrivo();

        //RICHIEDENTEà
        OperatorePaleo richiedente = getRichiedente(objectFactory);
        reqProtocolloArrivo.setOperatore(richiedente);

        //FINE RICHIEDENTE
        reqProtocolloArrivo.setCodiceRegistro(codiceRegistro);

        //OGGETTO
        String oggetto = getOggetto(fatturaElettronicaWrapper);
        reqProtocolloArrivo.setOggetto(oggetto);

        // PRIVATO: FALSE
        reqProtocolloArrivo.setPrivato(false);

        // FILE FATTURA
        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);
        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);
        String estensione = FileUtils.getFileExtension(nomeFile);

        byte [] fileFatturaFirmata = fatturazionePassivaFatturaManager.getFileFatturaByIdentificativiSdI(identificativoSdI);

        File fileFattura = getFileFattura(fileFatturaFirmata, nomeFile, estensione, objectFactory);
        reqProtocolloArrivo.setDocumentoPrincipale(objectFactory.createReqDocumentoDocumentoPrincipale(fileFattura));

        // FILE ALLEGATO : FATTURA IN HUMAN READABLE FORMAT
        if(fatturaElettronicaWrapper.getFatturaElettronicaHTML() != null && !fatturaElettronicaWrapper.getFatturaElettronicaHTML().trim().isEmpty()) {
            nomeFile = FileUtils.removeFileExtension(nomeFile);
            nomeFile = nomeFile + ".html";
            ArrayOfAllegato fatturahtml = getFatturaHTML(fatturaElettronicaWrapper, nomeFile, objectFactory);
            reqProtocolloArrivo.setDocumentiAllegati(objectFactory.createReqDocumentoDocumentiAllegati(fatturahtml));
        }
        // FINE FILE ALLEGATO : FATTURA IN HUMAN READABLE FORMAT

        //*** MAC ***
        //Caso seconda chiamata dopo che la prima ha restituito errore
        String paleoErrorMessage = exchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);
        //NOTE
        String note = getNote(mappaMetadati, paleoErrorMessage);
        reqProtocolloArrivo.setNote(objectFactory.createReqDocumentoNote(note));
        //******

        // ACQUISITO INTEGRALMENTE
        reqProtocolloArrivo.setDocumentoPrincipaleAcquisitoIntegralmente(true);

        // DATA ARRIVO
        Date dataArrivo = exchange.getIn().getHeader("dataRicezione", Date.class);
        reqProtocolloArrivo.setDataArrivo(DateUtils.DateToXMLGregorianCalendar(dataArrivo));

        //Classificazioni
        ArrayOfClassificazione arrayOfClassificazione = getClassificazioni(mappaMetadati, objectFactory, paleoErrorMessage);
        reqProtocolloArrivo.setClassificazioni(objectFactory.createReqDocumentoClassificazioni(arrayOfClassificazione));


        //regma 136 controllo se sono stati settati gli headers sulla presenza di piu' ruoli per il rup, in quel caso va' cambiato il messaggio d'errore
        //TRASMISSIONE
        Trasmissione trasmissione = getTrasmissione(fatturaElettronicaWrapper.getUOset(), mappaMetadati, objectFactory);

        reqProtocolloArrivo.setTrasmissione(objectFactory.createReqDocumentoTrasmissione(trasmissione));
        // FINE TRASMISSIONE

        // MITTENTE
        Corrispondente mittente = getMittente(fatturaElettronicaWrapper, objectFactory);
        reqProtocolloArrivo.setMittente(mittente);
        // FINE MITTENTE

        // Tipo Documento Principale Originale
        reqProtocolloArrivo.setDocumentoPrincipaleOriginale(TipoOriginale.DIGITALE);

        // Tipo Documento
        reqProtocolloArrivo.setTipoDocumento(objectFactory.createReqDocumentoTipoDocumento("FATTURAPA"));

        exchange.getIn().setBody(reqProtocolloArrivo);
    }

    /**
     * @param mappaMetadati
     * @param objectFactory
     * @return
     */
    protected Trasmissione getTrasmissione(Set<String> uoSet, Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati, ObjectFactory objectFactory) {

        // TRASMISSIONE
        Trasmissione trasmissione = objectFactory.createTrasmissione();

        ArrayOfTrasmissioneRuolo arrayOfTrasmissioneRuolo = objectFactory.createArrayOfTrasmissioneRuolo();
        ArrayOfTrasmissioneUtente arrayOfTrasmissioneUtente = objectFactory.createArrayOfTrasmissioneUtente();


        trasmissione.setInvioOriginaleCartaceo(false);


        TrasmissioneRuolo trasmissioneRuolo= null;
        TrasmissioneUtente  trasmissioneUtente = null;
        String codiceStruttura="";

        for (String numeroFattura : mappaMetadati.keySet()) {

            FatturaElettronicaMetadatiPaleo metadatiFattura = mappaMetadati.get(numeroFattura);

            codiceStruttura = metadatiFattura.getCodiceStruttura();

            LOG.info("CODICE STRUTTURA=\""+codiceStruttura+"\" UOSET SIZE ="+uoSet.size());


            // Controllo che il codice Struttura sia corretto altrimenti lo elimino dalla trasmissione mettendo a null l'id documento
            if(!uoSet.contains(metadatiFattura.getCodiceStruttura())){
                codiceStruttura = "";
            }

            // CASO 1 : Trasmissione Ruolo
            if ((codiceStruttura != null && !codiceStruttura.trim().isEmpty()) && (metadatiFattura.getRuoloRUP() == null || metadatiFattura.getRuoloRUP().trim().isEmpty())) {
                LOG.info("CASO 1 codiceStruttura="+codiceStruttura+" RUP="+metadatiFattura.getCodiceFiscaleRUP());


                trasmissioneRuolo = objectFactory.createTrasmissioneRuolo();

                trasmissioneRuolo.setRuoloDestinatario("Protocollista");
                trasmissioneRuolo.setCodiceUODestinataria(metadatiFattura.getCodiceStruttura());
                trasmissioneRuolo.setRagione("Inoltro a Ruolo");

                /*
                regma 137 devo verificare se il rup non c'e' perche' non e' stato trovato oppure perche' c'erano piu' ruoli disponibili.
                 */
                if(metadatiFattura.isRuoloUnico()) {

                    trasmissioneRuolo.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Fattura - RUP non definito per la fattura n. " + metadatiFattura.getNumeroFattura()));
                } else {
                    trasmissioneRuolo.setNote(objectFactory.createTrasmissioneRuoloNote("Impossibile identificare il RUP: utente " +
                            " " + metadatiFattura.getCognomeRUP() + " " +
                            " " + metadatiFattura.getNomeRUP() + " " +
                            " (" + metadatiFattura.getCodiceFiscaleRUP() + ") abilitato su diversi ruoli o diverse strutture"));
                }

                arrayOfTrasmissioneRuolo.getTrasmissioneRuolo().add(trasmissioneRuolo);

            }
            // CASO 2 : Trasmissione Utente

            if ((codiceStruttura == null || codiceStruttura.trim().isEmpty()) && (metadatiFattura.getRuoloRUP() != null && !metadatiFattura.getRuoloRUP().trim().isEmpty())) {

                LOG.info("CASO 2 codiceStruttura="+codiceStruttura+" RUP="+metadatiFattura.getCodiceFiscaleRUP());

                trasmissioneUtente = objectFactory.createTrasmissioneUtente();

                OperatorePaleo operatoreRUP = new OperatorePaleo();

                operatoreRUP.setCodiceFiscale(objectFactory.createOperatorePaleoCodiceFiscale(metadatiFattura.getCodiceFiscaleRUP()));
                operatoreRUP.setNome(objectFactory.createOperatorePaleoNome(metadatiFattura.getNomeRUP()));
                operatoreRUP.setCognome(metadatiFattura.getCognomeRUP());
                operatoreRUP.setCodiceUO(metadatiFattura.getCodiceUORUP());
                operatoreRUP.setRuolo(metadatiFattura.getRuoloRUP());

                trasmissioneUtente.setOperatoreDestinatario(operatoreRUP);
                trasmissioneUtente.setRagione("Assegnazione");
                trasmissioneUtente.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Fattura n. " + metadatiFattura.getNumeroFattura()+ " - Accettazione e Rifiuto della fattura vanno effettuati nell'interfaccia del sistema FattO"));
                arrayOfTrasmissioneUtente.getTrasmissioneUtente().add(trasmissioneUtente);
            }

            // CASO 3 : Trasmissione Utente

            if (codiceStruttura != null && !codiceStruttura.trim().isEmpty() && metadatiFattura.getRuoloRUP() != null && !metadatiFattura.getRuoloRUP().trim().isEmpty()) {

                LOG.info("CASO 3 codiceStruttura="+codiceStruttura+" RUP="+metadatiFattura.getCodiceFiscaleRUP());

                trasmissioneUtente = objectFactory.createTrasmissioneUtente();

                OperatorePaleo operatoreRUP = new OperatorePaleo();

                operatoreRUP.setCodiceFiscale(objectFactory.createOperatorePaleoCodiceFiscale(metadatiFattura.getCodiceFiscaleRUP()));
                operatoreRUP.setNome(objectFactory.createOperatorePaleoNome(metadatiFattura.getNomeRUP()));
                operatoreRUP.setCognome(metadatiFattura.getCognomeRUP());
                operatoreRUP.setCodiceUO(metadatiFattura.getCodiceUORUP());
                operatoreRUP.setRuolo(metadatiFattura.getRuoloRUP());

                trasmissioneUtente.setOperatoreDestinatario(operatoreRUP);
                trasmissioneUtente.setRagione("Assegnazione");
                trasmissioneUtente.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Fattura n. " + metadatiFattura.getNumeroFattura()+ " - Accettazione e Rifiuto della fattura vanno effettuati nell'interfaccia del sistema FattO"));
                arrayOfTrasmissioneUtente.getTrasmissioneUtente().add(trasmissioneUtente);


                trasmissioneRuolo = objectFactory.createTrasmissioneRuolo();
                trasmissioneRuolo.setRuoloDestinatario(ruolo);
                trasmissioneRuolo.setCodiceUODestinataria(codiceStruttura);
                trasmissioneRuolo.setRagione("Inoltro a Ruolo");
                trasmissioneUtente.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Fattura n. " + metadatiFattura.getNumeroFattura()));
                arrayOfTrasmissioneRuolo.getTrasmissioneRuolo().add(trasmissioneRuolo);
            }

            // CASO 4 : Trasmissione Ruolo

            if ((codiceStruttura == null || codiceStruttura.trim().isEmpty()) && (metadatiFattura.getRuoloRUP() == null || metadatiFattura.getRuoloRUP().trim().isEmpty())) {
                LOG.info("CASO 4 codiceStruttura="+codiceStruttura+" RUP="+metadatiFattura.getCodiceFiscaleRUP());

                trasmissioneRuolo = objectFactory.createTrasmissioneRuolo();

                trasmissioneRuolo.setRuoloDestinatario(ruolo);
                trasmissioneRuolo.setCodiceUODestinataria(uo);
                trasmissioneRuolo.setRagione("Inoltro a Ruolo");
                trasmissioneRuolo.setNote(objectFactory.createTrasmissioneRuoloNote("Non è stato possibile inoltrare la fattura n. " + metadatiFattura.getNumeroFattura()+" al responsabile del procedimento"));

                /*
                regma 137 in questo caso il problema e' che non si e' trovato il rup
                 */
                if(metadatiFattura.isRuoloUnico()) {

                    trasmissioneRuolo.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Fattura - RUP non definito per la fattura n. " + metadatiFattura.getNumeroFattura()));
                } else {
                    trasmissioneRuolo.setNote(objectFactory.createTrasmissioneRuoloNote("Impossibile identificare il RUP: utente " +
                            " " + metadatiFattura.getCognomeRUP() + " " +
                            " " + metadatiFattura.getNomeRUP() + " " +
                            " (" + metadatiFattura.getCodiceFiscaleRUP() + ") abilitato su diversi ruoli o diverse strutture"));
                }

                arrayOfTrasmissioneRuolo.getTrasmissioneRuolo().add(trasmissioneRuolo);

            }
        }

        LOG.info("NUMERO Trasmissioni Utente="+arrayOfTrasmissioneUtente.getTrasmissioneUtente().size());

        LOG.info("NUMERO Trasmissioni RUOLO="+arrayOfTrasmissioneRuolo.getTrasmissioneRuolo().size());


        if (arrayOfTrasmissioneRuolo.getTrasmissioneRuolo().size() > 0) {
            trasmissione.setTrasmissioniRuolo(objectFactory.createTrasmissioneTrasmissioniRuolo(arrayOfTrasmissioneRuolo));
        }

        if (arrayOfTrasmissioneUtente.getTrasmissioneUtente().size() > 0) {
            trasmissione.setTrasmissioniUtente(objectFactory.createTrasmissioneTrasmissioniUtente(arrayOfTrasmissioneUtente));
        }

        return trasmissione;
    }

    /**
     * @param fatturaElettronicaWrapper
     * @param objectFactory
     * @return
     */
    protected Corrispondente getMittente(FatturaElettronicaWrapper fatturaElettronicaWrapper, ObjectFactory objectFactory) {


        Corrispondente corrispondente = objectFactory.createCorrispondente();

        Cedente cedente = fatturaElettronicaWrapper.getCedente();

        if (cedente.getCodiceRubrica() != null) {
            corrispondente.setCodiceRubrica(objectFactory.createCorrispondenteCodiceRubrica(cedente.getCodiceRubrica()));
        } else {
            DatiCorrispondente datiCorrispondenteOccasionale = objectFactory.createDatiCorrispondente();

            if (cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica() != null) {
                // Se nella fattura sono valorizzati nome e cognome uso quelli, altrimenti uso la ragione sociale per valorizzare il campo cognome
                if (cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getDenominazione() == null) {
                    datiCorrispondenteOccasionale.setCognome(cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getCognome());
                    datiCorrispondenteOccasionale.setNome(objectFactory.createDatiCorrispondenteNome(cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getNome()));
                } else {
                    datiCorrispondenteOccasionale.setCognome(cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getDenominazione());
                }
            }

            if (cedente.getCedentePrestatoreType().getContatti() != null) {
                datiCorrispondenteOccasionale.setEmail(objectFactory.createDatiCorrispondenteEmail(cedente.getCedentePrestatoreType().getContatti().getEmail()));
            }

            corrispondente.setCorrispondenteOccasionale(objectFactory.createCorrispondenteCorrispondenteOccasionale(datiCorrispondenteOccasionale));

        }
        return corrispondente;

    }

    /**
     * @param fileFatturaBytesArray
     * @param objectFactory
     * @return
     */
    protected File getFileFattura(byte [] fileFatturaBytesArray, String nomeFile, String estensione, ObjectFactory objectFactory) {

        File fileFattura = objectFactory.createFile();

        fileFattura.setEstensione(objectFactory.createFileEstensione(estensione));

        fileFattura.setNome(nomeFile);

        fileFattura.setMimeType(objectFactory.createFileMimeType("application/octet-stream"));

        fileFattura.setStream(fileFatturaBytesArray);

        return fileFattura;

        // FINE FILE FATTURA
    }

    /**
     * @param objectFactory
     * @return
     */
    protected OperatorePaleo getRichiedente(ObjectFactory objectFactory) {
        OperatorePaleo operatorePaleo = objectFactory.createOperatorePaleo();

        //  CREO L'OPERATORE  PALEO RICHIEDENTE
        operatorePaleo.setCodiceFiscale(objectFactory.createOperatorePaleoCodiceFiscale(null));
        operatorePaleo.setCodiceRuolo(objectFactory.createOperatorePaleoCodiceRuolo(null));
        operatorePaleo.setCodiceUO(codiceUO);
        operatorePaleo.setCodiceUOSicurezza(objectFactory.createOperatorePaleoCodiceUOSicurezza(null));
        operatorePaleo.setCognome(cognome);
        operatorePaleo.setNome(objectFactory.createOperatorePaleoNome(nome));
        operatorePaleo.setRuolo(ruolo);
        operatorePaleo.setUO(objectFactory.createOperatorePaleoUO(uo));
        /// FINE OPERATORE  PALEO

        return operatorePaleo;

    }

    /**
     * @param fatturaElettronicaWrapper
     * @param objectFactory
     * @return
     */
    protected ArrayOfAllegato getFatturaHTML(FatturaElettronicaWrapper fatturaElettronicaWrapper, String nomeFile, ObjectFactory objectFactory) {


        File fileFatturaHTML = objectFactory.createFile();

        fileFatturaHTML.setNome(nomeFile);

        fileFatturaHTML.setEstensione(objectFactory.createFileEstensione("html"));

        fileFatturaHTML.setMimeType(objectFactory.createFileMimeType("text/html"));

        fileFatturaHTML.setStream(fatturaElettronicaWrapper.getFatturaElettronicaHTML().getBytes());

        ArrayOfAllegato arrayOfAllegato = objectFactory.createArrayOfAllegato();

        Allegato allegato = objectFactory.createAllegato();

        allegato.setNumeroPagine(1);

        allegato.setDescrizione("File Fattura in Formato HTML");

        allegato.setDocumento(objectFactory.createAllegatoDocumento(fileFatturaHTML));

        arrayOfAllegato.getAllegato().add(allegato);

        return arrayOfAllegato;

    }


    protected ArrayOfClassificazione getClassificazioni(Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati, ObjectFactory objectFactory, String paleoErrorMessage) {

        ArrayOfClassificazione arrayOfClassificazione = objectFactory.createArrayOfClassificazione();

        String codiceFascicolo ="";

        Classificazione classificazione=null;

        for (String numeroFattura : mappaMetadati.keySet()) {

            FatturaElettronicaMetadatiPaleo metadatiFattura = mappaMetadati.get(numeroFattura);

            codiceFascicolo = metadatiFattura.getCodiceFascicolo();

            if (codiceFascicolo != null) {

                classificazione = objectFactory.createClassificazione();

                if(paleoErrorMessage == null || "".equals(paleoErrorMessage)) {
                    classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(codiceFascicolo));
                }else{
                    classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(""));
                }

                arrayOfClassificazione.getClassificazione().add(classificazione);
            }
        }

        return arrayOfClassificazione;

    }

    protected String getOggetto(FatturaElettronicaWrapper fatturaElettronicaWrapper) {

        Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati = fatturaElettronicaWrapper.getFatturaMetadatiMap();

        String oggetto="";

        ///  INIZIALIZZO L' OGGETTO della FATTURA
        Cedente cedente = fatturaElettronicaWrapper.getCedente();

        if(mappaMetadati.size() >1){
            oggetto = "LOTTO di FATTURE: Ditta ";
        }
        else{
            oggetto = "FATTURA: Ditta ";
        }

        if (cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica() != null) {
            // Se nella fattura sono valorizzati nome e cognome uso quelli, altrimenti uso la ragione sociale per valorizzare il campo cognome
            if (cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getDenominazione() == null) {
                oggetto=oggetto+cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getNome()+" "+cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getCognome()+";";
            } else {
                oggetto=oggetto+cedente.getCedentePrestatoreType().getDatiAnagrafici().getAnagrafica().getDenominazione()+";";
            }
        }

        // FINE INIZIALIZZAZIONE OGGETTO;


        for (String numeroFattura : mappaMetadati.keySet()) {
            FatturaElettronicaMetadatiPaleo metadatiPaleo = mappaMetadati.get(numeroFattura);
            oggetto = oggetto + numeroFattura + ":"+mappaMetadati.get(numeroFattura).getDescrizione()+";";
        }

        if(oggetto.length() > 2000){
            oggetto = oggetto.substring(0,1993)+"....";

        }

        return oggetto;
    }

    private String getNote(Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati, String paleoErrorMessage){

        String note = "";

        if(paleoErrorMessage != null && !"".equals(paleoErrorMessage)) {

            ArrayList<String> listaCodiciFascicolo = getListaCodiciFascicolo(mappaMetadati);

            if(listaCodiciFascicolo == null || listaCodiciFascicolo.isEmpty()){

                note = "Non è stato possibile utilizzare il Codice Fasciscolo";

            }else{
                for(String codiceFascicolo : listaCodiciFascicolo){

                    note += "Non è stato possibile utilizzare il Codice Fasciscolo " + codiceFascicolo + ";";
                }
            }
        }

        return note;
    }

    private ArrayList<String> getListaCodiciFascicolo(Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati){

        ArrayList<String> listaCodiciFascicolo = new ArrayList<>();

        for (String numeroFattura : mappaMetadati.keySet()) {

            FatturaElettronicaMetadatiPaleo metadatiFattura = mappaMetadati.get(numeroFattura);

            String codiceFascicolo = metadatiFattura.getCodiceFascicolo();

            if (codiceFascicolo != null) {

                listaCodiciFascicolo.add(codiceFascicolo);

            }
        }

        return listaCodiciFascicolo;
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

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }

    public String getCodiceRegistro() {
        return codiceRegistro;
    }

    public void setCodiceRegistro(String codiceRegistro) {
        this.codiceRegistro = codiceRegistro;
    }
}