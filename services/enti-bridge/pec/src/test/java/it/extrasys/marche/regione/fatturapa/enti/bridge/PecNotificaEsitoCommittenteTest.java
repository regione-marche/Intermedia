package it.extrasys.marche.regione.fatturapa.enti.bridge;

import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by agosteeno on 23/04/15.
 */
public class PecNotificaEsitoCommittenteTest extends CamelTestSupport{

    @Test
    public void checkEmailPecTestMethod(){

        String listaEmailPec = "prova@miao.it,test@bau.it, mittente@email.it";

        String indirizzoEmailMittente = "mittente@email.it";

        boolean result = checkEmailPec(listaEmailPec, indirizzoEmailMittente);

        Assert.assertTrue(result);

    }

    private boolean checkEmailPec(String listaEmailPec, String indirizzoEmailMittente) {

        if (listaEmailPec == null || "".equals(listaEmailPec) || indirizzoEmailMittente == null || "".equals(indirizzoEmailMittente)){
            return false;
        }

        List<String> emails = new ArrayList<String>(Arrays.asList(listaEmailPec.split(",")));

        boolean found = false;
        for(String s : emails) {
            if (s.trim().toLowerCase().equals(indirizzoEmailMittente.trim().toLowerCase())) {
                found =  true;
                break;
            }

        }
        return found;
    }

    @Test
    public void estraiMessaggioAccettazioneRifiuto() throws FatturaPAFatturaNonTrovataException, FatturaPAException, FatturaPaPersistenceException, IOException, JAXBException, FatturaPAAllegatoAttivaNonTrovatoException, FatturaPAEnteNonTrovatoException, MessagingException {

        Exchange ex = new DefaultExchange(context);

        ex.getIn().setHeader("Return-Path", "posta-certificata@pec.ciao.it");
        ex.getIn().setHeader("subject", "CONSEGNA: Fattura EVOS SRL Società Uninominale, data ricezione dallo SdI 2016/05/31 - Identificativo SDI: 41085358");


        PecNotificaEsitoCommittente p = new PecNotificaEsitoCommittente();
        p.estraiMessaggioAccettazioneRifiuto(ex);

        Boolean emailDaIgnorare = (Boolean) ex.getIn().getHeader("emailDaIgnorare");

        Assert.assertTrue(emailDaIgnorare);
    }
}
