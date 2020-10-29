package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.OrderRequest;
import it.extrasys.marche.regione.fatturapa.api.rest.model.WarningStatiFatturaCountResponse;
import it.extrasys.marche.regione.fatturapa.api.rest.model.WarningStatiFatturaResponseList;
import it.extrasys.marche.regione.fatturapa.api.rest.model.WarningStatiFattureResponse;
import it.extrasys.marche.regione.fatturapa.api.rest.models.FatturaBindy;
import it.extrasys.marche.regione.fatturapa.api.rest.utils.WarningStatiFatture;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.MonitorFatturaPassivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.MonitorFatturaPassivaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.UtentiManager;
import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WarningStatiFatturePassiveImpl {
    private static final Logger LOG = LoggerFactory.getLogger(WarningStatiFatturePassiveImpl.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private DatiFatturaManager datiFatturaManager;
    private UtentiManager utentiManager;
    private MonitorFatturaPassivaManager monitorFatturaPassivaManager;
    private String durataToken;
    private Integer intervalDecTerm;
    private String interval;

    public void pcServizioWarningStatiFattureGET(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();
        String token = (String) parameters.get(0);
        OrderRequest orderRequest = (OrderRequest) parameters.get(1);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);

            List<MonitorFatturaPassivaEntity> monitorFatturePassive = monitorFatturaPassivaManager.getMonitorFatturePassive(orderRequest.getOrderBy(), orderRequest.getOrdering(), orderRequest.getNumberOfElements(), orderRequest.getPageNumber(), CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE.getValue());

            List<WarningStatiFattureResponse> result = mapWarningStatiFattureResponse(monitorFatturePassive);
            WarningStatiFatturaResponseList responseList = new WarningStatiFatturaResponseList();
            responseList.setWarningStatiFattureResponseList(result);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setBody(responseList);
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }


    public void pcServizioCountWarningStatiFattureGET(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();
        String token = (String) parameters.get(0);

        try {
            WarningStatiFatturaCountResponse response = new WarningStatiFatturaCountResponse();
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            Long count = monitorFatturaPassivaManager.getCountMonitorFatturePassive(CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE.getValue());
            response.setCount(count.intValue());
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setBody(response);
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }


    public void pcServizioStatiFattureGET(Exchange exchange) throws FatturaPAException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {
        MessageContentsList parameters = (MessageContentsList) exchange.getIn().getBody();
        String token = (String) parameters.get(0);

        try {
            String usernameUtente = Autenticazione.checkToken(token, utentiManager, durataToken);
            //TODO: Non so se serve la paginazione
            List<MonitorFatturaPassivaEntity> monitorFattureAttive = monitorFatturaPassivaManager.getMonitorFatturePassive(null, null, null, null, CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE.getValue());
            List<FatturaBindy> fatturaBindies = mapFatturaBindy(monitorFattureAttive);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
            exchange.getIn().setHeader("username", usernameUtente);
            exchange.getIn().setBody(fatturaBindies);
        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString() + " - " + e.getMessage());
            throw e;
        }
    }

    /*
    Metodi per il batch
     */
    public void salvaMonitorFatturaPassiva(Exchange exchange) throws FatturaPAException {
        LOG.info("MONITOR FATTURA PASSIVA BATCH - START Popola tabella monitor_fattura_passiva");
        List<Object[]> fattureAttive = datiFatturaManager.getUltimoStatoFatture(interval);
        List<MonitorFatturaPassivaEntity> monitorFatturaPassivaEntity = mapResponseWarning(fattureAttive, intervalDecTerm);

        monitorFatturaPassivaManager.salvaMonitorFatturaPassiva(monitorFatturaPassivaEntity);
        LOG.info("MONITOR FATTURA PASSIVA BATCH - START Popola tabella monitor_fattura_passiva");
    }


    private List<MonitorFatturaPassivaEntity> mapResponseWarning(List<Object[]> fatture, int intervalDecTermini) {
        List<MonitorFatturaPassivaEntity> list = new ArrayList<>();
        for (Object[] obj : fatture) {
            MonitorFatturaPassivaEntity monitorFatturaPassivaEntity = new MonitorFatturaPassivaEntity();
            monitorFatturaPassivaEntity.setIdentificativoSdi(BigInteger.valueOf((Long) obj[0]));
            monitorFatturaPassivaEntity.setDataUltimoStato((Timestamp) obj[1]);
            monitorFatturaPassivaEntity.setCodiceUfficioDestinatario((String) obj[2]);
            monitorFatturaPassivaEntity.setDataCreazione(new Timestamp(((Date) obj[3]).getTime()));
            monitorFatturaPassivaEntity.setNomeFile((String) obj[4]);
            monitorFatturaPassivaEntity.setStato((String) obj[5]);
            monitorFatturaPassivaEntity.setTipoCanale((String) obj[6]);
            monitorFatturaPassivaEntity.setIdDatiFattura(BigInteger.valueOf((Long) obj[8]));
            monitorFatturaPassivaEntity.setNote((String) obj[9]);
            monitorFatturaPassivaEntity.setFlag(WarningStatiFatture.calcolaWarningFatturePassive((String) obj[7], (Date) obj[3], intervalDecTermini));
            list.add(monitorFatturaPassivaEntity);
        }
        return list;
    }


    private static List<WarningStatiFattureResponse> mapWarningStatiFattureResponse(List<MonitorFatturaPassivaEntity> monitorFatturePassive) {
        List<WarningStatiFattureResponse> result = new ArrayList<>();

        for (MonitorFatturaPassivaEntity mfp : monitorFatturePassive) {
            WarningStatiFattureResponse w = new WarningStatiFattureResponse();
            w.setIdentificativoSdi(mfp.getIdentificativoSdi() + "");
            w.setDataUltimoStato(mfp.getDataUltimoStato());
            w.setDataCreazione(mfp.getDataCreazione());
            w.setCodiceUfficioDestinatario(mfp.getCodiceUfficioDestinatario());
            w.setNomeFile(mfp.getNomeFile());
            w.setTipoCanale(mfp.getTipoCanale());
            w.setStato(mfp.getStato());
            w.setFlag(mfp.getFlagWarning().getDescFlagWarning());

            result.add(w);
        }
        return result;
    }


    private static List<FatturaBindy> mapFatturaBindy(List<MonitorFatturaPassivaEntity> fatture) {
        return fatture.stream()
                .map(f -> {
                    FatturaBindy fb = new FatturaBindy();
                    fb.setIdentificativoSdi(f.getIdentificativoSdi() + "");
                    fb.setDataUltimoStato(sdf.format(f.getDataUltimoStato()));
                    fb.setCodiceUfficio(f.getCodiceUfficioDestinatario());
                    fb.setDataCreazione(sdf.format(f.getDataCreazione()));
                    fb.setNomeFile(f.getNomeFile());
                    fb.setTipoCanale(f.getTipoCanale());
                    fb.setStato(f.getStato());
                    fb.setFlag(f.getFlagWarning().getDescFlagWarning());
                    return fb;
                }).collect(Collectors.toList());

    }


    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public UtentiManager getUtentiManager() {
        return utentiManager;
    }

    public void setUtentiManager(UtentiManager utentiManager) {
        this.utentiManager = utentiManager;
    }

    public String getDurataToken() {
        return durataToken;
    }

    public void setDurataToken(String durataToken) {
        this.durataToken = durataToken;
    }

    public Integer getIntervalDecTerm() {
        return intervalDecTerm;
    }

    public void setIntervalDecTerm(Integer intervalDecTerm) {
        this.intervalDecTerm = intervalDecTerm;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public MonitorFatturaPassivaManager getMonitorFatturaPassivaManager() {
        return monitorFatturaPassivaManager;
    }

    public void setMonitorFatturaPassivaManager(MonitorFatturaPassivaManager monitorFatturaPassivaManager) {
        this.monitorFatturaPassivaManager = monitorFatturaPassivaManager;
    }
}
