package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 17/03/15.
 */
public class AggiornaStatoFattureAProtocollataProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoFattureAProtocollataProcessor.class);

    private static final String CANALE_AVANZATO_HEADER = "canaleAvanzato";
    private static final String INFO_TIPO_INVIO_FATTURA_CA_HEADER = "infoTipoInvioFatturaCA";

    private DatiFatturaManager fatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI",String.class);

        FatturaElettronicaWrapper fatturaElettronicaWrapper = exchange.getIn().getBody(FatturaElettronicaWrapper.class);

        LOG.info("AggiornaStatoFattureAProtocollataProcessor: Protocollo " + fatturaElettronicaWrapper.getSegnaturaProtocollo());

        String codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PROTOCOLLATA.getValue();

        String canaleAvanzato = message.getHeader(CANALE_AVANZATO_HEADER, String.class);
        String infoTipoInvioFatturaCA = message.getHeader(INFO_TIPO_INVIO_FATTURA_CA_HEADER, String.class);

        if(canaleAvanzato != null && !"".equals(canaleAvanzato)){

            if(infoTipoInvioFatturaCA.equals("InvioSingolo")){
                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_INVIO_UNICO.getValue();
            }else{
                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_PROTOCOLLATA.getValue();
            }
        }

        fatturaManager.protocollaFattura(identificativoSdI, fatturaElettronicaWrapper.getSegnaturaProtocollo(), codiceStatoFattura);
    }

	public DatiFatturaManager getFatturaManager() {
		return fatturaManager;
	}

	public void setFatturaManager(DatiFatturaManager fatturaManager) {
		this.fatturaManager = fatturaManager;
	}

}
