package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturaAttivaManagerImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;

public class SalvaFatturaAttivaProcessor implements Processor {

    private FatturaAttivaManagerImpl fatturaAttivaFromEntiManager;
    private EnteManager enteManager;

    @Override
    public void process(Exchange exchange) throws Exception {
        String file=(String) exchange.getProperty("fileOriginale");
        byte[] fileOriginale = file.getBytes();
        FatturaElettronicaType fatturaElettronicaType = (FatturaElettronicaType) exchange.getIn().getBody();
        String codiceUfficio = (String)exchange.getIn().getHeader("codiceUfficio");

        String formatoTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getFormatoTrasmissione().value();

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        //Controllo se è un ente di TEST
        Boolean isTest=Boolean.FALSE;
        if("STAGING".equalsIgnoreCase(enteEntity.getAmbienteCicloAttivo())) {
            isTest=Boolean.TRUE;
        }
        exchange.getIn().setHeader("fatturazioneTest", isTest);

        FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaFromEntiManager.salvaFatturaAttivaFtp(fileOriginale, (String) exchange.getIn().getHeader(FtpConstants.NOME_FILE), fatturaElettronicaType, enteEntity, formatoTrasmissione, isTest);

        exchange.getIn().setHeader("idFatturaAttiva", fatturaAttivaEntity.getIdFatturaAttiva());
        String messaggioEncoded = new String(Base64.encodeBase64(fileOriginale));
        exchange.getIn().setBody(messaggioEncoded);
    }

    public FatturaAttivaManagerImpl getFatturaAttivaFromEntiManager() {
        return fatturaAttivaFromEntiManager;
    }

    public void setFatturaAttivaFromEntiManager(FatturaAttivaManagerImpl fatturaAttivaFromEntiManager) {
        this.fatturaAttivaFromEntiManager = fatturaAttivaFromEntiManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }
}
