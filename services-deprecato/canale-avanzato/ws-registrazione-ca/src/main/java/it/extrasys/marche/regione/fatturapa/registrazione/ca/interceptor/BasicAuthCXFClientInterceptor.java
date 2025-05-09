package it.extrasys.marche.regione.fatturapa.registrazione.ca.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_BASIC;

public class BasicAuthCXFClientInterceptor extends AbstractPhaseInterceptor {

    // *** Nel caso in cui non dovesse funzionare la basic auth abilitare l'interceptor... Bisogna finirlo e testarlo

    private static final String USERNAME_HEADER = "username";
    private static final String PASSWORD_HEADER = "password";

    public BasicAuthCXFClientInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        Map<String, List<?>> headers = (Map<String, List<?>>) message.get(Message.PROTOCOL_HEADERS);

        String username = "";
        String password = "";

        String authorizationHeader = AUTH_TYPE_BASIC + " " +
                org.apache.cxf.common.util.Base64Utility.encode((username+":"+password).getBytes());

        headers.put(AUTHORIZATION, Collections.singletonList(authorizationHeader));
    }
}