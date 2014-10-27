# Zusammenfassung
Kein IT-System kommt ohne Interaktion mit anderen Systemen aus, sei es via Web-Services oder auch durch Import/Export von Dateien in CSV, XML oder sonstige Formaten.

Für die meisten Technologien gibt es entweder Unterstützung in Form von Standards wie JAX-WS, für Web-Services, oder Framework-Lösungen  wie Spring Batch, für CSV Verarbeitung. Für die Interaktion zwischen und in IT-System beschreibt das Standardwerk [Enterprise Integration Patterns] [eip] Ansätze um Skalierung und Erweiterbarkeit zu gewährleisten. 

Diese Enterprise Integration Patterns wurden in den Frameworks [Camel] [camel] und [Spring Integration] [si] umgesetzt und werden hier anhand eines Praxisbeispiels, eines Fahrradshops, erklärt und durchleuchtet. 
 
Anhand des Fahrradshop, wird gezeigt wie man beide Frameworks einsetzen kann und typische Integrationsszenarieren lösen kann:

 - Lieferscheinverarbeitung
 - Lagerverwaltung
 - Bestellsystem
 - Benachrichtigungssystem

# Domainmodell
![EIP im Fahrradshop](eip.png)

Der Fahrradshop hat ein Lager. Bei einer Bestellung wird zuerst geprüft falls genügend Anzahl des gewünschten Artikels auf Lager ist.
Auf Lager vorhandene Artikel werden ausgebucht, fehlende Menge nachbestellt. Der Lieferanten enden Lieferscheine, die als Lagereingänge behandelt werden.

Das Domainmodell in Kürze:

- OrderService: verwaltet Bestellungen. 
- BacklogService: bestellt auf Lager fehlende Artikeln beim Lieferanten.
- StockService: verwaltet Lagerbestände.
- Sms- bzw. MailService: sendet Bestellbestätigungen an den Kunden über SMS oder Mail.

# Use Cases
## CSV Import von Bestellungen
Die Aufträge sollten als CSV Dateien von einem Verzeichnis gelesen werden. Ein Auftrag besteht aus Kopf(ORDER) und Positionen(ITEM). CSV Format:

    ORDER;Bike support;1
    ITEM;FRAME;Road bike frame 60 cm;1935182366
    ITEM;DRIVE;Shimano HG LX;1935182439
    ORDER;Bike specialists;2
    ITEM;WHEEL;Spoke 28 inches;098876

Die Verarbeitungsschritte:

1. CSV Datei lesen, in Aufträge teilen (1 ORDER, n ITEM)
1. Prüfen falls bestellte Artikeln auf Lager sind:
    - falls nicht einen Bestellwunsch erzeugen
    - Andersfalls den Lagerbestand reduzieren
    
Eine weitere nicht funktionale Anforderung ist, dass die OrderService nicht den BacklogService "kennen" sollte. Diese lose Kopplung wird es ermöglichen die Systeme in die Zukunft getrennt zu betreiben. 

### Java Service Implementierungen
Die Implementierungen der Services sind völlig unwissend von Spring Integration bzw. Camel.
Hier ein Teil der Implementierung von OrderService:

   public Backlog handleOrder(Order order) {

        List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();
        Customer customer = customerRepository.findByName(order.getCustomerName());
        if (customer == null)
        {
            customer = new Customer(order.getCustomerName());
        }
        customer.getOrders().add(order);
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setStatus(OrderItemStatus.BACKLOG);
            StockItem stockItem = stockService.getStockItem(orderItem.getItem().getNumber());
            if (stockItem != null)
            {
                if (stockItem.getQuantity() > 0) {
                    orderItem.setStatus(OrderItemStatus.CHECKED_OUT);
                    stockService.checkoutStockItem(stockItem);
                    continue;
                }
            }
            backlogItems.add(new BacklogItem(new Item(orderItem.getItem())));
        }
        customerRepository.save(customer);
        return new Backlog(backlogItems);
    }

