package it.extrasys.marche.regione.fatturapa.enti.bridge.asur.protocollazione.paleo;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaMetadatiPaleo;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura.CreaRichiestaProtocollazioneEntrataProcessor;
import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;

import java.util.Date;
import java.util.Map;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 08/03/15.
 */
public class CreaRichiestaProtocollazioneEntrataASURProcessor extends CreaRichiestaProtocollazioneEntrataProcessor {


    @Override
    public void process(Exchange exchange) throws Exception {

        ObjectFactory objectFactory = new ObjectFactory();

        FatturaElettronicaWrapper fatturaElettronicaWrapper = exchange.getIn().getBody(FatturaElettronicaWrapper.class);

        Map<String, FatturaElettronicaMetadatiPaleo> mappaMetadati = fatturaElettronicaWrapper.getFatturaMetadatiMap();

        ReqProtocolloArrivo reqProtocolloArrivo = objectFactory.createReqProtocolloArrivo();

        //RICHIEDENTE
        OperatorePaleo richiedente = getRichiedente(objectFactory);
        reqProtocolloArrivo.setOperatore(richiedente);

        //FINE RICHIEDENTE
        reqProtocolloArrivo.setCodiceRegistro(getCodiceRegistro());

        //TODO: Nell'analisi non è specificato come valorizzarlo per ora valorizzo con "Protocollazione FatturaPA"
        String oggetto = getOggetto(fatturaElettronicaWrapper);
        reqProtocolloArrivo.setOggetto(oggetto);

        // PRIVATO: FALSE
        reqProtocolloArrivo.setPrivato(false);

        // FILE FATTURA
        String identificativoSdI = exchange.getIn().getHeader("identificativoSdI", String.class);
        String nomeFile = exchange.getIn().getHeader("nomeFile", String.class);
        String estensione = FileUtils.getFileExtension(nomeFile);

        byte [] fileFatturaFirmata = getFatturazionePassivaFatturaManager().getFileFatturaByIdentificativiSdI(identificativoSdI);

        File fileFattura = getFileFattura(fileFatturaFirmata, nomeFile, estensione, objectFactory);
        reqProtocolloArrivo.setDocumentoPrincipale(objectFactory.createReqDocumentoDocumentoPrincipale(fileFattura));

        // ACQUISITO INTEGRALMENTE
        reqProtocolloArrivo.setDocumentoPrincipaleAcquisitoIntegralmente(true);

        // DATA ARRIVO
        Date dataArrivo = exchange.getIn().getHeader("dataRicezione", Date.class);
        reqProtocolloArrivo.setDataArrivo(DateUtils.DateToXMLGregorianCalendar(dataArrivo));


        //TRASMISSIONE
        // TRASMISSIONE
        Trasmissione trasmissione = objectFactory.createTrasmissione();

        ArrayOfTrasmissioneRuolo arrayOfTrasmissioneRuolo = objectFactory.createArrayOfTrasmissioneRuolo();
        ArrayOfTrasmissioneUtente arrayOfTrasmissioneUtente = objectFactory.createArrayOfTrasmissioneUtente();

        TrasmissioneRuolo trasmissioneRuolo = objectFactory.createTrasmissioneRuolo();

        trasmissioneRuolo.setRuoloDestinatario(getRuolo());
        trasmissioneRuolo.setCodiceUODestinataria(getUo());
        trasmissioneRuolo.setRagione("Inoltro a Ruolo");
        arrayOfTrasmissioneRuolo.getTrasmissioneRuolo().add(trasmissioneRuolo);

        // MITTENTE
        Corrispondente mittente = getMittente(fatturaElettronicaWrapper, objectFactory);
        reqProtocolloArrivo.setMittente(mittente);
        // FINE MITTENTE

        // Tipo Documento Principale Originale
        reqProtocolloArrivo.setDocumentoPrincipaleOriginale(TipoOriginale.DIGITALE);

        // Tipo Documento
        reqProtocolloArrivo.setTipoDocumento(objectFactory.createReqDocumentoTipoDocumento("FATTURAPA"));

        exchange.getIn().setBody(reqProtocolloArrivo);
    }

}
