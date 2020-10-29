package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.Operatore;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManager;
import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class CreaRichiestaProtocollazioneEsitoCommittenteProcessor implements Processor {


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;
    private String codiceRegistro; //default : GRM
    private String codiceUOOperatoriTrasmissione; //default SGG

    private FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        ObjectFactory objectFactory = new ObjectFactory();

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);

        ReqProtocolloPartenza reqProtocolloPartenza = objectFactory.createReqProtocolloPartenza();

        //Canale avanzato: caso seconda chiamata dopo che la prima ha restituito errore
        String paleoErrorMessage = exchange.getIn().getHeader("PALEO_ERROR_MESSAGE", String.class);
        if(paleoErrorMessage != null) {
            //NOTE
            String note = getNote(notificaEsitoCommittenteWrapper.getClassificazioniList());
            reqProtocolloPartenza.setNote(objectFactory.createReqDocumentoNote(note));
        }

        //CLASSIFICAZIONI
        if (notificaEsitoCommittenteWrapper.getClassificazioniList() != null && notificaEsitoCommittenteWrapper.getClassificazioniList().size() > 0) {
            ArrayOfClassificazione arrayOfClassificazione = getClassificazioni(notificaEsitoCommittenteWrapper.getClassificazioniList(), objectFactory, paleoErrorMessage);
            reqProtocolloPartenza.setClassificazioni(objectFactory.createReqDocumentoClassificazioni(arrayOfClassificazione));
        }

        // FILE NOTIFICA
        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);
        String estensione = FileUtils.getFileExtension(nomeFile);

        byte [] notificaEsitoCommittenteFileFirmato = null;
        if(fatturazionePassivaNotificaDecorrenzaTerminiManager == null){
            notificaEsitoCommittenteFileFirmato = notificaEsitoCommittenteWrapper.getNotificaEsitoCommittente().getBytes();
        }else{
            notificaEsitoCommittenteFileFirmato = fatturazionePassivaNotificaDecorrenzaTerminiManager.getFileFatturaByIdentificativiSdI(identificativoSdI);
        }

        File fileFattura = getFileNotificaEsitoCommittente(notificaEsitoCommittenteFileFirmato, nomeFile, estensione, objectFactory);
        reqProtocolloPartenza.setDocumentoPrincipale(objectFactory.createReqDocumentoDocumentoPrincipale(fileFattura));

        // ACQUISITO INTEGRALMENTE
        reqProtocolloPartenza.setDocumentoPrincipaleAcquisitoIntegralmente(true);

        //TIPO DOCUMENTO PRINCIPALE ORIGINALE
        reqProtocolloPartenza.setDocumentoPrincipaleOriginale(TipoOriginale.DIGITALE);

        // Oggetto
        String oggetto = "Notifica " + nomeFile + " Identificativo SDI " + identificativoSdI;
        reqProtocolloPartenza.setOggetto(oggetto);


        //OPERATORE RICHIEDENTE
        OperatorePaleo richiedente = getRichiedente(objectFactory);
        reqProtocolloPartenza.setOperatore(richiedente);

        // PRIVATO: FALSE
        reqProtocolloPartenza.setPrivato(false);

        // Tipo Documento
        reqProtocolloPartenza.setTipoDocumento(objectFactory.createReqDocumentoTipoDocumento("FATTURAPA"));

        //Trasmissione
        List<Operatore> operatoreList = new ArrayList<>();
        Operatore operatoreDestinatario = new Operatore();
        operatoreDestinatario.setUo(notificaEsitoCommittenteWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoUo());
        operatoreDestinatario.setCognome(notificaEsitoCommittenteWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoCognome());
        operatoreDestinatario.setNome(notificaEsitoCommittenteWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoNome());
        operatoreDestinatario.setRuolo(notificaEsitoCommittenteWrapper.getEntePaleoCA().getEntePaleoCaEntity().getResponsabileProcedimentoRuolo());
        operatoreList.add(operatoreDestinatario);

        Trasmissione trasmissione = getTrasmissione(operatoreList, objectFactory);
        reqProtocolloPartenza.setTrasmissione(objectFactory.createReqDocumentoTrasmissione(trasmissione));

        if(codiceRegistro != null && !"".equals(codiceRegistro))
            reqProtocolloPartenza.setCodiceRegistro(codiceRegistro);
        else
            reqProtocolloPartenza.setCodiceRegistro("GRM");

        //DESTINATARI
        Corrispondente corrispondente = objectFactory.createCorrispondente();
        DatiCorrispondente datiCorrispondenteOccasionale = objectFactory.createDatiCorrispondente();
        datiCorrispondenteOccasionale.setCognome("IntermediaMarche");
        datiCorrispondenteOccasionale.setTipo(TipoVoceRubrica.ALTRO);
        corrispondente.setCorrispondenteOccasionale(objectFactory.createCorrispondenteCorrispondenteOccasionale(datiCorrispondenteOccasionale));

        ArrayOfCorrispondente arrayOfCorrispondente = objectFactory.createArrayOfCorrispondente();
        arrayOfCorrispondente.getCorrispondente().add(corrispondente);
        reqProtocolloPartenza.setDestinatari(arrayOfCorrispondente);

        //SEND TO ENDPOINT
        exchange.getIn().setBody(reqProtocolloPartenza);
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

    private ArrayOfClassificazione getClassificazioni(List<String> listaClassificazioni, ObjectFactory objectFactory, String paleoErrorMessage) {

        ArrayOfClassificazione arrayOfClassificazione = objectFactory.createArrayOfClassificazione();

        for (String classificazioneString : listaClassificazioni) {

            Classificazione classificazione = objectFactory.createClassificazione();

            if(paleoErrorMessage == null || "".equals(paleoErrorMessage)){
                classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(classificazioneString));
            }else{
                classificazione.setCodiceFascicolo(objectFactory.createClassificazioneCodiceFascicolo(""));
            }

            //classificazione.setNuovoFascicolo(objectFactory.createClassificazioneNuovoFascicolo(null));
            arrayOfClassificazione.getClassificazione().add(classificazione);
        }
        return arrayOfClassificazione;
    }

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
            trasmissioneUtente.setNote(objectFactory.createTrasmissioneRuoloNote("Trasmissione Notifica Esito Committente"));

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

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getFatturazionePassivaNotificaDecorrenzaTerminiManager() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManager = fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }
}