In der Implementierung ist zu sehen, dass keine Verbindung mit dem BacklogService besteht.

### Anmerkung zu der Konfiguration von Spring Integration und Camel
Spring Integration unterstützt "klassische" Spring XML Konfiguration als auch Annotationen. 
Camel unterstützt Spring Konfiguration mit oder ohne Annotationen. Weiter kann Camel mit Java DSL statt oder zusätzlich zu Spring verwendet werden.
Hier wird die Spring XML Konfiguration verwendendet zwecks Vergleichbarkeit der beiden Frameworks. 

### Umsetzung mit Spring Integration
Da in der Spring Familie ausgereifte Funktionalität für CSV Verarbeitung in Form vom  [Spring Batch] [sb] vorhanden ist, gibt es keine eigene Implementierung für CSV in Spring Integration.
Die Konfiguration für Spring Batch 

    <bean id="orderCsvReader" class="eip.spring.integration.OrderFlatFileItemReaderDelegate"
        scope="prototype">
        <constructor-arg>
            <bean class="org.springframework.batch.item.file.FlatFileItemReader">
                <property name="encoding" value="UTF-8" />
                <property name="lineMapper">
                    <bean
                        class="org.springframework.batch.item.file.mapping.DefaultLineMapper">
                        <property name="lineTokenizer">
                            <bean
                                class="org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer">
                                <property name="tokenizers">
                                    <map>
                                        <entry key="ORDER*">
                                            <bean
                                                class="org.springframework.batch.item.file.transform.DelimitedLineTokenizer">
                                                <property name="delimiter" value=";" />
                                                <property name="names" value="recType, customerName, orderNumber" />
                                            </bean>
                                        </entry>
                                        <entry key="ITEM*">
                                            <bean
                                                class="org.springframework.batch.item.file.transform.DelimitedLineTokenizer">
                                                <property name="delimiter" value=";" />
                                                <property name="names" value="recType, itemType,name,number" />
                                            </bean>
                                        </entry>
                                    </map>
                                </property>
                            </bean>
                        </property>
                        <property name="fieldSetMapper">
                            <bean
                                class="org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper" />
                        </property>
                    </bean>
                </property>
            </bean>
        </constructor-arg>
    </bean>
    
- DelimitedLineTokenizer: teilt jede Zeile in einzelne Felder.
- PatternMatchingCompositeLineTokenizer: entscheidet auf Grund die Name(ORDER oder ITEM) welche DelimitedLineTokenizer zu verwenden ist.
- FlatFileItemReader: liest die CSV Datei zeilenweise.

Spring Batch benötigt ein wenig Hilfe da es sich um einen s.g. Multi-Line Records handelt. Die Implementierung dafür ist in OrderFlatFileItemReaderDelegate

    public Order read() throws ... {
        FieldSet fieldSet = delegate.read();
        Order order = null;
        while (fieldSet != null) {
            if (nextOrder != null)
                order = nextOrder;
            String prefix = fieldSet.readString(0);
            if (prefix.equals("ORDER"))
            {
                if (order != null)
                {
                    nextOrder = new Order(fieldSet.readString("customerName"), fieldSet.readString("orderNumber")); 
                    return order;
                }
                else
                    order = new Order(fieldSet.readString("customerName"), fieldSet.readString("orderNumber"));
            }
            else if (prefix.equals("ITEM"))
            {
                Assert.notNull(order, "order must not be null");
                ItemType itemType;
                // Map ItemType excluded here
                Item item = new Item(itemType, fieldSet.readString("name"), fieldSet.readString("number"));
                order.getOrderItems().add(new OrderItem(item));
            }
            else
                throw new ParseException("No record matching "+prefix);
            fieldSet = delegate.read();
        }
        return order;
    }

