\# MRP Intermediate Submission



\## Projektbeschreibung

Dieses Projekt implementiert die \*\*Intermediate Submission\*\* des MRP-Projekts.  

Es umfasst:

\- Einen funktionierenden \*\*HTTP-Server\*\*

\- \*\*Login/Registration\*\* mit Passwort-Hashing (BCrypt)

\- \*\*Token-basierte Authentifizierung\*\*

\- \*\*CRUD-Funktionen\*\* für Media-Objekte (Create, Read, Update, Delete)



---



\## Startanleitung



\### Datenbank vorbereiten

PostgreSQL starten und sicherstellen, dass folgende DB existiert:



Starte Docker. Hier sollte bereits die Datenbank existieren.

Sichergehen dass im IntelliJ die Datenbank mit den korrekten login-Daten versehen sind

Die Login-Daten sind im DatabaseManager.java File zu finden (Testen ob connection passiert "APPLY" drücken und fertig)

Anschließend im Main.java builden und Starten. Es sollte daraufhin ausgegeben werden "Server started on port 10001".

DB-access:
db: mrpdb
username: mrpuser
password: mrpuser


https://github.com/Voikat14/mrp_restserver.git

