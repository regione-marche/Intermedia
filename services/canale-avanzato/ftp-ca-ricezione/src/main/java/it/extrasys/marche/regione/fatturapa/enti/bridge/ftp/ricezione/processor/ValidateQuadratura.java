package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.QuadraturaFTPType;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ValidateQuadratura implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        //sottrae il file di esito
        Integer numeroFileTotale = ((Integer) exchange.getProperty(FtpConstants.NUMERO_FILE_TOTALE)) - 1;

        QuadraturaFTPType quadratura = (QuadraturaFTPType) exchange.getIn().getBody();

        /*EC Notifica di esito committente*/
        int notificheEsitoCommittente = 0;
        if (quadratura.getNumeroFile().getNotificaEsitoCommittente() != null) {
            notificheEsitoCommittente = quadratura.getNumeroFile().getNotificaEsitoCommittente().intValue();
        }
        /*  FA File Fattura Elettronica PA o B2B */
        int fatture = 0;
        if (quadratura.getNumeroFile().getFatture() != null) {
            fatture = quadratura.getNumeroFile().getFatture().intValue();
        }

        if (numeroFileTotale != (notificheEsitoCommittente + fatture)) {
            exchange.getIn().setHeader(FtpConstants.VALIDATED, false);
        } else {
            exchange.getIn().setHeader(FtpConstants.VALIDATED, true);
        }
    }
}