Als jetzt Spring Batch so weit aufgesetzt ist, kann Spring Integration wie folgt konfiguriert werden:

    <int-file:inbound-channel-adapter id="orderChannelAdapter"
        directory="file:../eip-common/src/main/resources/orders" channel="csvOrderChannel">
        <int:poller fixed-rate="1000"/>
    </int-file:inbound-channel-adapter>

    <int:channel id="csvOrderChannel" />

    <int:service-activator input-channel="csvOrderChannel"
        ref="orderCsvImport" />

    <bean id="orderCsvImport" class="eip.spring.integration.OrderCsvImport">
        <constructor-arg name="reader" ref="orderCsvReader" />
        <constructor-arg name="channel" ref="orderServiceChannel" />
    </bean>

    <int:channel id="orderServiceChannel" />
    <int:chain input-channel="orderServiceChannel">
        <int:service-activator ref="orderService"  method="handleOrder"/>
        <int:service-activator ref="backlogService" method="saveBacklogItems"/>
    </int:chain>

- inbound-channel-adapter: überwacht eines Verzeichnis. Als eine nete Datei entdeckt wird, wird sie in den  _csvOrderChannel_ gesteckt.
- service-activator: nimmt einen Nachricht - hier die Dateiname - aus _csvOrderChannel_ und verarbeitet sie Zeilenweise mittels den o.g. _orderCsvReader_. Die vom _orderCsvReader_ erzeugte _Order_ Objekt werden als Message ins _orderServiceChannel_.
- chain: eine Kette wird hier verwendet um die Anzahl von expliciten input-/output-channels zu reduzieren. Ein _Order_ wird aus _orderServiceChannel_ genommen und verarbeitet. Als Ergebnis wird _Backlog_ Objekt erzeugt und den _BacklogService_ weitergegeben.
 
Es ist zwar einiges an Konfiguration vorzunehmen, die Flexibilität gegenüber von einer klassischen Javaimplementierung ist aber wesentlich höher. 
 
### Umsetzung mit Camel
Camel hat eine hohe Anzahl von Komponenten(Components). Diese werden in Form von URIs konfiguriert: 

    <camel:camelContext id="orderImport">
        <camel:route>
            <camel:from
                uri="file://../eip-common/src/main/resources/orders?consumer.delay=1000&noop=true" />
            <camel:split streaming="true">
                <camel:tokenize token="ORDER" xml="false" />
                <camel:unmarshal>
                    <camel:csv delimiter=";" />
                </camel:unmarshal>
                <camel:process ref="csvToOrderProcessor"/>
                <camel:bean ref="orderService" />
                <camel:bean ref="backlogService"/>
            </camel:split>
        </camel:route>
    </camel:camelContext>

- from: die File-Komponente liest vom Verzeichnis eine Datei.
- split: die Datei wird aufgeteilt in ORDER mit ITEMs.
- unmarshal: die CSV Komponente wird hier verwendet.
- process: die _CsvToOrderProcessor_ erzeugt aus ORDER/ITEM Zeilen einen _Order_ Objekt
- bean: _OrderService_ verarbeitet den _Order_, erzuegt ein _Backlog_ Objekt. Dies wird den _BacklogService_ übergeben.

Die einzige noch notwendige Implementierung ist die CsvToOrderProcessor:

    public void process(Exchange exchange) throws Exception {
        String csvString = exchange.getIn().getBody(String.class);
        //[[, Bike support, 1], [ITEM, FRAME, Road bike frame 60 cm, 1935182366], [ITEM, DRIVE, Shimano HG LX, 1935182439]]
        List<String> recs = Arrays.asList(csvString.split("\\],"));
        String orderString  = recs.get(0).replace("[", "");
        orderString = orderString.replace("]", "");
        List<String> orderStrings = Arrays.asList(orderString.split(","));
        String customerName = orderStrings.get(1).trim();
        String orderNumber = orderStrings.get(2).trim();
    
        Set<OrderItem> orderItems = new HashSet<OrderItem>();
        for (int i = 1; i < recs.size(); i++) {
            orderString  = recs.get(i).replace("[", "");
            orderString = orderString.replace("]", "");
            orderStrings = Arrays.asList(orderString.split(","));
            Assert.isTrue(orderStrings.size() == 4);
            ItemType itemType = ItemType.OTHER;
            ItemType itemType;
            // Map ItemType excluded here
            OrderItem orderItem = new OrderItem(new Item(itemType, orderStrings.get(2).trim(), orderStrings.get(3).trim()));
            orderItems.add(orderItem);
        }
        Order order = new Order(customerName, orderNumber, orderItems);
        exchange.getIn().setBody(order);
    }

