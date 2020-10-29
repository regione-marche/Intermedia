package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.*;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.RispostaSdINotificaEsitoType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaSDIServizioNonDisponibileException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.services.common.SdiBridgeOutboundCommon;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.message.MessageContentsList;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by agosteeno on 16/02/15.
 */
public class NotificaEsitoManager {

	private static final Logger LOG = LoggerFactory.getLogger(NotificaEsitoManager.class);

	private static final String VERSIONE_XSD = "1.0";
	private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";
	private static final String FILENAME_HEADER = "nomeFile";
	private static final String TIPO_MESSAGGIO_NOTIFICA_ESITO = "EC";
	private static final String DATI_FATTURA_ID_HEADER = "datiFatturaId_header";
	private static final String TIPO_NOTIFICA = "tipoNotifica";
	private static final String IS_NOTIFICA_SCARTATA = "notificaScarto";
	private static final String IS_NOTIFICA_ACCETTATA = "notificaOk";
	private static final String ORIGINAL_MESSAGE_FROM_SDI = "originalMessageFromSdi";

	private static final String TIPO_NOTIFICA_FROM_ENTE = "tipoNotificaFromEnte";
	private static final String NOTIFICA_FROM_ENTE_ACCETTAZIONE = "enteAccettazione";
	private static final String NOTIFICA_FROM_ENTE_RIFIUTO = "enteRifiuto";

	private static final String CODICE_UFFICIO_HEADER = "codiceUfficio";

	private static final String VALIDATION_RESULT = "validationResult";

	private static final String ESTENZIONE_FILE = ".xml";
	private static final String ESITO_RISPOSTA_HEADER = "esitoRisposta";
	private static final String NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER = "nomeFileScartoEsito";
	private static final String CONTENUTO_FILE_SCARTO = "contenutoFileScarto";


	private static final String CHECK_FLUSSO_SEMPLIFICATO_HEADER = "checkFlussoSemplificato";
	private static final String NOTIFICA_ESITO_COMMITTENTE_ORIGINALE = "notificaEsitoCommittenteOriginale";
	private static final String FILENAME_ORIGINALE_HEADER = "fileNameOriginale";

	private static final String TIPO_NOTIFICA_HEADER = "tipoNotifica";

	private DatiFatturaManager datiFatturaManager;

