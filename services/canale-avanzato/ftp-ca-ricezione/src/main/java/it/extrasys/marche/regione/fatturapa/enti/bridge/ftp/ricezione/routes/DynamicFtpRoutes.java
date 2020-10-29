package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes;

import it.extrasys.marche.regione.fatturapa.contracts.ca.ftp.beans.EsitoTrasferimentoFTPType;
import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.io.File;
import java.util.Date;

public class DynamicFtpRoutes extends RouteBuilder {

    private EnteEntity enteEntity;

    private String componentFtp;

    private String dirRoot;

    private String dirIn;

    private String dirZip;

    private String dirOut;

    private String password;


    public DynamicFtpRoutes(EnteEntity enteEntity, String componentFtp, String dirRoot, String dirIn, String dirZip, String dirOut, String password) {
        this.enteEntity = enteEntity;
        this.componentFtp = componentFtp;
        this.dirRoot = dirRoot;
        this.dirIn = dirIn;
        this.dirZip = dirZip;
        this.dirOut=dirOut;
        this.password=password;
    }


    @Override
    public void configure() throws Exception {

    /*Crea dinamicamente le rotte per leggere i file .zip dal server ftp.
      - salva il file zippato nella cartella con il nome dell'ente
    */
        from( enteEntity.getEndpointFattureAttivaCa().getEndpoint() + File.separator + enteEntity.getEndpointFattureAttivaCa().getPath() /*dirRoot*/ + File.separator + dirIn + "?include=.*done&username=" + enteEntity.getEndpointFattureAttivaCa().getUsername() + "&password=" + password + "&localWorkDirectory=" + dirZip + File.separator + "DOWNLOAD&filter=#ftpFilter&passiveMode=true&binary=true&delay={{fatturapa.ftp.delay}}&move=processati" + File.separator + "${file:name}.ok&stepwise=false&ftpClient.connectTimeout=500000")
                .routeId(FtpConstants.FTP_FILE_ROUTE.concat(enteEntity.getCodiceUfficio()))
                .onException(Exception.class)
                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Exception")
                .log("FTP CA RICEZIONE: STACKTRACE: ${property." + Exchange.EXCEPTION_CAUGHT + "}")
                .setHeader(FtpConstants.ESITO_FTP, simple(EsitoTrasferimentoFTPType.ET_02.value()))
                .to(FtpConstants.INVIA_FILE_ESITO_ENDPOINT)
                .handled(true)
                .end()

                .setHeader(FtpConstants.FTP, simple(componentFtp))
                .setHeader(FtpConstants.ENDPOINT_FTP, simple(enteEntity.getEndpointFattureAttivaCa().getEndpoint()))
                .setHeader(FtpConstants.DIR_ROOT, simple(dirRoot))
                .setHeader(FtpConstants.DIR_OUT, simple(dirOut))
                .setHeader(FtpConstants.USERNAME, simple(enteEntity.getEndpointFattureAttivaCa().getUsername()))
                .setHeader(FtpConstants.PASSWORD, simple(password))

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] STARTED")
                .to("file://" + dirZip + enteEntity.getCodiceUfficio())

                //Salvo l'orario di ricezione del file e il codice fiscale dell'ente
                .process(exchange -> {
                    exchange.getIn().setHeader(FtpConstants.FILE_NAME_ZIP, exchange.getIn().getHeader(Exchange.FILE_NAME));
                    exchange.getIn().setHeader(FtpConstants.ORA_RICEZIONE, new Date());
                    exchange.getIn().setHeader(FtpConstants.COD_FISCALE_ENTE, enteEntity.getIdFiscaleCommittente());
                })

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] Salvato il file: ${headers." + Exchange.FILE_NAME_PRODUCED + "}")

                //Devo usare il wiretap per forzare l'ftp a spostare il file appena finito di scaricarlo
                .wireTap(FtpConstants.UNZIP_FILE_ENDPOINT.concat("-").concat(enteEntity.getCodiceUfficio()))

                .log("FTP CA RICEZIONE: [ROUTE ${routeId}] FINISHED");



    }


    public EnteEntity getEnteEntity() {
        return enteEntity;
    }

    public void setEnteEntity(EnteEntity enteEntity) {
        this.enteEntity = enteEntity;
    }

    public String getComponentFtp() {
        return componentFtp;
    }

    public void setComponentFtp(String componentFtp) {
        this.componentFtp = componentFtp;
    }

    public String getDirRoot() {
        return dirRoot;
    }

    public void setDirRoot(String dirRoot) {
        this.dirRoot = dirRoot;
    }

    public String getDirIn() {
        return dirIn;
    }

    public void setDirIn(String dirIn) {
        this.dirIn = dirIn;
    }

    public String getDirZip() {
        return dirZip;
    }

    public void setDirZip(String dirZip) {
        this.dirZip = dirZip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
