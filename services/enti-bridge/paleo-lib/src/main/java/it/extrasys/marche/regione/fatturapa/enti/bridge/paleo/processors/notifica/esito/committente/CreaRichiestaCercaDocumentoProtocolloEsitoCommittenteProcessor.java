package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.marche.regione.paleo.services.ObjectFactory;
import it.marche.regione.paleo.services.OperatorePaleo;
import it.marche.regione.paleo.services.ReqCercaProtocollo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class CreaRichiestaCercaDocumentoProtocolloEsitoCommittenteProcessor implements Processor {


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;
    private String codiceRegitro; // default GRM


    @Override
    public void process(Exchange exchange) throws Exception {

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        ObjectFactory objectFactory = new ObjectFactory();

        List<Object> parametri = new ArrayList<Object>();

        ReqCercaProtocollo reqCercaProtocollo = new ReqCercaProtocollo();

        reqCercaProtocollo.setOperatore(getRichiedente(objectFactory));

        reqCercaProtocollo.setSegnatura(objectFactory.createReqCercaProtocolloSegnatura(notificaEsitoCommittenteWrapper.getSegnaturaProtocolloFattura()));

        parametri.add(reqCercaProtocollo);

        exchange.getIn().setBody(parametri);
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

    public String getCodiceRegitro() {
        return codiceRegitro;
    }

    public void setCodiceRegitro(String codiceRegitro) {
        this.codiceRegitro = codiceRegitro;
    }
}


