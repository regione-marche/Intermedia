package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class WarningStatiFattureTest {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdflong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void checkDecorrenzaTermini() throws ParseException {
        String flag = "FLAG_VERDE";
        String dataRicezioneString = "2019-09-29";
        DateTime dataRicezione = new DateTime(sdf.parse(dataRicezioneString));
        DateTime now = DateTime.now();
        Days days = Days.daysBetween(dataRicezione, now);
        if (days.isGreaterThan(Days.days(16))) {
            flag = "FLAG_ROSSO";
        }
        System.out.println("Difference between " + dataRicezione + " and " + now + " is " + days.getDays() + " - flag: " + flag);
    }

    @Test
    public void testDifferenceMinutes() throws ParseException {
        String dataRicezioneString = "2019-10-28 17:50:00";
        DateTime now = new DateTime();
        DateTime dataRielaborazione = new DateTime(sdflong.parse(dataRicezioneString));
        Minutes minutes = Minutes.minutesBetween(dataRielaborazione, now);
        if (minutes.isGreaterThan(Minutes.minutes(10))) {
            System.out.println("OK");
        } else {
            System.out.println("KO");
        }

        System.out.println("Difference minutes between " + dataRielaborazione + " and " + now + " is " + minutes.getMinutes());

    }

    @Test
    public void generaNomeFile() {
        String nomeFile="IT01234567890_11111.xml";
        Long identificativoSdi=1L;

        String numeroSequenza = nomeFile.substring(nomeFile.indexOf("_")+1, nomeFile.indexOf("."));//Lunghezza 5
        String numeroSequenzaNew = "";
        //Se l'identificativoSdi supera 99999 prendo gli ultimi 5 caratteri
        if (identificativoSdi > 100000) {
            String identificativoSdiString = identificativoSdi + "";
            numeroSequenzaNew = identificativoSdiString.substring(identificativoSdiString.length() - 5);
        } else {
            numeroSequenzaNew = StringUtils.leftPad(identificativoSdi + "", 5,"0");
        }
        String replace = nomeFile.replace(numeroSequenza, numeroSequenzaNew);
        Assert.assertEquals("IT01234567890_00001.xml", replace);
    }

}