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
package it.extrasys.marche.regione.fatturapa.mock.registrazione.attiva.ca;

import it.extrasys.marche.regione.fatturapa.contracts.inoltro.notifiche.fatturazione.attiva.ca.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.notifiche.fatturazione.attiva.ca.beans.NotificaAttivaEsitoNotificaResponseCodeType;
import it.extrasys.marche.regione.fatturapa.contracts.inoltro.notifiche.fatturazione.attiva.ca.beans.NotificaAttivaEsitoNotificaResponseDescrizioneType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrazioneAttivaProcessor implements Processor {

    private static final transient Logger LOG = LoggerFactory.getLogger(RegistrazioneAttivaProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();
        String operationName = message.getHeader(CxfConstants.OPERATION_NAME, String.class);

        LOG.info("********************* Dentro il processor [" + operationName + "]");

        EsitoNotificaType responseNotifica = new EsitoNotificaType();
        responseNotifica.setCodice(NotificaAttivaEsitoNotificaResponseCodeType.NA_00);
        responseNotifica.setDescrizione(NotificaAttivaEsitoNotificaResponseDescrizioneType.NOTIFICA_PRESA_IN_CARICO);

        message.setBody(responseNotifica);
    }
}