	public void convertNotificaEsitoCommittenteToEsitoFatturaMessageRequest(Exchange msgExchange) throws FatturaPaPersistenceException, FatturaPAException, JAXBException, IOException {

		Message msg = msgExchange.getIn();

		/*
 		FIXME INIZIO: causa bug "REGMA-55 Replicazione modifica NotificaRifiutoProcessor per tutti gli enti coinvolti"
		 */
		Object bodyObject = msg.getBody();
		EsitoFatturaMessageType esitoFattura;

		if(bodyObject instanceof EsitoFatturaMessageType){
			esitoFattura = (EsitoFatturaMessageType) bodyObject;
		} else if(bodyObject instanceof EsitoFatturaMessageRequest){
			EsitoFatturaMessageRequest esitoFatturaMessageRequest = (EsitoFatturaMessageRequest) bodyObject;

			esitoFattura = esitoFatturaMessageRequest.getEsitoFatturaMessage().get(0);

		} else {
			throw new FatturaPAException("NotificaEsitoManager - convertNotificaEsitoCommittenteToEsitoFatturaMessageRequest: il tipo del body non e' quello atteso!");
		}

		//FIXME questo e da scommentare se si toglie il FIX
		//EsitoFatturaMessageType esitoFattura = msg.getBody(EsitoFatturaMessageType.class);

		/*
 		FIXME FINE: causa bug "REGMA-55 Replicazione modifica NotificaRifiutoProcessor per tutti gli enti coinvolti"
		 */

		// Se è false è un rifiuto automatico
		Boolean validationResult = msg.getHeader(VALIDATION_RESULT, Boolean.class);
		if (validationResult == null){
			validationResult = true;
			msg.setHeader(VALIDATION_RESULT, true);

		}

		String codiceUfficio = esitoFattura.getCodUfficio();
		msg.setHeader(CODICE_UFFICIO_HEADER, codiceUfficio);

		RiferimentoFatturaType riferimentoFatturaType = null;
		BigInteger identificativoSdI = null;
		String nomeFile = "";

		String nomeFileFatturaOriginale = (String) msg.getHeader(FILENAME_HEADER);
		msg.setHeader(FILENAME_ORIGINALE_HEADER, nomeFileFatturaOriginale);

		if (!validationResult) {
			// rifiuto automatico: si gestisce per lotto e senza riferimenti alla fattura
			identificativoSdI = new BigInteger(msg.getHeader(IDENTIFICATIVO_SDI_HEADER, String.class));
			List<DatiFatturaEntity> fatture = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdI);

			for (DatiFatturaEntity fattura : fatture){
				nomeFile = createFileName(fattura);
			}

		} else {
			// rifiuto da ente
			String numeroFattura = esitoFattura.getNumeroFattura();
			List<DatiFatturaEntity> datiFatturaEntityList = datiFatturaManager.getFatturaByNumeroProtocolloCodiceUfficioAndNumeroFattura(esitoFattura.getNumeroProtocollo(), esitoFattura.getCodUfficio(), numeroFattura);
			DatiFatturaEntity datiFatturaEntity = datiFatturaEntityList.get(0);
			identificativoSdI = (datiFatturaEntity.getIdentificativoSdI());
			msg.setHeader(IDENTIFICATIVO_SDI_HEADER, String.valueOf(identificativoSdI));

			riferimentoFatturaType = new RiferimentoFatturaType();
			riferimentoFatturaType.setAnnoFattura(BigInteger.valueOf(esitoFattura.getDataFattura().getYear()));
			riferimentoFatturaType.setNumeroFattura(numeroFattura);
			riferimentoFatturaType.setPosizioneFattura(BigInteger.valueOf(datiFatturaEntity.getPosizioneFattura()));
			msg.setHeader(DATI_FATTURA_ID_HEADER, datiFatturaEntity.getIdDatiFattura());

			nomeFile = createFileName(datiFatturaEntity);
		}

		String esitoFatturaString = esitoFattura.getEsito().value();
		setHeaderAccettazioneRifiuto(msg, esitoFatturaString, EsitoCommittenteType.EC_01.value());

		String descrizioneEsito = esitoFattura.getDescrizione();

		// creazione notifica id committente
		it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.ObjectFactory objectFactory = new it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.ObjectFactory();
		NotificaEsitoCommittenteType notificaEsitoCommittenteType = objectFactory.createNotificaEsitoCommittenteType();
		notificaEsitoCommittenteType.setDescrizione(descrizioneEsito);
		notificaEsitoCommittenteType.setEsito(EsitoCommittenteType.fromValue(esitoFatturaString));
		notificaEsitoCommittenteType.setIdentificativoSdI(identificativoSdI);
		if (riferimentoFatturaType != null) {
			notificaEsitoCommittenteType.setRiferimentoFattura(riferimentoFatturaType);
		}

		// nel caso di rifiuto automatico non è popolato
		if (esitoFattura.getMessageIdCommittente() != null)
			notificaEsitoCommittenteType.setMessageIdCommittente(String.valueOf(esitoFattura.getMessageIdCommittente()));
		// la firma e' opzionale e non viene mai valorizzata
		notificaEsitoCommittenteType.setSignature(null);

		notificaEsitoCommittenteType.setVersione(VERSIONE_XSD);

		msg.setHeader(FILENAME_HEADER, nomeFile);

		FileSdIType fileSdIType = getFileSdIType(notificaEsitoCommittenteType, identificativoSdI, nomeFile);

		msg.setBody(fileSdIType);

