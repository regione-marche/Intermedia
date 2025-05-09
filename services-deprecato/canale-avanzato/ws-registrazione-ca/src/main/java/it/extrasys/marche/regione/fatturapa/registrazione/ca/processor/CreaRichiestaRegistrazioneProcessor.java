package it.extrasys.marche.regione.fatturapa.registrazione.ca.processor;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaDecorrenzaTerminiType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.ScartoEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaDecorrenzaTerminiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.FileFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FatturazionePassivaNotificaDecorrenzaTerminiManager;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.fattura.FatturaRequestType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.fattura.FatturaType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.fattura.XMLType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.EsitoType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.NotificaRequestType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.NotificaType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.TipoNotificaRequestCodeType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Date;

public class CreaRichiestaRegistrazioneProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CreaRichiestaRegistrazioneProcessor.class);

    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
    private static final String NOME_FILE_HEADER = "nomeFile";

    private FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager;
    private FatturazionePassivaNotificaDecorrenzaTerminiManager notificaDecorrenzaTerminiManager;

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();

        String identificativoSdI = message.getHeader(IDENTIFICATIVO_SDI_HEADER, String.class);
        String nomeFile = message.getHeader(NOME_FILE_HEADER, String.class);

        Object body = message.getBody();

        //Fattura
        if(body instanceof FatturaElettronicaWrapper){

            FatturaElettronicaWrapper fatturaElettronicaWrapper = exchange.getIn().getBody(FatturaElettronicaWrapper.class);

            FatturaRequestType fatturaRequest = new FatturaRequestType();

            FatturaType fatturaType = new FatturaType();
            fatturaType.setIdentificativoSDI(identificativoSdI);
            //fatturaType.setDataProtocollo("");
            fatturaType.setNumeroProtocollo(fatturaElettronicaWrapper.getSegnaturaProtocollo());

            XMLType fatturaXmlType = new XMLType();
            fatturaXmlType.setNomeFile(nomeFile);

            // Dentro il modulo SdI Inbound si elimina dalla fattura la firma e poichè per il CA vogliamo inviare la fattura così come ci arriva dallo SdI
            // la riprendiamo dal DB cosa che si fa anche per PEC ed FTP
            FileFatturaEntity fileFatturaEntity = fatturazionePassivaFatturaManager.getFileFatturaEntityByIdentificativiSdI(identificativoSdI);
            //fatturaXmlType.setValue(fatturaElettronicaWrapper.getFatturaElettronica().getBytes());
            fatturaXmlType.setValue(fileFatturaEntity.getContenutoFile());

            fatturaType.setXML(fatturaXmlType);

            fatturaRequest.setRegistraFattura(fatturaType);

            exchange.getIn().setBody(fatturaRequest);
        }

        //Notifica Scarto Esito Committente
        else if(body instanceof NotificaScartoEsitoCommittenteWrapper){

            NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = exchange.getIn().getBody(NotificaScartoEsitoCommittenteWrapper.class);

            ScartoEsitoCommittenteType scartoEsitoCommittenteType = JaxBUtils.getScartoEsito(notificaScartoEsitoCommittenteWrapper.getNotificaScartoEsitoCommittente());

            NotificaRequestType notificaRequest = new NotificaRequestType();

            NotificaType notificaType = new NotificaType();
            notificaType.setIdentificativoSDI(identificativoSdI);

            EsitoType esitoType = new EsitoType();
            esitoType.setData(DateUtils.getDateAsString("yyyy-MM-dd HH:mm:ss", new Date()));
            esitoType.setCodice(TipoNotificaRequestCodeType.NS);
            esitoType.setNote(scartoEsitoCommittenteType.getNote());

            notificaType.setEsito(esitoType);

            it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.XMLType notificaXmlType =
                    new it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.XMLType();
            notificaXmlType.setNomeFile(nomeFile);
            notificaXmlType.setValue(notificaScartoEsitoCommittenteWrapper.getNotificaScartoEsitoCommittente().getBytes());

            notificaType.setXML(notificaXmlType);

            notificaRequest.setRegistraNotifica(notificaType);

            exchange.getIn().setBody(notificaRequest);
        }

        //Notifica Decorrenza Termini
        else if(body instanceof NotificaDecorrenzaTerminiWrapper){

            NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = exchange.getIn().getBody(NotificaDecorrenzaTerminiWrapper.class);

            NotificaDecorrenzaTerminiType decorrenzaTerminiType = JaxBUtils.getDecorrenzaTermini(notificaDecorrenzaTerminiWrapper.getNotificaDecorrenzaTermini());

            NotificaRequestType notificaRequest = new NotificaRequestType();

            NotificaType notificaType = new NotificaType();
            notificaType.setIdentificativoSDI(identificativoSdI);

            EsitoType esitoType = new EsitoType();
            esitoType.setData(DateUtils.getDateAsString("yyyy-MM-dd HH:mm:ss", new Date()));
            esitoType.setCodice(TipoNotificaRequestCodeType.DT);
            esitoType.setNote(decorrenzaTerminiType.getNote());

            notificaType.setEsito(esitoType);

            it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.XMLType notificaXmlType =
                    new it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.XMLType();
            notificaXmlType.setNomeFile(nomeFile);

            // Dentro il modulo SdI Inbound si elimina dalla notifica la firma e poichè per il CA vogliamo inviare la notifica così come ci arriva dallo SdI
            // la riprendiamo dal DB cosa che si fa anche per PEC ed FTP
            NotificaDecorrenzaTerminiEntity notificaDecorrenzaTerminiEntity = notificaDecorrenzaTerminiManager.getNotificaDecorrenzaTerminiByIdentificativoSDI(new BigInteger(identificativoSdI));
            //notificaXmlType.setValue(notificaDecorrenzaTerminiWrapper.getNotificaDecorrenzaTermini().getBytes());
            notificaXmlType.setValue(notificaDecorrenzaTerminiEntity.getContenutoFile());

            notificaType.setXML(notificaXmlType);

            notificaRequest.setRegistraNotifica(notificaType);

            exchange.getIn().setBody(notificaRequest);
        }
    }

    public FatturazionePassivaFatturaManager getFatturazionePassivaFatturaManager() {
        return fatturazionePassivaFatturaManager;
    }

    public void setFatturazionePassivaFatturaManager(FatturazionePassivaFatturaManager fatturazionePassivaFatturaManager) {
        this.fatturazionePassivaFatturaManager = fatturazionePassivaFatturaManager;
    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getNotificaDecorrenzaTerminiManager() {
        return notificaDecorrenzaTerminiManager;
    }

    public void setNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager notificaDecorrenzaTerminiManager) {
        this.notificaDecorrenzaTerminiManager = notificaDecorrenzaTerminiManager;
    }
}