package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.marche.regione.paleo.services.ObjectFactory;
import it.marche.regione.paleo.services.OperatorePaleo;
import it.marche.regione.paleo.services.ReqDocProtocolliInFascicolo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 07/04/15.
 */
public class CreaRichiestaGetDocumentiProtocolliInFascicoloProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaRichiestaGetDocumentiProtocolliInFascicoloProcessor.class);


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTUREPA
    private String cognome; // default: SGG
    private String ruolo; // default: Protocollista
    private String uo;   //default SGG - Segreteria Generale


    @Override
    public void process(Exchange exchange) throws Exception {

        String codiceFascicolo = exchange.getIn().getHeader("codiceFascicolo", String.class);
        ObjectFactory objectFactoryPaleo = new ObjectFactory();

        OperatorePaleo operatorePaleo = objectFactoryPaleo.createOperatorePaleo();
        operatorePaleo.setCodiceFiscale(objectFactoryPaleo.createOperatorePaleoCodiceFiscale(null));
        operatorePaleo.setCodiceRuolo(objectFactoryPaleo.createOperatorePaleoCodiceRuolo(null));
        operatorePaleo.setCodiceUO(codiceUO);
        operatorePaleo.setCodiceUOSicurezza(objectFactoryPaleo.createOperatorePaleoCodiceUOSicurezza(null));
        operatorePaleo.setCognome(cognome);
        operatorePaleo.setNome(objectFactoryPaleo.createOperatorePaleoNome(nome));
        operatorePaleo.setRuolo(ruolo);
        operatorePaleo.setUO(objectFactoryPaleo.createOperatorePaleoUO(uo));


        ReqDocProtocolliInFascicolo richiesta = objectFactoryPaleo.createReqDocProtocolliInFascicolo();

        richiesta.setOperatore(operatorePaleo);
        richiesta.setSoloPubblici(false);
        richiesta.setCodiceFascicolo(codiceFascicolo);

        exchange.getIn().setBody(richiesta, ReqDocProtocolliInFascicolo.class);

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
