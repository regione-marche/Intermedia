# INTERMEDIA MARCHE

Gestione inoltro fatture elettroniche per/da la PA

Il sistema IntermediaMarche consente di agevolare e automatizzare la gestione della fattura facilitando l’integrazione sia con i sistemi di protocollo che con i sistemi contabili degli enti, o inserendo dei
controlli su specifici campi della fattura secondo logiche proprie di ogni ente, così diminuendo la gestione manuale della fatture. 

## SERVIZI

**Configurazione Ciclo Passivo**
- Ricezione verso Sistema di Protocollo: per la ricezione del file Fattura
  e delle notifiche, e l'indirizzamento verso il sistema di protocollo
  dell’ente;
- Ricezione verso Sistema di Registrazione: per la ricezione del file
  Fattura e delle notifiche, e l'indirizzamento verso il sistema
  gestionale/contabile dell’ente;
- Invio Notifiche Esito Committente: per l'invio delle notifiche di esito
  committente di accettazione/rifiuto del file fattura;
- Pre-Validazione Campi Opzionali: per definire specifici criteri di
  scarto delle fatture, aggiuntivi a quelli già imposti da normativa
  nazionale;
**Configurazione Ciclo Attivo**
- Invio Fatture: per l'invio delle fatture attive;
- Ricezione Notifiche: per la ricezione delle Notifiche, e l'eventuale'indirizzamento verso il sistema gestionale/contabile dell’ente.
  Per ogni fase invio/ricezione di fatture e notifiche è possibile scegliere
  tra i seguenti canali:
   a) Canale PEC: Sistema di posta elettronica certificata
   b) Canale FTP: Sistema di trasmissione dati tra terminali remoti basato su
      protocollo FTP
   c) Canale Web Service (WS): Sistema di integrazione tramite Web Services.


## Spiegazione struttura del repository anche a beneficio dei potenziali contributori

Il software si compone di diversi bundle da compilare tramite maven ed installare su ESB RED HAT FUSE 7.10
l'ordine d'installazione è mandatorio ed è il seguente:

* intermedia-core
* intermedia-persistence
* intermedia-commons 
* intermedia-api-rest
* intermedia-ftp-enti-ca
* intermedia-mocks
* intermedia-patches
* intermedia-enti-base
* intermedia-enti-ca
* intermedia-sdi-bridge

Di seguito l'elenco dei repository di riferimento:

* **intermedia-core** per poter permettere a tutti gli altri bundle di essere compilati correttamente contiene librerie di utility comuni e i contracts dei vari servizi invocati (ad esempio PALEO)
  * [intermedia-core](https://github.com/regione-marche/intermedia-core.git): libreria usata da tutti i bundle.
  
* **intermedia-persistence** da compilare necessari per tutti i bundle, contiene la libreria unica usata per gestire la persistenza su DB
  * [intermedia-persistence](https://github.com/regione-marche/intermedia-persistence.git)
  
* **intermedia-commons** da compilare necessari per tutti i bundle, contiene librerie e business unit comuni a tutti i bundle
  * [intermedia-commons](https://github.com/regione-marche/intermedia-commons.git)

* **intermedia-api-rest** da compilare successivamente ai componenti precedenti.
  * [intermedia-api-rest](https://github.com/regione-marche/intermedia-api-rest.git): Contiene i servizi esposti all app per la gestione delle configurazioni dei canali

* **intermedia-ftp-enti-ca** da compilare successivamente ai componenti precedenti.
  * [intermedia-ftp-enti-ca](https://github.com/regione-marche/intermedia-ftp-enti-ca.git): bundle per la gestione del canale avanzato FTP.

* **intermedia-mocks** da compilare successivamente ai componenti precedenti.
  * [intermedia-mocks](https://github.com/regione-marche/intermedia-mocks.git): bundle che contiene vari mocks da utilizzare in fase di test.
  
* **intermedia-patches** da compilare successivamente ai componenti precedenti.
  * [intermedia-patches](https://github.com/regione-marche/intermedia-patches.git): bundle che contiene vari servizi rest utiliti per la gestione dell'AM. Ad esempio reinvio di fatture ad un ente.

* **intermedia-enti-base** da compilare successivamente ai componenti precedenti.
  * [intermedia-enti-base](https://github.com/regione-marche/intermedia-enti-base.git): bundle  per la gestione degli enti configurati con canale base WS.

* **intermedia-enti-ca** da compilare successivamente ai componenti precedenti.
  * [intermedia-enti-ca](https://github.com/regione-marche/intermedia-enti-ca.git): bundle  per la gestione degli enti configurati con canale avanzato sia di tipo WS che di tipo PEC.
  
* **intermedia-sdi-bridge** da compilare successivamente ai componenti precedenti.
  * [intermedia-sdi-bridge](https://github.com/regione-marche/intermedia-sdi-bridge.git): bundle per la gestione dei servizi da/verso il sistema d'interscambio.
  

## Elenco dettagliato prerequisiti e dipendenze

Tutti i repo Intermedia Marche utilizzano MAVEN come  strumento di automazione della costruzione e di gestione delle dipendenze. 

All'interno di ogni repo Intermedia Marche è presente nel proprio pom.xml il richiamo di tutte le dipendenze necessarie.

## Istruzioni per l'installazione:

- compilare con maven tutti i repo.
- installare e avviare instanza di ESB FUSE 7.10
- aggiungere tramite console karaf le repo con il comando feature:repo-add 
	esempio: feature:repo-add mvn:it.extra.red.marche.regione.fatturapa.sdi-bridge/intermedia-marche-feature-sdi-bridge/3.0.10/xml/features
- installare la feature con il comando feature:installare
	esempio: feature:install intermedia-marche-feature-sdi-bridge-features

## Status del progetto:

I repo hanno al loro interno la branch "develop" e "master":

* **develop**: branch rilascio sviluppo e collaudo.
* **master**: branch stabile ed **installata in ambiente di esercizio**

## Nomi dei detentori di copyright

Copyright (c) **Regione Marche Giunta Regionale**, Via Gentile da Fabriano, 9 - 60125 Ancona

## Nomi dei soggetti incaricati del mantenimento del progetto open source

* Mirko Morelli
* Gianfranco Amatore

Ditta: **Extra Red Srl**, via Salvo D’Acquisto 40/P, 56025 Pontedera, Italia - codice fiscale e partita IVA: 02263370500, CCIAA Pisa,


## Indirizzo e-mail a cui inviare segnalazioni di sicurezza

Tutte le segnalazioni di sicurezza vanno inviate **esclusivamente** all'indirizzo **supporto.intermediamarche@extrasys.it**