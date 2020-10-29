package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.marche.regione.paleo.services.ObjectFactory;
import it.marche.regione.paleo.services.OperatorePaleo;
import it.marche.regione.paleo.services.ReqFindRubrica;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 06/03/15.
 */
public class CreaRichiestaFindRubricaExtProcessor implements Processor {


    private String codiceUO; // default: SGG
    private String nome;  // default: FATTURAPA
    private String cognome; // default: SGG
    private String ruolo; // Protocollista
    private String uo;  // default SGG - Segreteria Generale


    public void process(Exchange exchange) throws Exception {

        List<Object> parametri = new ArrayList<Object>();

        ObjectFactory objectFactoryPaleo = new ObjectFactory();

        OperatorePaleo operatorePaleo = objectFactoryPaleo.createOperatorePaleo();

        //  CREO IL PRIMO PARAMETRO : OPERATORE PALEO
        operatorePaleo.setCodiceFiscale(objectFactoryPaleo.createOperatorePaleoCodiceFiscale(null));
        operatorePaleo.setCodiceRuolo(objectFactoryPaleo.createOperatorePaleoCodiceRuolo(null));
        operatorePaleo.setCodiceUO(codiceUO);
        operatorePaleo.setCodiceUOSicurezza(objectFactoryPaleo.createOperatorePaleoCodiceUOSicurezza(null));
        operatorePaleo.setCognome(cognome);
        operatorePaleo.setNome(objectFactoryPaleo.createOperatorePaleoNome(nome));
        operatorePaleo.setRuolo(ruolo);
        operatorePaleo.setUO(objectFactoryPaleo.createOperatorePaleoUO(uo));
        /// FINE

        parametri.add(operatorePaleo);

        ReqFindRubrica reqFindRubrica = new ReqFindRubrica();

        String prolog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

        String cedenteString = exchange.getIn().getHeader("FatturaCedentePrestatore", String.class);

        cedenteString = prolog + cedenteString;

        CedentePrestatoreType cedentePrestatoreType = JaxBUtils.getCedentePrestatoreType(cedenteString.getBytes());

        String idFiscale = "";

        if(cedentePrestatoreType.getDatiAnagrafici().getIdFiscaleIVA() != null){
            idFiscale=cedentePrestatoreType.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
        }else{
            idFiscale=cedentePrestatoreType.getDatiAnagrafici().getCodiceFiscale();
        }

        reqFindRubrica.setIdFiscale(new ObjectFactory().createReqFindRubricaIdFiscale(idFiscale));

        parametri.add(reqFindRubrica);

        exchange.getIn().setBody(parametri,List.class);

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
