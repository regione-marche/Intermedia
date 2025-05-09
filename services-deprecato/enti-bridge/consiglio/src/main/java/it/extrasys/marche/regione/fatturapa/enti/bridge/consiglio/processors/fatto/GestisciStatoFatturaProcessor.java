package it.extrasys.marche.regione.fatturapa.enti.bridge.consiglio.processors.fatto;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.math.BigInteger;
import java.util.List;

public class GestisciStatoFatturaProcessor implements Processor {

    private DatiFatturaManager datiFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        BigInteger identificativoSdI = new BigInteger((String) exchange.getIn().getHeader("identificativoSdI"));

        List<DatiFatturaEntity> fatture = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdI);

        exchange.getIn().setBody(fatture);

    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }


}
