package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GestioneStatoManagerTest {

    @Test
    public void aggiornaStatoFattureAttiveFtpTest() {
        Map<String, List<FatturaFtpModel>> mapInput = new HashMap<>();

        FatturaFtpModel fm1 = new FatturaFtpModel();
        fm1.setIdFattura(BigInteger.ONE);
        fm1.setTipoFattura(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.name());
        fm1.setNomeFile("file1");
        fm1.setIdFiscaleEnte("ente1");

        FatturaFtpModel fm2 = new FatturaFtpModel();
        fm2.setIdFattura(BigInteger.ONE);
        fm2.setTipoFattura(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.ATTESTAZIONE_TRASMISSIONE_FATTURA.name());
        fm2.setNomeFile("file2");
        fm2.setIdFiscaleEnte("ente2");

        FatturaFtpModel fm3 = new FatturaFtpModel();
        fm3.setIdFattura(BigInteger.ONE);
        fm3.setTipoFattura(TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_DECORRENZA_TERMINI.name());
        fm3.setNomeFile("file3");
        fm3.setIdFiscaleEnte("ente3");

        List<FatturaFtpModel> list = new ArrayList<>();
        list.add(fm1);
        list.add(fm2);
        list.add(fm3);
        mapInput.put("zip1", list);

        Map<String, List<FatturaFtpModel>> mapTipoFattura = mapInput.get("zip1").stream()
                .collect(Collectors.groupingBy(FatturaFtpModel::getTipoFattura));

        System.out.println("FINISHED");
    }

    @Test
    public void testTime() {
        ZoneId idz = ZoneId.of("Europe/Rome");
        Instant instant = Instant.now();

        DateTime dateTime = new DateTime();
        int ddd1 = dateTime.getDayOfYear();
        int aaaa1 = dateTime.getYear();
        int hh1 = dateTime.getHourOfDay();
        int mm1 = dateTime.getMinuteOfHour();

        int ddd = instant.atZone(idz).get(ChronoField.DAY_OF_YEAR);
        int aaaa = instant.atZone(idz).get(ChronoField.YEAR);
        int hh = instant.atZone(idz).get(ChronoField.HOUR_OF_DAY);
        int mm = instant.atZone(idz).get(ChronoField.MINUTE_OF_HOUR);

        System.out.println(ddd + " - " + aaaa + " - " + hh + " - " + mm);
        System.out.println(ddd1 + " - " + aaaa1 + " - " + hh1 + " - " + mm1);
    }
}