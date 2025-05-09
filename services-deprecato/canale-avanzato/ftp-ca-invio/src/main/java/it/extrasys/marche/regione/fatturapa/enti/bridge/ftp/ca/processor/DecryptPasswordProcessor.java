package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.ChiaveManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DecryptPasswordProcessor implements Processor {
    private ChiaveManager chiaveManager;

    @Override
    public void process(Exchange exchange) throws Exception {
        String password = (String)exchange.getIn().getHeader("passwordEncrypted");

        //Decifra la password
        String passwordDecrypted = CommonUtils.decryptPassword(password, chiaveManager.getChiave());

        exchange.getIn().setHeader("password", passwordDecrypted);
    }

    public ChiaveManager getChiaveManager() {
        return chiaveManager;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }
}
