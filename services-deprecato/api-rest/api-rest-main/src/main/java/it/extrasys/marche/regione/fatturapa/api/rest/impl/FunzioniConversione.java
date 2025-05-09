package it.extrasys.marche.regione.fatturapa.api.rest.impl;

import it.extrasys.marche.regione.fatturapa.api.rest.model.*;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAConfigurazioneNonTrovataException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.*;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.EndpointAttivaCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.attiva.FatturaAttivaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.UtenteEntity;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunzioniConversione {

    public static String protocollo = "PC_Integrazione Sistema Protocollo";
    public static String registrazione = "PC_Integrazione Sistema Registrazione";
    public static String invioUnico = "PC_Invio Unico";
    public static String esitoCommittente = "PC_Integrazione Sistema Gestione Notifiche Esito Committente";
    public static String ricezione = "AC_Integrazione Sistema Ricezione Fattura";
    public static String inoltroNotifiche = "AC_Integrazione Sistema Inoltro Notifiche";

    public static EntitiesResponse enteEntityToEntityResp(EnteEntity enteEntity) {

        EntitiesResponse entitiesResponse = new EntitiesResponse();

        entitiesResponse.setIdEnte(enteEntity.getIdEnte().intValue());
        entitiesResponse.setNome(enteEntity.getDenominazioneEnte());
        entitiesResponse.setNomeUfficioFatturazione(enteEntity.getNome());
        entitiesResponse.setIdFiscaleCommittente(enteEntity.getIdFiscaleCommittente());
        entitiesResponse.setCodiceUfficio(enteEntity.getCodiceUfficio());
        entitiesResponse.setEmailSupporto(enteEntity.getContatti());
        entitiesResponse.setCicloAttivo(enteEntity.isCicloAttivo());
        entitiesResponse.setCicloPassivo(enteEntity.getCicloPassivo());
        entitiesResponse.setCampiOpzionali(enteEntity.getCampiOpzionali());
        entitiesResponse.setAmbienteCicloAttivo(enteEntity.getAmbienteCicloAttivo());
        entitiesResponse.setAmbienteCicloPassivo(enteEntity.getAmbienteCicloPassivo());
        entitiesResponse.setTipoCanale(enteEntity.getTipoCanale().getCodTipoCanale() + "-" + enteEntity.getTipoCanale().getDescCanale());
        return entitiesResponse;
    }

    public static ACinvoicesResponse enteEntityToAcinvoicesResp(EnteEntity enteEntity) throws FatturaPAConfigurazioneNonTrovataException {

        ACinvoicesResponse aCinvoicesResponse = new ACinvoicesResponse();

        if (enteEntity.getEndpointFattureAttivaCa() == null) {
            throw new FatturaPAConfigurazioneNonTrovataException();
        }

        String tipoCanale = enteEntity.getEndpointFattureAttivaCa().getCanaleCa().getCodCanale();

        aCinvoicesResponse.setTipoCanaleCa(tipoCanale + "-" + enteEntity.getEndpointFattureAttivaCa().getCanaleCa().getDescCanale());
        aCinvoicesResponse.setAmbienteCicloAttivo(enteEntity.getAmbienteCicloAttivo());
        aCinvoicesResponse.setCertificato(enteEntity.getEndpointFattureAttivaCa().getCertificato());

        if (tipoCanale.equals(CanaleCaEntity.CANALE_CA.PEC.getValue()))
            aCinvoicesResponse.setEndpoint(enteEntity.getEndpointFattureAttivaCa().getEndpoint());
        else if (tipoCanale.equals(CanaleCaEntity.CANALE_CA.FTP.getValue())) {
            String endpoint = enteEntity.getEndpointFattureAttivaCa().getEndpoint();
            if (enteEntity.getEndpointFattureAttivaCa().getPath() != null) {
                aCinvoicesResponse.setEndpoint(endpoint + "/" + enteEntity.getEndpointFattureAttivaCa().getPath());
            } else {
                aCinvoicesResponse.setEndpoint(endpoint);
            }

            aCinvoicesResponse.setUser(enteEntity.getEndpointFattureAttivaCa().getUsername());
            aCinvoicesResponse.setPassword(enteEntity.getEndpointFattureAttivaCa().getPassword());

        } else if (tipoCanale.equals(CanaleCaEntity.CANALE_CA.WS.getValue()) || tipoCanale.equals(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanale.equals(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())) {
            aCinvoicesResponse.setEndpoint(enteEntity.getEndpointFattureAttivaCa().getEndpoint());
            aCinvoicesResponse.setUser(enteEntity.getEndpointFattureAttivaCa().getUsername());
            aCinvoicesResponse.setPassword(enteEntity.getEndpointFattureAttivaCa().getPassword());
        }

        return aCinvoicesResponse;

    }

    public static ACnotificationsResponse enteEntityToACnotificationsResponse(EnteEntity enteEntity) throws FatturaPAConfigurazioneNonTrovataException {
        ACnotificationsResponse aCnotificationsResponse = new ACnotificationsResponse();

        if (enteEntity.getEndpointNotificheAttivaCa() == null) {
            throw new FatturaPAConfigurazioneNonTrovataException();
        }


        String tipoCanale = enteEntity.getEndpointNotificheAttivaCa().getCanaleCa().getCodCanale();


        aCnotificationsResponse.setTipoCanaleCa(tipoCanale + "-" + enteEntity.getEndpointNotificheAttivaCa().getCanaleCa().getDescCanale());
        aCnotificationsResponse.setAmbienteCicloAttivo(enteEntity.getAmbienteCicloAttivo());
        aCnotificationsResponse.setCertificato(enteEntity.getEndpointNotificheAttivaCa().getCertificato());

        if (tipoCanale.equals(CanaleCaEntity.CANALE_CA.PEC.getValue())) {
            aCnotificationsResponse.setEndpoint(enteEntity.getEndpointNotificheAttivaCa().getEndpoint());

        } else if (tipoCanale.equals(CanaleCaEntity.CANALE_CA.FTP.getValue())) {
            String endpoint = enteEntity.getEndpointNotificheAttivaCa().getEndpoint();
            if (enteEntity.getEndpointNotificheAttivaCa().getPath() != null) {
                aCnotificationsResponse.setEndpoint(enteEntity.getEndpointNotificheAttivaCa().getEndpoint() + "/" + enteEntity.getEndpointFattureAttivaCa().getPath());
            } else {
                aCnotificationsResponse.setEndpoint(enteEntity.getEndpointNotificheAttivaCa().getEndpoint());
            }

            aCnotificationsResponse.setUser(enteEntity.getEndpointNotificheAttivaCa().getUsername());
            aCnotificationsResponse.setPassword(enteEntity.getEndpointNotificheAttivaCa().getPassword());

        } else if (tipoCanale.equals(CanaleCaEntity.CANALE_CA.WS.getValue()) || tipoCanale.equals(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanale.equals(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())) {
            aCnotificationsResponse.setEndpoint(enteEntity.getEndpointNotificheAttivaCa().getEndpoint());
            aCnotificationsResponse.setUser(enteEntity.getEndpointNotificheAttivaCa().getUsername());
            aCnotificationsResponse.setPassword(enteEntity.getEndpointNotificheAttivaCa().getPassword());
        }

        return aCnotificationsResponse;
    }

    public static PCprotocolResponse enteEntityToPCprotocolResponse(EnteEntity enteEntity) throws FatturaPAException, FatturaPAConfigurazioneNonTrovataException {
        PCprotocolResponse pCprotocolResponse = new PCprotocolResponse();

        //controllo che l'ente sia di tipo CA
        if (!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
            throw new FatturaPAException("Errore tipo canale");
        }

        if (enteEntity.getEndpointProtocolloCa() == null) {
            throw new FatturaPAConfigurazioneNonTrovataException();
        }

        String tipoCanaleCa = enteEntity.getEndpointProtocolloCa().getCanaleCa().getCodCanale();
        pCprotocolResponse.setInvioUnico(enteEntity.getInvioUnico());
        pCprotocolResponse.setAmbienteCicloPassivo(enteEntity.getAmbienteCicloPassivo());

        if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.PEC.getValue())) {
            pCprotocolResponse.setTipoCanaleCa(tipoCanaleCa + "-PEC");
            pCprotocolResponse.setEndpoint(enteEntity.getEndpointProtocolloCa().getEndpoint());
            pCprotocolResponse.setCertificato(enteEntity.getEndpointProtocolloCa().getCertificato());
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.FTP.getValue())) {
            pCprotocolResponse.setTipoCanaleCa(tipoCanaleCa + "-FTP");
            if (enteEntity.getEndpointProtocolloCa().getPath() != null) {
                pCprotocolResponse.setEndpoint(enteEntity.getEndpointProtocolloCa().getEndpoint() + "/" + enteEntity.getEndpointProtocolloCa().getPath());
            } else {
                pCprotocolResponse.setEndpoint(enteEntity.getEndpointProtocolloCa().getEndpoint());
            }

            pCprotocolResponse.setUser(enteEntity.getEndpointProtocolloCa().getUsername());
            pCprotocolResponse.setPassword((enteEntity.getEndpointProtocolloCa().getPassword()));
            pCprotocolResponse.setCertificato(enteEntity.getEndpointProtocolloCa().getCertificato());
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS.getValue())) {
            pCprotocolResponse.setTipoCanaleCa(tipoCanaleCa + "-WS_AGID");
            //pCprotocolResponse.setTipoWs("AGID");
            pCprotocolResponse.setEndpoint(enteEntity.getEndpointProtocolloCa().getEndpoint());
            pCprotocolResponse.setUser(enteEntity.getEndpointProtocolloCa().getUsername());
            pCprotocolResponse.setPassword((enteEntity.getEndpointProtocolloCa().getPassword()));
            pCprotocolResponse.setCertificato(enteEntity.getEndpointProtocolloCa().getCertificato());
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())) {
            pCprotocolResponse.setTipoCanaleCa(tipoCanaleCa + "-WS_PALEO");
            //pCprotocolResponse.setTipoWs("PALEO");
            pCprotocolResponse.setEndpoint(enteEntity.getEndpointProtocolloCa().getEndpoint());
            pCprotocolResponse.setUser(enteEntity.getEntePaleoCaEntity().getUserIdWs());
            pCprotocolResponse.setPassword((enteEntity.getEntePaleoCaEntity().getPasswordWs()));
            pCprotocolResponse.setProtocollistaNome(enteEntity.getEntePaleoCaEntity().getProtocollistaNome());
            pCprotocolResponse.setProtocollistaCognome(enteEntity.getEntePaleoCaEntity().getProtocollistaCognome());
            pCprotocolResponse.setProtocollistaUo(enteEntity.getEntePaleoCaEntity().getProtocollistaUo());
            pCprotocolResponse.setProtocollistaRuolo(enteEntity.getEntePaleoCaEntity().getProtocollistaRuolo());
            pCprotocolResponse.setResponsabileProcedimentoNome(enteEntity.getEntePaleoCaEntity().getResponsabileProcedimentoNome());
            pCprotocolResponse.setResponsabileProcedimentoCognome(enteEntity.getEntePaleoCaEntity().getResponsabileProcedimentoCognome());
            pCprotocolResponse.setResponsabileProcedimentoUo(enteEntity.getEntePaleoCaEntity().getResponsabileProcedimentoUo());
            pCprotocolResponse.setResponsabileProcedimentoRuolo(enteEntity.getEntePaleoCaEntity().getResponsabileProcedimentoRuolo());
            pCprotocolResponse.setCodiceRegistro(enteEntity.getEntePaleoCaEntity().getCodiceRegistro());
            pCprotocolResponse.setCodiceAmm(enteEntity.getEntePaleoCaEntity().getCodiceAMM());
            pCprotocolResponse.setCertificato(enteEntity.getEndpointProtocolloCa().getCertificato());
        }

        return pCprotocolResponse;
    }

    public static PCregistrationResponse enteEntityToPCregistrationResponse(EnteEntity enteEntity) throws FatturaPAException, FatturaPAConfigurazioneNonTrovataException {
        PCregistrationResponse pCregistrationResponse = new PCregistrationResponse();

        //controllo che l'ente sia di tipo CA
        if (!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
            throw new FatturaPAException("Errore tipo canale");
        }

        if (enteEntity.getEndpointRegistrazioneCa() == null) {
            throw new FatturaPAConfigurazioneNonTrovataException();
        }

        pCregistrationResponse.setAmbienteCicloPassivo(enteEntity.getAmbienteCicloPassivo());

        String tipoCanaleCa = enteEntity.getEndpointRegistrazioneCa().getCanaleCa().getCodCanale();

        if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.PEC.getValue())) {
            pCregistrationResponse.setTipoCanaleCa(tipoCanaleCa + "-PEC");
            pCregistrationResponse.setEndpoint(enteEntity.getEndpointRegistrazioneCa().getEndpoint());
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.FTP.getValue())) {
            pCregistrationResponse.setTipoCanaleCa(tipoCanaleCa + "-FTP");
            if (enteEntity.getEndpointRegistrazioneCa().getPath() != null) {
                pCregistrationResponse.setEndpoint(enteEntity.getEndpointRegistrazioneCa().getEndpoint() + "/" + enteEntity.getEndpointProtocolloCa().getPath());
            } else {
                pCregistrationResponse.setEndpoint(enteEntity.getEndpointRegistrazioneCa().getEndpoint());
            }

            pCregistrationResponse.setUser(enteEntity.getEndpointRegistrazioneCa().getUsername());
            pCregistrationResponse.setPassword((enteEntity.getEndpointRegistrazioneCa().getPassword()));
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_AGID.getValue())) {
            pCregistrationResponse.setTipoCanaleCa(tipoCanaleCa + "-WS_AGID");
            pCregistrationResponse.setEndpoint(enteEntity.getEndpointRegistrazioneCa().getEndpoint());
            pCregistrationResponse.setUser(enteEntity.getEndpointRegistrazioneCa().getUsername());
            pCregistrationResponse.setPassword((enteEntity.getEndpointRegistrazioneCa().getPassword()));
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS.getValue())) {
            pCregistrationResponse.setTipoCanaleCa(tipoCanaleCa + "-WS");
            pCregistrationResponse.setEndpoint(enteEntity.getEndpointRegistrazioneCa().getEndpoint());
            pCregistrationResponse.setUser(enteEntity.getEndpointRegistrazioneCa().getUsername());
            pCregistrationResponse.setPassword((enteEntity.getEndpointRegistrazioneCa().getPassword()));
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())) {
            pCregistrationResponse.setTipoCanaleCa(tipoCanaleCa + "-WS_PALEO");
            pCregistrationResponse.setEndpoint(enteEntity.getEndpointRegistrazioneCa().getEndpoint());
            pCregistrationResponse.setUser(enteEntity.getEndpointRegistrazioneCa().getUsername());
            pCregistrationResponse.setPassword((enteEntity.getEndpointRegistrazioneCa().getPassword()));
        }

        pCregistrationResponse.setCertificato(enteEntity.getEndpointRegistrazioneCa().getCertificato());

        return pCregistrationResponse;
    }

    public static PCesitoCommittenteResponse enteEntityToPCesitoCommittenteResponse(EnteEntity enteEntity) throws FatturaPAException, FatturaPAConfigurazioneNonTrovataException {
        PCesitoCommittenteResponse pCesitoCommittenteResponse = new PCesitoCommittenteResponse();

        //controllo che l'ente sia di tipo CA
        if (!enteEntity.getTipoCanale().getCodTipoCanale().equalsIgnoreCase(TipoCanaleEntity.TIPO_CANALE.CA.getValue())) {
            throw new FatturaPAException("Errore tipo canale");
        }

        if (enteEntity.getEndpointEsitoCommittenteCa() == null) {
            throw new FatturaPAConfigurazioneNonTrovataException();
        }

        EndpointCaEntity endpointCaEntity = enteEntity.getEndpointEsitoCommittenteCa();
        String tipoCanaleCa = endpointCaEntity.getCanaleCa().getCodCanale();
        pCesitoCommittenteResponse.setTipoCanaleCa(tipoCanaleCa + "-" + endpointCaEntity.getCanaleCa().getDescCanale());
        pCesitoCommittenteResponse.setAmbienteCicloPassivo(enteEntity.getAmbienteCicloPassivo());

        if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.PEC.getValue())) {
            pCesitoCommittenteResponse.setEndpoint(endpointCaEntity.getEndpoint());
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.FTP.getValue())) {
            if (endpointCaEntity.getPath() != null) {
                pCesitoCommittenteResponse.setEndpoint(endpointCaEntity.getEndpoint() + "/" + endpointCaEntity.getPath());
            } else {
                pCesitoCommittenteResponse.setEndpoint(endpointCaEntity.getEndpoint());
            }
            pCesitoCommittenteResponse.setUser(endpointCaEntity.getUsername());
            pCesitoCommittenteResponse.setPassword((endpointCaEntity.getPassword()));
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_AGID.getValue()) || tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS.getValue())) {
            pCesitoCommittenteResponse.setEndpoint(endpointCaEntity.getEndpoint());
            pCesitoCommittenteResponse.setUser(endpointCaEntity.getUsername());
            pCesitoCommittenteResponse.setPassword((endpointCaEntity.getPassword()));
        } else if (tipoCanaleCa.equalsIgnoreCase(CanaleCaEntity.CANALE_CA.WS_PALEO.getValue())) {
            pCesitoCommittenteResponse.setEndpoint(endpointCaEntity.getEndpoint());
            pCesitoCommittenteResponse.setUser(endpointCaEntity.getUsername());
            pCesitoCommittenteResponse.setPassword((endpointCaEntity.getPassword()));
        }

        pCesitoCommittenteResponse.setCertificato(endpointCaEntity.getCertificato());

        return pCesitoCommittenteResponse;
    }

    public static InvoicesPCResponse datiFatturaEntityListToInvoicesPcResponse(List<DatiFatturaEntity> datiFatturaEntityList) {
        InvoicesPCResponse invoicesPCResponse = new InvoicesPCResponse();

        List<InvoicesPCResult> invoicesPCResultList = new ArrayList<>();

        for (DatiFatturaEntity datiFatturaEntity : datiFatturaEntityList) {
            InvoicesPCResult invoicesPCResult = new InvoicesPCResult();
            invoicesPCResult.setNomeFileFattura(datiFatturaEntity.getNomeFile());
            invoicesPCResult.setIdentificativoSdi(datiFatturaEntity.getIdentificativoSdI().toString());
            invoicesPCResult.setCodiceUfficioDestinatario(datiFatturaEntity.getCodiceDestinatario());
            invoicesPCResult.setDataRicezione((datiFatturaEntity.getDataFattura()));
            long giorni15 = 1296000000L;
            long dataRicezione = datiFatturaEntity.getDataFattura().getTime();
            long dataDecorrenza = dataRicezione + giorni15;
            invoicesPCResult.setDataDecorrenzaTermini(new Date(dataDecorrenza));
            invoicesPCResultList.add(invoicesPCResult);
        }

        invoicesPCResponse.setResults(invoicesPCResultList);

        return invoicesPCResponse;
    }

    public static InvoicesACResponse fatturaAttivaEntityListToInvoicesACResponse(List<FatturaAttivaEntity> fatturaEntityList) {
        InvoicesACResponse invoicesACResponse = new InvoicesACResponse();

        List<InvoicesACResult> invoicesACResponseList = new ArrayList<>();

        for (FatturaAttivaEntity fatturaEntity : fatturaEntityList) {
            InvoicesACResult invoicesACResult = new InvoicesACResult();
            invoicesACResult.setNomeFileFattura(fatturaEntity.getNomeFile());
            invoicesACResult.setIdentificativoSdi(fatturaEntity.getIdentificativoSdi().toString());
            invoicesACResult.setCodiceUfficioMittente(fatturaEntity.getEnte().getCodiceUfficio());
            invoicesACResult.setDataInoltro((fatturaEntity.getDataRicezioneFromEnti()));
            long giorni15 = 1296000000L;
            long dataRicezione = fatturaEntity.getDataRicezioneFromEnti().getTime();
            long dataDecorrenza = dataRicezione + giorni15;
            invoicesACResult.setDataDecorrenzaTermini(new Date(dataDecorrenza));
            invoicesACResponseList.add(invoicesACResult);
        }

        invoicesACResponse.setResults(invoicesACResponseList);

        return invoicesACResponse;
    }

    public static InvoiceACDetail fatturaEntityListToInvoiceACDetail(List<FatturaAttivaEntity> fatturaEntityList) {
        InvoiceACDetail invoiceACDetail = new InvoiceACDetail();

        EnteEntity enteEntity = fatturaEntityList.get(0).getEnte();
        invoiceACDetail.setIdentificativoSdi(fatturaEntityList.get(0).getIdentificativoSdi().toString());
        invoiceACDetail.setNomeFileFattura(fatturaEntityList.get(0).getNomeFile());
        invoiceACDetail.setCodiceUfficioMittente(enteEntity.getCodiceUfficio());
        invoiceACDetail.setDataInoltro((fatturaEntityList.get(0).getDataRicezioneFromEnti()));
        long giorni15 = 1296000000L;
        long dataRicezione = fatturaEntityList.get(0).getDataRicezioneFromEnti().getTime();
        long dataDecorrenza = dataRicezione + giorni15;
        invoiceACDetail.setDataDecorrenzaTermini(new Date(dataDecorrenza));
        invoiceACDetail.setFormatoTrasmissione(fatturaEntityList.get(0).getFormatoTrasmissione());
        invoiceACDetail.setFlussoSemplificato(fatturaEntityList.get(0).isFatturazioneInterna());
        String tipoCanaleCod = enteEntity.getTipoCanale().getCodTipoCanale();
        String tipoCanaleDesc = enteEntity.getTipoCanale().getDescCanale();

        invoiceACDetail.setTipoCanale(tipoCanaleCod + "-" + tipoCanaleDesc);

        return invoiceACDetail;
    }

    public static InvoicePCDetail datiFatturaEntityListToInvoicePCDetail(List<DatiFatturaEntity> datiFatturaEntityList) {
        InvoicePCDetail invoicePCDetail = new InvoicePCDetail();

        invoicePCDetail.setIdentificativoSdi(datiFatturaEntityList.get(0).getIdentificativoSdI().toString());
        invoicePCDetail.setNomeFileFattura(datiFatturaEntityList.get(0).getNomeFile());
        invoicePCDetail.setSegnaturaProtocollo(datiFatturaEntityList.get(0).getNumeroProtocollo());
        invoicePCDetail.setCodiceUfficioDestinatario(datiFatturaEntityList.get(0).getCodiceDestinatario());
        invoicePCDetail.setDataRicezione((datiFatturaEntityList.get(0).getDataCreazione()));
        long giorni15 = 1296000000L;
        long dataRicezione = datiFatturaEntityList.get(0).getDataCreazione().getTime();
        long dataDecorrenza = dataRicezione + giorni15;
        invoicePCDetail.setDataDecorrenzaTermini(new Date(dataDecorrenza));
        invoicePCDetail.setFlussoSemplificato(datiFatturaEntityList.get(0).getFatturazioneInterna());

        return invoicePCDetail;
    }

    public static UserDto utenteEntityToUserDto(UtenteEntity utenteEntity) {
        UserDto userDto = new UserDto();

        userDto.setUsername(utenteEntity.getUsername());
        userDto.setNome(utenteEntity.getNome());
        userDto.setCognome(utenteEntity.getCognome());
        userDto.setRuolo(utenteEntity.getRuolo());

        return userDto;
    }

    public static ConfigPassiveCycleDTO enteEntityToConfigPassiveCycleDTO(EnteEntity enteEntity) {
        ConfigPassiveCycleDTO configPassiveCycleDTO = new ConfigPassiveCycleDTO();
        configPassiveCycleDTO.setInvioUnico(enteEntity.getInvioUnico());
        EndpointCaEntity endpointCaEntity = enteEntity.getEndpointEsitoCommittenteCa();
        if (endpointCaEntity != null) {
            configPassiveCycleDTO.setTipoEsitoCommittente(endpointCaEntity.getCanaleCa().getCodCanale() + "-" + endpointCaEntity.getCanaleCa().getDescCanale());
        }
        endpointCaEntity = enteEntity.getEndpointProtocolloCa();
        if (endpointCaEntity != null) {
            configPassiveCycleDTO.setTipoProtocollo(endpointCaEntity.getCanaleCa().getCodCanale() + "-" + endpointCaEntity.getCanaleCa().getDescCanale());
        }
        endpointCaEntity = enteEntity.getEndpointRegistrazioneCa();
        if (endpointCaEntity != null) {
            configPassiveCycleDTO.setTipoRegistrazione(endpointCaEntity.getCanaleCa().getCodCanale() + "-" + endpointCaEntity.getCanaleCa().getDescCanale());
        }
        return configPassiveCycleDTO;
    }

    public static ConfigActiveCycleDTO enteEntityToConfigActiveCycleDTO(EnteEntity enteEntity) {
        ConfigActiveCycleDTO configActiveCycleDTO = new ConfigActiveCycleDTO();

        EndpointAttivaCaEntity endpointCaEntity = enteEntity.getEndpointFattureAttivaCa();
        if (endpointCaEntity != null) {
            configActiveCycleDTO.setTipoFatturaAttiva(endpointCaEntity.getCanaleCa().getCodCanale() + "-" + endpointCaEntity.getCanaleCa().getDescCanale());
        }
        endpointCaEntity = enteEntity.getEndpointNotificheAttivaCa();
        if (endpointCaEntity != null) {
            configActiveCycleDTO.setTipoNotificaAttiva(endpointCaEntity.getCanaleCa().getCodCanale() + "-" + endpointCaEntity.getCanaleCa().getDescCanale());
        }

        return configActiveCycleDTO;
    }

    public static StatsFatture statsFatturePassive(EnteEntity enteEntity, StatsFatture statsFatture) {

        if (enteEntity != null) {
            String descTipoCanale = enteEntity.getTipoCanale().getDescCanale();
            if (!descTipoCanale.equalsIgnoreCase("CA")) {
                switch (descTipoCanale) {
                    case "WS":
                        statsFatture.setWsBase(statsFatture.getWsBase() + 1);
                        break;
                    case "PEC":
                        statsFatture.setPecBase(statsFatture.getPecBase() + 1);
                }
            } else {
                if (enteEntity.getInvioUnico() != null) {
                    if (!enteEntity.getInvioUnico()) {
                        statsFatture.setCaNonInvioUnico(statsFatture.getCaNonInvioUnico() + 1);
                    } else {
                        if (enteEntity.getEndpointProtocolloCa() != null) {
                            String descTipoCanaleCa = enteEntity.getEndpointProtocolloCa().getCanaleCa().getDescCanale();
                            switch (descTipoCanaleCa) {
                                case "PEC":
                                    statsFatture.setPecCa(statsFatture.getPecCa() + 1);
                                    break;
                                case "FTP":
                                    statsFatture.setFtpCa(statsFatture.getFtpCa() + 1);
                                    break;
                                default:
                                    statsFatture.setWsCa(statsFatture.getWsCa() + 1);
                            }
                        }
                    }
                }
            }

        }
        return statsFatture;
    }

    /*
    0:desc_canale, e.invio_unico, e.id_endpoint_protocollo_ca, cca.desc_canale
     */
    public static StatsFatture statsFatturePassive(Object[] obj, StatsFatture statsFatture) {
        String descTipoCanale = (String) obj[0];
        String invioUnico = (String) obj[1]; //T o F

        String descTipoCanaleCa = (String) obj[3];

        if (StringUtils.isEmpty(descTipoCanale) || !descTipoCanale.equalsIgnoreCase("CA")) {
            switch (descTipoCanale) {
                case "WS":
                    statsFatture.setWsBase(statsFatture.getWsBase() + 1);
                    break;
                case "PEC":
                    statsFatture.setPecBase(statsFatture.getPecBase() + 1);
            }
        } else {
            if (invioUnico != null) {
                if ("F".equals(invioUnico)) {
                    statsFatture.setCaNonInvioUnico(statsFatture.getCaNonInvioUnico() + 1);
                } else {
                    if (obj[2] != null) { //obj[2] = idEndPointProtocolloCA
                        if (descTipoCanaleCa != null) {
                            switch (descTipoCanaleCa) {
                                case "PEC":
                                    statsFatture.setPecCa(statsFatture.getPecCa() + 1);
                                    break;
                                case "FTP":
                                    statsFatture.setFtpCa(statsFatture.getFtpCa() + 1);
                                    break;
                                default:
                                    statsFatture.setWsCa(statsFatture.getWsCa() + 1);
                            }
                        }
                    }
                }
            }
        }
        return statsFatture;
    }


    public static StatsFatture statsFattureAttive(EnteEntity enteEntity, StatsFatture statsFatture) {

        if (enteEntity != null) {
            String descTipoCanale = enteEntity.getTipoCanale().getDescCanale();
            if (!descTipoCanale.equalsIgnoreCase("CA")) {
                switch (descTipoCanale) {
                    case "WS":
                        statsFatture.setWsBase(statsFatture.getWsBase() + 1);
                        break;
                    case "PEC":
                        statsFatture.setPecBase(statsFatture.getPecBase() + 1);
                }
            } else {
                if (enteEntity.getEndpointFattureAttivaCa() != null) {
                    String descTipoCanaleCa = enteEntity.getEndpointFattureAttivaCa().getCanaleCa().getDescCanale();
                    switch (descTipoCanaleCa) {
                        case "PEC":
                            statsFatture.setPecCa(statsFatture.getPecCa() + 1);
                            break;
                        case "FTP":
                            statsFatture.setFtpCa(statsFatture.getFtpCa() + 1);
                            break;
                        default:
                            statsFatture.setWsCa(statsFatture.getWsCa() + 1);
                    }
                }
            }

        }
        return statsFatture;
    }


    public static StatsFatture statsFattureAttive(Object[] obj, StatsFatture statsFatture) {
        String descTipoCanale = (String) obj[0];

        if (StringUtils.isEmpty(descTipoCanale) || !descTipoCanale.equalsIgnoreCase("CA")) {
            switch (descTipoCanale) {
                case "WS":
                    statsFatture.setWsBase(statsFatture.getWsBase() + 1);
                    break;
                case "PEC":
                    statsFatture.setPecBase(statsFatture.getPecBase() + 1);
            }
        } else {
            if (obj[1] != null) { //obj[1]=id_endpoint_fatture_attiva_ca
                String descTipoCanaleCa = (String) obj[2];
                switch (descTipoCanaleCa) {
                    case "PEC":
                        statsFatture.setPecCa(statsFatture.getPecCa() + 1);
                        break;
                    case "FTP":
                        statsFatture.setFtpCa(statsFatture.getFtpCa() + 1);
                        break;
                    default:
                        statsFatture.setWsCa(statsFatture.getWsCa() + 1);
                }
            }
        }

        return statsFatture;
    }


    public static CampiOpzionaliDto campoOpzionaleFatturaToCampiOpzionaliDto(List<CampoOpzionaleFatturaEntity> campoOpzionaleFatturaEntityList) {
        CampiOpzionaliDto campiOpzionaliDto = new CampiOpzionaliDto();
        List<String> campi = new ArrayList<>();
        for (CampoOpzionaleFatturaEntity campoOpzionaleFatturaEntity : campoOpzionaleFatturaEntityList) {
            campi.add(campoOpzionaleFatturaEntity.getIdTag() + "-" + campoOpzionaleFatturaEntity.getCampo());
        }
        campiOpzionaliDto.setCampi(campi);
        return campiOpzionaliDto;
    }


}
