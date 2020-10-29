package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.model.FatturaElettronicaWrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.*;
import org.apache.camel.Exchange;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.cms.CMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReinviaFatturaImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ReinviaFatturaImpl.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private MetadatiFatturaManager metadatiFatturaManager;
    private FileFatturaManager fileFatturaManager;
    private DatiFatturaManager datiFatturaManager;
    private EnteManager enteManager;
    private FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager;

    public void recuperaFatturaDalDatabase(Exchange exchange) throws FatturaPAFatturaNonTrovataException, FatturaPAEnteNonTrovatoException, FatturaPAException, FatturaPaPersistenceException, IOException, JAXBException, SAXException, ParserConfigurationException, CMSException, XPathExpressionException, TransformerException {
        BigInteger identificativoSdI = new BigInteger((String) exchange.getIn().getBody());

        FileFatturaEntity fileFattura = fileFatturaManager.getFileFatturaByIdentificativoSdi(identificativoSdI);

        String nomeFileFattura = fileFattura.getNomeFileFattura();
        String dataRicezioneSdI = sdf.format(fileFattura.getDataRicezione());


        MetadatiFatturaEntity metadatiFattura = metadatiFatturaManager.getMetadatiByIdentificativoSdi(identificativoSdI);

        DataSource dataSourceMetadati = null;
        DataHandler dataHandlerMetadati = null;

        List<DatiFatturaEntity> datiFattura = datiFatturaManager.getFatturaByIdentificativoSDI(identificativoSdI);

        String cedenteCodiceIva = datiFattura.get(0).getCedenteIdFiscaleIVA();
        String committenteCodiceIva = datiFattura.get(0).getCommittenteIdFiscaleIVA();

        byte[] fileMetadati = metadatiFattura.getContenutoFile();
        String nomeFileMetadati = metadatiFattura.getNomeFileMetadati();
        String codiceUfficio = metadatiFattura.getCodiceDestinatario();

        EnteEntity ente = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        dataSourceMetadati = new ByteArrayDataSource(fileMetadati, "application/xml");

        dataHandlerMetadati = new DataHandler(dataSourceMetadati);

        String metadati = getMetadati(dataHandlerMetadati);

        exchange.getIn().setHeader("vecchioWS", Boolean.FALSE);
        //Controlla se WS 'vecchio'
        if (TipoCanaleEntity.TIPO_CANALE.WS.getValue().equals(ente.getTipoCanale().getCodTipoCanale())) {
            exchange.getIn().setHeader("vecchioWS", Boolean.TRUE);
            exchange.getIn().setHeader("codaGestionale", ente.getCodaGestionaleWsCustomAvazato());
        } else if (TipoCanaleEntity.TIPO_CANALE.PEC.getValue().equals(ente.getTipoCanale().getCodTipoCanale())) { //PEC
            exchange.getIn().setHeader("invioUnico", Boolean.TRUE);
        } else { //Canale avanzato
            if (ente.getInvioUnico() != null && ente.getInvioUnico()) {
                exchange.getIn().setHeader("invioUnico", Boolean.TRUE);
            } else {
                exchange.getIn().setHeader("invioUnico", Boolean.FALSE);
                exchange.getIn().setHeader("codaGestionale", ente.getCodaGestionaleCa());
            }
        }
        exchange.getIn().setHeader("identificativoSdI", identificativoSdI);
        exchange.getIn().setHeader("nomeFile", nomeFileFattura);
        exchange.getIn().setHeader("nomeFileMetadati", nomeFileMetadati);
        exchange.getIn().setHeader("metadati", metadati);
        exchange.getIn().setHeader("cedenteCodiceIva", cedenteCodiceIva);
        exchange.getIn().setHeader("committenteCodiceIva", committenteCodiceIva);
        exchange.getIn().setHeader("codiceUfficio", codiceUfficio);
        exchange.getIn().setHeader("tipoMessaggio", "FatturaElettronica");
        exchange.getIn().setHeader("dataRicezioneSdI", dataRicezioneSdI);
        exchange.getIn().setHeader("idFiscaleCommittente", ente.getIdFiscaleCommittente());

        byte[] fatturaElettronicaBytesArray = fileFattura.getContenutoFile();

        String fatturaElettronica = FileUtils.getFatturaElettonicaSenzaFirma(nomeFileFattura, fatturaElettronicaBytesArray);

        if ((boolean) exchange.getIn().getHeader("onlyRegistrazione") == true) {
            String tipoEnte = checkTipoCodiceUfficio(exchange, codiceUfficio);

            if ("giunta".equalsIgnoreCase(tipoEnte) || "consiglio".equalsIgnoreCase(tipoEnte)) {
                FatturaElettronicaWrapper fatturaElettronicaWrapper = new FatturaElettronicaWrapper();
                fatturaElettronicaWrapper.setFatturaElettronica(fatturaElettronica);
                fatturaElettronicaWrapper.setSegnaturaProtocollo(datiFattura.get(0).getNumeroProtocollo());
                fatturaElettronicaWrapper.setEnteEntity(ente);
                exchange.getIn().setBody(fatturaElettronicaWrapper);
            } else if ("aziendeOspedaliere".equalsIgnoreCase(tipoEnte)) {
                exchange.getIn().setBody(fatturaElettronica, String.class);
            } else {
                LOG.error("L'ente con codice ufficio " + codiceUfficio + " non appartiene a nessun gruppo");
            }
        } else {
            exchange.getIn().setBody(fatturaElettronica, String.class);
        }

    }


    public void recuperaDecorrenzaTerminiDalDatabase(Exchange exchange) throws Exception {
        BigInteger identificativoSdI = new BigInteger((String) exchange.getIn().getBody());

        NotificaDecorrenzaTerminiEntity notificaDecorrenzaTermini = fatturazionePassivaNotificaDecorrenzaTerminiManager.getNotificaDecorrenzaTerminiByIdentificativoSDI(identificativoSdI);

        String nomeFileFattura = notificaDecorrenzaTermini.getNomeFile();
        String codiceUfficio = notificaDecorrenzaTermini.getCodiceUfficio();
        String committenteCodiceIva = notificaDecorrenzaTermini.getIdFiscaleCommittente();
        String nomeFileDecorrenzaTermini = FileUtils.getNomeDecorrenzaTerminiFromNomeFattura(nomeFileFattura);
        String dataRicezioneSdI = sdf.format(notificaDecorrenzaTermini.getDataRicezione());

        exchange.getIn().setHeader("identificativoSdI", identificativoSdI);
        exchange.getIn().setHeader("nomeFile", nomeFileDecorrenzaTermini);
        exchange.getIn().setHeader("codiceUfficio", codiceUfficio);
        exchange.getIn().setHeader("committenteCodiceIva", committenteCodiceIva);
        exchange.getIn().setHeader("tipoMessaggio", "NotificaDecorrenzaTermini");
        exchange.getIn().setHeader("dataRicezioneSdI", dataRicezioneSdI);

        byte[] decorrenzaTerminiEntityContenutoFile = notificaDecorrenzaTermini.getContenutoFile();

        //Controllo se
        if (Base64Utils.isBase64(decorrenzaTerminiEntityContenutoFile)) {
            decorrenzaTerminiEntityContenutoFile = Base64.decodeBase64(decorrenzaTerminiEntityContenutoFile);
        }

        /// Rimuovo la firma
        String decorrenzaTermini = XAdESUnwrapper.unwrap(decorrenzaTerminiEntityContenutoFile);
        // fine rimozione firma

        exchange.getIn().setBody(decorrenzaTermini, String.class);
    }


    public void recuperaEnte(Exchange exchange) throws FatturaPAException, FatturaPAEnteNonTrovatoException, FatturaPaPersistenceException {
        String codiceUfficio = (String) exchange.getIn().getHeader("codiceUfficio");
        EnteEntity ente = enteManager.getEnteByCodiceUfficio(codiceUfficio);

        if (ente.getInvioUnico()) {
            exchange.getIn().setHeader("invioUnico", Boolean.TRUE);
        } else {
            exchange.getIn().setHeader("invioUnico", Boolean.FALSE);
            exchange.getIn().setHeader("codaGestionale", ente.getCodaGestionaleCa());
        }
    }


    private static String getMetadati(DataHandler dh) throws IOException, JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] metadati;
        try {
            dh.writeTo(outputStream);
            metadati = outputStream.toByteArray();
            return new String(metadati);
        } finally {
            outputStream.reset();
            outputStream.close();
        }

        // InputStream is = dh.getInputStream();
        // byte[] bytes = IOUtils.toByteArray(is);
        // return new String(bytes);
    }


    private static String checkTipoCodiceUfficio(Exchange exchange, String codiceUfficio) {
        String giunta = (String) exchange.getIn().getHeader("giunta");
        String consiglio = (String) exchange.getIn().getHeader("consiglio");
        String aziendeOspedaliere = (String) exchange.getIn().getHeader("aziendeOspedaliere");
        if (giunta.contains(codiceUfficio)) {
            return "giunta";
        } else if (consiglio.contains(codiceUfficio)) {
            return "consiglio";
        } else if (aziendeOspedaliere.contains(codiceUfficio)) {
            return "aziendeOspedaliere";
        } else {
            return null;
        }
    }

    public MetadatiFatturaManager getMetadatiFatturaManager() {
        return metadatiFatturaManager;
    }

    public void setMetadatiFatturaManager(MetadatiFatturaManager metadatiFatturaManager) {
        this.metadatiFatturaManager = metadatiFatturaManager;
    }

    public FileFatturaManager getFileFatturaManager() {
        return fileFatturaManager;
    }

    public void setFileFatturaManager(FileFatturaManager fileFatturaManager) {
        this.fileFatturaManager = fileFatturaManager;
    }

    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public FatturazionePassivaNotificaDecorrenzaTerminiManager getFatturazionePassivaNotificaDecorrenzaTerminiManager() {
        return fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }

    public void setFatturazionePassivaNotificaDecorrenzaTerminiManager(FatturazionePassivaNotificaDecorrenzaTerminiManager fatturazionePassivaNotificaDecorrenzaTerminiManager) {
        this.fatturazionePassivaNotificaDecorrenzaTerminiManager = fatturazionePassivaNotificaDecorrenzaTerminiManager;
    }
}
