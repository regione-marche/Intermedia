package it.extrasys.marche.regione.fatturapa.services.notificaesito;

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoCommittenteType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.NotificaEsitoType;
import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.ObjectFactory;
import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.file.JaxBUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.TipoNotificaAttivaFromSdiEntity;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;


/**
 * Created by agosteeno on 17/08/15.
 */
public class NotificaEsitoFlussoSemplificatoProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(NotificaEsitoFlussoSemplificatoProcessor.class);

    private static final String TIPO_MESSAGGIO_HEADER = "tipoMessaggio";
    private static final String NOTIFICA_ESITO_COMMITTENTE_ORIGINALE_HEADER = "notificaEsitoCommittenteOriginale";
    private static final String NOME_FILE_HEADER = "nomeFile";
    private static final String FILENAME_ORIGINALE_HEADER = "fileNameOriginale";
    private static final String NOME_FILE_FATTURA = "nomeFileFattura";

    @Override
    public void process(Exchange exchange) throws Exception {

        String notificaEsitoCommittenteOriginaleHeader = (String) exchange.getIn().getHeader(NOTIFICA_ESITO_COMMITTENTE_ORIGINALE_HEADER);
        byte[] messaggioOriginaleByteArray = Base64.decode(notificaEsitoCommittenteOriginaleHeader);

        String nomeFile = (String) exchange.getIn().getHeader(NOME_FILE_HEADER);

        NotificaEsitoCommittenteType notificaEsitoCommittenteType = JaxBUtils.getNotificaEsitoCommittenteType(messaggioOriginaleByteArray);

        String fileNameOriginaleHeader = (String) exchange.getIn().getHeader(FILENAME_ORIGINALE_HEADER);

        String nomeFileFatturaHeader = (String) exchange.getIn().getHeader(NOME_FILE_FATTURA);

        String nomeFileOriginale = "";

        if(nomeFileFatturaHeader != null && !"".equals(nomeFileFatturaHeader)){
            nomeFileOriginale = nomeFileFatturaHeader;
        } else if(fileNameOriginaleHeader != null && !"".equals(fileNameOriginaleHeader)){
            nomeFileOriginale = fileNameOriginaleHeader;
        }

        String nomeNotificaEsito = new String (nomeFileOriginale);

        ObjectFactory objectFactory = new ObjectFactory();

        NotificaEsitoType notificaEsitoType = objectFactory.createNotificaEsitoType();
        notificaEsitoType.setEsitoCommittente(notificaEsitoCommittenteType);
        notificaEsitoType.setIdentificativoSdI(notificaEsitoCommittenteType.getIdentificativoSdI());
        notificaEsitoType.setNomeFile(nomeFileOriginale);

        String tipoMessaggio = "";
        tipoMessaggio = TipoNotificaAttivaFromSdiEntity.TIPO_NOTIFICA_FROM_SDI.NOTIFICA_ESITO.getValue();

        exchange.getIn().setHeader(TIPO_MESSAGGIO_HEADER, tipoMessaggio);

        String notificaEsitoString = getNotificaEsitoAsString(notificaEsitoType);
        String notificaEsitoEncoded = new String(Base64.encode(notificaEsitoString.getBytes()));

        String estensioneFile = FileUtils.getFileExtension(nomeNotificaEsito);

        if("p7m".equalsIgnoreCase(estensioneFile)){

            //2 volte perche' se l'estensione e' p7m ci sono 2 punti ( .xml.p7m )

            nomeNotificaEsito = FileUtils.removeFileExtension(nomeNotificaEsito);

            nomeNotificaEsito = FileUtils.removeFileExtension(nomeNotificaEsito);

        } else{

            nomeNotificaEsito = FileUtils.removeFileExtension(nomeNotificaEsito);
        }

        nomeNotificaEsito = nomeNotificaEsito + "_NE_001.xml";


        LOG.info("NotificaEsitoFlussoSemplificatoProcessor: nome notifica esito creato " + nomeNotificaEsito);

        exchange.getIn().setHeader(NOME_FILE_HEADER, nomeNotificaEsito);

        exchange.getIn().setBody(notificaEsitoEncoded);

    }

    private static String getNotificaEsitoAsString(NotificaEsitoType notificaEsitoType) throws IOException, JAXBException {

        StringWriter stringWriter = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance("it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans");

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        QName qName = new QName("http://www.fatturapa.gov.it/sdi/messaggi/v1.0", "NotificaEsito");

        JAXBElement<NotificaEsitoType> root = new JAXBElement<NotificaEsitoType>(qName, NotificaEsitoType.class, notificaEsitoType);

        jaxbMarshaller.marshal(root, stringWriter);

        return stringWriter.toString();
    }


}
