/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package it.extrasys.marche.regione.fatturapa.mock.paleo.ca;

import it.marche.regione.paleo.services.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloPaleoProcessor implements Processor {

    private static final transient Logger LOG = LoggerFactory.getLogger(ProtocolloPaleoProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();
        String operationName = message.getHeader(CxfConstants.OPERATION_NAME, String.class);

        LOG.info("********************* Dentro il processor [" + operationName + "]");

        ObjectFactory factory = new ObjectFactory();

        if("GetDocumentiProtocolliInFascicolo".equals(operationName)){
        LOG.info("********************* Ramo GetDocumentiProtocolliInFascicolo");

            message.setBody(factory.createGetDocumentiProtocolliInFascicoloResponse().getGetDocumentiProtocolliInFascicoloResult());
        }

        else if("ProtocollazioneEntrata".equals(operationName)){

        LOG.info("********************* Ramo ProtocollazioneEntrata");

            message.setBody(factory.createProtocollazioneEntrataResponse().getProtocollazioneEntrataResult());
        }

        else if("ProtocollazionePartenza".equals(operationName)){

        LOG.info("********************* Ramo ProtocollazionePartenza");

            message.setBody(factory.createProtocollazionePartenzaResponse().getProtocollazionePartenzaResult());
        }

        else if("CercaDocumentoProtocollo".equals(operationName)){

        LOG.info("********************* Ramo CercaDocumentoProtocollo");

            message.setBody(factory.createCercaDocumentoProtocolloResponse());
        }

        else if("FindRubricaExt".equals(operationName)){

            LOG.info("********************* Ramo FindRubricaExt");

            message.setBody(factory.createFindRubricaExtResponse());
        }

        else {
            //default
            LOG.info("********************* Ramo DEFAULT");

            message.setBody("DEFAULT");
        }
    }

}
