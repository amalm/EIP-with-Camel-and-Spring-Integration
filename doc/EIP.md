# Zusammenfassung
Kein IT-System kommt ohne Interaktion mit anderen System aus, sei es via Web-Services oder auch durch Import/Export von Dateien in CSV, XML oder sonstige Formaten.

Für die meisten Technologien gibt es entweder Unterstützung in Form von Standards wie JAX-WS, für Web-Services, oder Framework-Lösungen  wie Spring Batch, für CSV Verarbeitung. Für die Interaktion zwischen und in IT-System beschreibt das Standardwerk [Enterprise Integration Patterns] [eip] Ansätze um Skalierung und Erweiterbarkeit zu gewährleisten. 

Diese Enterprise Integration Patterns wurden in den Frameworks [Camel] [camel] und [Spring Integration] [si] umgesetzt und werden hier anhand eines Praxisbeispiels, eines Fahrradshops, erklärt und durchleuchtet. 
 
Anhand des Fahrradshop, wird gezeigt wie man beide Frameworks einsetzen kann und typische Integrationsszenarieren lösen kann:

 - Lieferscheinverarbeitung
 - Lagerverwaltung
 - Bestellungsystem
 - Benachrichtungssystem

![EIP im Fahrradshop](images/eip.png)

# Referenzen
[eip]: http://www.enterpriseintegrationpatterns.com/ "Enterprise Integration Patterns, Gregor Hohpe & Bobby Woolf"
[camel]: http://camel.apache.org/ "Apache Camel"
[si]: http://projects.spring.io/spring-integration/ "Spring Integration"
