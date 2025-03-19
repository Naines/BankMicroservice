Spring cloud config: Centralized config server. Provides client and server support for externalized config.
2 core elements:

- A data store to handle config data, helping with durability, version management, access control.
- Server which oversees config data within data store, management, distribution to multiple apps.

Spring cloud: provides framework to developers for build ms implementing common patterns.
Config data can be stored at following places:

- Database
- Github
- File system

Configuration for spring cloud config:

1. Add spring-cloud-config-server and spring-cloud-starter-config to server and client poms respectively.

A “native” profile in the Config Server that does not use Git but loads the config files from the local classpath or
file system(any static URL you want to point to with spring.cloud.config.server.native.searchLocations).

"Git" profile needed for git repo configs.

- refresh link, exposed by actuator, would be required to refresh each ms config without restart.
- Spring cloud bus need to refresh all ms configs.(need a event pipeline).
  Just send one busRequest from any on ms ins, refreshed to all ins connected to that pipeline.

- Both refresh link and busRequest are manual.Use spring monitor to automate fully.
  This creates a webhook inside github repo, when change happen in github, invoke monitor api path which implements
  refresh
  using busRefresh api path. This dependency(spring-cloud-config-monitor) is only added to config server.

_Use https://console.hookdeck.com/ to open a webhook console._
Install scoop from internet and install hookdeck using scope.

---

Docker cmd:

- docker run -p 3307:3306 --name accountsdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=accountsdb -d mysql
- To confirm pwd: docker exec -it accountsdb mysql -u root -p

* docker run -p 3309:3306 --name loansdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=loansdb -d mysql
* docker run -p 3308:3306 --name loansdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=cardsdb -d mysql

Connection string url:

- jdbc:mysql://localhost:3307/accountsdb?allowPublicKeyRetrieval=true&useSSL=false
- jdbc:mysql://localhost:3309/loansdb?allowPublicKeyRetrieval=true&useSSL=false
- jdbc:mysql://localhost:3308/cardsdb?allowPublicKeyRetrieval=true&useSSL=false

=========================================================================
Section 8:

Spring Eureka dependencies:
spring eureka server + actuator+ cloud config client to get configs.

Single eureka server file is created as eureka is env agnostic in this case.
server.port:8070
hostname: localhost
fetchRegistry: false. Eureka don't fetch registry of individual ms.
serviceUrl's default zone: for others to register with eureka.

Client side: openfeign client jar + eureka client jar.
Without Feign, we would have to autowire an instance of EurekaClient into our controller with which we could
receive service-information by service-name as an Application object.

===================================================================================
Section 9: Gateway

Maintain a single entrypoint into ms-n/w?
Handle Cross cutting concerns - logging, auditing, tracing and security across ms?
Route based on custom requirements?

Use Edge Server.

Order of start:
config-> eureka -> accounts /cards/loans -> gwserver

Create filters in gwserver and create correlationid and send to all via request header.

Patterns of application gateway:

- API Gateway Pattern : Entry point for ms, streamline communication, security, routing.
- Gateway Routing pattern: Edge server capable of routing on basis of factors like URL, headers or request params.
- Gateway Offloading pattern: Offload cross cutting concerns like Security,Caching, Rate limiting and monitoring from
  ms to gateway server.
- BFF pattern: Single edge servers for each client type.
- Gateway Aggregator/Composition pattern: Fetch info from multiple clients and send info to client. here gw consolidates
  the requests to a single response.

====================================================================================
Section 10: Resilency

Handle cascading failures?
Handle failures gracefully with fallbacks?
Make our services self-healing capable? retries, timeouts, and give time for a failed services to recover.

Resilience lightwt fault tolerant designed for fn programming. Offers following patterns for increasing fault tolerance
duw to nw problems or failure of any of the multiple services:

Circuit breaker patterns:

