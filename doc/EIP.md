# Einleitung
Kein IT-System kommt Interaktion mit anderen Systemes vorbei. Sei es Web-Services, Import/Export mittels Dateien in CSV, XML oder sonstige Formate.

Für die meiste Technologien gibt es entweder Unterstützung in Form von Standads wie JAX-WS, für andere gute Frameworks wie Spring Batch.

Ansätze um Skalierung und Erweiterbarkeit eines IT-System zu gewährleisten, bietet das Standardwerk [Enterprise Integration Patterns] [eip].

Zwei bekannte Frameworks unterstützen die Enterprise Integration Patterns: [Camel] [camel] und [Spring Integration] [si].
 
In diesem Artikel zeigen wir wie man die Frameworks einsetzen kann. Als Modell nehmen wir einen fiktiven Fahrradshop.

Lieferscheine von verschiedenen Liefernaten werden in CSV Format in einen Verzeichnis gespeichert. Die Lagerbestände werden bei der Verarbeitung
aktualisiert.

Bestellungen von Kunden werden ebenso in CSV geliefert. Bei der Bestellung wird geprüft falls genügend Artikeln auf Bestand sind. Falls nicht 
werden Bestellungen an einen Lieferant geschickt.

Weiters werden die Kunden beim Eingang der Bestellung benachrichtigt, entweder über SMS oder Email.      

![Alt graphic](eip.png)

# Referenzen
[eip]: http://www.enterpriseintegrationpatterns.com/ "Enterprise Integration Patterns, Gregor Hohpe & Bobby Woolf"
[camel]: http://camel.apache.org/ "Apache Camel"
[si]: http://projects.spring.io/spring-integration/ "Spring Integration"