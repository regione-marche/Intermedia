package it.extrasys.marche.regione.fatturapa.mock.agid.ca;

import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.Documento;
import it.marche.regione.intermediamarche.fatturazione.protocollazione.services.SegnaturaEnvelopeType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

import java.io.FileOutputStream;

public class RicevutaConsegna implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList mcl = (MessageContentsList) exchange.getIn().getBody();

        SegnaturaEnvelopeType set = (SegnaturaEnvelopeType) mcl.get(0);

        byte[] allegati = (byte[]) mcl.get(1);

        String nomeZip = ((Documento) set.getSegnatura().getDescrizione().getAllegati().getDocumentoOrFascicolo().get(0)).getNome();
        FileOutputStream fos = new FileOutputStream((String) exchange.getIn().getHeader("pathZip") + nomeZip + ".zip");
        fos.write(allegati);
        fos.flush();
        fos.close();
    }
}
