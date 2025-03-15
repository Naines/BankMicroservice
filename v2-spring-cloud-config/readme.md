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

A “native” profile in the Config Server that does not use Git but loads the config files from the local classpath or file system(any static URL you want to point to with spring.cloud.config.server.native.searchLocations).

"Git" profile needed for git repo configs.

- refresh link, exposed by actuator, would be required to refresh each ms config without restart.
- Spring cloud bus need to refresh all ms configs.(need a event pipeline).
Just send one busRequest from any on ms ins, refreshed to all ins connected to that pipeline.

- Both refresh link and busRequest are manual.Use spring monitor to automate fully.
This creates a webhook inside github repo, when change happen in github, invoke monitor api path which implements refresh 
using busRefresh api path. This dependency(spring-cloud-config-monitor) is only added to config server.