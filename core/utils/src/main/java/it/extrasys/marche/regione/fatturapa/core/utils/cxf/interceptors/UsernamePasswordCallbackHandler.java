package it.extrasys.marche.regione.fatturapa.core.utils.cxf.interceptors;

//vedi https://developer.jboss.org/thread/269751
//import org.apache.ws.security.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 26/02/15.
 */
public class UsernamePasswordCallbackHandler implements CallbackHandler {
    
    String username;
    String password;
    
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback c = (WSPasswordCallback) callbacks[0];
        c.setIdentifier(username);
        c.setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
