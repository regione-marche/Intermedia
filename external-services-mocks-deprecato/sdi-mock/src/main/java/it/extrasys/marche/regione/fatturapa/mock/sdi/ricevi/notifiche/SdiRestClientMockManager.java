package it.extrasys.marche.regione.fatturapa.mock.sdi.ricevi.notifiche;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.FileSdIConMetadatiType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.RicezioneFatture;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevifatture.beans.RispostaRiceviFattureType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by agosteeno on 10/03/15.
 */
public class SdiRestClientMockManager {

    private static final Logger LOG = LoggerFactory.getLogger(SdiRestClientMockManager.class);

    private static final String TIPO_MESSAGGIO = "tipoMessaggio";
    private static final String FATTURA = "fattura";
    private static final String METADATI = "metadati";
    private static final String NOTIFICA_DECORRENZA = "notificaDecorrenza";
    private static final String NOME_FILE_FATTURA = "nomeFileFattura";
    private static final String NOME_FILE_METADATI = "nomeFileMetadati";
    private static final String NOME_FILE_NOTIFICA_DECORRENZA = "nomeFileNotificaDecorrenza";
    private static final String IDENTIFICATIVO_SDI = "identificativoSdi";

    private String indirizzoServizioCxf;

    public void sendCxfMessage(Exchange msgExchange) {

        Message msg = msgExchange.getIn();

        String tipoMessaggio = (String) msg.getHeader(TIPO_MESSAGGIO);
        String identificativoSdi = (String) msg.getHeader(IDENTIFICATIVO_SDI);

        if (tipoMessaggio == null || "".equals(tipoMessaggio)) {
            msg.setBody("DATI INCOMPLETI: header 'tipoMessaggio' e' obbligatorio (valori possibili: fattura/notificaDecorrenza)");
            return;
        }

        if (identificativoSdi == null || "".equals(identificativoSdi)) {
            msg.setBody("DATI INCOMPLETI: header 'identificativoSDI' e' obbligatorio");
            return;
        }

        try {

            if (FATTURA.equals(tipoMessaggio)) {

                String fattura = (String) msg.getBody();
                String metadati = (String) msg.getHeader(METADATI);
                String nomeFileFattura = (String) msg.getHeader(NOME_FILE_FATTURA);
                String nomeFileMedatadi = (String) msg.getHeader(NOME_FILE_METADATI);

                if (fattura == null || "".equals(fattura)) {
                    msg.setBody("DATI INCOMPLETI: body deve contenere il file della fattura");
                    return;
                }

                if (metadati == null || "".equals(metadati)) {
                    msg.setBody("DATI INCOMPLETI: header 'metadati' deve contenere il file dei metadati");
                    return;
                }

                if (nomeFileFattura == null || "".equals(nomeFileFattura)) {
                    msg.setBody("DATI INCOMPLETI: header 'nomeFileFattura' deve contenere il nome del file fattura");
                    return;
                }

                if (nomeFileMedatadi == null || "".equals(nomeFileMedatadi)) {
                    msg.setBody("DATI INCOMPLETI: header 'nomeFileMedatadi' deve contenere il nome del file metadati");
                    return;
                }

                RispostaRiceviFattureType rispostaFattura = (RispostaRiceviFattureType) inviaFatturaCXF(fattura, metadati, nomeFileFattura, nomeFileMedatadi, identificativoSdi);


            } else if (NOTIFICA_DECORRENZA.equals(tipoMessaggio)) {

                String notificaDecorrenza = (String) msg.getBody();
                String nomeFileNotificaDecorrenza = (String) msg.getHeader(NOME_FILE_NOTIFICA_DECORRENZA);

                if (notificaDecorrenza == null || "".equals(notificaDecorrenza)) {

                    msg.setBody("DATI INCOMPLETI: body deve contenere il file della notifica decorrenza termini");
                }

                if (nomeFileNotificaDecorrenza == null || "".equals(nomeFileNotificaDecorrenza)) {
                    msg.setBody("DATI INCOMPLETI: header 'nomeFileNotificaDecorrenza' deve contenere il nome del file notifica decorrenza termini");
                    return;
                }

                inviaDecorrenzaTerminiCXF(notificaDecorrenza, nomeFileNotificaDecorrenza, identificativoSdi);

            } else {
                msg.setBody("DATI ERRATI: header 'tipoMessaggio' valori possibili: fattura/notificaDecorrenza. Ora e' " + tipoMessaggio);
                return;
            }
        } catch (Exception e) {
            LOG.error("SdiRestClientMockManager - sendCxfMessage: catturata eccezione " + e.getMessage() + e.getStackTrace(), e);

            msg.setBody("catturata eccezione " + e.getMessage() + e.getStackTrace());
        }

    }

    private void inviaDecorrenzaTerminiCXF(String notificaDecorrenza, String nomeFileDecorrenza, String identificavoSdi) throws IOException {

        //DataSource notificaDecorrenzaTermini = new FileDataSource(new File(this.getClass().getResource("/cxfEndpoint/notificaDecorrenzaTermini.xml").getPath()));

        DataSource notificaDecorrenzaTermini = new ByteArrayDataSource(notificaDecorrenza, "text/plain; charset=UTF-8");

        FileSdIType fileSdIType = new FileSdIType();

        fileSdIType.setIdentificativoSdI(new BigInteger(identificavoSdi));
        fileSdIType.setFile(new DataHandler(notificaDecorrenzaTermini));
        fileSdIType.setNomeFile(nomeFileDecorrenza);

        // create the webservice client and send the request
        RicezioneFatture client = createCXFClient(indirizzoServizioCxf);

        client.notificaDecorrenzaTermini(fileSdIType);

        return;

    }

    private Object inviaFatturaCXF(String fattura, String metadati, String nomeFileFattura, String nomeFileMetadati, String identificavoSdi) throws IOException {

        // create input parameter

        DataSource fatturapa = new ByteArrayDataSource(fattura, "text/plain; charset=UTF-8");

        DataSource metadatipa = new ByteArrayDataSource(metadati, "text/plain; charset=UTF-8");


        FileSdIConMetadatiType fileSdIConMetadatiType = new FileSdIConMetadatiType();

        fileSdIConMetadatiType.setIdentificativoSdI(new BigInteger(identificavoSdi));
        fileSdIConMetadatiType.setFile(new DataHandler(fatturapa));
        fileSdIConMetadatiType.setMetadati(new DataHandler(metadatipa));
        fileSdIConMetadatiType.setNomeFile(nomeFileFattura);
        fileSdIConMetadatiType.setNomeFileMetadati(nomeFileMetadati);

        RicezioneFatture client = createCXFClient(indirizzoServizioCxf);
        Object out = client.riceviFatture(fileSdIConMetadatiType);
        return out;
    }

    private static RicezioneFatture createCXFClient(String url) {
        // we use CXF to create a client for us as its easier than JAXWS and works
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("mtom-enabled", Boolean.TRUE);
        factory.setProperties(props);

        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(RicezioneFatture.class);
        factory.setAddress(url);
        return (RicezioneFatture) factory.create();
    }

    public String getIndirizzoServizioCxf() {
        return indirizzoServizioCxf;
    }

    public void setIndirizzoServizioCxf(String indirizzoServizioCxf) {
        this.indirizzoServizioCxf = indirizzoServizioCxf;
    }
}
