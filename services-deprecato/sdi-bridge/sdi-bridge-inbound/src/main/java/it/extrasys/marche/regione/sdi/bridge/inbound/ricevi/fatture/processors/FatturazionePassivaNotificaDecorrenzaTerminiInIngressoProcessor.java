package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.processors;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaDecorrenzaTerminiType;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;

/**
 * Created by agosteeno on 06/03/15.
 */
public class FatturazionePassivaNotificaDecorrenzaTerminiInIngressoProcessor implements Processor {

    boolean salvaMessaggioOriginale;
    private FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl fatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl;

    @Override
    public void process(Exchange exchange) throws Exception {

        String originalSoapMessage = null;

        if (salvaMessaggioOriginale) {
            originalSoapMessage = (String) exchange.getIn().getHeader("originalSoapMessage");
        }

        byte[] notificaDecorrenzaBytesArray = Base64.decode(exchange.getIn().getBody(String.class));

        //Tutti i messaggi prodotti ed inviati dal Sistema di Interscambio, a
        //eccezione del file dei metadati, vengono firmati elettronicamente mediante una firma elettronica di tipo XAdES-Bes.
        // Tolgo la firma XAdES-Bes dal file di notifica Decorrenza Termini
        String notificaDecorrenza = XAdESUnwrapper.unwrap(notificaDecorrenzaBytesArray);

        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);

        BigInteger identificativoSdI = new BigInteger(exchange.getIn().getHeader("identificativoSdI", String.class));

        String nomeFileMetadati = exchange.getIn().getHeader("nomeFileMetadati", String.class);

        NotificaDecorrenzaTerminiType notificaDecorrenzaType = JaxBUtils.getDecorrenzaTermini(notificaDecorrenza);

        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = fatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl.salvaNotificaDecorrenzaTermini(notificaDecorrenzaType.getNomeFile(), identificativoSdI, notificaDecorrenzaType, notificaDecorrenzaBytesArray, originalSoapMessage);

        if(notificaDecorrenzaTerminiEntity != null) {
            //setto questo header che verra' utilizzato nel modulo multi ente per ricavare l'idFiscaleCommittente
            exchange.getIn().setHeader("committenteCodiceIva", notificaDecorrenzaTerminiEntity.getIdFiscaleCommittente());
            exchange.getIn().setHeader("codiceUfficio", notificaDecorrenzaTerminiEntity.getCodiceUfficio());
            exchange.getIn().setHeader("decorrenzaTerminiGiaRicevuta", false);
            exchange.getIn().setHeader("dataRicezioneSdI", notificaDecorrenzaTerminiEntity.getDataRicezione());
            exchange.getIn().setHeader("nomeCedentePrestatore", notificaDecorrenzaTerminiEntity.getNomeCedentePrestatore());
        }else{
            //Caso in cui è già presente
            exchange.getIn().setHeader("decorrenzaTerminiGiaRicevuta", true);
        }

        exchange.getIn().removeHeader("originalSoapMessage");

        exchange.getIn().setBody(notificaDecorrenza);

    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl getFatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl(FatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl fatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl = fatturazionePassivaNotificaDecorrenzaTerminiManagerXAImpl;
    }

    public boolean isSalvaMessaggioOriginale() {
        return salvaMessaggioOriginale;
    }

    public void setSalvaMessaggioOriginale(boolean salvaMessaggioOriginale) {
        this.salvaMessaggioOriginale = salvaMessaggioOriginale;
    }
}
