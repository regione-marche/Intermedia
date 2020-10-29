package it.extrasys.marche.regione.sdi.bridge.inbound.ricevi.fatture.processors;

import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CessionarioCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.MetadatiInvioFileType;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManagerXAImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/02/15.
 */

public class FatturazionePassivaFatturaInIngressoProcessor implements Processor {


    private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaFatturaInIngressoProcessor.class);

    private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";

    FatturazionePassivaFatturaManagerXAImpl fatturazionePassivaFatturaManager;

    boolean salvaMessaggioOriginale;

    /**
     * @param actor CessionarioCommittenteType oppure CedentePrestatoreType di cui si vuole l'id fiscale
     * @return l'id fiscale dell'attore passato come parametro
     */
    private static String cfOrPiva(Object actor) {

        if (actor instanceof CessionarioCommittenteType) {
            CessionarioCommittenteType committente = (CessionarioCommittenteType) actor;
            return committente.getDatiAnagrafici().getIdFiscaleIVA() != null ? committente.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() + committente.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() : committente.getDatiAnagrafici().getCodiceFiscale();
        }
        if (actor instanceof CedentePrestatoreType) {
            CedentePrestatoreType prestatore = (CedentePrestatoreType) actor;
            return prestatore.getDatiAnagrafici().getIdFiscaleIVA() != null ? prestatore.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() + prestatore.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() : prestatore.getDatiAnagrafici().getCodiceFiscale();
        }
        return null;
    }

    /**
     * *
     *
     * @param exchange
     * @throws Exception
     */
    public void process(Exchange exchange) throws Exception {

        String originalSoapMessage = null;

        if (salvaMessaggioOriginale) {
            originalSoapMessage = (String) exchange.getIn().getHeader("originalSoapMessage");
        }
        Boolean isTest = Boolean.FALSE;
        if (exchange.getIn().getHeader("fatturazioneTest") != null) {
            isTest = Boolean.parseBoolean((String) exchange.getIn().getHeader("fatturazioneTest"));
        }
        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);
        BigInteger identificativoSdI = new BigInteger(exchange.getIn().getHeader("identificativoSdI", String.class));

        if(fatturazionePassivaFatturaManager.getFileFatturaFromIdentificativoSdi(identificativoSdI) != null){
            exchange.getIn().setHeader("idSdiGiaPresente", true);
            return;
        }
        else{
            exchange.getIn().setHeader("idSdiGiaPresente", false);
        }

        String nomeFileMetadati = exchange.getIn().getHeader("nomeFileMetadati", String.class);
        byte[] fatturaElettronicaBytesArray = Base64.decodeBase64(exchange.getIn().getBody(String.class));

        String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFile, fatturaElettronicaBytesArray);

        byte[] metadatiByteArray = exchange.getIn().getHeader("metadati", String.class).getBytes();

        MetadatiInvioFileType metadatiInvioFileType = JaxBUtils.getMetadati(exchange.getIn().getHeader("metadati", String.class));

        FatturaElettronicaType fatturaElettronicaType = JaxBUtils.getFatturaElettronicaType(fatturaElettronica);

        //REGMA112 aggiunta per flusso semplificato
        boolean isFatturaInterna;

        String checkFlussoSemplificatoHeader = (String) exchange.getIn().getHeader("checkFlussoSemplificato");

        if (!CHECK_FLUSSO_SEMPLIFICATO_HEADER.equals(checkFlussoSemplificatoHeader)) {
            isFatturaInterna = false;
        } else {
            isFatturaInterna = true;
        }

        String dataRicezioneSdIString = (String) exchange.getIn().getHeader("dataRicezioneSdI");

        Date dataRicezioneSdI = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(dataRicezioneSdIString);

        fatturazionePassivaFatturaManager.salvaFatture(nomeFile, nomeFileMetadati, identificativoSdI, fatturaElettronicaType, metadatiInvioFileType, fatturaElettronicaBytesArray, metadatiByteArray, originalSoapMessage, isFatturaInterna, dataRicezioneSdI, isTest);

        exchange.getIn().removeHeader("originalSoapMessage");
        exchange.getIn().setHeader("cedenteCodiceIva", cfOrPiva(fatturaElettronicaType.getFatturaElettronicaHeader().getCedentePrestatore()));
        exchange.getIn().setHeader("committenteCodiceIva", cfOrPiva(fatturaElettronicaType.getFatturaElettronicaHeader().getCessionarioCommittente()));
        exchange.getIn().setHeader("codiceUfficio", fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione().getCodiceDestinatario());
        exchange.getIn().setBody(fatturaElettronica, String.class);
    }

    public FatturazionePassivaFatturaManagerXAImpl getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManagerXAImpl fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }

    public boolean isSalvaMessaggioOriginale() {
        return salvaMessaggioOriginale;
    }

    public void setSalvaMessaggioOriginale(boolean salvaMessaggioOriginale) {
        this.salvaMessaggioOriginale = salvaMessaggioOriginale;
    }
}