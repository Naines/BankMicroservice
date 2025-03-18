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
docker run -p 7080:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.1.4 start-dev

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
