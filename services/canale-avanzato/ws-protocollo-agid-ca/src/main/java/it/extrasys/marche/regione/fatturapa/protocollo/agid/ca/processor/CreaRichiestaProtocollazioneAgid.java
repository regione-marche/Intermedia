package it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaDecorrenzaTerminiWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.NotificaScartoEsitoCommittenteWrapper;
import it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.utils.AgidConstant;
import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.utils.AgidConstant.sdfLong;
import static it.extrasys.marche.regione.fatturapa.protocollo.agid.ca.utils.AgidConstant.sdfShort;


public class CreaRichiestaProtocollazioneAgid implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {

        String file = "";
        String fileMetadati = "";
        String nomeMittente = "";
        String nomeMetadati = "";
        Date dataRicezione = null;

        if (exchange.getIn().getBody() instanceof FatturaElettronicaWrapper) {
            FatturaElettronicaWrapper fatturaElettronicaWrapper = (FatturaElettronicaWrapper) exchange.getIn().getBody();
            file = fatturaElettronicaWrapper.getFatturaElettronica();
            fileMetadati = (String) exchange.getIn().getHeader("metadati");
            nomeMittente = fatturaElettronicaWrapper.getMittente();
            nomeMetadati = (String) exchange.getIn().getHeader("nomeFileMetadati");
            dataRicezione = sdfLong.parse((String) exchange.getIn().getHeader("dataRicezioneSdI"));
        } else if (exchange.getIn().getBody() instanceof NotificaEsitoCommittenteWrapper) {
            NotificaEsitoCommittenteWrapper notificaEsitoCommittenteWrapper = (NotificaEsitoCommittenteWrapper) exchange.getIn().getBody();
            file = notificaEsitoCommittenteWrapper.getNotificaEsitoCommittente();
            try {
                dataRicezione = DateUtils.parseDate((String) exchange.getIn().getHeader("dataRicezioneEnte"), "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            }catch (ParseException e) {
                dataRicezione = sdfLong.parse((String) exchange.getIn().getHeader("dataRicezioneEnte"));
            }
            nomeMittente = (String) exchange.getIn().getHeader(AgidConstant.NOME_ENTE_DESTINAZIONE);
        } else if (exchange.getIn().getBody() instanceof NotificaScartoEsitoCommittenteWrapper) {
            NotificaScartoEsitoCommittenteWrapper notificaScartoEsitoCommittenteWrapper = (NotificaScartoEsitoCommittenteWrapper) exchange.getIn().getBody();
            file = notificaScartoEsitoCommittenteWrapper.getNotificaScartoEsitoCommittente();
            try {
            dataRicezione = DateUtils.parseDate((String) exchange.getIn().getHeader("dataRicezioneSdI"), "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            }catch (ParseException e) {
                dataRicezione = sdfLong.parse((String) exchange.getIn().getHeader("dataRicezioneSdI"));
            }
            nomeMittente = (String) exchange.getIn().getHeader(AgidConstant.NOME_ENTE_DESTINAZIONE);
        } else if (exchange.getIn().getBody() instanceof NotificaDecorrenzaTerminiWrapper) {
            NotificaDecorrenzaTerminiWrapper notificaDecorrenzaTerminiWrapper = (NotificaDecorrenzaTerminiWrapper) exchange.getIn().getBody();
            file = notificaDecorrenzaTerminiWrapper.getNotificaDecorrenzaTermini();
            dataRicezione =  DateUtils.parseDate((String) exchange.getIn().getHeader("dataRicezioneSdI"), "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            nomeMittente = (String) exchange.getIn().getHeader(AgidConstant.NOME_ENTE_DESTINAZIONE);
        }

        String nomeFile = (String) exchange.getIn().getHeader("nomeFile");
        String identificativoSdi = (String) exchange.getIn().getHeader("identificativoSdI");

        List<Object> params = new ArrayList<>();

        ObjectFactory factory = new ObjectFactory();

        SegnaturaEnvelopeType segnaturaEnvelopeType = new SegnaturaEnvelopeType();

        Segnatura segnatura = factory.createSegnatura();

        Intestazione intestazione = factory.createIntestazione();

        Identificatore identificatore = factory.createIdentificatore();
        //IDENTIFICATORE
        CodiceAmministrazione codiceAmministrazione = factory.createCodiceAmministrazione();
        codiceAmministrazione.setContent("MEF");
        identificatore.setCodiceAmministrazione(codiceAmministrazione);

        CodiceAOO codiceAOO = factory.createCodiceAOO();
        codiceAOO.setContent("MEF");
        identificatore.setCodiceAOO(codiceAOO);

        CodiceRegistro codiceRegistro = factory.createCodiceRegistro();
        codiceRegistro.setContent("REG-MEF");
        identificatore.setCodiceRegistro(codiceRegistro);

        NumeroRegistrazione numeroRegistrazione = factory.createNumeroRegistrazione();
        numeroRegistrazione.setContent(identificativoSdi); //identif_sdi

        identificatore.setNumeroRegistrazione(numeroRegistrazione);
        DataRegistrazione dataRegistrazione = factory.createDataRegistrazione();

        dataRegistrazione.setContent(sdfShort.format(dataRicezione));//dai metadati

        identificatore.setDataRegistrazione(dataRegistrazione);

        intestazione.setIdentificatore(identificatore);
        //END IDENTIFICATORE

        //ORIGINE
        Origine origine = factory.createOrigine();
        IndirizzoTelematico indirizzoTelematico = factory.createIndirizzoTelematico();
        indirizzoTelematico.setContent("Sistema di Interscambio");
        indirizzoTelematico.setTipo("NMTOKEN");
        //indirizzoTelematico.setNote("");
        origine.setIndirizzoTelematico(indirizzoTelematico);

        Mittente mittente = factory.createMittente();

        Amministrazione amministrazione = factory.createAmministrazione();
        Denominazione denominazione = factory.createDenominazione();
        denominazione.setContent("MINISTERO ECONOMIA E FINANZE");
        amministrazione.setDenominazione(denominazione);

        UnitaOrganizzativa unitaOrganizzativa = factory.createUnitaOrganizzativa();
        Denominazione denominazioneUnita = factory.createDenominazione();
        denominazioneUnita.setContent("Sistema di Interscambio");
        unitaOrganizzativa.setTipo("permanente");
        unitaOrganizzativa.setDenominazione(denominazioneUnita);
        Identificativo identificativo = factory.createIdentificativo();
        identificativo.setContent("SDI");
        unitaOrganizzativa.setIdentificativo(identificativo);

        amministrazione.setUnitaOrganizzativa(unitaOrganizzativa);

        IndirizzoPostale indirizzoPostale = factory.createIndirizzoPostale();
        Denominazione denominazione1 = factory.createDenominazione();
        denominazione1.setContent("--");
        indirizzoPostale.setDenominazione(denominazione1);

        amministrazione.setIndirizzoPostale(indirizzoPostale);
        mittente.setAmministrazione(amministrazione);

        AOO aoo = factory.createAOO();
        Denominazione denominazione2 = factory.createDenominazione();
        denominazione2.setContent("MINISTERO ECONOMIA E FINANZE");
        aoo.setDenominazione(denominazione2);

        mittente.setAOO(aoo);

        origine.setMittente(mittente);

        intestazione.setOrigine(origine);

        Destinazione destinazione = factory.createDestinazione();
        IndirizzoTelematico indirizzoTelematicoDest = factory.createIndirizzoTelematico();
        indirizzoTelematicoDest.setContent(nomeMittente + " - Cod Uff: " + (String) exchange.getIn().getHeader("codiceUfficio")); //FAtturaPA:  campo 1.2.1.3.1 Denominazione - [fattura pa- campo 1.1.4 Codice destinatario]
        indirizzoTelematicoDest.setTipo("NMTOKEN");

        destinazione.setIndirizzoTelematico(indirizzoTelematicoDest);

        intestazione.getDestinazione().addAll(Arrays.asList(destinazione));

        Oggetto oggetto = factory.createOggetto();
        oggetto.setContent("File " + nomeFile + ", Identificativo SDI " + identificativoSdi);
        intestazione.setOggetto(oggetto);

        segnatura.setIntestazione(intestazione);
        //END INTESTAZIONE

        Descrizione descrizione = factory.createDescrizione();
        Documento documento = factory.createDocumento();
        documento.setNome("Invio file " + nomeFile + " con identificativo " + identificativoSdi + ", destinato all'ufficio con CodiceDestinatario " + (String) exchange.getIn().getHeader("codiceUfficio"));
        // documento.setTipoRiferimento("MIME");
        descrizione.setDocumento(documento);

        Documento documentoFattura = factory.createDocumento();
        documentoFattura.setNome(nomeFile);
        documentoFattura.setTipoRiferimento("MIME");

        Allegati allegati = factory.createAllegati();
        if (StringUtils.isNotEmpty(nomeMetadati)) {
            Documento documentoMetadati = factory.createDocumento();
            documentoMetadati.setNome(nomeMetadati);
            documentoMetadati.setTipoRiferimento("MIME");
            allegati.getDocumentoOrFascicolo().add(documentoMetadati);
        }

        allegati.getDocumentoOrFascicolo().add(documentoFattura);

        descrizione.setAllegati(allegati);

        segnatura.setDescrizione(descrizione);
        segnatura.setVersione("2009-12-03");
        segnatura.setXmlLang("it");

        segnaturaEnvelopeType.setSegnatura(segnatura);

        factory.createSegnaturaEnvelope(segnaturaEnvelopeType);

        params.add(segnaturaEnvelopeType);

        params.add(creaAllegatoZip(file, nomeFile, fileMetadati, nomeMetadati).toByteArray());

        exchange.getIn().setBody(params);

    }


    public static ByteArrayOutputStream creaAllegatoZip(String contenutoFile, String nomeFile, String contenutoFileMetadati, String nomeFileMetadati) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(nomeFile);
            zos.putNextEntry(entry);
            zos.write(contenutoFile.getBytes(), 0, contenutoFile.length());
            zos.closeEntry();

            //Solo in caso di fattura
            if (StringUtils.isNotEmpty(contenutoFileMetadati)) {
                ZipEntry entryMetadati = new ZipEntry(nomeFileMetadati);
                zos.putNextEntry(entryMetadati);
                zos.write(contenutoFileMetadati.getBytes(StandardCharsets.UTF_8), 0, contenutoFileMetadati.length());
                zos.closeEntry();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return baos;
    }


    private static byte[] creaAllegati(String fileFattura) {
        byte[] bytes = fileFattura.getBytes(StandardCharsets.UTF_8);

        return bytes;
    }
}
