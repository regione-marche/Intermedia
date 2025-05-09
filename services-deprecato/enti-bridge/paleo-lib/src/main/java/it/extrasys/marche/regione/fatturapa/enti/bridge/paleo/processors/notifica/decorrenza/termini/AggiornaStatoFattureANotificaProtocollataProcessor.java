package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.notifica.decorrenza.termini;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggiornaStatoFattureANotificaProtocollataProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornaStatoFattureANotificaProtocollataProcessor.class);

    private static final String CANALE_AVANZATO_HEADER = "canaleAvanzato";
    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String INFO_TIPO_INVIO_FATTURA_CA_HEADER = "infoTipoInvioFatturaCA";

    private static final String MESSAGGIO_NOTIFICA_DECORRENZA = "NotificaDecorrenzaTermini";
    private static final String MESSAGGIO_ESITO_COMMITENTE = "NotificaEsitoCommittente";
    private static final String MESSAGGIO_SCARTO_ESITO_COMMITENTE = "NotificaScartoEsito";

    private DatiFatturaManager fatturaManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);

        NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = exchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

        LOG.info("AggiornaStatoFattureANotificaProtocollataProcessor: Protocollo " + notificaDecorrenzaTerminiWrapper.getSegnaturaProtocolloNotifica());

        String codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.PROTOCOLLATA.getValue();

        String canaleAvanzato = message.getHeader(CANALE_AVANZATO_HEADER, String.class);
        String tipoMessaggio = message.getHeader(TIPO_MESSAGGIO_HEADER, String.class);
        String infoTipoInvioFatturaCA = message.getHeader(INFO_TIPO_INVIO_FATTURA_CA_HEADER, String.class);

        if (tipoMessaggio == null || "".equals(tipoMessaggio))
            throw new FatturaPAException("Tipo messaggio non valido!");

        if (canaleAvanzato != null && !"".equals(canaleAvanzato)) {

            switch (tipoMessaggio) {

                case MESSAGGIO_NOTIFICA_DECORRENZA:

                    if (infoTipoInvioFatturaCA != null && !"".equals(infoTipoInvioFatturaCA)) {
                        switch (infoTipoInvioFatturaCA) {

                            case "InvioSingolo":
                                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_INVIO_UNICO.getValue();
                                break;
                            case "Protocollazione":
                                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_PROTOCOLLATA.getValue();
                                break;
                        }
                    } else {
                        throw new FatturaPAException("Info Stato 'NOTIFICA DEC. TERMINI' non valido");
                    }

                    break;

                case MESSAGGIO_ESITO_COMMITENTE:

                    if (infoTipoInvioFatturaCA != null && !"".equals(infoTipoInvioFatturaCA)) {
                        switch (infoTipoInvioFatturaCA) {

                            case "InvioSingolo":
                                throw new FatturaPAException("Tentativo di invio unico notifica esito committente");

                            case "Protocollazione":
                                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_EC_PROTOCOLLATA.getValue();
                                break;
                        }
                    } else {
                        throw new FatturaPAException("Info Stato 'NOTIFICA ESITO COMM' non valido");
                    }

                    break;

                case MESSAGGIO_SCARTO_ESITO_COMMITENTE:

                    if (infoTipoInvioFatturaCA != null && !"".equals(infoTipoInvioFatturaCA)) {
                        switch (infoTipoInvioFatturaCA) {

                            case "InvioSingolo":
                                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_INVIO_UNICO.getValue();
                                break;

                            case "Protocollazione":
                                codiceStatoFattura = CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_PROTOCOLLATA.getValue();
                                break;
                        }
                    } else {
                        throw new FatturaPAException("Info Stato 'NOTIFICA SCARTO ESITO COMM' non valido");
                    }

                    break;

                    default:
                        throw new FatturaPAException("Tipo messaggio non riconosciuto");
            }
        }

        fatturaManager.protocollaNotifica(identificativoSdI, codiceStatoFattura);
    }

    public DatiFatturaManager getFatturaManager() {
        return fatturaManager;
    }

    public void setFatturaManager(DatiFatturaManager fatturaManager) {
        this.fatturaManager = fatturaManager;
    }
}