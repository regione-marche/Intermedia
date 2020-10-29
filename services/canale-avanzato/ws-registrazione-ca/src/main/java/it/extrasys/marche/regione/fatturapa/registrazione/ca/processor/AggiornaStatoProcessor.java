package it.extrasys.marche.regione.fatturapa.registrazione.ca.processor;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggiornaStatoProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String INVIO_UNICO_HEADER = "invioUnico";

    private DatiFatturaManager datiFatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = message.getHeader(IDENTIFICATIVO_SDI_HEADER, String.class);

        String tipoMessaggio = message.getHeader(TIPO_MESSAGGIO_HEADER, String.class);

        Boolean invioUnico = message.getHeader(INVIO_UNICO_HEADER, Boolean.class);

        String codiceStatoFattura = "";

        switch (tipoMessaggio){

            case "FatturaElettronica":

                if(invioUnico != null) {
                    codiceStatoFattura = invioUnico ? CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_INVIO_UNICO.getValue() : CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_REGISTRATA.getValue();
                }else{
                    codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_REGISTRATA.getValue();
                }

                break;

            case "NotificaScartoEsito":

                if(invioUnico != null) {
                    codiceStatoFattura = invioUnico ? CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_INVIO_UNICO.getValue() : CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_REGISTRATA.getValue();
                }else {
                    codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_REGISTRATA.getValue();
                }

                break;

            case "NotificaDecorrenzaTermini":

                if(invioUnico != null) {
                    codiceStatoFattura = invioUnico ? CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_INVIO_UNICO.getValue() : CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_REGISTRATA.getValue();
                }else {
                    codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_REGISTRATA.getValue();
                }

                break;
        }

        datiFatturaManager.registrazioneFatturaNotificaScarto(identificativoSdI, codiceStatoFattura);
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }
}