Auch hier gelingt es mit Spring Konfiguration und wenig Kode die Services zu verdrahten.

## Entkopplung Bestellbestätigung mittels JMS
Der Kunde sollte nach der Bestellannahme eine Bestätigung erhalten. In eine Systemkonfiguration ist hinterlegt ob der Kunde mittels SMS oder Mail die Bestätigung erhalten soll.
Senden der Bestätigung sollte asynchron von der Verarbeitung stattfinden da die Bestätigung nicht so hohe Priorität wie (neue) Bestellungen hat.
Daher wird ActiveMQ als JMS Implementierung verwendet als Entkopplung zwischen OrderService und Sms- bzw. MailService.

Weiter sollte der OrderService nicht mit der Entscheidung SMS oder Mail bzw. die dafür notwendige Paramatern für SMS oder Mail Versand.
 
ActiveMQ wird für beide Implementierung gleich konfiguriert:
    <amq:broker useJmx="false" persistent="false">
        <amq:transportConnectors>
            <amq:transportConnector uri="vm://localhost" />
        </amq:transportConnectors>
    </amq:broker>

### Umsetzung mit Spring Integration
Zuerst wird die Bestellbestätigung _Notification_ in entweder einen _SmsNotification_ oder einen _MailNotification_ umgewandelt.
Dafür wird einen _Transformer_ implementiert:

    public Message<?> transform(Message<?> message) {
        Notification notification = (Notification) message.getPayload();
        Notification outNotification;
        if (notification.getCustomer().equals("customerWithSms"))
            outNotification = new SmsNotification(notification.getCustomer(),
                    notification.getMessage(), "smsNumber");
        else
            outNotification = new MailNotification(notification.getCustomer(),
                    "mailAddress", "mailSubject", notification.getMessage());
        return MessageBuilder.withPayload(outNotification).build();
    }
    
Die Konfiguration ob der Kunde SMS oder Mail erhalten soll, ist hier "geschummelt" - es wird die Name des Kunden als Basis verwendet.

Die Spring Konfiguration schaut wie folgt aus:

    <int:channel id="transformerChannel" />
    <int:transformer id="notificationTransformer" input-channel="transformerChannel"
        method="transform" output-channel="routingChannel">
        <bean class="eip.spring.integration.NotificationTransformer" />
    </int:transformer>
 
Das Versenden erfolgt durch der Sms- bzw. MailService. Dazu wird ein _Router_ verwendet:

    <int:channel id="routingChannel" />
    <int:payload-type-router input-channel="routingChannel">
        <int:mapping type="eip.common.services.SmsNotification"
            channel="smsOutQueue" />
        <int:mapping type="eip.common.services.MailNotification"
            channel="mailOutQueue" />
    </int:payload-type-router>

Die Art des Messages, _SmsNotification_ oder _MailNotification_ entscheidet der zu verwendeten Channel.

Zum Schluss die Konfiguration für die JMS Anbindung . hier für Sms:

    <int:channel id="smsOutQueue" />
    <int-jms:outbound-channel-adapter id="smsJms"
        channel="smsOutQueue" destination="smsJmsQueue" />

    <bean id="smsJmsQueue" class="org.apache.activemq.command.ActiveMQQueue">
        <constructor-arg value="queue.sms" />
    </bean>

    <int:poller id="poller" default="true" fixed-delay="1000" />

    <int-jms:message-driven-channel-adapter
        id="smsIn" destination="smsJmsQueue" channel="smsInQueue" />
    <int:channel id="smsInQueue" />

    <int:service-activator input-channel="smsInQueue"
        ref="smsServiceMock" method="send" />

