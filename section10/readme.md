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

*Use https://console.hookdeck.com/ to open a webhook console.*
Install scoop from internet and install hookdeck using scope.

-----------------------------------------------------------------------

Docker cmd:

- docker run -p 3307:3306 --name accountsdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=accountsdb -d mysql
- To confirm pwd: docker exec -it accountsdb mysql -u root -p

* docker run -p 3309:3306 --name loansdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=loansdb -d mysql
* docker run -p 3308:3306 --name loansdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=cardsdb -d mysql

Connection string url:

* jdbc:mysql://localhost:3307/accountsdb?allowPublicKeyRetrieval=true&useSSL=false
* jdbc:mysql://localhost:3309/loansdb?allowPublicKeyRetrieval=true&useSSL=false
* jdbc:mysql://localhost:3308/cardsdb?allowPublicKeyRetrieval=true&useSSL=false

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

* API Gateway Pattern : Entry point for ms, streamline communication, security, routing.
* Gateway Routing pattern: Edge server capable of routing on basis of factors like URL, headers or request params.
* Gateway Offloading pattern: Offload cross cutting concerns like Security,Caching, Rate limiting and monitoring from
  ms to gateway server.
* BFF pattern: Single edge servers for each client type.
* Gateway Aggregator/Composition pattern: Fetch info from multiple clients and send info to client. here gw consolidates
  the requests to a single response.

====================================================================================