- [Circuit breaker](https://resilience4j.readme.io/docs/circuitbreaker): Used to stop making requests when a service
  invoked is failing.
- Fallback: Alternative paths to failing requests.
- Retry: Used to make retries when a service has temporarily failed.
- Rate Limit: Limits the number of calls that a service receives in a time.
- Bulkhead: Limits the number of outgoing concurrent requests to a service to avoid overloading.

Circuit breaker pattern: In distributed env, calls to remote services may fail (nw connections, timeouts). These faults
correct themselves after some time.
In ckt breaker pattern, if calls are long, ckt breaker will intercede and kill the call. If many call fails,ckt break
implementation will pop, failing fast and prevent future calls to failing resource.

Advantages:

- Fail fast
- Fail gracefully
- Recover seamlessly

3 states of ckt breaker:
CLOSED
OPEN
HALF_OPEN

ckt breaker can be implemented at ms level or at edge server.

- Retry pattern in case of Http timeouts:
  Dont wait use fallback url based on business logic.

Considerations for retry logic:
Define timeouts per route, use metadata() alongwith the route.
Retry logic: Based on error codes, exceptions ot response status, determine when and how many tries to retry an
operation.
Backoff strategy: Increasing delay gradually between each retry (exponential backoff)(Strategy for delaying retries to
avoid overwhelming the system).
CKT breaker integration: Combine retry + ckt breaker.
Idempotent ops: Ensure retried logic is idempotent. No sideeffects.

Rate Limiter: Helps control and limit rate of incoming requests to a service or API, to prevent self abuse and ensure
fair usage.
Uncontrolled request in ms communication lead to performance degradation, resource exhaustion, DOS attacks. Rate limiter
provide mechanism to enforce limits on rate of incoming requests.
Specific limit is enforced based on a chosen strategy, such as limiting requests per session, IP address, user or
tenant.

Redis: docker run -p 6379:6379 --name rediscontainer -d redis

BulkHead Pattern: Aims to improve resilience and isolation of components or services within a system.
Allocate/limit resources which can be used for specific service.
============================================================================================
Section 10: Observability and monitoring

Debugging a problem in ms? Trace txs across multiple containers? Combine all logs into a central location where they can
be indexed, searched, filtered,and grouped to find bugs.
Monitoring performance of service calls? Track path of a specific chain service call through our ms nw and time it took
at each ms?
Monitoring services health and metrics?Create alerts and notifications for abnormal behaviour of services.
They solve challenge of identifying and resolving above the problems before they cause outage.

Observability: ABility to understand internal state of a system by observing its o/p.
Observability is attained by collecting metrics, logs and traces.

Metrics: CPU usage, memory usage, response times.
Logs: Record of events that occur in a system, used to track errors, exceptions, other unexpected events.
Traces: Record of path that req takes through a system-track performance of req and identify bottlenecks.

Grafana:tools for observability and monitoring, opensource
logs: G-loki, metrics: G-prometheus, traces: G-tempo

https://grafana.com/docs/loki/latest/get-started/quick-start/

Logs:
Promtail (agent running in ms), gets logs and fwds to loki (log aggregation system). Grafana searches them using query.
Promtail is replaced by alloy in grafana loki v3.0
Grafana: http://localhost:3000/explore

Metrics:
Event logs dont provide answers like CPU, memory,threads usage and error requests.
Metrics are numerical measurements of application performance, collected and aggregated at regular intervals.
Actuator expose metrics,Micrometer expose /actuator/metrics data into something monitoring system understand.
Prometheus aggregate metrics, Grafana-visualization tool to create dashboards and charts from prometheus.

Prometheus: http://localhost:9090/targets

Tracing: Logs, metrics fail to account for distributed nature of cloud apps. Given that a user request often travels
multiple apps.
To correlate data across app boundaries, **distributed tracing** is used. Gives insights into req processing.

3 concepts for distributed tracing:

- Tags: metadata for span context (req uri, username, etc)
- traceid: Trace denotes collection of actions tied to request or tx, distinguished by traceid.
- spanid: Individual stage of request processing.
  micrometer.io/docs/tracing or opentelemetry.io

Opentelemetry generate traces and spans automatically,
Tempo provides solution for storage, retrieval and analysis,
Grafana is used to connect to tempo as datasource and see distributed tracing with help of visuals.(loki and tempo can
be integrated to jump to tracing details directly from logs inside Loki)

=============================================================================================================================
Section12:Security

Keycloak: auth server
docker run -p 7080:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin
quay.io/keycloak/keycloak:26.1.4 start-dev

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

Converts gw server to resource server:
<dependency>
<groupId>org.springframework.security</groupId>
<artifactId>spring-security-oauth2-resource-server</artifactId>
</dependency>

Convert gw server to oauth 2 server:
<dependency>
<groupId>org.springframework.security</groupId>
<artifactId>spring-security-oauth2-jose</artifactId>
</dependency>

CORS stop communication bw 2 domains and port number until backend allows it.

=============================================================================================================
Section 13:Event driven microservice
Problems:

* Avoid temporal coupling if possible: Temporal coupling occurs when a caller service expects immediate response from
  callee before continuing its processing. Happens in sync communication bw services. How to prevent temporal coupling
  and mitigate its effects?
* Using async communication:Sync comm is not necessary. Async communication can fulfill the requirements. How to
  establish async communication?
* Building event driven microservices: An event (incident) signify significant occurance in system (eg. state tx). After
  event produced, alert concerned parties. How to construction EDS?

EDms can be build using EDA, producing and consuming events using async comm, event brokers, spring cloud fn and Spring
cloud stream.

EDA 2 models:

* Publisher/Subscriber(pub-sub) Model: Revolves around subscriptions. Producer generate events distributed to all
  subscriptions. Once event is received, it cannot be replayed, hence new subscriber joining later will not have access
  to past events. Paired with RabbitMq.
* Event Streaming Model: Events are written to a log in sequential manner.Producers publish events as they occur, which
  are stored in order. Instead of subscribing to events,consumers have ability to read from any part of event.Advantage:
  events can be replayed, allowing clients to join at any time and receive all past events. Paired with Kafka. 

Spring Cloud Function is used to build msg microservice.
SCF facilitates the development of business logic by utilizing fns which adhere to std interface in JAVA8 which are:
- Supplier: fn that no i/p and produce o/p. (producer/publisher/source)
- Function: fn which accept i/p and produce o/p. (processor)
- Consumer: consume i/p and never generate o/p. (subscriber/sink)

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-function-web</artifactId>
</dependency>
Helps expose functions as Restapis.

Spring cloud stream: Integrate functions with event brokers.
Components:
1. Destination Binders: Components that provide integration with external msg systems.
2. Destination Bindings: Bridge bw ext msg sys and app code (prd/cmr) provided by end usr.
3. Message: Data structure used by prd and cmr to communicate with Destination Binder (and hence other apps).

Rabbit mq:
```
docker run -it --rm rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
```

We are using Spring Cloud Stream with RabbitMQ to enable asynchronous communication between your Accounts microservice and Message microservice. Let me explain this step-by-step, including binders, exchanges, queues, and the flow of messages.

1. Spring Cloud Stream

Spring Cloud Stream is an abstraction layer for messaging systems (like RabbitMQ, Kafka).

It decouples business logic from messaging middleware (RabbitMQ in our case).

2. Binder

A binder acts as a bridge between the application and the message broker (RabbitMQ).

For example, with below configuration:

spring.cloud.stream.bindings.updateCommunication-in-0
spring.cloud.stream.bindings.sendCommunication-out-0
updateCommunication-in-0: Represents an input binding that listens for messages.

sendCommunication-out-0: Represents an output binding that sends messages.

3. Exchange (RabbitMQ Concept)

In RabbitMQ, an Exchange is responsible for routing messages to one or more queues.

Types of exchanges can be :

Direct Exchange (1-to-1 routing) -> We are using this in the course

Topic Exchange (Pattern-based routing)

Fanout Exchange (Broadcast to all queues)

4. Queue

A queue is where messages are stored and consumed by message listeners.

Each queue is bound to an exchange.

5. Destination

In our config:

sendCommunication-out-0:
destination: send-communication
updateCommunication-in-0:
destination: communication-sent
send-communication: Queue/Exchange where messages from Accounts are sent.

communication-sent: Queue/Exchange where Message microservice listens for messages.

Step-by-Step Flow Explanation

1) Accounts Microservice (sendCommunication-out-0)