- jms:outbound-channel-adapter schiebt die Messages vom _smsOutQueue_ zum _smsJmsQueue_.
- smsJmsQueue: definiert ein ActiveMQ Queue _queue.sms_.
- poller: definiert wie oft den Empfänger, der _jms:message-driven-channel-adapter_ pollen soll.
- jms:message-driven-channel-adapter nimmt den Message vom ActiveMQ und gibt es an den _service-activator_.
- service-activator: ruft der Spring Bean auf mit dem Parameter SmsNotification.
        
### Umsetzung mit Camel
Wie beim Spring Integration wird zuerst die  _Notification_ in einen _SmsNotification_ bzw. _MailNotification_ umgewandelt.
Mit Camel wird es als ein _Processor_ implementiert:

    public void process(final Exchange exchange) throws Exception {
        Notification notification = exchange.getIn()
                .getBody(Notification.class);
        if (notification.getCustomer().equals("customerWithSms"))
            exchange.getIn().setBody(
                    new SmsNotification(notification.getCustomer(),
                            notification.getMessage(), "smsNumber"));
        else
            exchange.getIn().setBody(
                    new MailNotification(notification.getCustomer(),
                            "mailAddress", "mailSubject", notification
                                    .getMessage()));
    }

Die Entscheidung wohin damit, fordert in Camel folgende Java Implementierung:

    public String slip(final Notification notification,
            @Properties Map<String, Object> properties) {
        // End routing by returning null, otherwise endless loop
        // The current endpoint is in the properties.
        // First run - where routing should be done - it will be null
        if (properties.get(Exchange.SLIP_ENDPOINT) != null) {
            return null;
        }
        if (notification instanceof SmsNotification) {
            return "activemq:sms";
        } else if (notification instanceof MailNotification) {
            return "activemq:mail";
        }
        return null;
    }


Spring Konfiguration dazu:

    <camel:camelContext id="activeMqTest">
        <camel:proxy id="notificationService" serviceInterface="eip.common.services.NotificationService"
            serviceUrl="direct:notification" />

        <camel:route>
            <camel:from uri="direct:notification"/>
            <camel:bean ref="notificationEnricher"/>
            <camel:dynamicRouter>
                <camel:method ref="notificationRouter" method="slip"/>
            </camel:dynamicRouter>
        </camel:route>
        <camel:route>
            <camel:from uri="activemq:sms" />
            <camel:bean ref="smsServiceMock" />
        </camel:route>
        <camel:route>
            <camel:from uri="activemq:mail" />
            <camel:bean ref="mailServiceMock" />
        </camel:route>
    </camel:camelContext>

    <bean id="notificationEnricher" class="eip.camel.NotificationEnricher"/>
    <bean id="notificationRouter" class="eip.camel.NotificationRouter"/>
    
    <bean id="smsServiceMock" factory-method="mock" class="org.mockito.Mockito">
        <constructor-arg value="eip.common.services.SmsService" />
    </bean>
    <bean id="mailServiceMock" factory-method="mock" class="org.mockito.Mockito">
        <constructor-arg value="eip.common.services.MailService" />
    </bean>
    
## Lieferantenbestellung mit SOAP Web Service

Sofern eine Bestellung nicht mit dem Lagerbestand abgedeckt werden kann, werden die Teile im Backlog abgelegt und es wird eine Bestellung beim Lieferanten durchgeführt. Die Bestellung sofern erfolgreich wird mit einer Bestellnummer quittiert. Beide SOAP Client sowohl von Camel als auch Spring setzen auf eine Generierung von Java Code auf. Die Basis für diese Generierung ist die Beschreibung des Web Services in der Web Service Description Language (WSDL). 

### Umsetzung mit Spring Integration

