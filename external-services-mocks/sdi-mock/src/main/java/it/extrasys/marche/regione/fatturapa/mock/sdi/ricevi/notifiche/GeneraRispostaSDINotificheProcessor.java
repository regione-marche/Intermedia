package it.extrasys.marche.regione.fatturapa.mock.sdi.ricevi.notifiche;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.*;
import it.extrasys.marche.regione.fatturapa.mock.sdi.setup.ImpostaNotificaServiceImpl;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 23/02/15.
 */
public class GeneraRispostaSDINotificheProcessor implements SdIRiceviNotifica {

    @Override
    public RispostaSdINotificaEsitoType notificaEsito(FileSdIType parameters) {

        InputStream is = null;

        try {

            RispostaSdINotificaEsitoType returned = new RispostaSdINotificaEsitoType();
            returned.setEsito(EsitoNotificaType.ES_00);

            RispostaSdINotificaEsitoType notifica = ImpostaNotificaServiceImpl.getNotifica();

            File file =  new File(getClass().getClassLoader().getResource("esempi/IT01641790702_g1g4_SE_001.xml").getFile());

            is = new FileInputStream(file);

            byte[] scartoNotificaEC = IOUtils.toByteArray(is);

            FileSdIBaseType fileSdIBaseType = new FileSdIBaseType();

            DataSource notificaScartoECDs = new ByteArrayDataSource(scartoNotificaEC, "text/plain; charset=UTF-8");

            fileSdIBaseType.setFile(new DataHandler(notificaScartoECDs));
            fileSdIBaseType.setNomeFile("IT01641790702_g1g4_SE_001.xml");

            returned.setScartoEsito(fileSdIBaseType);

            notifica.setScartoEsito(fileSdIBaseType);

            if (notifica != null)
                return notifica;
            return returned;
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}