package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificheCAManagerXAImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

public class SalvaEsitoCommittenteProcessor implements Processor{

    FatturazionePassivaNotificheCAManagerXAImpl fatturazionePassivaNotificheCAManagerXA;

    @Override
    public void process(Exchange exchange) throws Exception {

        NotificaEsitoCommittenteType notificaEsitoCommittenteType = (NotificaEsitoCommittenteType)exchange.getIn().getBody();

        NotificaFromEntiEntity notificaFromEntiEntity = fatturazionePassivaNotificheCAManagerXA.salvaNotificaFtp(notificaEsitoCommittenteType,(String)exchange.getIn().getHeader("codiceUfficio"), (String)exchange.getIn().getHeader(FtpConstants.FILE_NAME_ZIP),(String)exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY));

        //Creo l'oggetto da mettere nella coda per inviare all sdi
        byte[] fattura = ((String) exchange.getProperty("fileOriginale")).getBytes();
        DataSource fatturapa = new ByteArrayDataSource(fattura, "text/plain; charset=UTF-8");

        EsitoFatturaMessageType esitoFatturaMessageType = new EsitoFatturaMessageType();
        EsitoFatturaMessageRequest esitoFatturaMessageRequest = new EsitoFatturaMessageRequest();

        esitoFatturaMessageType.setIdFiscaleCommittente(notificaFromEntiEntity.getIdFiscaleCommittente());
        esitoFatturaMessageType.setCodUfficio(notificaFromEntiEntity.getCodUfficio());
        esitoFatturaMessageType.setNomeFile((String)exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY));
        esitoFatturaMessageType.setFile(new DataHandler(fatturapa));

        esitoFatturaMessageRequest.setEsitoFatturaMessage(esitoFatturaMessageType);

       // exchange.getIn().setHeader("codiceUfficio", notificaFromEntiEntity.getCodUfficio());
        exchange.getIn().setHeader("committenteCodiceIva", notificaFromEntiEntity.getIdFiscaleCommittente());
        exchange.getIn().setHeader("idFiscaleCommittente", notificaFromEntiEntity.getIdFiscaleCommittente());
        exchange.getIn().setHeader("identificativoSdi", notificaFromEntiEntity.getIdentificativoSdI());

        exchange.getIn().setBody(esitoFatturaMessageRequest);
    }


    public FatturazionePassivaNotificheCAManagerXAImpl getFatturazionePassivaNotificheCAManagerXA() {
        return fatturazionePassivaNotificheCAManagerXA;
    }

    public void setFatturazionePassivaNotificheCAManagerXA(FatturazionePassivaNotificheCAManagerXAImpl fatturazionePassivaNotificheCAManagerXA) {
        this.fatturazionePassivaNotificheCAManagerXA = fatturazionePassivaNotificheCAManagerXA;
    }
}
