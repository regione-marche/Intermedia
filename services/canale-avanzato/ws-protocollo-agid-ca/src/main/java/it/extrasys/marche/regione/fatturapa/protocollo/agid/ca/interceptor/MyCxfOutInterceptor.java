package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.interceptor;

import it.extrasys.marche.regione.fatturapa.core.utils.cxf.interceptors.UsernamePasswordCallbackHandler;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EntePaleoCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.CallbackHandler;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MyCxfOutInterceptor extends WSS4JOutInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MyCxfOutInterceptor.class);

    //HEADERS
    private static final String NOME_ENTE_HEADER = "nomeEnte";
    private static final String ADDRESS_HEADER = "address";
    private static final String WSDL_URL_HEADER = "wsdlURL";


    private EnteManager enteManager;

    private String nomeEnte;

    private EntePaleoCaEntity entePaleoCA;

    public MyCxfOutInterceptor() {

    }

    public MyCxfOutInterceptor(Map<String, Object> props) {
        super(props);
    }

    @Override
    protected Crypto loadCryptoFromPropertiesFile(String propFilename, RequestData reqData) throws WSSecurityException {
        return super.loadCryptoFromPropertiesFile(propFilename, reqData);
    }

    @Override
    public void handleMessage(SoapMessage mc) throws Fault {

        TreeMap treeMap = (TreeMap) mc.getExchange().get(org.apache.cxf.message.Message.PROTOCOL_HEADERS);
        nomeEnte = (String) ((ArrayList) treeMap.get(NOME_ENTE_HEADER)).get(0);

        treeMap.remove(ADDRESS_HEADER);
        treeMap.remove(WSDL_URL_HEADER);
        treeMap.remove(NOME_ENTE_HEADER);

        super.handleMessage(mc);
    }

    @Override
    public CallbackHandler getPasswordCallbackHandler(RequestData reqData) throws WSSecurityException {

        EnteEntity enteEntity = null;

        try {
            enteEntity = enteManager.getEnteByCodiceUfficio(nomeEnte);
        } catch (Exception e) {
           LOG.error("Impossibile trovare l'ente: " + nomeEnte + " - Msg: " + e.getMessage());
        }

        UsernamePasswordCallbackHandler usernamePasswordCallbackHandler = (UsernamePasswordCallbackHandler) super.getProperties().get("passwordCallbackRef");
        //todo:
        usernamePasswordCallbackHandler.setUsername("username");
        usernamePasswordCallbackHandler.setPassword("password");

        return super.getPasswordCallbackHandler(reqData);
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public String getNomeEnte() {
        return nomeEnte;
    }

    public void setNomeEnte(String nomeEnte) {
        this.nomeEnte = nomeEnte;
    }

    public EntePaleoCaEntity getEntePaleoCA() {
        return entePaleoCA;
    }

    public void setEntePaleoCA(EntePaleoCaEntity entePaleoCA) {
        this.entePaleoCA = entePaleoCA;
    }
}