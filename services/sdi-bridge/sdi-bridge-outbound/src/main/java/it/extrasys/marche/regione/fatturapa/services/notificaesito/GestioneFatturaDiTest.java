package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.math.BigInteger;
import java.util.List;

public class GestioneFatturaDiTest implements Processor {

    private DatiFatturaManager datiFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {
        String identificativoSdi = (String) exchange.getIn().getHeader("identificativoSdI");

        if (identificativoSdi != null) {
            List<DatiFatturaEntity> fattura = datiFatturaManager.getFatturaByIdentificativoSDI(new BigInteger(identificativoSdi));

            for (DatiFatturaEntity dfe : fattura) {
                if (dfe.getFatturazioneTest()) {
                    exchange.getIn().setHeader("fatturazioneTest", Boolean.TRUE);
                    break;
                } else {
                    exchange.getIn().setHeader("fatturazioneTest", Boolean.FALSE);
                }
            }
        } else {
            exchange.getIn().setHeader("fatturazioneTest", Boolean.FALSE);
        }
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}
