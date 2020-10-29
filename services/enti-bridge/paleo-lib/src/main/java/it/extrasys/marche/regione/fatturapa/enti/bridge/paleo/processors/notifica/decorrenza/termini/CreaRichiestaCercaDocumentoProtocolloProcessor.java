package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.marche.regione.paleo.services.ObjectFactory;
import it.marche.regione.paleo.services.OperatorePaleo;
import it.marche.regione.paleo.services.ReqCercaProtocollo;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 11/03/15.
 */
public class CreaRichiestaCercaDocumentoProtocolloProcessor implements Processor {


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;


    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;


    @Override
    public void process(Exchange exchange) throws Exception {

        Message msg = exchange.getIn();

        String segnaturaProtocolloFattura = "";

        if(msg.getBody() instanceof NotificaDecorrenzaTerminiWrapper) {

            NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = exchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

            segnaturaProtocolloFattura = notificaDecorrenzaTerminiWrapper.getSegnaturaProtocolloFattura();

        }else if(msg.getBody() instanceof NotificaEsitoCommittenteWrapper){

            NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

            segnaturaProtocolloFattura = notificaEsitoCommittenteWrapper.getSegnaturaProtocolloFattura();

        }else if(msg.getBody() instanceof NotificaScartoEsitoCommittenteWrapper){

            NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

            segnaturaProtocolloFattura = notificaScartoEsitoCommittenteWrapper.getSegnaturaProtocolloFattura();
        }

        ObjectFactory objectFactory = new ObjectFactory();

        List<Object> parametri = new ArrayList<Object>();

        ReqCercaProtocollo reqCercaProtocollo = new ReqCercaProtocollo();

        reqCercaProtocollo.setOperatore(getRichiedente(objectFactory));

        reqCercaProtocollo.setSegnatura(objectFactory.createReqCercaProtocolloSegnatura(segnaturaProtocolloFattura));

        parametri.add(reqCercaProtocollo);

        exchange.getIn().setBody(parametri);
    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
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
}