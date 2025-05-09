package it.extrasys.marche.regione.fatturapa.patch.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.Base64Utils;
import it.extrasys.marche.regione.fatturapa.core.utils.signature.XAdESUnwrapper;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.NotificaDecorrenzaTerminiEntity;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

/**
 * Created by agosteeno on 11/09/15.
 */
public class Regma122InviaDecorrenzaTerminiDaDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(Regma122InviaFatturaDaDatabase.class);
    private static final String IDENTIFICATIVO_SDI_HEADER = "identificativoSdI";

    private final static String queryRetrieveDecorrenzaTermini = "SELECT dt FROM NotificaDecorrenzaTerminiEntity dt WHERE dt.identificativoSdi = :identificativoSdi";

    private EntityManagerFactory entityManagerFactory;

    public void prelevaDecorrenzaTerminiDalDatabase(Exchange exchange) throws IOException, ParserConfigurationException, TransformerException, SAXException, XPathExpressionException {

        Message message = exchange.getIn();

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String identificativoSdiString = (String) message.getBody();

        LOG.info("Regma122InviaFatturaDaDatabase - prelevaFatturaDalDatabase: identificativoSdI = " + identificativoSdiString);

        if(identificativoSdiString != null && !"".equals(identificativoSdiString)) {

            message.setHeader(IDENTIFICATIVO_SDI_HEADER, identificativoSdiString.trim());

            BigInteger identificativoSdi = new BigInteger(identificativoSdiString.trim());

            TypedQuery<NotificaDecorrenzaTerminiEntity> typedQuery = entityManager.createQuery(queryRetrieveDecorrenzaTermini, NotificaDecorrenzaTerminiEntity.class);
            typedQuery.setParameter("identificativoSdi", identificativoSdi);

            NotificaDecorrenzaTerminiEntity decorrenzaTerminiEntity = typedQuery.getSingleResult();

            String nomeFileFattura = decorrenzaTerminiEntity.getNomeFile();

            String codiceUfficio = decorrenzaTerminiEntity.getCodiceUfficio();
            String committenteCodiceIva = decorrenzaTerminiEntity.getIdFiscaleCommittente();

            String nomeFileDecorrenzaTermini = FileUtils.getNomeDecorrenzaTerminiFromNomeFattura(nomeFileFattura);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dataRicezioneSdI = sdf.format(decorrenzaTerminiEntity.getDataRicezione());


            message.setHeader("nomeFile", nomeFileDecorrenzaTermini);
            message.setHeader("codiceUfficio", codiceUfficio);
            message.setHeader("committenteCodiceIva", committenteCodiceIva);
            message.setHeader("tipoMessaggio", "NotificaDecorrenzaTermini");
            message.setHeader("dataRicezioneSdI", dataRicezioneSdI);

            byte[] decorrenzaTerminiEntityContenutoFile = decorrenzaTerminiEntity.getContenutoFile();

            //Controllo se
            if (Base64Utils.isBase64(decorrenzaTerminiEntityContenutoFile)) {
                decorrenzaTerminiEntityContenutoFile = Base64.decodeBase64(decorrenzaTerminiEntityContenutoFile);
            }

            /// Rimuovo la firma
            String decorrenzaTermini = "";


            decorrenzaTermini = XAdESUnwrapper.unwrap(decorrenzaTerminiEntityContenutoFile);

            // fine rimozione firma

            message.setBody(decorrenzaTermini, String.class);
        } else {
            LOG.error("Regma122InviaFatturaDaDatabase - prelevaFatturaDalDatabase: identificativoSdI vuoto o NULL");
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
}