Spring empfiehlt eine Code-Generierung mit dem Maven jaxb2-plugin, die WSDL-Datei des WebServices wird definiert und in welchen Package die generierten Paketen liegen sollen.
 
     <build>
        <plugins>
            <plugin>
                <groupId>org.jvnet.jaxb2.maven2</groupId>
                <artifactId>maven-jaxb2-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <schemaLanguage>WSDL</schemaLanguage>
                    <generatePackage>parts.eip</generatePackage>
                    <forceRegenerate>true</forceRegenerate>
                    <schemas>
                        <schema>
                            <fileset>
                                <directory>${basedir}/src/main/resources/</directory>
                                <includes>
                                    <include>partsorder.wsdl</include>
                                </includes>
                            </fileset>
                        </schema>
                    </schemas>
                </configuration>
            </plugin>
        </plugins>
     </build>

Durch die Generierung stehen uns die Basisdatentypen für die Service Interaktion zur Verfügung. Die einzelnen Operation des WebService welche verwendet werden wollen müssen explizit implementiert werden. Unser WebService Client leitet von der Klasse _WebServiceGatewaySupport_ ab welche uns Basismethoden zur Interaktion zur Verfügung stellt. Die gewünschte Operation des WebService muss normalerweise definiert werden, da unser Service jedoch nur eine Operation zur Verfügung stellt, ist das hier nicht notwendig. Die Operations-Payload ist unserem Fall die Bestellung. 

     public class PartsOrderService extends WebServiceGatewaySupport {
         public OrderResponse order(OrderRequest orderRequest) {
             OrderResponse response = (OrderResponse) getWebServiceTemplate().marshalSendAndReceive(orderRequest);
             return response;
         };
     }

Um den WebService Client letztendlich auch zu verwenden ist es notwendig zwei Spring-Beans zu definieren. Erstens einen Marshaller für die Verarbeitung von Java Objekten zu XML und vice versa und zweitens den Service selbst. Der Service benötigt für die Funktionsfähigkeit den Marshaller und die URI des WebService. 

    <bean id="marshaller" class="org.springframework.oxm.jaxb.Jaxb2Marshaller">
        <property name="contextPath" value="parts.eip" />
    </bean>

    <bean id="webserviceTemplate" class="parts.eip.PartsOrderService" >
        <property name="defaultUri" value="http://localhost:8080/eip-webservice-camel/partsOrder"></property>
        <property name="marshaller" ref="marshaller" />
        <property name="unmarshaller" ref="marshaller" />
    </bean>
   
### Umsetzung mit Camel

     Bei einer Umsetzung mit Camel kommt das Apache Framework für Open-Source Services kurz Apache CXF [cxf] zur Verwendung. Analog zu Spring Integration wird hier eine Maven Plugin für die Codegenerierung verwendet. 
     <build>
        <plugins>
            <plugin>
                <groupId>org.apache.cxf</groupId>
                <artifactId>cxf-codegen-plugin</artifactId>
                <version>3.0.1</version>
                <executions>
                    <execution>
                        <id>generate-sources</id>
                        <phase>generate-sources</phase>
                        <configuration>
                            <sourceRoot>${project.build.directory}/generated/cxf</sourceRoot>
                            <wsdlOptions>
                                <wsdlOption>
                                    <wsdl>${basedir}/src/main/resources/partsorder.wsdl</wsdl>
                                </wsdlOption>
                            </wsdlOptions>
                        </configuration>
                        <goals>
                            <goal>wsdl2java</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
      </build>

Der wesentliche Unterschied zu Spring Integration ist dass hier ein Java-Interface definiert wird was eine Java Beschreibung des WebServices ist und bereits die Operationen des WebService als Methoden definiert sind. 
     
     @WebService(targetNamespace = "http://eip.parts", name = "PartsOrder")
     @XmlSeeAlso({ObjectFactory.class})
     @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
     public interface PartsOrder {
     
         @WebResult(name = "OrderResponse", targetNamespace = "http://eip.parts", partName = "OrderResponse")
         @WebMethod(operationName = "Order")
         public OrderResponse order(
             @WebParam(partName = "OrderRequest", name = "OrderRequest", targetNamespace = "http://eip.parts")
             OrderRequest orderRequest
         );
     }

