package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CanaleCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GestioneEntiManager {
    private static final Logger LOG = LoggerFactory.getLogger(GestioneEntiManager.class);

    private EnteManager enteManager;

    public void getEnteByCodiceUfficioFtpReportSt(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        List<EnteEntity> entiFtp = enteManager.getEnteByCodiceUfficioFtpReportSt(exchange.getIn().getHeader("codiceEnte", String.class));
        exchange.getIn().setBody(entiFtp);
    }

    public void recuperaEntiInvioSingolo(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        List<EnteEntity> entiFtp = enteManager.getEnteFtpInvioSingoloByTipoCanale(CanaleCaEntity.CANALE_CA.FTP.getValue());
        exchange.getIn().setBody(entiFtp);
    }

    public void recuperaEntiInvioProtocollo(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        List<EnteEntity> entiFtp = enteManager.getEnteFtpInvioProtocolloByTipoCanale(CanaleCaEntity.CANALE_CA.FTP.getValue());
        exchange.getIn().setBody(entiFtp);
    }

    public void recuperaEntiGestionale(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        List<EnteEntity> entiFtp = enteManager.getEnteFtpInvioGestionaleByTipoCanale(CanaleCaEntity.CANALE_CA.FTP.getValue());
        exchange.getIn().setBody(entiFtp);
    }


    public void recuperaEntiFatturaAttivaFtp(Exchange exchange) throws FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPaPersistenceException {
        List<EnteEntity> entiFtp = enteManager.getEnteFtpInvioFatturaAttivaByTipoCanale(CanaleCaEntity.CANALE_CA.FTP.getValue());

        exchange.getIn().setBody(entiFtp);
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }
}
