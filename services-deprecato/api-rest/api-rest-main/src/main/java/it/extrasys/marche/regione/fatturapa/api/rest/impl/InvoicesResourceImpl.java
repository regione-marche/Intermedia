package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.*;
import it.extrasys.marche.regione.fatturapa.core.exceptions.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.EnteDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaAttivaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.*;
import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoicesResourceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(InvoicesResourceImpl.class);

    private DatiFatturaManager datiFatturaManager;

    private UtentiManager utentiManager;

    private FatturaAttivaManagerImpl fatturaAttivaManager;

    private InvoicesManager invoicesManager;

    private EnteDao enteDao;

    private EnteManager enteManager;

    private FileFatturaManager fileFatturaManager;

    private String durataToken;

    private long treGiorni = 259200000;

    /**
     * Servizio utilizzato per recuperare gli id e Nome tag XML del File Fattura
     */
    public List<InvoiceFieldsDetail> servizioInvoicesCampiGET(Exchange exchange){

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioInvoicesCampiGET **********");

        String token = (String) parameters.get(0);

        return null;
    }

    /**
     * Servizio utilizzato per recuperare il numero di fatture passive
     */
    public int servizioCountInvoicesPassiveCyclePOST(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioCountInvoicesPassiveCyclePOST **********");

        InvoicesPCRequest body = (InvoicesPCRequest) parameters.get(0);
        String token = (String) parameters.get(1);

        int count;

        try {
            Autenticazione.checkToken(token, utentiManager, durataToken);
            Date dateFrom = (body.getDataRicezioneDa());
            Date dateTo = (body.getDataRicezioneA());
            count = datiFatturaManager.getCountFattureByCodiceDestinatarioAndDate(body.getCodiceUfficioDestinatario(), dateFrom, dateTo);
        } catch (FatturaPAException | FatturaPATokenNonValidoException | FatturaPAUtenteNonTrovatoException | FatturaPAUtenteNonAutorizzatoException | FatturaPAEnteNonTrovatoException | FatturaPaPersistenceException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

        return count;
    }

    /**
     * Servizio utilizzato per ricercare le fatture passive
     */
    public InvoicesPCResponse servizioInvoicesPassiveCyclePOST(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioInvoicesPassiveCyclePOST **********");

        InvoicesPCRequest body = (InvoicesPCRequest) parameters.get(0);
        String token = (String) parameters.get(1);

        List<DatiFatturaEntity> datiFatturaEntityList = new ArrayList<>();

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            if(body.getNomeFileFattura() != null) {
                datiFatturaEntityList = datiFatturaManager.getFattureByNomeFileFattura(body.getNomeFileFattura());
            }
            else if(body.getIdentificativoSdi() != null) {
                datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDIProduzione(new BigInteger(body.getIdentificativoSdi()));
            }
            else if(body.getSegnaturaProtocollo() != null) {
                datiFatturaEntityList = datiFatturaManager.getFattureByNumeroProtocollo(body.getSegnaturaProtocollo());
            }
            else if(body.getCodiceUfficioDestinatario() != null && body.getDataRicezioneA() != null && body.getDataRicezioneDa() != null) {
                Date dateFrom = (body.getDataRicezioneDa());
                Date dateTo = (body.getDataRicezioneA());
                long da = dateFrom.getTime();
                long a = dateTo.getTime();
                if(a-da > treGiorni){
                    throw new BadRequestException("servizioInvoicesPassiveCyclePost");
                }

                datiFatturaEntityList = datiFatturaManager.getFattureByCodiceDestinatarioAndDate(body.getCodiceUfficioDestinatario(), dateFrom, dateTo, body.getOrderBy(), body.getNumberOfElements(), body.getPageNumber(), body.getOrdering());
            }

            if(datiFatturaEntityList.size() == 0){
                throw new FatturaPAFatturaNonTrovataException();
            }

            List<DatiFatturaEntity> datiFatturaEntityList1 = new ArrayList<>();

            for(DatiFatturaEntity datiFatturaEntity : datiFatturaEntityList){
                String codiceDestinatario = datiFatturaEntity.getCodiceDestinatario();
                UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
                utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
                utenteEnteEntity.setEnte(enteManager.getEnteByCodiceUfficio(codiceDestinatario));
                if(utentiManager.checkAuth(utenteEnteEntity)){
                   datiFatturaEntityList1.add(datiFatturaEntity);
                }
            }

            if(datiFatturaEntityList1.size() == 0){
                throw new FatturaPAUtenteNonAutorizzatoException("Ricerca non autorizzata");
            }

            return FunzioniConversione.datiFatturaEntityListToInvoicesPcResponse(datiFatturaEntityList1);

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPaPersistenceException
                | FatturaPAFatturaNonTrovataException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    /**
     * Servizio utilizzato per mostrare il dettaglio delle fatture passive
     */
    public InvoicePCDetail servizioInvoicesPassiveCycleDettaglioGET(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioInvoicesPassiveCycleDettaglioGET **********");

        Integer identificativoSdi = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);
        List<DatiFatturaEntity> datiFatturaEntityList;
        InvoicePCDetail invoicePCDetail;
        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDIProduzione(BigInteger.valueOf(identificativoSdi));
            invoicePCDetail = FunzioniConversione.datiFatturaEntityListToInvoicePCDetail(datiFatturaEntityList);
            EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(invoicePCDetail.getCodiceUfficioDestinatario());

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
            utenteEnteEntity.setEnte(enteEntity);

            if(!utentiManager.checkAuth(utenteEnteEntity)){
                throw new FatturaPAUtenteNonAutorizzatoException("Ricerca non autorizzata");
            }

            String tipoCanaleCod = enteEntity.getTipoCanale().getCodTipoCanale();
            String tipoCanaleDesc = enteEntity.getTipoCanale().getDescCanale();

            invoicePCDetail.setTipoCanale(tipoCanaleCod+"-"+tipoCanaleDesc);

            List<InvoiceFlowPC> invoiceFlowPCList = new ArrayList<>();

            for(DatiFatturaEntity datiFatturaEntity : datiFatturaEntityList){
                List<StatoFatturaEntity> sfl = datiFatturaManager.getStatiFattura(datiFatturaEntity);
                for(int i=0; i<sfl.size(); i++){
                    InvoiceFlowPC invoiceFlowPC = new InvoiceFlowPC();
                    invoiceFlowPC.setNumeroFattura(datiFatturaEntity.getNumeroFattura());
                    CodificaStatiEntity cs = datiFatturaManager.getCodificaStatoByIdCodStato(sfl.get(i).getStato().getCodStato().getValue());
                    invoiceFlowPC.setStato(cs.getCodStato().getValue()+"-"+cs.getDescStato());
                    invoiceFlowPC.setData(sfl.get(i).getData());
                    invoiceFlowPCList.add(invoiceFlowPC);
                }
            }

            invoicePCDetail.setInvoiceFlow(invoiceFlowPCList);

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPaPersistenceException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

        return invoicePCDetail;
    }

    /**
     * Servizio utilizzato per ricercare le fatture attive
     */
    public InvoicesACResponse servizioInvoicesActiveCyclePOST(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioInvoicesActiveCyclePOST **********");

        InvoicesACRequest body = (InvoicesACRequest) parameters.get(0);
        String token = (String) parameters.get(1);

        List<FatturaAttivaEntity> fatturaAttivaEntityList = null;

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            if(body.getNomeFileFattura() != null){
                fatturaAttivaEntityList = fatturaAttivaManager.getFatturaAttivaFromNomeFileFattura(body.getNomeFileFattura());
            }
            else if(body.getIdentificativoSdi() != null){
                fatturaAttivaEntityList = fatturaAttivaManager.getFatturaAttivaListFromIdentificativoSdi(new BigInteger(body.getIdentificativoSdi()));
            }
            else if(body.getCodiceUfficioMittente() != null && body.getDataInoltroDa() != null && body.getDataInoltroA()!= null){
                Date dateFrom = (body.getDataInoltroDa());
                Date dateTo = (body.getDataInoltroA());
                long da = dateFrom.getTime();
                long a = dateTo.getTime();
                if(a-da > treGiorni){
                    throw new BadRequestException("servizioInvoicesPassiveCyclePost");
                }
                fatturaAttivaEntityList = fatturaAttivaManager.getFatturaAttivaListFromCodiceUfficioMittenteAndDate(body.getCodiceUfficioMittente(), dateFrom, dateTo);
            }

            if(fatturaAttivaEntityList.size() == 0){
                throw new FatturaPAFatturaNonTrovataException();
            }

            List<FatturaAttivaEntity> fatturaAttivaEntityList1 = new ArrayList<>();

            for(FatturaAttivaEntity fatturaAttivaEntity : fatturaAttivaEntityList){
                String codiceDestinatario = fatturaAttivaEntity.getEnte().getCodiceUfficio();
                UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
                utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
                utenteEnteEntity.setEnte(enteManager.getEnteByCodiceUfficio(codiceDestinatario));
                if(utentiManager.checkAuth(utenteEnteEntity)){
                    fatturaAttivaEntityList1.add(fatturaAttivaEntity);
                }
            }

            if(fatturaAttivaEntityList1.size() == 0){
                throw new FatturaPAUtenteNonAutorizzatoException("Ricerca non autorizzata");
            }

            return FunzioniConversione.fatturaAttivaEntityListToInvoicesACResponse(fatturaAttivaEntityList1);


        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    /**
     * Servizio utilizzato per mostrare il dettaglio delle fatture attive
     */
    public InvoiceACDetail servizioInvoicesActiveCycleDettaglioGET(Exchange exchange) throws FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException, FatturaPAFatturaNonTrovataException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** servizioInvoicesActiveCycleDettaglioGET **********");

        Integer identificativoSdi = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);

        List<FatturaAttivaEntity> fatturaAttivaEntityList = null;

        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);

            fatturaAttivaEntityList = fatturaAttivaManager.getFatturaAttivaListFromIdentificativoSdi(BigInteger.valueOf(identificativoSdi));

            //popola solo i campi comuni dell'oggetto ma non l'array con data e stato
            InvoiceACDetail invoiceACDetail = FunzioniConversione.fatturaEntityListToInvoiceACDetail(fatturaAttivaEntityList);

            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
            utenteEnteEntity.setEnte(fatturaAttivaEntityList.get(0).getEnte());

            if(!utentiManager.checkAuth(utenteEnteEntity)){
                throw new FatturaPAUtenteNonAutorizzatoException("Ricerca non autorizzata");
            }

            BigInteger idFatturaAttiva = fatturaAttivaEntityList.get(0).getIdFatturaAttiva();

            List<InvoiceFlow> invoiceFlowList = new ArrayList<>();

            String stato, cod, desc;

            InvoiceFlow invoiceFlow = new InvoiceFlow();

            List<StatoFatturaAttivaEntity> ent0 = invoicesManager.getStatoFromIdFatturaAttiva(idFatturaAttiva);

            if(ent0 != null){
                for(StatoFatturaAttivaEntity statoFatturaAttivaEntity : ent0){
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(statoFatturaAttivaEntity.getData());
                    invoiceFlow.setNotifica("Fattura");
                    if("001".equalsIgnoreCase(statoFatturaAttivaEntity.getStato().getCodStato().getValue())){
                        invoiceFlow.setStato("RICEVUTA");
                    }
                    else{
                        invoiceFlow.setStato("INVIATA");
                    }
                    invoiceFlowList.add(invoiceFlow);
                }
            }

            List<FatturaAttivaNotificaEsitoEntity> ent = invoicesManager.getListFatturaAttivaNotificaEsitoFromIdFatturaAttiva(idFatturaAttiva);
            if(ent != null) {
                for(FatturaAttivaNotificaEsitoEntity fatturaAttivaNotificaEsitoEntity : ent) {
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(fatturaAttivaNotificaEsitoEntity.getData());
                    StatoAttivaNotificaEsitoEntity notEs = invoicesManager.getStatoAttivaNotificaEsitoEntityFromIdNotificaEsito(fatturaAttivaNotificaEsitoEntity);
                    invoiceFlow.setNotifica("NotificaEsito");
                    if (notEs != null) {
                        cod = notEs.getStato().getCodStato().getValue();
                        desc = descNotifica(cod);
                        stato = cod +"-"+ desc;
                        invoiceFlow.setStato(stato);
                        invoiceFlowList.add(invoiceFlow);
                    }
                }
            }
            //--------------------------------------
            List<FatturaAttivaNotificaDecorrenzaTerminiEntity> ent2 = invoicesManager.getListFatturaAttivaNotificaDecorrenzaTerminiFromIdFatturaAttiva(idFatturaAttiva);
            if(ent2 != null) {
                for(FatturaAttivaNotificaDecorrenzaTerminiEntity fatturaAttivaNotificaDecorrenzaTerminiEntity : ent2) {
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(fatturaAttivaNotificaDecorrenzaTerminiEntity.getData());
                    StatoAttivaNotificaDecorrenzaTerminiEntity notDec = invoicesManager.getStatoAttivaNotificaDecorrenzaTerminiEntityFromIdNotificaDecorrenza(fatturaAttivaNotificaDecorrenzaTerminiEntity);
                    invoiceFlow.setNotifica("NotificaDecorrenza");
                    if (notDec != null) {
                        cod = notDec.getStato().getCodStato().getValue();
                        desc = descNotifica(cod);
                        stato = cod +"-"+ desc;
                        invoiceFlow.setStato(stato);
                        invoiceFlowList.add(invoiceFlow);
                    }
                }
            }
            //---------------------------------------
            List<FatturaAttivaAttestazioneTrasmissioneFatturaEntity> ent3 = invoicesManager.getListFatturaAttivaAttestazioneTrasmissioneFromIdFatturaAttiva(idFatturaAttiva);
            if(ent3 != null) {
                for(FatturaAttivaAttestazioneTrasmissioneFatturaEntity fatturaAttivaAttestazioneTrasmissioneFatturaEntity : ent3) {
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(fatturaAttivaAttestazioneTrasmissioneFatturaEntity.getData());
                    StatoAttivaAttestazioneTrasmissioneFatturaEntity attTrasm = invoicesManager.getStatoAttivaAttestazioneTrasmissioneFatturaEntityFromIdNotificaTrasmissione(fatturaAttivaAttestazioneTrasmissioneFatturaEntity);
                    invoiceFlow.setNotifica("AttestazioneTrasmissione");
                    if (attTrasm != null) {
                        cod = attTrasm.getStato().getCodStato().getValue();
                        desc = descNotifica(cod);
                        stato = cod +"-"+ desc;
                        invoiceFlow.setStato(stato);
                        invoiceFlowList.add(invoiceFlow);
                    }
                }
            }
            //---------------------------------------
            List<FatturaAttivaNotificaMancataConsegnaEntity> ent4= invoicesManager.getListFatturaAttivaNotificaMancataConsegnaFromIdFatturaAttiva(idFatturaAttiva);
            if(ent4 != null) {
                for(FatturaAttivaNotificaMancataConsegnaEntity fatturaAttivaNotificaMancataConsegnaEntity : ent4) {
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(fatturaAttivaNotificaMancataConsegnaEntity.getData());
                    StatoAttivaNotificaMancataConsegnaEntity notManc = invoicesManager.getStatoAttivaNotificaMancataConsegnaEntityFromIdNotificaMancata(fatturaAttivaNotificaMancataConsegnaEntity);
                    invoiceFlow.setNotifica("NotificaMancataConsegna");
                    if (notManc != null) {
                        cod = notManc.getStato().getCodStato().getValue();
                        desc = descNotifica(cod);
                        stato = cod +"-"+ desc;
                        invoiceFlow.setStato(stato);
                        invoiceFlowList.add(invoiceFlow);
                    }
                }
            }
            //---------------------------------------
            List<FatturaAttivaNotificaScartoEntity> ent5 = invoicesManager.getListFatturaAttivaNotificaScartoFromIdFatturaAttiva(idFatturaAttiva);
            if(ent5 != null) {
                for(FatturaAttivaNotificaScartoEntity fatturaAttivaNotificaScartoEntity : ent5) {
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(fatturaAttivaNotificaScartoEntity.getData());
                    StatoAttivaNotificaScartoEntity notScar = invoicesManager.getStatoAttivaNotificaScartoEntityFromIdNotificaScarto(fatturaAttivaNotificaScartoEntity);
                    invoiceFlow.setNotifica("NotificaScarto");
                    if (notScar != null) {
                        cod = notScar.getStato().getCodStato().getValue();
                        desc = descNotifica(cod);
                        stato = cod +"-"+ desc;
                        invoiceFlow.setStato(stato);
                        invoiceFlowList.add(invoiceFlow);
                    }
                }
            }
            //---------------------------------------
            List<FatturaAttivaRicevutaConsegnaEntity> ent6 = invoicesManager.getListFatturaAttivaRicevutaConsegnaFromIdFatturaAttiva(idFatturaAttiva);
            if(ent6 != null) {
                for(FatturaAttivaRicevutaConsegnaEntity fatturaAttivaRicevutaConsegnaEntity : ent6) {
                    invoiceFlow = new InvoiceFlow();
                    invoiceFlow.setData(fatturaAttivaRicevutaConsegnaEntity.getData());
                    StatoAttivaRicevutaConsegnaEntity ricCon = invoicesManager.getStatoAttivaRicevutaConsegnaFromIdRicevutaConsegna(fatturaAttivaRicevutaConsegnaEntity);
                    invoiceFlow.setNotifica("RicevutaConsegna");
                    if (ricCon != null) {
                        cod = ricCon.getStato().getCodStato().getValue();
                        desc = descNotifica(cod);
                        stato = cod +"-"+ desc;
                        invoiceFlow.setStato(stato);
                        invoiceFlowList.add(invoiceFlow);
                    }
                }
            }

            quickSort(invoiceFlowList, 0, invoiceFlowList.size()-1);

            invoiceACDetail.setInvoiceFlow(invoiceFlowList);

            return invoiceACDetail;

        } catch (FatturaPAException
                | FatturaPATokenNonValidoException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAFatturaNonTrovataException
                | FatturaPaPersistenceException
                | FatturaPAEnteNonTrovatoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }

    }

    public FileFattura getFileFatturaPassiva(Exchange exchange) throws FatturaPAFatturaNonTrovataException, FatturaPATokenNonValidoException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPAUtenteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException, FatturaPaPersistenceException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getFileFatturaPassiva **********");

        Integer identificativoSdi = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);
        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(BigInteger.valueOf(identificativoSdi));
            String codiceDestinatario = datiFatturaEntityList.get(0).getCodiceDestinatario();
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
            utenteEnteEntity.setEnte(enteManager.getEnteByCodiceUfficio(codiceDestinatario));
            if(!utentiManager.checkAuth(utenteEnteEntity)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            FileFatturaEntity fileFatturaEntity = fileFatturaManager.getFileFatturaByIdentificativoSdi(BigInteger.valueOf(identificativoSdi));
            byte[] contFile = fileFatturaEntity.getContenutoFile();
            FileFattura fileFattura = new FileFattura();
            fileFattura.setFattura(contFile);
            fileFattura.setNome(fileFatturaEntity.getNomeFileFattura());
            return fileFattura;
        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPAException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPaPersistenceException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    public FileFattura getFileFatturaAttiva(Exchange exchange) throws FatturaPAFatturaNonTrovataException, FatturaPAException, FatturaPaPersistenceException, FatturaPATokenNonValidoException, FatturaPAUtenteNonTrovatoException, FatturaPAEnteNonTrovatoException, FatturaPAUtenteNonAutorizzatoException {

        MessageContentsList parameters  = (MessageContentsList) exchange.getIn().getBody();

        LOG.info("********** getFileFatturaAttiva **********");

        Integer identificativoSdi = (Integer) parameters.get(0);
        String token = (String) parameters.get(1);
        try {
            String username = Autenticazione.checkToken(token, utentiManager, durataToken);
            FatturaAttivaEntity fatturaAttivaEntity = fatturaAttivaManager.getFatturaAttivaFromIdentificativSdi(BigInteger.valueOf(identificativoSdi));
            UtenteEnteEntity utenteEnteEntity = new UtenteEnteEntity();
            utenteEnteEntity.setEnte(fatturaAttivaEntity.getEnte());
            utenteEnteEntity.setUtente(utentiManager.getUtenteByUsername(username));
            if(!utentiManager.checkAuth(utenteEnteEntity)){
                throw new FatturaPAUtenteNonAutorizzatoException();
            }
            byte[] contFile = fatturaAttivaEntity.getFileFatturaOriginale();
            FileFattura fileFattura = new FileFattura();
            fileFattura.setFattura(contFile);
            fileFattura.setNome(fatturaAttivaEntity.getNomeFile());

            return fileFattura;
        } catch (FatturaPAFatturaNonTrovataException
                | FatturaPAException
                | FatturaPaPersistenceException
                | FatturaPAUtenteNonTrovatoException
                | FatturaPAUtenteNonAutorizzatoException
                | FatturaPAEnteNonTrovatoException
                | FatturaPATokenNonValidoException e) {
            LOG.error(e.getStackTrace().toString());
            throw e;
        }
    }

    private String descNotifica(String codStato){
        return codStato.equalsIgnoreCase("001") ? "RICEVUTA" :  "INVIATA";
    }

    private void quickSort(List<InvoiceFlow> list, int begin, int end){
        if(begin < end){
            int partitionIndex = partition(list, begin, end);

            quickSort(list, begin, partitionIndex - 1);
            quickSort(list, partitionIndex+1, end);
        }
    }

    private int partition(List<InvoiceFlow> list, int begin, int end){
        InvoiceFlow pivot = list.get(end);
        int i = begin -1;

        for(int j=begin; j<end; j++){
            if(list.get(j).getData().before(pivot.getData())){
                i++;
                InvoiceFlow swapTemp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, swapTemp);
            }
        }

        InvoiceFlow swapTemp = list.get(i+1);
        list.set(i+1, list.get(end));
        list.set(end, swapTemp);

        return i+1;
    }

    public DatiFatturaManager getDatiFatturaManager () {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager (DatiFatturaManager datiFatturaManager) {
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

    public EnteDao getEnteDao() {
        return enteDao;
    }

    public void setEnteDao(EnteDao enteDao) {
        this.enteDao = enteDao;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public FatturaAttivaManagerImpl getFatturaAttivaManager() {
        return fatturaAttivaManager;
    }

    public void setFatturaAttivaManager(FatturaAttivaManagerImpl fatturaAttivaManager) {
        this.fatturaAttivaManager = fatturaAttivaManager;
    }

    public InvoicesManager getInvoicesManager() {
        return invoicesManager;
    }

    public void setInvoicesManager(InvoicesManager invoicesManager) {
        this.invoicesManager = invoicesManager;
    }

    public FileFatturaManager getFileFatturaManager() {
        return fileFatturaManager;
    }

    public void setFileFatturaManager(FileFatturaManager fileFatturaManager) {
        this.fileFatturaManager = fileFatturaManager;
    }
}
