package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MonitorRielaborazioniManager;
import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class MonitorRielaborazioniImpl {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorRielaborazioniImpl.class);

    private MonitorRielaborazioniManager monitorRielaborazioniManager;
    private Integer giorniRipulitura;
    private String pathFileReport;

    public void updateMonitorRielaborazioni(Exchange exchange) throws FatturaPAException {
        BigInteger identificativoSdi;
        try {
            identificativoSdi = new BigInteger((String) exchange.getIn().getHeader("identificativoSdI"));
        }
        catch(ClassCastException e){
            identificativoSdi = BigInteger.valueOf((Long) exchange.getIn().getHeader("identificativoSdI"));
        }
        String nomeReport = (String) exchange.getIn().getHeader("MONITORAGGIO.nomeReport");

        monitorRielaborazioniManager.updateMonitorRielaborazioniByIdentificativoSdiAndNomeReport(identificativoSdi, nomeReport, (String) exchange.getIn().getBody());
    }


    public void ripulisciMonitorRielaborazioni(Exchange exchange) throws FatturaPAException, IOException {

        DateTime now = DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0);

        DateTime date = now.minusDays(giorniRipulitura);

        List<String> nomeReport = monitorRielaborazioniManager.deleteMonitorRielaborazioniBeforeDate(date.toDate());

        //Cancello dal file system i file di report relativi alle rielaborazioni del database
        for (String f : nomeReport) {
            File file = new File(pathFileReport + f);
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        }
    }

    public MonitorRielaborazioniManager getMonitorRielaborazioniManager() {
        return monitorRielaborazioniManager;
    }

    public void setMonitorRielaborazioniManager(MonitorRielaborazioniManager monitorRielaborazioniManager) {
        this.monitorRielaborazioniManager = monitorRielaborazioniManager;
    }

    public Integer getGiorniRipulitura() {
        return giorniRipulitura;
    }

    public void setGiorniRipulitura(Integer giorniRipulitura) {
        this.giorniRipulitura = giorniRipulitura;
    }

    public String getPathFileReport() {
        return pathFileReport;
    }

    public void setPathFileReport(String pathFileReport) {
        this.pathFileReport = pathFileReport;
    }
}
