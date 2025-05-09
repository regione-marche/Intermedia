package it.extrasys.marche.regione.fatturapa.protocollo.paleo.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.*;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura.CreaRichiestaGetDocumentiProtocolliInFascicoloProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura.CreaRichiestaProtocollazioneEntrataProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini.CreaRichiestaCercaDocumentoProtocolloProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini.CreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.esito.committente.CreaRichiestaProtocollazioneEsitoCommittenteProcessor;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.scarto.esito.committente.CreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntePaleoCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

public class PaleoCAManager {

    private static final Logger LOG = LoggerFactory.getLogger(PaleoCAManager.class);

    //HEADERS
    private static final String ID_SDI_HEADER = "idSDI";
    private static final String NOME_ENTE_HEADER = "nomeEnte";
    private static final String ADDRESS_HEADER = "address";
    private static final String WSDL_URL_HEADER = "wsdlURL";
    private static final String ENTE_PALEO_CA_HEADER = "entePaleoCA";

    //MANAGER
    private EnteManager enteManager;
    private DatiFatturaManager datiFatturaManager;
    private FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager;
    private NotificaFromEntiManager notificaFromEntiManager;
    private NotificaFromSdiManager notificaFromSdiManager;

    //PROCESSOR
    private CreaRichiestaGetDocumentiProtocolliInFascicoloProcessor creaRichiestaGetDocumentiProtocolliInFascicoloProcessor;
    private CreaRichiestaProtocollazioneEntrataProcessor creaRichiestaProtocollazioneEntrataProcessor;
    private CreaRichiestaCercaDocumentoProtocolloProcessor creaRichiestaCercaDocumentoProtocolloProcessor;
    private CreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor;
    private CreaRichiestaProtocollazioneEsitoCommittenteProcessor creaRichiestaProtocollazioneEsitoCommittenteProcessor;
    private CreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor;

