package it.extrasys.marche.regione.fatturapa.services.invioFatturazioneAttiva;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.TestManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Date;

public class EsitoInvioFatturazioneAttivaTestProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EsitoInvioFatturazioneAttivaTestProcessor.class);

    private FatturaAttivaManagerImpl fatturaAttivaManager;
    private TestManager testManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        String idFattura = (String) exchange.getIn().getHeader("idFatturaAttiva");
        BigInteger max = fatturaAttivaManager.getMaxIdentificativoSdiTest();
        BigInteger maxIdentificativoSdiTest = max.add(BigInteger.ONE);

        LOG.info("TEST - EsitoInvioFatturazioneAttivaProcessor - salvo la fattura con Identificativo SdI " + maxIdentificativoSdiTest + " e idFatturaFattiva " + idFattura + ";");
        FatturaAttivaEntity fatturaAttiva = fatturaAttivaManager.getFatturaAttivaFromIdFatturaAttiva(new BigInteger(idFattura));
        //Salva in tabella testCicloAttivo
        testManager.salvaTestCicloAttivo((String) exchange.getIn().getHeader("nomeFile"), maxIdentificativoSdiTest, (String) exchange.getIn().getHeader("codiceUfficio"), fatturaAttiva.getRicevutaComunicazione());

        // Savla SdI e statoFattura
        fatturaAttivaManager.salvaIdentificativoSdIAttiva(new BigInteger(idFattura), maxIdentificativoSdiTest);


        LOG.info("TEST - EsitoInvioFatturazioneAttivaProcessor - Identificativo SdI " + maxIdentificativoSdiTest + " e idFatturaFattiva " + idFattura + ": fattura salvata");

        //cancello il body perché dà fastidio ad activemq
        String nomeFileFatt = exchange.getIn().getHeader("nomeFile", String.class);
        if(nomeFileFatt.contains(".p7m")){
            nomeFileFatt = nomeFileFatt.replaceAll(".p7m", "");
        }

        String nomeFileRC = nomeFileFatt.replace(".xml", "_RC_001.xml");

        exchange.getIn().setHeader("identificativoSdI", maxIdentificativoSdiTest);
        exchange.getIn().setHeader("nomeFileRC", nomeFileRC);

        XMLGregorianCalendar now = DateUtils.DateToXMLGregorianCalendar(new Date());
        exchange.getIn().setHeader("dataOraRicezione", fatturaAttiva.getDataRicezioneFromEnti());
        exchange.getIn().setHeader("dataOraConsegna", now);
        exchange.getIn().setBody(null);
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }

    public TestManager getTestManager() {
        return testManager;
    }

    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }
}