		String flussoSemplificato = (String) msg.getHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER);

		//TODO regma 112 siccome il messaggio dovra' essere utilizzato per costruire la notifica di esito in caso di flusso semplificato, la metto anche in un header
		if(flussoSemplificato != null && CHECK_FLUSSO_SEMPLIFICATO_HEADER.equals(flussoSemplificato)){

			byte[] fileRicevuto = SdiBridgeOutboundCommon.getMessaggioAsBytesArray(fileSdIType.getFile());
			String fileRicevutoEncoded = new String(org.bouncycastle.util.encoders.Base64.encode(fileRicevuto));
			msg.setHeader(NOTIFICA_ESITO_COMMITTENTE_ORIGINALE, fileRicevutoEncoded);
		}

	}

	public void convertNotificaEsitoCommittenteCAToEsitoFatturaMessageRequest(Exchange msgExchange) throws FatturaPaPersistenceException, FatturaPAException, JAXBException, IOException {

		Message msg = msgExchange.getIn();

		/*
 		FIXME INIZIO: causa bug "REGMA-55 Replicazione modifica NotificaRifiutoProcessor per tutti gli enti coinvolti"
		 */
		Object bodyObject = msg.getBody();
		it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageType esitoFattura;

		if(bodyObject instanceof it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageType){
			esitoFattura = (it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageType) bodyObject;
		} else if(bodyObject instanceof it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest){
			it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest esitoFatturaMessageRequest = (it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest) bodyObject;

			esitoFattura = esitoFatturaMessageRequest.getEsitoFatturaMessage();

		} else {
			throw new FatturaPAException("NotificaEsitoManager - convertNotificaEsitoCommittenteCAToEsitoFatturaMessageRequest: il tipo del body non e' quello atteso!");
		}

		// Se è false è un rifiuto automatico
		Boolean validationResult = msg.getHeader(VALIDATION_RESULT, Boolean.class);
		if (validationResult == null){
			validationResult = true;
			msg.setHeader(VALIDATION_RESULT, true);
		}

		msg.setHeader(CODICE_UFFICIO_HEADER, esitoFattura.getCodUfficio());

		NotificaEsitoCommittenteType notificaEsitoCommittente = JaxBUtils.getNotificaEsitoCommittenteType(esitoFattura.getFile());

		BigInteger identificativoSdI = notificaEsitoCommittente.getIdentificativoSdI();
		msg.setHeader(IDENTIFICATIVO_SDI_HEADER, String.valueOf(identificativoSdI));

		//msg.setHeader(FILENAME_ORIGINALE_HEADER, nomeFileFatturaOriginale);
		//msg.setHeader(DATI_FATTURA_ID_HEADER, datiFatturaEntity.getIdDatiFattura());

		String esitoFatturaString = notificaEsitoCommittente.getEsito().value();
		setHeaderAccettazioneRifiuto(msg, esitoFatturaString, EsitoCommittenteType.EC_01.value());

		notificaEsitoCommittente.setVersione(VERSIONE_XSD);

		msg.setHeader(FILENAME_HEADER, esitoFattura.getNomeFile());

		FileSdIType fileSdIType = getFileSdIType(notificaEsitoCommittente, identificativoSdI, esitoFattura.getNomeFile());

		msg.setBody(fileSdIType);

		String flussoSemplificato = (String) msg.getHeader(CHECK_FLUSSO_SEMPLIFICATO_HEADER);

		//TODO regma 112 siccome il messaggio dovra' essere utilizzato per costruire la notifica di esito in caso di flusso semplificato, la metto anche in un header
		if(flussoSemplificato != null && CHECK_FLUSSO_SEMPLIFICATO_HEADER.equals(flussoSemplificato)){

			byte[] fileRicevuto = SdiBridgeOutboundCommon.getMessaggioAsBytesArray(fileSdIType.getFile());
			String fileRicevutoEncoded = new String(org.bouncycastle.util.encoders.Base64.encode(fileRicevuto));
			msg.setHeader(NOTIFICA_ESITO_COMMITTENTE_ORIGINALE, fileRicevutoEncoded);
		}
	}

	private void setHeaderAccettazioneRifiuto(Message msg, String valueCorrente, String valueAccettata) {
		/*
		 * metto l'esito in un header perche' questo rappresenta il tipo di notifica inviato dall'ente. Mi serve per aggiornare opportunamente lo stato
		 */
		if (EsitoCommittenteType.fromValue(valueCorrente).equals(EsitoCommittenteType.fromValue(valueAccettata))) {
			msg.setHeader(TIPO_NOTIFICA_FROM_ENTE, NOTIFICA_FROM_ENTE_ACCETTAZIONE);
		} else {
			msg.setHeader(TIPO_NOTIFICA_FROM_ENTE, NOTIFICA_FROM_ENTE_RIFIUTO);
		}
	}

	private FileSdIType getFileSdIType(NotificaEsitoCommittenteType notificaEsitoCommittenteType, BigInteger identificativoSdi, String nomeFile) throws JAXBException, PropertyException {

		StringWriter stringWriter = new StringWriter();

		JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		QName qName = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaEsitoCommittente");

		JAXBElement<NotificaEsitoCommittenteType> root = new JAXBElement<NotificaEsitoCommittenteType>(qName, NotificaEsitoCommittenteType.class, notificaEsitoCommittenteType);

		jaxbMarshaller.marshal(root, stringWriter);

		FileSdIType fileSdIType = new FileSdIType();

		fileSdIType.setNomeFile(nomeFile);
		fileSdIType.setIdentificativoSdI(identificativoSdi);

		String messaggio = stringWriter.toString();

		DataSource dataSource = new ByteArrayDataSource(messaggio.getBytes(), "application/octet-stream");

		DataHandler dataHandler = new DataHandler(dataSource);

		fileSdIType.setFile(dataHandler);
		return fileSdIType;
	}

	public void estraiRispostaFromSdi(Exchange exchange) throws IOException, FatturaPAException, FatturaPaPersistenceException,FatturaPaSDIServizioNonDisponibileException {

		Message msg = exchange.getIn();

		org.apache.cxf.message.Message cxfMessage = msg.getHeader(CxfConstants.CAMEL_CXF_MESSAGE, org.apache.cxf.message.Message.class);

		String tipoNotificaFromEnte = (String) msg.getHeader(TIPO_NOTIFICA_FROM_ENTE);

		BigInteger idFattura = msg.getHeader(DATI_FATTURA_ID_HEADER, BigInteger.class);
		BigInteger identificativoSdI = new BigInteger((String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER));
		String stato = "";
		if (NOTIFICA_FROM_ENTE_ACCETTAZIONE.equals(tipoNotificaFromEnte)) {

			stato = CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE.getValue();
		} else {
			stato = CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATO_RIFIUTO.getValue();
		}

		String originalSoapMessage = (String) cxfMessage.getExchange().get("originalSoapMessage");

		RispostaSdINotificaEsitoType rispostaSdINotificaEsito = null;

		MessageContentsList contentsList = msg.getBody(MessageContentsList.class);

		// setto il messaggio originale ricevuto dallo SDI che verra' poi
		// salvato su db
		msg.setHeader(ORIGINAL_MESSAGE_FROM_SDI, originalSoapMessage);

		if (contentsList != null && contentsList.size() != 0) {

			String statoRisposta = "";

			rispostaSdINotificaEsito = (RispostaSdINotificaEsitoType) contentsList.get(0);

			// trasformo l'oggetto in byte array e lo metto nel body del messaggio
			if (rispostaSdINotificaEsito.getScartoEsito() != null) {

				// se si tratta di uno scarto mi serve prendere l'oggetto
				/*byte[] fileSdiBaseTypeByteArray = SdiBridgeOutboundCommon.getFileSdiTypeBytesArray(rispostaSdINotificaEsito.getScartoEsito().getFile());
				String stringRispostaNotifica = new String(fileSdiBaseTypeByteArray);
				msg.setBody(stringRispostaNotifica);*/

				byte[] test = Base64.encode(SdiBridgeOutboundCommon.getMessaggioAsBytesArray(rispostaSdINotificaEsito.getScartoEsito().getFile()));
				msg.setHeader(CONTENUTO_FILE_SCARTO, new String(Base64.decode(test)));
				msg.setHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER, rispostaSdINotificaEsito.getScartoEsito().getNomeFile());

				msg.setBody(test);
			} else {
				msg.setBody(null);
			}

			String esitoRisposta = rispostaSdINotificaEsito.getEsito().value();
			msg.setHeader(ESITO_RISPOSTA_HEADER, esitoRisposta);

			if (EsitoNotificaType.ES_00.value().equals(esitoRisposta)) {

				ScartoType scartoType = null;
				String noteScartoType = null;

				try{
					ScartoEsitoCommittenteType scartoEsitoCommittenteType = JaxBUtils.getScartoEsito(rispostaSdINotificaEsito.getScartoEsito().getFile());
					scartoType = scartoEsitoCommittenteType.getScarto();
					noteScartoType = scartoEsitoCommittenteType.getNote();
				}catch (Exception e){
					LOG.error("NotificaEsitoManager - aggiornaFattura: Errore Estrazione Tipo Scarto Esito. MSG: " + e.getMessage());
				}

				if ((scartoType != null && "EN01".equals(scartoType.value())) && (noteScartoType != null && noteScartoType.contains("EN02"))){

					msg.setBody(null);

					// NOTIFICA ACCETTATA
					//statoRisposta = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_ACCETTATA.getValue();
					statoRisposta = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA_PER_REINVIO.getValue();
					msg.setHeader(TIPO_NOTIFICA, IS_NOTIFICA_ACCETTATA);

				}else{

					// NOTIFICA NON ACCETTATA
					statoRisposta = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA.getValue();
					msg.setHeader(TIPO_NOTIFICA, IS_NOTIFICA_SCARTATA);

				}
			} else if (EsitoNotificaType.ES_01.value().equals(esitoRisposta)) {
				// NOTIFICA ACCETTATA
				statoRisposta = CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_ACCETTATA.getValue();
				msg.setHeader(TIPO_NOTIFICA, IS_NOTIFICA_ACCETTATA);

			} else if (EsitoNotificaType.ES_02.value().equals(esitoRisposta)) {
				// SERVIZIO NON DISPONIBILE
				throw new FatturaPaSDIServizioNonDisponibileException("NotificaEsitoManager - aggiornaFattura: SDI Esito: SERVIZIO NON DISPONIBILE");

			} else {
				// CASO NON CENSITO
				throw new FatturaPAException("NotificaEsitoManager - aggiornaFattura: Esito risposta non censito");
			}
			msg.setHeader("STATO_RISPOSTA",statoRisposta);


		} else {

			throw new FatturaPAException("NotificaEsitoManager - aggiornaFattura: Risposta dallo SDI non valida");
		}

		/*
		dopo aver controllato che sia tutto ok aggiorno lo stato ad "inviata allo SDI" perche' in caso di problemi viene lanciata un'eccezione che
		fa' partire la redelivery policy. Se questo aggiorna fattura viene eseguito prima dei controlli proverebbe ad inserire 2 volte lo stesso
		stato
		 */

		aggiornaStatoFattura(idFattura, stato, identificativoSdI);
	}

	public void preparaMessaggioPerProtocollazione(Exchange exchange) throws FatturaPAException, IOException {

		Message msg = exchange.getIn();

		String tipoNotifica = msg.getHeader(TIPO_NOTIFICA_HEADER, String.class);

		switch (tipoNotifica) {

			case "notificaOk":

				it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest esitoFatturaMessageRequest = null;

				try {
					esitoFatturaMessageRequest = JaxBUtils.getEsitoFatturaCA(msg.getBody(String.class));
				} catch (Exception e) {
					throw new FatturaPAException();
				}

				msg.setHeader("nomeFile", esitoFatturaMessageRequest.getEsitoFatturaMessage().getNomeFile());

				String notifica = new String(SdiBridgeOutboundCommon.getMessaggioAsBytesArray(esitoFatturaMessageRequest.getEsitoFatturaMessage().getFile()));

				String notificaXml = getXml(notifica);

				msg.setBody(notificaXml);

				break;

			case "notificaScarto":

				msg.setHeader("nomeFile", msg.getHeader(NOME_FILE_SCARTO_ESITO_RISPOSTA_HEADER));

				String notificaScartoXml = getXml(msg.getBody());

				msg.setBody(notificaScartoXml);

				break;
		}
	}

	public void aggiornaFattura(Exchange msgExchange) throws FatturaPAException, FatturaPaSDIServizioNonDisponibileException, IOException, JAXBException, FatturaPaPersistenceException {

		Message msg = msgExchange.getIn();
		// aggorno lo stato con INVIATA_ACCETTAZIONE o INVIATO_RIFIUTO e poi controllo l'esito della risposta e aggiorno con RICEVUTO_RIFIUTO o
		// RICEVUTA_ACCETTAZIONE a seconda della risposta ottenuta
		String idFatturaString = msg.getHeader(DATI_FATTURA_ID_HEADER, String.class);
		BigInteger idFattura = null;
		if (idFatturaString != null)
			idFattura = new BigInteger(idFatturaString);
		BigInteger identificativoSdI = new BigInteger((String) msg.getHeader(IDENTIFICATIVO_SDI_HEADER));
		String esitoRisposta = (String) msg.getHeader(ESITO_RISPOSTA_HEADER);
		String stato = msg.getHeader("STATO_RISPOSTA",String.class);

		aggiornaStatoFattura(idFattura, stato, identificativoSdI);

	}

	private void aggiornaStatoFattura(BigInteger idFattura, String stato, BigInteger identificativoSdI) throws FatturaPAException, FatturaPaPersistenceException {
		if (idFattura != null) {
			datiFatturaManager.aggiornaStatoFatturaEsito(idFattura, stato);
		} else {

			List<DatiFatturaEntity> fatture = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdI);
			for (DatiFatturaEntity fattura : fatture) {
				datiFatturaManager.aggiornaStatoFatturaEsito(fattura.getIdDatiFattura(), stato);
			}
		}
	}

	private String createFileName(DatiFatturaEntity datiFatturaEntity) throws FatturaPAException {

		String nomeFile = "";

		String numeroProgressivo;

		String nomeFileDatiFattura = datiFatturaEntity.getNomeFile();

		StringTokenizer st = new StringTokenizer(nomeFileDatiFattura);

		nomeFileDatiFattura = st.nextToken(".");

		if (datiFatturaEntity.getProgressivoInvioNotifica() == null || "".equals(datiFatturaEntity.getProgressivoInvioNotifica())) {
			numeroProgressivo = "001";
		} else {
			numeroProgressivo = datiFatturaEntity.getProgressivoInvioNotifica();
		}

		if (numeroProgressivo.length() > 3) {
			numeroProgressivo = numeroProgressivo.substring(numeroProgressivo.length() - 3, numeroProgressivo.length());
		}

		nomeFile = nomeFile + nomeFileDatiFattura + "_" + TIPO_MESSAGGIO_NOTIFICA_ESITO + "_" + numeroProgressivo + ESTENZIONE_FILE;

		/*
		 * questa lista temporanea serve perche' il metodo di datiFatturaManager potrebbe modificare il valore anche per liste di fatture
		 */
		List<DatiFatturaEntity> datiFatturaEntityListTmp = new ArrayList<DatiFatturaEntity>();
		datiFatturaEntityListTmp.add(datiFatturaEntity);

		datiFatturaManager.aggiornaNumeroProgressivo(datiFatturaEntityListTmp, numeroProgressivo);

		return nomeFile;
	}

	private String getXml(Object body) throws FatturaPAException {

		String xml = "";

		if (body instanceof String) {

			xml = (String) body;

		} else if (body instanceof byte[]) {

			byte[] bytes = (byte[]) body;
			xml = new String(bytes);
		}

		try {

			if (Base64Utils.isBase64(xml.getBytes())) {

				byte[] bytesDecod = org.apache.commons.codec.binary.Base64.decodeBase64(xml);
				xml = new String(bytesDecod);

			}

		} catch (Exception ex) {
			throw new FatturaPAException();
		}

		return xml;
	}

	public DatiFatturaManager getDatiFatturaManager() {
		return datiFatturaManager;
	}

	public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
		this.datiFatturaManager = datiFatturaManager;
	}
}