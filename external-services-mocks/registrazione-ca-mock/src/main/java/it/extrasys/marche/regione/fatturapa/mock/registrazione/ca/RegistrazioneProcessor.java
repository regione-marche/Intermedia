/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package it.extrasys.marche.regione.fatturapa.mock.registrazione.ca;

import it.marche.regione.intermediamarche.fatturazione.registrazione.services.fattura.EsitoFatturaResponseCodeType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.fattura.EsitoFatturaResponseDescriptionType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.fattura.EsitoFatturaType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.EsitoNotificaResponseCodeType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.EsitoNotificaResponseDescriptionType;
import it.marche.regione.intermediamarche.fatturazione.registrazione.services.notifica.EsitoNotificaType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrazioneProcessor implements Processor {

    private static final transient Logger LOG = LoggerFactory.getLogger(RegistrazioneProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();
        String operationName = message.getHeader(CxfConstants.OPERATION_NAME, String.class);

        LOG.info("********************* Dentro il processor [" + operationName + "]");

        switch (operationName){

            case "RiceviFattura":

                EsitoFatturaType responseFattura = new EsitoFatturaType();
                responseFattura.setCodice(EsitoFatturaResponseCodeType.EF_00);
                responseFattura.setDescrizione(EsitoFatturaResponseDescriptionType.FATTURA_PRESA_IN_CARICO);

                message.setBody(responseFattura);

                break;

            case "RiceviNotifica":

                EsitoNotificaType responseNotifica = new EsitoNotificaType();
                responseNotifica.setCodice(EsitoNotificaResponseCodeType.EN_00);
                responseNotifica.setDescrizione(EsitoNotificaResponseDescriptionType.NOTIFICA_PRESA_IN_CARICO);

                message.setBody(responseNotifica);

                break;
        }
    }
}