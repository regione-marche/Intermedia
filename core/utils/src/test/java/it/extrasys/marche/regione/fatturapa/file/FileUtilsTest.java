package it.extrasys.marche.regione.fatturapa.file;

import it.extrasys.marche.regione.fatturapa.core.utils.file.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by agosteeno on 23/03/16.
 */
public class FileUtilsTest {

    @Test
    public void testCasoOK(){

        String dlqQueue = "DLQ.enti.bridge.asur.registrazione.fatture.in.queue";

        String queue = "enti.bridge.asur.registrazione.fatture.in.queue";

        String queueCalculateName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueue);

        Assert.assertEquals(queue, queueCalculateName);
    }


    @Test
    public void testQueueNonDlq(){

        String dlqQueue = "enti.bridge.asur.registrazione.fatture.in.queue";

        String queueCalculateName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueue);

        Assert.assertEquals(null, queueCalculateName);
    }


    @Test
    public void testNomeDlqErrato1(){

        String dlqQueue = "dlq.enti.bridge.asur.registrazione.fatture.in.queue";


        String queueCalculateName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueue);

        Assert.assertEquals(null, queueCalculateName);
    }

    @Test
    public void testNomeDlqErrato2(){

        String dlqQueue = "DLq.enti.bridge.asur.registrazione.fatture.in.queue";

        String queueCalculateName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueue);

        Assert.assertEquals(null, queueCalculateName);
    }

    @Test
    public void testNomeDlqErrato3(){

        String dlqQueue = "DLQenti.bridge.asur.registrazione.fatture.in.queue";

        String queueCalculateName = FileUtils.estraiNomeOriginaleDaDlq(dlqQueue);

        Assert.assertEquals(null, queueCalculateName);
    }

    @Test
    public void testNomeDecorrenzaTermini() {

        String nomeFattura = "IT123456789_00001.xml";
        String nomeDecorrenzaAtteso = "IT123456789_00001_DT_001.xml";

        String nomeDecorrenzaCalcolato = FileUtils.getNomeDecorrenzaTerminiFromNomeFattura(nomeFattura);

        Assert.assertEquals(nomeDecorrenzaAtteso, nomeDecorrenzaCalcolato);
    }

    @Test
    public void testNomeRicevutaConsegnaFromNomeFileFattura(){
        String nomeFattura = "IT123456789_00001.xml";
        String nomeRicevutaConsegnaAtteso = "IT123456789_00001_RC_001.xml";

        String nomeRicevutaConsegnaCalcolato = FileUtils.getNomeRicevutaConsegnaFromNomeFileFattura(nomeFattura);

        System.out.println("Nome Ricevuta Consegna Calcolato: "+ nomeRicevutaConsegnaCalcolato);
        Assert.assertEquals(nomeRicevutaConsegnaAtteso,nomeRicevutaConsegnaCalcolato);
    }
}
