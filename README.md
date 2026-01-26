ATTENZIONE questo programma è eseguibile solo con una versione di Java 21 installata

Descrizione del progetto
SmartLibrary è un’applicazione Java progettata per la gestione di una piccola biblioteca digitale.
Permette di:

  -registrare e autenticare utenti

  -gestire libri (inserimento, modifica, eliminazione)

  -effettuare ricerche avanzate

  -visualizzare e aggiornare informazioni

  -salvare i dati tramite database H2

L’interfaccia è realizzata in Java Swing, mentre la persistenza dei dati utilizza JDBC e H2 Database.

  -Tecnologie utilizzate

  -Java 21 (LTS)

  -Eclipse IDE

  -JDBC

  -H2 Database Engine

  -Swing per l’interfaccia grafica

 - Git / GitHub per il versionamento

Esecuzione dell’applicazione
  Requisito fondamentale
  Per eseguire il programma è necessario avere installato Java 21 (JRE o JDK).

Struttura della repository
Codice
SmartLibrary/
│
├── src/                     # Codice sorgente Java
├── resources/               # File di configurazione e risorse
├── release/                 # JAR eseguibile per il professore
├── database/                # File del database H2 (ignorati da Git)
├── .gitignore               # File di esclusione Git
├── README.md                # Questo file
└── pom.xml / .project       # Configurazione del progetto (se Maven/Eclipse)

Funzionalità principali
✔ Gestione utenti
  Registrazione
  
  Login
  
  Ruoli (utente / amministratore)

✔ Gestione libri
  Aggiunta
  
  Modifica
  
  Eliminazione
  
  Visualizzazione dettagli

✔ Ricerca avanzata
  Per titolo, autore, anno, genere
  
  Supporto a filtri combinati
  
  Ricerca flessibile tramite espressioni regolari (regex)

✔ Persistenza dati
  Database H2 integrato
  
  Connessione tramite JDBC
  
  Creazione automatica delle tabelle se mancanti

Non è necessario configurare manualmente il database: H2 crea automaticamente i file necessari
Il codice è organizzato in package separati (UI, DAO, Model, Service) per garantire chiarezza e manutenibilità

Licenza
Progetto realizzato per scopi accademici.
Tutti i diritti riservati all’autore.
