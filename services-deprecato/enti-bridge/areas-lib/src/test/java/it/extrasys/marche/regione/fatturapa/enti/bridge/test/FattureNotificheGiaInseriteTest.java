package it.extrasys.marche.regione.fatturapa.enti.bridge.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agosteeno on 03/08/16.
 */
public class FattureNotificheGiaInseriteTest {

    private static String CODICE_NOTIFICA_GIA_INSERITA= "EN001";
    private static String CODICE_FATTURA_GIA_INSERITA= "EF001";

    private static Pattern patternFatturaGiaInserita = Pattern.compile(".*" + CODICE_FATTURA_GIA_INSERITA + ".*", Pattern.CASE_INSENSITIVE);

    private static Pattern patternNotificaGiaInserita = Pattern.compile(".*" + CODICE_NOTIFICA_GIA_INSERITA + ".*", Pattern.CASE_INSENSITIVE);


    private static String STRINGA_FATTURA_GIA_INSERITA = "java.lang.Exception: EF001: Documento 45375414 già inserito nel sistema (id archivio temporaneo 2124703)!";
    private static String STRINGA_NOTIFICA_GIA_INSERITA = "java.lang.Exception: java.lang.Exception: EN001: Notifica IT80415740580_0M5MP_DT_004.xml già inserita";
    private static String STRINGA_ALTRO = "miao";

    @Test
    public void test(){

        Matcher matcherFattura = patternFatturaGiaInserita.matcher(STRINGA_FATTURA_GIA_INSERITA);

        Matcher matcherNotifica = patternNotificaGiaInserita.matcher(STRINGA_NOTIFICA_GIA_INSERITA);

        Matcher matcherAltro = patternFatturaGiaInserita.matcher(STRINGA_ALTRO);

        if (matcherFattura.matches()) {
            System.out.println("matcherFattura OK");
        } else {
            System.out.println("matcherFattura KO");
            Assert.fail();
        }

        if (matcherNotifica.matches()) {
            System.out.println("matcherNotifica OK");
        } else {
            System.out.println("matcherNotifica KO");
            Assert.fail();
        }

        if (!matcherAltro.matches()) {
            System.out.println("matcherAltro OK");
        } else {
            System.out.println("matcherAltro KO");
            Assert.fail();
        }

    }
}