The Accounts service sends a message to the RabbitMQ exchange/queue named send-communication.

This happens through the Spring Cloud Stream Binder configured under:

spring.cloud.stream.bindings.sendCommunication-out-0.destination: send-communication

2) RabbitMQ Exchange (send-communication)

The message enters the send-communication exchange.

RabbitMQ routes this message to the appropriate queue based on the binding configuration.

3) Message Microservice (emailsms-in-0)

The Message service listens on the send-communication queue/exchange via:

spring.cloud.stream.bindings.emailsms-in-0.destination: send-communication

The message is consumed by the message microservice and processed.

4) Message Processing and Acknowledgment:

Once the Message microservice processes the message, it might send an acknowledgment or further communication.

5) Callback or Further Communication:

If the Message microservice needs to send a status update back, it uses:

spring.cloud.stream.bindings.emailsms-out-0.destination: communication-sent

This can notify the Accounts microservice or other services about the status.

Sequence Flow

1. Accounts → (Spring Cloud Stream Binding) → RabbitMQ Exchange (send-communication)
2. RabbitMQ Exchange → Queue → Message Service (emailsms-in-0)
3. Message Service Processes the Message
4. (Optional) Message Service → RabbitMQ Exchange (communication-sent)
5. Accounts → (updateCommunication-in-0) → Acknowledgment or Update Received

========================================================================================================================
=============================================================================================================
Section 14: Apache Kafka

* Design
* Data retention
* Performance
* Scalability

Broker, Topic, Partitions, Consumer Groups, Consumer, Producer

* Replication
* Streams
* Consumers
* Consumer groups