Um den WebService schlussendlich zu verwenden ist es noch notwendig im Spring Context den WebService Client zu definieren. Hierbei wird das Java-Interface mit der URI des WebService verdrahtet und kann verwendet werden. 

     <jaxws:client id="partsOrderServiceClient" serviceName="partsOrderService" endpointName="partsOrderEndpoint" address="http://localhost:8080/eip-webservice/partsOrder" serviceClass="parts.eip.PartsOrder">
     </jaxws:client>

### Verwendung

Bei der Einbindung von Fremdsystem sollte man beachten dass diese womöglich nicht verfügbar sind auch wenn die eigene Applikation zur Verfügung steht, dadurch ist es sinnvoll diese von einander zu entkoppeln. Da ansonsten die Verfügbarkeit der eigenen Applikation von dem Fremdsystem abhängt und wenn das Fremdsystem nicht zur Verfügung steht steht auch die eigene Applikation gar nicht oder nur eingeschränkt zur Verfügung steht. Der Scheduler vom Spring Framework bietet eine einfache Möglichkeit diese Entkopplung zu erreichen. 

Mit der folgenden Spring Konfiguration Datei, definieren den Scheduler. Was vom Scheduler zu steuern ist wird über Annotationen direkt im Java Code gesteuert. 


     <task:annotation-driven scheduler="myScheduler"/>
     <task:scheduler id="myScheduler" pool-size="1" />

Wir definieren in unserem Backlog Service eine Methode welche periodisch abgearbeitet wird, wobei erst nach einer Sekunde nachdem die Verarbeitung abgeschlossen ist eine neue Verarbeitung startet. Unsere Method überprüft ob sich in unseren Backlog Element befinden die bestellt werden können. Sofern dies der Fall wird eine Bestellung getätigt, falls nicht bleibt das Element im Backlog enthalten. 

     @Scheduled(fixedDelay = 1000)
     public void processBacklog() {
             if (getBacklogItems().size() > 0) {
                 BacklogItem backlogItem = getBacklogItems().get(0);
                 OrderResponse orderResponse = getSOAPWebServiceClient().order(toOrderRequest(backlogItem));
                 if (orderResponse != null && isValidOrderNumber(orderResponse.getOrderNumberUuid())) {
                     list.remove(backlogItem);
                 } 
             }
     }

# Fazit
Mit Enterprise Integration Patterns definiert einen Katalog von Müstern für ein erweiterbare Architektur im Sinne Event Driven Architecture (EDA).
Damit können Systeme flexibler und skalierbare umsetzen. Es kostet aber auch was und zwar Komplexität und Mehraufwand.
Mit den beiden hier vorgestellten Frameworks Spring Integration und Camel  kann man den Mehraufwand deutlich reduzieren und die Komplexität den Frameworks zum Teil überlassen.

Beide Frameworks sind ausgereift, gut Dokumentiert und vielfältig erprobt.

Die Investition in ein solches Framework ist ab mittlere Systemgröße zu empfehlen, einmal vorhanden und verstanden, werden Sie eine Vielzahl von Anwendungsfälle und Möglichkeiten entdecken und schätzen. 

# Referenzen
[eip]: http://www.enterpriseintegrationpatterns.com/ "Enterprise Integration Patterns, Gregor Hohpe & Bobby Woolf"
[camel]: http://camel.apache.org/ "Apache Camel"
[si]: http://projects.spring.io/spring-integration/ "Spring Integration"
[sb]: http://projects.spring.io/spring-batch/ "Spring Batch"
[cxf]: http://cxf.apache.org/ "Apache CXF: An Open-Source Services Framework"
