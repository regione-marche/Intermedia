package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.processor;

import it.extrasys.marche.regione.fatturapa.core.utils.CommonUtils;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes.DynamicElaboraFileRoutes;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes.DynamicFtpRoutes;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.routes.DynamicSedaUnzipFileRoutes;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CanaleCaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.ChiaveManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.EnteManager;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicStartRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicStartRoutes.class);

    private EnteManager enteManager;
    private ChiaveManager chiaveManager;

    private String componentFtp;

    private String dirIn;

    private String dirOut;

    private String dirZip;


    public void startRoutes(Exchange exchange) throws Exception {
        List<EnteEntity> enteFtp = enteManager.getEnteFtpRicezioneFatturaByTipoCanale(CanaleCaEntity.CANALE_CA.FTP.getValue());

        LOG.info("FTP CA RICEZIONE: Trovati " + enteFtp.size() + " enti");

        CamelContext context = exchange.getContext();
        //Tutte le rotte dinamiche del camel-context
        Set<String> routesId = filterDynamicRoutes(context.getRouteDefinitions());

        for (EnteEntity ente : enteFtp) {
            //Decifra la password
            String password = CommonUtils.decryptPassword(ente.getEndpointFattureAttivaCa().getPassword(), chiaveManager.getChiave());

            String dirRoot = ente.getEndpointFattureAttivaCa().getPath();
            boolean esitoRottaFtp = true;
            //Crea una nuova rotta solo se NON esiste
            if (!routesId.contains(FtpConstants.FTP_FILE_ROUTE.concat(ente.getCodiceUfficio()))) {
                esitoRottaFtp = addRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.FTP_FILE_ROUTE, password);
            } else {
                LOG.info("FTP CA RICEZIONE: Route dinamica per l'ente [" + ente.getCodiceUfficio() + "] Esiste già");
                routesId.remove(FtpConstants.FTP_FILE_ROUTE.concat(ente.getCodiceUfficio()));
            }

            if (esitoRottaFtp && !routesId.contains(FtpConstants.UNZIP_FILE_ROUTE.concat(ente.getCodiceUfficio()))) {
                addRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.UNZIP_FILE_ROUTE, password);
            } else {
                routesId.remove(FtpConstants.UNZIP_FILE_ROUTE.concat(ente.getCodiceUfficio()));
            }

            if (esitoRottaFtp && !routesId.contains(FtpConstants.ELABORA_FILE_ROUTE.concat(ente.getCodiceUfficio()))) {
                addRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.ELABORA_FILE_ROUTE, password);
            } else {
                routesId.remove(FtpConstants.ELABORA_FILE_ROUTE.concat(ente.getCodiceUfficio()));
            }
        }

        eliminaRotteDinamicheZombie(context, routesId);

        LOG.info("FTP CA RICEZIONE - Caricate le rotte: " + filterDynamicRoutes(context.getRouteDefinitions()));
    }


    public void updateRoutes(Exchange exchange) throws Exception {
        EnteEntity ente = enteManager.getEnteById(BigInteger.valueOf((Integer) exchange.getIn().getBody()));
        //Decifra la password
        String password = CommonUtils.decryptPassword(ente.getEndpointFattureAttivaCa().getPassword(), chiaveManager.getChiave());


        LOG.info("FTP CA RICEZIONE: Aggiorna ente " + ente.getCodiceUfficio());
        boolean esitoRottaFtp = true;
        String dirRoot = ente.getEndpointFattureAttivaCa().getPath();

        CamelContext context = exchange.getContext();
        Set<String> routesId = filterDynamicRoutes(context.getRouteDefinitions());

        //Esiste già la rotta per l'ente (non è cambiato il codice_ufficio)
        if (routesId.contains(FtpConstants.FTP_FILE_ROUTE.concat(ente.getCodiceUfficio()))) {
            //Elimina la vecchia
            stopAndRemoveRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.FTP_FILE_ROUTE);
        }
        if (routesId.contains(FtpConstants.UNZIP_FILE_ROUTE.concat(ente.getCodiceUfficio()))) {
            //Elimina la vecchia
            stopAndRemoveRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.UNZIP_FILE_ROUTE);
        }
        if (routesId.contains(FtpConstants.ELABORA_FILE_ROUTE.concat(ente.getCodiceUfficio()))) {
            //Elimina la vecchia
            stopAndRemoveRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.ELABORA_FILE_ROUTE);
        }

        esitoRottaFtp = addRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.FTP_FILE_ROUTE, password);
        if (esitoRottaFtp) {
            addRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.UNZIP_FILE_ROUTE, password);
            addRotta(context, ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, FtpConstants.ELABORA_FILE_ROUTE, password);
        }

        //recupera tutte le rotte dinamiche presenti a questo punto
        routesId = filterDynamicRoutes(context.getRouteDefinitions());
        //Recupera tutti gli enti ftp presenti sul DB
        List<EnteEntity> enteFtp = enteManager.getEnteFtpRicezioneFatturaByTipoCanale(CanaleCaEntity.CANALE_CA.FTP.getValue());
        //Costruisce i nomi delle rotte dinamiche
        Set<String> entiFromDB = new HashSet<>();
        enteFtp.forEach(
                r -> {
                    entiFromDB.add(FtpConstants.FTP_FILE_ROUTE + r.getCodiceUfficio());
                    entiFromDB.add(FtpConstants.UNZIP_FILE_ROUTE + r.getCodiceUfficio());
                    entiFromDB.add(FtpConstants.ELABORA_FILE_ROUTE + r.getCodiceUfficio());
                });

        //In questo modo in routesId ho solo le rotte dinamiche di enti che non sono più presenti!
        boolean b = routesId.removeAll(entiFromDB);

        eliminaRotteDinamicheZombie(context, routesId);

        LOG.info("FTP CA RICEZIONE - Caricate le rotte: " + filterDynamicRoutes(context.getRouteDefinitions()));
    }


    /*
        Aggiunge una nuova rotta al camelContext. In caso di eccezione ritorna false, altrimenti true;
        N.B.: inc aso di eccezione la rotta viene comunque creata, va eliminata esplicitamente!!!
     */
    private static boolean addRotta(CamelContext context, EnteEntity ente, String componentFtp, String dirRoot, String dirIn, String dirZip, String dirOut, String nomeRotta, String password) throws Exception {
        boolean esitoRottaFtp = true;
        try {
            //Controlla tutti i campi obbligatori
            if (ente == null || StringUtils.isEmpty(ente.getCodiceUfficio()) || /*StringUtils.isEmpty(dirRoot) ||*/ StringUtils.isEmpty(dirIn) || StringUtils.isEmpty(dirOut)) {
                LOG.error("FTP CA RICEZIONE: Impossibile creare la rotta per l'ente " + ente.getCodiceUfficio() + " - configurazione incompleta dirRoot: "+dirRoot+" - dirIn: "+dirIn+ " - dirOut: "+dirOut);
                return esitoRottaFtp;
            }


            if (FtpConstants.FTP_FILE_ROUTE.equals(nomeRotta)) {
                context.addRoutes(new DynamicFtpRoutes(ente, componentFtp, dirRoot, dirIn, dirZip, dirOut, password));
            } else if (FtpConstants.UNZIP_FILE_ROUTE.equals(nomeRotta)) {
                context.addRoutes(new DynamicSedaUnzipFileRoutes(ente, componentFtp, dirRoot, dirOut));
            } else if (FtpConstants.ELABORA_FILE_ROUTE.equals(nomeRotta)) {
                context.addRoutes(new DynamicElaboraFileRoutes(ente, dirZip));
            }
            LOG.info("FTP CA RICEZIONE: Route [" + nomeRotta + ente.getCodiceUfficio() + "] Started");
        } catch (IllegalArgumentException e) {
            context.stopRoute(nomeRotta.concat(ente.getCodiceUfficio()));
            context.removeRoute(nomeRotta.concat(ente.getCodiceUfficio()));
            esitoRottaFtp = false;
            LOG.error("FTP CA RICEZIONE: Impossibile creare la rotta per l'ente " + ente.getCodiceUfficio() + " - " + e.getMessage());
        }

        return esitoRottaFtp;
    }

    /*
        Stoppa e rimuove la rotta.
        Ritorna true se ok, altrimenti false
     */
    private static boolean stopAndRemoveRotta(CamelContext context, EnteEntity ente, String componentFtp, String dirRoot, String dirIn, String dirZip, String dirOut, String nomeRotta) throws Exception {
        //Elimina la vecchia
        context.stopRoute(nomeRotta.concat(ente.getCodiceUfficio()));
        boolean removed = context.removeRoute(nomeRotta.concat(ente.getCodiceUfficio()));
        //Crea la nuova
        if (removed) {
            LOG.info("FTP CA RICEZIONE: Eliminata la rotta [" + nomeRotta.concat(ente.getCodiceUfficio()) + "]");
        } else {
            LOG.info("FTP CA RICEZIONE: Impossibile eliminare la rotta [" + nomeRotta.concat(ente.getCodiceUfficio()) + "]");
        }
        return removed;
    }


    private static void eliminaRotteDinamicheZombie(CamelContext context, Set<String> routesId) {
        //A questo punto in routesId ho le rotte zombie di enti che non esistono più sul database!
        routesId.forEach(
                idRoute -> {
                    try {
                        context.stopRoute(idRoute);
                        boolean removed = context.removeRoute(idRoute);
                        if (removed) {
                            LOG.info("FTP CA RICEZIONE: Eliminata la rotta [" + idRoute + "]");
                        } else {
                            LOG.info("FTP CA RICEZIONE: Impossibile eliminare la rotta [" + idRoute + "]");
                        }
                    } catch (Exception e) {
                        LOG.info("FTP CA RICEZIONE: Impossibile eliminare la rotta [" + idRoute + "]");
                    }
                });
    }


    //Filtro solo le rotte dinamiche del camel-context
    private static Set<String> filterDynamicRoutes(List<RouteDefinition> routeDefinitions) {
        Set<String> routesId = routeDefinitions.stream()
                .filter(rd -> rd.getId().contains(FtpConstants.FTP_FILE_ROUTE)
                        || rd.getId().contains(FtpConstants.UNZIP_FILE_ROUTE)
                        || rd.getId().contains(FtpConstants.ELABORA_FILE_ROUTE))
                .map(rd -> rd.getId())
                .collect(Collectors.toSet());

        return routesId;
    }


    public EnteManager getEnteManager() {
        return enteManager;
    }

    public void setEnteManager(EnteManager enteManager) {
        this.enteManager = enteManager;
    }

    public String getComponentFtp() {
        return componentFtp;
    }

    public void setComponentFtp(String componentFtp) {
        this.componentFtp = componentFtp;
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

    public String getDirOut() {
        return dirOut;
    }

    public void setDirOut(String dirOut) {
        this.dirOut = dirOut;
    }

    public ChiaveManager getChiaveManager() {
        return chiaveManager;
    }

    public void setChiaveManager(ChiaveManager chiaveManager) {
        this.chiaveManager = chiaveManager;
    }
}
