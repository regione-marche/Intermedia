package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaMetadatiPaleo;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura.CreaRichiestaProtocollazioneEntrataProcessor;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntePaleoCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MetadatiFatturaManager;
import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CreaRichiestaProtocollazioneEntrataCAProcessor extends CreaRichiestaProtocollazioneEntrataProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura.CreaRichiestaProtocollazioneEntrataProcessor.class);

    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;   //default SGG - Segreteria Generale
    private String codiceRegistro; // default: GRM

    private MetadatiFatturaManager metadatiFatturaManager;
    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        ObjectFactory objectFactory = new ObjectFactory();

        FatturaElettronicaWrapper fatturaElettronicaWrapper = exchange.getIn().getBody(FatturaElettronicaWrapper.class);

        Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati = fatturaElettronicaWrapper.getFatturaMetadatiMap();

        ReqProtocolloArrivo reqProtocolloArrivo = objectFactory.createReqProtocolloArrivo();

        //RICHIEDENTE
        OperatorePaleo richiedente = getRichiedente(objectFactory);
        reqProtocolloArrivo.setOperatore(richiedente);

        //FINE RICHIEDENTE
        reqProtocolloArrivo.setCodiceRegistro(codiceRegistro);

        // PRIVATO: FALSE
        reqProtocolloArrivo.setPrivato(false);

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        // FILE FATTURA
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

        // FILE METADATI
        String nomeFileMetadati = exchange.getIn().getHeader("nomeFileMetadati", String.class);
        String estensioneMetadati = FileUtils.getFileExtension(nomeFileMetadati);

        byte [] fileMetadatiBytes = metadatiFatturaManager.getMetadatiByIdentificativoSdi(new BigInteger(identificativoSdI)).getContenutoFile();

        File fileMetadati = getFileFattura(fileMetadatiBytes, nomeFileMetadati, estensioneMetadati, objectFactory);

        Allegato allegato = objectFactory.createAllegato();
        //allegato.setNumeroPagine(1);
        allegato.setDescrizione("Metadati Fattura");
        allegato.setDocumento(objectFactory.createAllegatoDocumento(fileMetadati));

        if(reqProtocolloArrivo.getDocumentiAllegati() == null){

            ArrayOfAllegato arrayOfAllegato = objectFactory.createArrayOfAllegato();
            arrayOfAllegato.getAllegato().add(allegato);

            reqProtocolloArrivo.setDocumentiAllegati(objectFactory.createReqDocumentoDocumentiAllegati(arrayOfAllegato));
        }else{
            reqProtocolloArrivo.getDocumentiAllegati().getValue().getAllegato().add(allegato);
        }

        //Canale avanzato: caso seconda chiamata dopo che la prima ha restituito errore
        String paleoErrorMessage = exchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);

        //NOTE
        String note = getNote(mappaMetadati, paleoErrorMessage);
        reqProtocolloArrivo.setNote(objectFactory.createReqDocumentoNote(note));

        //OGGETTO
        String oggetto = getOggetto(nomeFile, identificativoSdI);
        reqProtocolloArrivo.setOggetto(oggetto);

        // ACQUISITO INTEGRALMENTE
        reqProtocolloArrivo.setDocumentoPrincipaleAcquisitoIntegralmente(true);

        // DATA ARRIVO
        Date dataRicezione = fatturazionePassivaFatturaManager.recuperaDataRicezioneByIdentificativoSdI(identificativoSdI);
        if(differentDate(dataRicezione)){
            reqProtocolloArrivo.setDataArrivo(DateUtils.DateToXMLGregorianCalendar(dataRicezione));
        }else{
            reqProtocolloArrivo.setDataArrivo(DateUtils.DateToXMLGregorianCalendar(new Date()));
        }

        //Classificazioni
        ArrayOfClassificazione arrayOfClassificazione = getClassificazioni(mappaMetadati, objectFactory, paleoErrorMessage);
        reqProtocolloArrivo.setClassificazioni(objectFactory.createReqDocumentoClassificazioni(arrayOfClassificazione));

        //regma 136 controllo se sono stati settati gli headers sulla presenza di piu' ruoli per il rup, in quel caso va' cambiato il messaggio d'errore
        //TRASMISSIONE
        Trasmissione trasmissione = getTrasmissione(fatturaElettronicaWrapper.getEntePaleoCA().getEntePaleoCaEntity(), objectFactory);

        reqProtocolloArrivo.setTrasmissione(objectFactory.createReqDocumentoTrasmissione(trasmissione));
        // FINE TRASMISSIONE

        // MITTENTE
        Corrispondente mittente = getMittente(fatturaElettronicaWrapper, objectFactory);
        reqProtocolloArrivo.setMittente(mittente);
        // FINE MITTENTE

        // Tipo Documento Principale Originale
        reqProtocolloArrivo.setDocumentoPrincipaleOriginale(TipoOriginale.DIGITALE);

        reqProtocolloArrivo.setPrivato(false);

        // Tipo Documento
        reqProtocolloArrivo.setTipoDocumento(objectFactory.createReqDocumentoTipoDocumento("FATTURAPA"));

        exchange.getIn().setBody(reqProtocolloArrivo);
    }

    @Override
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

    @Override
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

    private Trasmissione getTrasmissione(EntePaleoCaEntity entePaleoCaEntity, ObjectFactory objectFactory){

        Trasmissione trasmissione = objectFactory.createTrasmissione();
        trasmissione.setInvioOriginaleCartaceo(false);

        ArrayOfTrasmissioneUtente arrayOfTrasmissioneUtente = objectFactory.createArrayOfTrasmissioneUtente();

        TrasmissioneUtente trasmissioneUtente = objectFactory.createTrasmissioneUtente();

        OperatorePaleo operatoreRUP = new OperatorePaleo();

        operatoreRUP.setNome(objectFactory.createOperatorePaleoNome(entePaleoCaEntity.getResponsabileProcedimentoNome()));
        operatoreRUP.setCognome(entePaleoCaEntity.getResponsabileProcedimentoCognome());
        operatoreRUP.setCodiceUO(entePaleoCaEntity.getResponsabileProcedimentoUo());
        operatoreRUP.setRuolo(entePaleoCaEntity.getResponsabileProcedimentoRuolo());

        trasmissioneUtente.setOperatoreDestinatario(operatoreRUP);
        trasmissioneUtente.setRagione("Assegnazione");
        //trasmissioneUtente.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Fattura n. " + metadatiFattura.getNumeroFattura()+ " - Accettazione e Rifiuto della fattura vanno effettuati nell'interfaccia del sistema FattO"));
        arrayOfTrasmissioneUtente.getTrasmissioneUtente().add(trasmissioneUtente);

        if (arrayOfTrasmissioneUtente.getTrasmissioneUtente().size() > 0) {
            trasmissione.setTrasmissioniUtente(objectFactory.createTrasmissioneTrasmissioniUtente(arrayOfTrasmissioneUtente));
        }

        return trasmissione;
    }

    @Override
    protected Corrispondente getMittente(FatturaElettronicaWrapper fatturaElettronicaWrapper, ObjectFactory objectFactory) {

        Corrispondente corrispondente = objectFactory.createCorrispondente();

        DatiCorrispondente datiCorrispondenteOccasionale = objectFactory.createDatiCorrispondente();

        datiCorrispondenteOccasionale.setCognome(fatturaElettronicaWrapper.getMittente());

        datiCorrispondenteOccasionale.setTipo(TipoVoceRubrica.ALTRO);

        corrispondente.setCorrispondenteOccasionale(objectFactory.createCorrispondenteCorrispondenteOccasionale(datiCorrispondenteOccasionale));

        return corrispondente;
    }

    private String getNote(Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati, String paleoErrorMessage){

        String note = "";

        if(paleoErrorMessage == null || "".equals(paleoErrorMessage)) {

            for (String numeroFattura : mappaMetadati.keySet()) {

                FatturaElettronicaMetadatiPaleo metadatiFattura = mappaMetadati.get(numeroFattura);

                if ("KO".equals(metadatiFattura.getFascicoloResult())) {
                    note += numeroFattura + " - " + metadatiFattura.getProtocolloEntrataNote() + ";";
                }
            }
        }else{

            ArrayList<String> listaCodiciFascicolo = getListaCodiciFascicolo(mappaMetadati);

            if(listaCodiciFascicolo == null || listaCodiciFascicolo.isEmpty()){

                note = "Non è stato possibile utilizzare il fascicolo" ;

            }else{
                for(String codiceFascicolo : listaCodiciFascicolo){

                    note += "Non è stato possibile utilizzare il fascicolo " + codiceFascicolo + ";";
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

    private String getOggetto(String nomeFile, String idSdi) {

        return "Fattura " + nomeFile + "; Identificativo SDI " + idSdi;

    }

    private boolean differentDate(Date dataRicezione) {

        Date now = new Date();

        long startTime = dataRicezione.getTime();
        long endTime = now.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);

        if(diffDays > 0){
            return true;
        }else{
            return false;
        }
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

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }

    public MetadatiFatturaManager getMetadatiFatturaManager() {
        return metadatiFatturaManager;
    }

    public void setMetadatiFatturaManager(MetadatiFatturaManager metadatiFatturaManager) {
        this.metadatiFatturaManager = metadatiFatturaManager;
    }
}