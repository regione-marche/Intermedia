package it.extrasys.marche.regione.fatturapa.enti.bridge.paleo.processors.fattura;

import it.marche.regione.paleo.services.ArrayOfUOInfo;
import it.marche.regione.paleo.services.BEListOfUOInfoZA0HwLp5;
import it.marche.regione.paleo.services.UOInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/03/15.
 */
public class RispostaGetUOProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);

        Set<String> UOset = new HashSet<String>();

        if(messageContentsList != null && messageContentsList.size() > 0 ){

            BEListOfUOInfoZA0HwLp5 beListOfUOInfo = (BEListOfUOInfoZA0HwLp5) messageContentsList.get(0);

            ArrayOfUOInfo arrayOfUOInfo = beListOfUOInfo.getLista().getValue();

            List<UOInfo> uoInfoList =  arrayOfUOInfo.getUOInfo();

            for(UOInfo uo : uoInfoList){
                UOset.add(uo.getCodice().getValue().trim());
            }
        }
        exchange.getIn().setBody(UOset);
    }
}
