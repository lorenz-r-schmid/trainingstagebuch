Trainingstagebuch
Eine Java-Konsolenanwendung zur Verwaltung von Trainingseinheiten. Entwickelt von Lorenz Schmid und Florian Schmid im Rahmen eines Informatik-Projekts an der HFT Stuttgart.
Funktionen
Trainer
Gesamtübersicht aller Sportler (letzte 5 Trainingstage)
Detailansicht einzelner Sportler
Sportlerdaten einsehen und ergänzen
Sportler löschen
Neue Übungen hinzufügen
Nutzer
Set-Übersicht (letzte 5 Trainingstage)
Sets eintragen (Übung, Wiederholungen, Gewicht)
Alle verfügbaren Übungen einsehen
Voraussetzungen
Java 22
PostgreSQL 18
Eclipse IDE
Maven (wird von Eclipse verwaltet)
Setup
1. Datenbank einrichten
PostgreSQL installieren und starten
In pgAdmin eine neue Datenbank mit dem Namen `Fitness` erstellen
2. Konfiguration einrichten
`config.example.properties` kopieren und in `config.properties` umbenennen
Eigene PostgreSQL-Zugangsdaten eintragen:
```properties
db.url=jdbc:postgresql://localhost:5432/Fitness
db.username=postgres
db.password=dein\\\_passwort
```
3. Projekt importieren
Eclipse öffnen → File → Import → Existing Projects into Workspace
Projektordner auswählen → Finish
Rechtsklick auf Projekt → Maven → Update Project
4. Starten
`WelcomePage.java` ausführen
Beim ersten Start werden alle Tabellen automatisch erstellt
Der erste angelegte Account wird automatisch als Trainer gesetzt
Projektstruktur
```
trainingstagebuch/
├── src/
│   ├── database/
│   │   ├── DatabaseManager.java     # Datenbankverbindung \\\& alle SQL-Operationen
│   │   ├── UserDTO.java             # Datenklasse für Nutzer
│   │   ├── ExerciseDTO.java         # Datenklasse für Übungen
│   │   └── SetDTO.java              # Datenklasse für Sets
│   ├── main/
│   │   ├── WelcomePage.java         # Einstiegspunkt \\\& Menüsteuerung
│   │   ├── LoginManager.java        # Login-Logik
│   │   ├── AccountCreation.java     # Account-Erstellung
│   │   ├── TrainerLogic.java        # Trainer-Funktionen
│   │   └── UserLogic.java           # Nutzer-Funktionen
│   └── tools/
│       ├── Menu.java                # Wiederverwendbare Menü-Komponente
│       ├── Table.java               # Tabellenausgabe in der Konsole
│       ├── TableColumn.java         # Einzelne Tabellenspalte
│       ├── ScannerManager.java      # Zentraler Scanner (Singleton)
│       └── Helper.java              # Hilfsfunktionen
├── docs/
│   ├── uml/                         # UML-Diagramme
│   ├── erd/                         # ER-Diagramme
│   └── demo/
│       └── Demo.pdf                 # Programmdemo
├── config.example.properties        # Vorlage für Konfiguration
├── config.properties                # Lokale Zugangsdaten (nicht auf GitHub)
├── README.md
├── pom.xml
└── .gitignore
```
Datenbankschema
userdata – Nutzerdaten (Name, Login, Passwort, Alter, Geschlecht, Fitness-Level, Trainer-Flag)
exercises – Übungen (Titel, Beschreibung)
sets – Trainingseinträge (Datum, Wiederholungen, Gewicht, Nutzer-ID, Übungs-ID)
Hinweise
Beim ersten Start wird automatisch ein Trainer-Account angelegt
Passwortanforderungen: min. 6 Zeichen, min. 1 Großbuchstabe, min. 1 Kleinbuchstabe, min. 1 Ziffer
Loginnamen werden automatisch generiert (z.B. `sclo1234`)
Pro Nutzer kann eine Übung nur einmal pro Tag eingetragen werden