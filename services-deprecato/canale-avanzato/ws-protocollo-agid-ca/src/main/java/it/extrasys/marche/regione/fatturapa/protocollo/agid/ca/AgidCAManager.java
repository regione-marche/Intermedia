package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.*;
import it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.utils.AgidConstant;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_BASIC;

public class AgidCAManager {
    private static final Logger LOG = LoggerFactory.getLogger(AgidCAManager.class);

    private EnteManager enteManager;
    private NotificaFromSdiManager notificaFromSdiManager;
    private DatiFatturaManager datiFatturaManager;
    private FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager;
    private ChiaveManager chiaveManager;

    public void setInfoEnteAgid(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String codiceUfficio = (String) exchange.getIn().getHeader(AgidConstant.NOME_ENTE_HEADER);
        EnteEntity enteEntity = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        if (enteEntity == null) {
            throw new FatturaPAEnteNonTrovatoException();
        } else {
            exchange.getIn().setHeader(AgidConstant.ADDRESS_HEADER, enteEntity.getEndpointProtocolloCa().getEndpoint());
            exchange.getIn().setHeader(AgidConstant.WSDL_URL_HEADER, enteEntity.getEndpointProtocolloCa().getEndpoint());
            exchange.getIn().setHeader(AgidConstant.NOME_ENTE_DESTINAZIONE, enteEntity.getNome());

            String passwordDecrypted = CommonUtils.decryptPassword(enteEntity.getEndpointRegistrazioneCa().getPassword(), chiaveManager.getChiave());

            String authorization = AUTH_TYPE_BASIC + " " +
                    org.apache.cxf.common.util.Base64Utility.encode((enteEntity.getEndpointRegistrazioneCa().getUsername() + ":" + passwordDecrypted).getBytes());

            exchange.getIn().setHeader(AgidConstant.AUTHORIZATION, authorization);
        }

        exchange.getIn().setBody(enteEntity);
    }


    public void setEnteHeaderInfo(Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        if (msg.getBody() instanceof FatturaElettronicaWrapper) {

            FatturaElettronicaWrapper fatturaElettronicaWrapper = msg.getBody(FatturaElettronicaWrapper.class);

            msg.setHeader(AgidConstant.NOME_ENTE_HEADER, fatturaElettronicaWrapper.getEnteEntity().getCodiceUfficio());
            msg.setHeader(AgidConstant.ADDRESS_HEADER, fatturaElettronicaWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.WSDL_URL_HEADER, fatturaElettronicaWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());

        } else if (msg.getBody() instanceof NotificaDecorrenzaTerminiWrapper) {

            NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = msg.getBody(NotificaDecorrenzaTerminiWrapper.class);

            msg.setHeader(AgidConstant.NOME_ENTE_HEADER, notificaDecorrenzaTerminiWrapper.getEnteEntity().getCodiceUfficio());
            msg.setHeader(AgidConstant.ADDRESS_HEADER, notificaDecorrenzaTerminiWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.WSDL_URL_HEADER, notificaDecorrenzaTerminiWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.NOME_ENTE_DESTINAZIONE, notificaDecorrenzaTerminiWrapper.getEnteEntity().getNome());

        } else if (msg.getBody() instanceof NotificaEsitoCommittenteWrapper) {

            NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = msg.getBody(NotificaEsitoCommittenteWrapper.class);

            msg.setHeader(AgidConstant.NOME_ENTE_HEADER, notificaEsitoCommittenteWrapper.getEnteEntity().getCodiceUfficio());
            msg.setHeader(AgidConstant.ADDRESS_HEADER, notificaEsitoCommittenteWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.WSDL_URL_HEADER, notificaEsitoCommittenteWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.NOME_ENTE_DESTINAZIONE, notificaEsitoCommittenteWrapper.getEnteEntity().getNome());


        } else if (msg.getBody() instanceof NotificaScartoEsitoCommittenteWrapper) {

            NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = msg.getBody(NotificaScartoEsitoCommittenteWrapper.class);

            msg.setHeader(AgidConstant.NOME_ENTE_HEADER, notificaScartoEsitoCommittenteWrapper.getEnteEntity().getCodiceUfficio());
            msg.setHeader(AgidConstant.ADDRESS_HEADER, notificaScartoEsitoCommittenteWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.WSDL_URL_HEADER, notificaScartoEsitoCommittenteWrapper.getEnteEntity().getEndpointProtocolloCa().getEndpoint());
            msg.setHeader(AgidConstant.NOME_ENTE_DESTINAZIONE, notificaScartoEsitoCommittenteWrapper.getEnteEntity().getNome());

        }
    }


    public void protocollaEsitoCommittente(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = msgExchange.getIn().getBody(NotificaEsitoCommittenteWrapper.class);

        LOG.info("Agid CA: Esito Committente - Salvo Numero Protocollo " + notificaEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());

        notificaFromSdiManager.protocollaNotificaECFromSdI(new BigInteger(identificativoSdI), notificaEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());
    }

    public void protocollaScartoEsitoCommittente(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = msgExchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

        LOG.info("Agid CA: Scarto Notifica Esito Committente - Salvo Numero Protocollo " + notificaScartoEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());

        notificaFromSdiManager.protocollaNotificaScartoECFromSdI(new BigInteger(identificativoSdI), notificaScartoEsitoCommittenteWrapper.getSegnaturaProtocolloNotifica());
    }


    public void setNomeEnteFromIdSDI(Exchange msgExchange) throws Exception {

        Message msg = msgExchange.getIn();

        String idSDI = (String) msg.getHeader(AgidConstant.ID_SDI_HEADER);

        List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(idSDI));

        if (datiFatturaEntityList == null || datiFatturaEntityList.isEmpty()) {

            throw new FatturaPAFatturaNonTrovataException();

        } else {

            DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);
            msg.setHeader(AgidConstant.NOME_ENTE_HEADER, datiFatturaEntity.getCodiceDestinatario());
        }
    }

    public void protocollaDecorrenzaTermini(Exchange msgExchange) throws Exception {

        String identificativoSdI = msgExchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = msgExchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        LOG.info("Agid CA: Decorrenza Termini - Salvo Numero Protocollo " + notificaDecorrenzaTerminiWrapper.getSegnaturaProtocolloNotifica());

        fatturazionePassivaNotificaDecorrenzaTerminiManager.protocollaNotificaDecorrenzaTermini(new BigInteger(identificativoSdI), notificaDecorrenzaTerminiWrapper.getSegnaturaProtocolloNotifica());
    }


    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public NotificaFromSdiManager getNotificaFromSdiManager() {
        return notificaFromSdiManager;
    }

    public void setNotificaFromSdiManager(NotificaFromSdiManager notificaFromSdiManager) {
        this.notificaFromSdiManager = notificaFromSdiManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getFatturazionePassivaNotificaDecorrenzaTerminiManager() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManager = fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public ChiaveManager getChiaveManager() {
        return chiaveManager;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }
}
