package it.extrasys.marche.regione.fatturapa.api.rest.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.TestManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class TestCicloPassivoAttivoProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(TestCicloPassivoAttivoProcessor.class);

    private TestManager testManager;
    private Integer giorniRipulitura;

    @Override
    public void process(Exchange exchange) throws Exception {

        DateTime now = DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0);

        DateTime date = now.minusDays(giorniRipulitura);
        LOG.info("TEST CICLO PASSIVO/ATTIVO - RIPULITURA FATTURE ATTIVE START");
        testManager.ripulisciFattureAttiveTestBeforeDate(new Timestamp(date.toDate().getTime()));
        LOG.info("TEST CICLO PASSIVO/ATTIVO - RIPULITURA FATTURE ATTIVE END");

        LOG.info("TEST CICLO PASSIVO/ATTIVO - RIPULITURA FATTURE PASSIVE START");
        testManager.ripulisciFatturePassiveTestBeforeDate(new Timestamp(date.toDate().getTime()));
        LOG.info("TEST CICLO PASSIVO/ATTIVO - RIPULITURA FATTURE PASSIVE END");

    }

    public TestManager getTestManager() {
        return testManager;
    }

    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }

    public Integer getGiorniRipulitura() {
        return giorniRipulitura;
    }

    public void setGiorniRipulitura(Integer giorniRipulitura) {
        this.giorniRipulitura = giorniRipulitura;
    }
}
