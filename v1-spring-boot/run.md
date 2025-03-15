precedence: CLI > JVM args > env vars
Send CLI args: java -jar accounts-service-0.0.1-SNAPSHOT.jar --build.version="1.1"
Send JVM args: java -jar accounts-service-0.0.0-SNAPSHOT.jar -Dbuild.version="1.2" 
External env vars: System.getenv(), use capital leters
eg. env:BUILD_VERSION="1.3"

Drawbacks of externalizing configs:
1. CLI args, JVM props, env vars used to externalize config introduce human error and dependence.
2. Anyone access to app code, can see all the configs.
3. Env vars lack granular access, like exposing vars to server admins.
4. As number of app instance grows, handling config in distributed manner for each ins become hard.
5. SB props and env vars dont support config encryption, secrets cant be managed.
6. After modifying config data, how to ensure that app can read at runtime without restarting ins?
