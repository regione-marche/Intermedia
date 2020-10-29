package it.extrasys.marche.regione.fatturapa.enti.bridge;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
/**
 * Created by agosteeno on 20/07/16.
 */
public class TestSubject  {


    private static final Logger LOG = LoggerFactory.getLogger(TestSubject.class);

    @Test
    public void testSubjectBase64() throws Exception {

        String subject = "=?ISO-8859-1?B?Q09OU0VHTkE6IEZhdHR1cmEgQWRkYSBTb2Njb3JzbyBFbWVyZ2VuemEgJiBTZXJ2aXppIFNvY2lldOAgQ29vcGVyYXRpdmEgU29jaWFsZSBPLk4uTC5VLlMuLCBkYXRhIHJpY2V6aW9uZSBkYWxsbyBTZEkgMjAxNi8wNy8xOSAtIElkZW50aWZpY2F0aXZvIFNESTogNDU0MDUxNjI=?=";

        subject.trim();

        LOG.info("PecScansioneCasellaCheckSubjectProcessor: subject prima decode = [" + subject + "]");

        subject = javax.mail.internet.MimeUtility.decodeText(subject);

        LOG.info("PecScansioneCasellaCheckSubjectProcessor: subject dopo decode = [" + subject + "]");

        assertEquals("CONSEGNA: Fattura Adda Soccorso Emergenza & Servizi Società Cooperativa Sociale O.N.L.U.S., data ricezione dallo SdI 2016/07/19 - Identificativo SDI: 45405162", subject);
    }
}
