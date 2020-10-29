package it.extrasys.marche.regione.fatturapa.core.utils.file;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.CedentePrestatoreType;
import it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans.FatturaElettronicaType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.*;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import org.apache.geronimo.mail.util.Base64;

import javax.activation.DataHandler;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;

public class JaxBUtils {

    private static final String NAMESPACE_FATTURA_ELETTRONICA = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2";

    //**************************************************************************************************************
    //************************************************ UNMARSHALLER ************************************************
    //**************************************************************************************************************

    /**
     * REVO-3
     * <p>
     * inserita per avere un metodo generico per avere una fattura a partire dalla versione in formato String
     *
     * @param fatturaElettronica
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public static FatturaElettronicaType getFatturaElettronicaType(String fatturaElettronica) throws JAXBException, UnsupportedEncodingException {

        InputStream is = new ByteArrayInputStream(fatturaElettronica.getBytes());

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        JAXBElement<FatturaElettronicaType> jbe = (JAXBElement<FatturaElettronicaType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<FatturaElettronicaType> jbe = (JAXBElement<FatturaElettronicaType>) jaxbUnmarshaller.unmarshal(is);

        FatturaElettronicaType fatturaElettronicaType = jbe.getValue();

        return fatturaElettronicaType;
    }

    /**
     * @param dh
     * @return
     * @throws java.io.IOException
     * @throws javax.xml.bind.JAXBException
     */
    public static FatturaElettronicaType getFatturaElettronica(DataHandler dh) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(dh.getInputStream(), "UTF-8");
        JAXBElement<FatturaElettronicaType> jbe = (JAXBElement<FatturaElettronicaType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<FatturaElettronicaType> jbe = (JAXBElement<FatturaElettronicaType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        FatturaElettronicaType fatturaElettronicaType = jbe.getValue();

        return fatturaElettronicaType;
    }

    public static RicevutaConsegnaType getRicevutaConsegna(String originalMessage) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        InputStream in = new ByteArrayInputStream(originalMessage.getBytes(Charset.forName("UTF-8")));

        //REVO-17
        Reader reader = new InputStreamReader(in, "UTF-8");
        JAXBElement<RicevutaConsegnaType> jbe = (JAXBElement<RicevutaConsegnaType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<RicevutaConsegnaType> jbe = (JAXBElement<RicevutaConsegnaType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        RicevutaConsegnaType ricevutaConsegnaType = jbe.getValue();

        return ricevutaConsegnaType;
    }

    public static NotificaDecorrenzaTerminiType getDecorrenzaTermini(String originalMessage) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        InputStream in = new ByteArrayInputStream(originalMessage.getBytes(Charset.forName("UTF-8")));

        //REVO-17
        Reader reader = new InputStreamReader(in, "UTF-8");
        JAXBElement<NotificaDecorrenzaTerminiType> jbe = (JAXBElement<NotificaDecorrenzaTerminiType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<RicevutaConsegnaType> jbe = (JAXBElement<RicevutaConsegnaType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        NotificaDecorrenzaTerminiType decorrenzaTerminiType = jbe.getValue();

        return decorrenzaTerminiType;
    }

    public static NotificaEsitoCommittenteType getNotificaEsitoCommittenteType(byte[] notificaEsito) throws IOException, JAXBException {

        InputStream is = new ByteArrayInputStream(notificaEsito);

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        JAXBElement<NotificaEsitoCommittenteType> jbe = (JAXBElement<NotificaEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(reader);
//      JAXBElement<NotificaEsitoCommittenteType> jbe = (JAXBElement<NotificaEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(is);

        NotificaEsitoCommittenteType notificaEsitoCommittenteType = jbe.getValue();

        return notificaEsitoCommittenteType;
    }

    public static NotificaEsitoCommittenteType getNotificaEsitoCommittenteType(DataHandler dh) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(dh.getInputStream(), "UTF-8");
        JAXBElement<NotificaEsitoCommittenteType> jbe = (JAXBElement<NotificaEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(reader);

        NotificaEsitoCommittenteType notificaEsitoCommittenteType = jbe.getValue();

        return notificaEsitoCommittenteType;
    }

    public static CedentePrestatoreType getCedentePrestatoreType(byte[] cedente) throws IOException, JAXBException {

        InputStream is = new ByteArrayInputStream(cedente);

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        JAXBElement<CedentePrestatoreType> jbe = jaxbUnmarshaller.unmarshal(new StreamSource(reader), CedentePrestatoreType.class);
        //JAXBElement<CedentePrestatoreType> jbe = jaxbUnmarshaller.unmarshal(new StreamSource(is), CedentePrestatoreType.class);

        CedentePrestatoreType cedentePrestatoreType = jbe.getValue();

        return cedentePrestatoreType;
    }

    public static RicevutaConsegnaType getRicevutaConsegna(DataHandler dh) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(dh.getInputStream(), "UTF-8");
        JAXBElement<RicevutaConsegnaType> jbe = (JAXBElement<RicevutaConsegnaType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<RicevutaConsegnaType> jbe = (JAXBElement<RicevutaConsegnaType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        RicevutaConsegnaType ricevutaConsegnaType = jbe.getValue();

        return ricevutaConsegnaType;
    }

    public static NotificaDecorrenzaTerminiType getNotificaDecorrenza(DataHandler dh) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(dh.getInputStream(), "UTF-8");
        JAXBElement<NotificaDecorrenzaTerminiType> jbe = (JAXBElement<NotificaDecorrenzaTerminiType>) jaxbUnmarshaller.unmarshal(reader);
//      JAXBElement<NotificaDecorrenzaTerminiType> jbe = (JAXBElement<NotificaDecorrenzaTerminiType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        NotificaDecorrenzaTerminiType notificaDecorrenzaTerminiType = jbe.getValue();

        return notificaDecorrenzaTerminiType;
    }

    /**
     * @param metadati
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public static MetadatiInvioFileType getMetadati(String metadati) throws IOException, JAXBException {

        InputStream is = new ByteArrayInputStream(metadati.getBytes());

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        JAXBElement<MetadatiInvioFileType> jbe = (JAXBElement<MetadatiInvioFileType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<MetadatiInvioFileType> jbe = (JAXBElement<MetadatiInvioFileType>) jaxbUnmarshaller.unmarshal(is);

        MetadatiInvioFileType metadatiInvioFileType = jbe.getValue();

        return metadatiInvioFileType;
    }

    public static ScartoEsitoCommittenteType getScartoEsito(DataHandler dh) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(dh.getInputStream(), "UTF-8");
        JAXBElement<ScartoEsitoCommittenteType> jbe = (JAXBElement<ScartoEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<ScartoEsitoCommittenteType> jbe = (JAXBElement<ScartoEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        ScartoEsitoCommittenteType scartoEsitoType = jbe.getValue();

        return scartoEsitoType;
    }

    public static ScartoEsitoCommittenteType getScartoEsito(String scartoEsito) throws IOException, JAXBException {

        InputStream is = new ByteArrayInputStream(scartoEsito.getBytes());

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        JAXBElement<ScartoEsitoCommittenteType> jbe = (JAXBElement<ScartoEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<ScartoEsitoCommittenteType> jbe = (JAXBElement<ScartoEsitoCommittenteType>) jaxbUnmarshaller.unmarshal(dh.getInputStream());

        ScartoEsitoCommittenteType scartoEsitoType = jbe.getValue();

        return scartoEsitoType;
    }

    private NotificaScartoType getNotificaScarto(String notificaScarto) throws JAXBException, UnsupportedEncodingException {

        InputStream is = new ByteArrayInputStream(notificaScarto.getBytes());

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        JAXBElement<NotificaScartoType> jbe = (JAXBElement<NotificaScartoType>) jaxbUnmarshaller.unmarshal(reader);
        //JAXBElement<NotificaScartoType> jbe = (JAXBElement<NotificaScartoType>) jaxbUnmarshaller.unmarshal(is);

        NotificaScartoType notificaScartoType = jbe.getValue();

        return notificaScartoType;
    }

    public static EsitoFatturaMessageRequest getEsitoFatturaCA(String esitoFattura) throws IOException, JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        InputStream is = new ByteArrayInputStream(esitoFattura.getBytes(Charset.forName("UTF-8")));

        //REVO-17
        Reader reader = new InputStreamReader(is, "UTF-8");
        EsitoFatturaMessageRequest esitoFatturaMessageRequest = null;

        try{
            esitoFatturaMessageRequest = (EsitoFatturaMessageRequest) jaxbUnmarshaller.unmarshal(reader);
        }catch (Exception e){
            JAXBElement<EsitoFatturaMessageRequest> jbe = (JAXBElement<EsitoFatturaMessageRequest>) jaxbUnmarshaller.unmarshal(reader);
            esitoFatturaMessageRequest = jbe.getValue();
        }

        return esitoFatturaMessageRequest;
    }

    //**************************************************************************************************************
    //**************************************************************************************************************


    //**************************************************************************************************************
    //************************************************ MARSHALLER **************************************************
    //**************************************************************************************************************

    public Object getNotificaDecorrenzaTerminiAsString(NotificaDecorrenzaTerminiType notificaDecorrenzaTermini) throws JAXBException {

        StringWriter stringWriter = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        QName qName = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaDecorrenzaTermini");

        JAXBElement<NotificaDecorrenzaTerminiType> root = new JAXBElement<NotificaDecorrenzaTerminiType>(qName, NotificaDecorrenzaTerminiType.class, notificaDecorrenzaTermini);

        jaxbMarshaller.marshal(root, stringWriter);

        return stringWriter.toString();
    }

    /**
     * @param fatturaElettronica
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public static String getFatturaElettronicaAsString(FatturaElettronicaType fatturaElettronica) throws JAXBException {

        StringWriter stringWriter = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.fatturazione.elettronica.beans");

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        QName qName = new QName(NAMESPACE_FATTURA_ELETTRONICA, "FatturaElettronica");

        JAXBElement<FatturaElettronicaType> root = new JAXBElement<FatturaElettronicaType>(qName, FatturaElettronicaType.class, fatturaElettronica);

        jaxbMarshaller.marshal(root, stringWriter);

        return stringWriter.toString();
    }

    //**************************************************************************************************************
    //**************************************************************************************************************
}