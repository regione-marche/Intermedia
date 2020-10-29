package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.scarto.esito.committente;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Date;
import java.util.List;

public class CreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor implements Processor {


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;

    @Override
    public void process(Exchange exchange) throws Exception {

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        ObjectFactory objectFactory = new ObjectFactory();

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);

        ReqProtocolloArrivo reqProtocolloArrivo = objectFactory.createReqProtocolloArrivo();

        //RICHIEDENTE
        OperatorePaleo richiedente = getRichiedente(objectFactory);
        reqProtocolloArrivo.setOperatore(richiedente);

        //FINE RICHIEDENTE
        reqProtocolloArrivo.setCodiceRegistro("GRM");

        String oggetto = "NOTIFICA SCARTO ESITO COMMITTENTE - Identificativo SdI: " + identificativoSdI;
        /*
        if (notificaEsitoCommittenteWrapper.isAccettata()) {
            oggetto = "NOTIFICA ESITO COMMITTENTE - ACCETTAZIONE FATTURA - Identificativo SdI: " + identificativoSdI;
        } else {
            oggetto = "NOTIFICA ESITO COMMITTENTE - RIFIUTO FATTURA - Identificativo SdI: " + identificativoSdI;
        }
        */
        reqProtocolloArrivo.setOggetto(oggetto);

        // PRIVATO: FALSE
        reqProtocolloArrivo.setPrivato(false);


        // FILE NOTIFICA
        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);
        String estensione = FileUtils.getFileExtension(nomeFile);

        //byte [] notificaEsitoCommittenteFileFirmato = fatturazionePassivaNotificaDecorrenzaTerminiManager.getFileFatturaByIdentificativiSdI(identificativoSdI);

        byte [] notificaEsitoCommittenteFileFirmato = notificaScartoEsitoCommittenteWrapper.getNotificaScartoEsitoCommittente().getBytes();

        File fileFattura = getFileNotificaEsitoCommittente(notificaEsitoCommittenteFileFirmato, nomeFile, estensione, objectFactory);
        reqProtocolloArrivo.setDocumentoPrincipale(objectFactory.createReqDocumentoDocumentoPrincipale(fileFattura));

        // ACQUISITO INTEGRALMENTE
        reqProtocolloArrivo.setDocumentoPrincipaleAcquisitoIntegralmente(true);

        // DATA ARRIVO
        //Date dataArrivo = exchange.getIn().getHeader("dataRicezione", Date.class);
        reqProtocolloArrivo.setDataArrivo(DateUtils.DateToXMLGregorianCalendar(new Date()));

        //CLASSIFICAZIONI
        if (notificaScartoEsitoCommittenteWrapper.getClassificazioniList() != null && notificaScartoEsitoCommittenteWrapper.getClassificazioniList().size() > 0) {
            ArrayOfClassificazione arrayOfClassificazione = getClassificazioni(notificaScartoEsitoCommittenteWrapper.getClassificazioniList(), objectFactory);
            reqProtocolloArrivo.setClassificazioni(objectFactory.createReqDocumentoClassificazioni(arrayOfClassificazione));
        }
        //MITTENTE
        Corrispondente corrispondente = objectFactory.createCorrispondente();

        DatiCorrispondente datiCorrispondenteOccasionale = objectFactory.createDatiCorrispondente();

        datiCorrispondenteOccasionale.setCognome(cognome);

        corrispondente.setCorrispondenteOccasionale(objectFactory.createCorrispondenteCorrispondenteOccasionale(datiCorrispondenteOccasionale));

        reqProtocolloArrivo.setMittente(corrispondente);
        //TIPO DOCUMENTO PRINCIPALE ORIGINALE
        reqProtocolloArrivo.setDocumentoPrincipaleOriginale(TipoOriginale.DIGITALE);

        // Tipo Documento
        reqProtocolloArrivo.setTipoDocumento(objectFactory.createReqDocumentoTipoDocumento("FATTURAPA"));

        //Canale avanzato: caso seconda chiamata dopo che la prima ha restituito errore con desc = 'Altro'
        String paleoErrorMessage = exchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);
        if(paleoErrorMessage != null && "altro".equalsIgnoreCase(paleoErrorMessage)) {
            //NOTE
            String note = getNote(notificaScartoEsitoCommittenteWrapper.getClassificazioniList());
            reqProtocolloArrivo.setNote(objectFactory.createReqDocumentoNote(note));
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
        operatorePaleo.setUO(objectFactory.createOperatorePaleoUO(null));
        operatorePaleo.setUO(objectFactory.createOperatorePaleoUO(uo));
        /// FINE OPERATORE  PALEO

        return operatorePaleo;

    }

    /**
     * @param fileEsitoCommittente
     * @param objectFactory
     * @return
     */
    private File getFileNotificaEsitoCommittente(byte [] fileEsitoCommittente, String nomeFile, String estensione, ObjectFactory objectFactory) {

        File fileNotificaEsitoCommittente = objectFactory.createFile();

        fileNotificaEsitoCommittente.setEstensione(objectFactory.createFileEstensione(estensione));

        fileNotificaEsitoCommittente.setNome(nomeFile);

        fileNotificaEsitoCommittente.setMimeType(objectFactory.createFileMimeType("application/octet-stream"));

        fileNotificaEsitoCommittente.setStream(fileEsitoCommittente);

        return fileNotificaEsitoCommittente;
    }

    private ArrayOfClassificazione getClassificazioni(List<String> listaClassificazioni, ObjectFactory objectFactory) {


        ArrayOfClassificazione arrayOfClassificazione = objectFactory.createArrayOfClassificazione();

        for (String classificazioneString : listaClassificazioni) {
            Classificazione classificazione = objectFactory.createClassificazione();
            classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(classificazioneString));
            classificazione.setNuovoFascicolo(objectFactory.createClassificazioneNuovoFascicolo(null));
            arrayOfClassificazione.getClassificazione().add(classificazione);
        }
        return arrayOfClassificazione;

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
}