    public void setCxfConfigHeaders (Exchange msgExchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {

        Message msg = msgExchange.getIn();

        String codiceUfficio = (String) msg.getHeader(NOME_ENTE_HEADER);

        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        if(enteEntity == null){
            throw new FatturaPAEnteNonTrovatoException();
        }else{

            msg.setHeader(ADDRESS_HEADER, enteEntity.getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(WSDL_URL_HEADER, enteEntity.getEndpointProtocolloCa().getEndpoint() + "?wsdl");
            msg.setHeader(ENTE_PALEO_CA_HEADER, enteEntity.getEntePaleoCaEntity());
        }
    }

    public void setEntePaleoCA(Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCA entePaleoCA = new EntePaleoCA();

        String nomeEnte = (String) msg.getHeader(NOME_ENTE_HEADER);
        String address = (String) msg.getHeader(ADDRESS_HEADER);
        String wsdlURL = (String) msg.getHeader(WSDL_URL_HEADER);
        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        entePaleoCA.setNomeEnte(nomeEnte);
        entePaleoCA.setAddress(address);
        entePaleoCA.setWsdlURL(wsdlURL);
        entePaleoCA.setEntePaleoCaEntity(entePaleoCaEntity);

        msgExchange.getIn().setBody(entePaleoCA);
    }


    public void setEnteHeaderInfo(Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        if(msg.getBody() instanceof FatturaElettronicaWrapper) {

            FatturaElettronicaWrapper fatturaElettronicaWrapper = msg.getBody(FatturaElettronicaWrapper.class);

            msg.setHeader(NOME_ENTE_HEADER, fatturaElettronicaWrapper.getEntePaleoCA().getNomeEnte());
            msg.setHeader(ADDRESS_HEADER, fatturaElettronicaWrapper.getEntePaleoCA().getAddress());
            msg.setHeader(WSDL_URL_HEADER, fatturaElettronicaWrapper.getEntePaleoCA().getAddress() + "?wsdl");
            msg.setHeader(ENTE_PALEO_CA_HEADER, fatturaElettronicaWrapper.getEntePaleoCA().getEntePaleoCaEntity());

        }else if(msg.getBody() instanceof NotificaDecorrenzaTerminiWrapper){

            NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = msg.getBody(NotificaDecorrenzaTerminiWrapper.class);

            msg.setHeader(NOME_ENTE_HEADER, notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getNomeEnte());
            msg.setHeader(ADDRESS_HEADER, notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getAddress());
            msg.setHeader(WSDL_URL_HEADER, notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getAddress() + "?wsdl");
            msg.setHeader(ENTE_PALEO_CA_HEADER, notificaDecorrenzaTerminiWrapper.getEntePaleoCA().getEntePaleoCaEntity());

        }else if(msg.getBody() instanceof NotificaEsitoCommittenteWrapper){

            NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = msg.getBody(NotificaEsitoCommittenteWrapper.class);

            msg.setHeader(NOME_ENTE_HEADER, notificaEsitoCommittenteWrapper.getEntePaleoCA().getNomeEnte());
            msg.setHeader(ADDRESS_HEADER, notificaEsitoCommittenteWrapper.getEntePaleoCA().getAddress());
            msg.setHeader(WSDL_URL_HEADER, notificaEsitoCommittenteWrapper.getEntePaleoCA().getAddress() + "?wsdl");
            msg.setHeader(ENTE_PALEO_CA_HEADER, notificaEsitoCommittenteWrapper.getEntePaleoCA().getEntePaleoCaEntity());

        }else if(msg.getBody() instanceof NotificaScartoEsitoCommittenteWrapper){

            NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = msg.getBody(NotificaScartoEsitoCommittenteWrapper.class);

            msg.setHeader(NOME_ENTE_HEADER, notificaScartoEsitoCommittenteWrapper.getEntePaleoCA().getNomeEnte());
            msg.setHeader(ADDRESS_HEADER, notificaScartoEsitoCommittenteWrapper.getEntePaleoCA().getAddress());
            msg.setHeader(WSDL_URL_HEADER, notificaScartoEsitoCommittenteWrapper.getEntePaleoCA().getAddress() + "?wsdl");
            msg.setHeader(ENTE_PALEO_CA_HEADER, notificaScartoEsitoCommittenteWrapper.getEntePaleoCA().getEntePaleoCaEntity());
        }
    }

    public void setParamCreaRichiestaGetDocumentiProtocolliInFascicoloProcessor (Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        creaRichiestaGetDocumentiProtocolliInFascicoloProcessor.setCodiceUO(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaGetDocumentiProtocolliInFascicoloProcessor.setCognome(entePaleoCaEntity.getProtocollistaCognome());
        creaRichiestaGetDocumentiProtocolliInFascicoloProcessor.setNome(entePaleoCaEntity.getProtocollistaNome());
        creaRichiestaGetDocumentiProtocolliInFascicoloProcessor.setRuolo(entePaleoCaEntity.getProtocollistaRuolo());
        creaRichiestaGetDocumentiProtocolliInFascicoloProcessor.setUo(entePaleoCaEntity.getProtocollistaUo());
    }

    public void setParamCreaRichiestaProtocollazioneEntrataProcessor (Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        creaRichiestaProtocollazioneEntrataProcessor.setCodiceUO(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneEntrataProcessor.setCognome(entePaleoCaEntity.getProtocollistaCognome());
        creaRichiestaProtocollazioneEntrataProcessor.setNome(entePaleoCaEntity.getProtocollistaNome());
        creaRichiestaProtocollazioneEntrataProcessor.setRuolo(entePaleoCaEntity.getProtocollistaRuolo());
        creaRichiestaProtocollazioneEntrataProcessor.setUo(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneEntrataProcessor.setCodiceRegistro(entePaleoCaEntity.getCodiceRegistro());
    }

    public void setParamCreaRichiestaCercaDocumentoProtocolloProcessor (Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        creaRichiestaCercaDocumentoProtocolloProcessor.setCodiceUO(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaCercaDocumentoProtocolloProcessor.setCognome(entePaleoCaEntity.getProtocollistaCognome());
        creaRichiestaCercaDocumentoProtocolloProcessor.setNome(entePaleoCaEntity.getProtocollistaNome());
        creaRichiestaCercaDocumentoProtocolloProcessor.setRuolo(entePaleoCaEntity.getProtocollistaRuolo());
        creaRichiestaCercaDocumentoProtocolloProcessor.setUo(entePaleoCaEntity.getProtocollistaUo());
    }

    public void setParamCreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor (Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setCodiceUO(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setCognome(entePaleoCaEntity.getProtocollistaCognome());
        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setNome(entePaleoCaEntity.getProtocollistaNome());
        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setRuolo(entePaleoCaEntity.getProtocollistaRuolo());
        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setUo(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setCodiceRegistro(entePaleoCaEntity.getCodiceRegistro());
        //creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setCodiceUOOperatoriTrasmissione(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor.setCodiceUOOperatoriTrasmissione(entePaleoCaEntity.getResponsabileProcedimentoUo());
    }

    public void setParamCreaRichiestaProtocollazioneNotificaEsitoCommittenteProcessor (Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setCodiceUO(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setCognome(entePaleoCaEntity.getProtocollistaCognome());
        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setNome(entePaleoCaEntity.getProtocollistaNome());
        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setRuolo(entePaleoCaEntity.getProtocollistaRuolo());
        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setUo(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setCodiceRegistro(entePaleoCaEntity.getCodiceRegistro());
        //creaRichiestaProtocollazioneEsitoCommittenteProcessor.setCodiceUOOperatoriTrasmissione(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneEsitoCommittenteProcessor.setCodiceUOOperatoriTrasmissione(entePaleoCaEntity.getResponsabileProcedimentoUo());
    }

    public void setParamCreaRichiestaProtocollazioneNotificaScartoEsitoCommittenteProcessor (Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        EntePaleoCaEntity entePaleoCaEntity = (EntePaleoCaEntity) msg.getHeader(ENTE_PALEO_CA_HEADER);

        creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor.setCodiceUO(entePaleoCaEntity.getProtocollistaUo());
        creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor.setCognome(entePaleoCaEntity.getProtocollistaCognome());
        creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor.setNome(entePaleoCaEntity.getProtocollistaNome());
        creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor.setRuolo(entePaleoCaEntity.getProtocollistaRuolo());
        creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor.setUo(entePaleoCaEntity.getProtocollistaUo());
    }

    public void protocollaDecorrenzaTermini(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = msgExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        LOG.info("Paleo CA: Decorrenza Termini - Salvo Numero Protocollo " + notificaDecorrenzaTerminiWrapper.getSegnaturaProtocolloNotifica());

        fatturazionePassivaNotificaDecorrenzaTerminiManager.protocollaNotificaDecorrenzaTermini(new BigInteger(identificativoSdI), notificaDecorrenzaTerminiWrapper.getSegnaturaProtocolloNotifica());
    }

    /*
    public void protocollaEsitoCommittente(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = msgExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        LOG.info("Paleo CA: Esito Committente - Salvo Numero Protocollo " + notificaEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());

        notificaFromSdiManager.protocollaNotificaECFromSdI(new BigInteger(identificativoSdI), notificaEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());
    }
    */

    public void protocollaEsitoCommittente(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = msgExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        LOG.info("Paleo CA: Esito Committente - Salvo Numero Protocollo " + notificaEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());

        notificaFromEntiManager.protocollaNotificaEsitoCommittente(new BigInteger(identificativoSdI), notificaEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());
    }

    public void protocollaScartoEsitoCommittente(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = msgExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        LOG.info("Paleo CA: Scarto Notifica Esito Committente - Salvo Numero Protocollo " + notificaScartoEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());

        notificaFromSdiManager.protocollaNotificaScartoECFromSdI(new BigInteger(identificativoSdI), notificaScartoEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());
    }

    public void setNomeEnteFromIdSDI(Exchange msgExchange) throws Exception {

        Message msg = msgExchange.getIn();

        String idSDI = (String) msg.getHeader(ID_SDI_HEADER);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(idSDI));

        if(datiFatturaEntityList == null || datiFatturaEntityList.isEmpty()){

            throw new FatturaPAFatturaNonTrovataException();

        }else{

            DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);
            msg.setHeader(NOME_ENTE_HEADER, datiFatturaEntity.getCodiceDestinatario());
        }
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public CreaRichiestaGetDocumentiProtocolliInFascicoloProcessor getCreaRichiestaGetDocumentiProtocolliInFascicoloProcessor() {
        return creaRichiestaGetDocumentiProtocolliInFascicoloProcessor;
    }

    public void setCreaRichiestaGetDocumentiProtocolliInFascicoloProcessor(CreaRichiestaGetDocumentiProtocolliInFascicoloProcessor creaRichiestaGetDocumentiProtocolliInFascicoloProcessor) {
        this.creaRichiestaGetDocumentiProtocolliInFascicoloProcessor = creaRichiestaGetDocumentiProtocolliInFascicoloProcessor;
    }

    public CreaRichiestaProtocollazioneEntrataProcessor getCreaRichiestaProtocollazioneEntrataProcessor() {
        return creaRichiestaProtocollazioneEntrataProcessor;
    }

    public void setCreaRichiestaProtocollazioneEntrataProcessor(CreaRichiestaProtocollazioneEntrataProcessor creaRichiestaProtocollazioneEntrataProcessor) {
        this.creaRichiestaProtocollazioneEntrataProcessor = creaRichiestaProtocollazioneEntrataProcessor;
    }

    public CreaRichiestaCercaDocumentoProtocolloProcessor getCreaRichiestaCercaDocumentoProtocolloProcessor() {
        return creaRichiestaCercaDocumentoProtocolloProcessor;
    }

    public void setCreaRichiestaCercaDocumentoProtocolloProcessor(CreaRichiestaCercaDocumentoProtocolloProcessor creaRichiestaCercaDocumentoProtocolloProcessor) {
        this.creaRichiestaCercaDocumentoProtocolloProcessor = creaRichiestaCercaDocumentoProtocolloProcessor;
    }

    public CreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor getCreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor() {
        return creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor;
    }

    public void setCreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor(CreaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor) {
        this.creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor = creaRichiestaProtocollazioneNotificaDecorrenzaTerminiProcessor;
    }

    public static String getAddressHeader() {
        return ADDRESS_HEADER;
    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getFatturazionePassivaNotificaDecorrenzaTerminiManager() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManager = fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public CreaRichiestaProtocollazioneEsitoCommittenteProcessor getCreaRichiestaProtocollazioneEsitoCommittenteProcessor() {
        return creaRichiestaProtocollazioneEsitoCommittenteProcessor;
    }

    public void setCreaRichiestaProtocollazioneEsitoCommittenteProcessor(CreaRichiestaProtocollazioneEsitoCommittenteProcessor creaRichiestaProtocollazioneEsitoCommittenteProcessor) {
        this.creaRichiestaProtocollazioneEsitoCommittenteProcessor = creaRichiestaProtocollazioneEsitoCommittenteProcessor;
    }

    public NotificaFromEntiManager getNotificaFromEntiManager() {
        return notificaFromEntiManager;
    }

    public void setNotificaFromEntiManager(NotificaFromEntiManager notificaFromEntiManager) {
        this.notificaFromEntiManager = notificaFromEntiManager;
    }

    public CreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor getCreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor() {
        return creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor;
    }

    public void setCreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor(CreaRichiestaProtocollazioneScartoEsitoCommittenteProcessor creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor) {
        this.creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor = creaRichiestaProtocollazioneScartoEsitoCommittenteProcessor;
    }

    public NotificaFromSdiManager getNotificaFromSdiManager() {
        return notificaFromSdiManager;
    }

    public void setNotificaFromSdiManager(NotificaFromSdiManager notificaFromSdiManager) {
        this.notificaFromSdiManager = notificaFromSdiManager;
    }
}