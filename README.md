# Media Downloader Index
Simple Java application for indexing media-related semantic data, mainly created for the purpose to be used by [Media Downloader](https://github.com/sunecz/Media-Downloader/).

The project uses:
- [Maven](https://maven.apache.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [JOPA](https://github.com/kbss-cvut/jopa/)
- [Apache Jena Fuseki](https://jena.apache.org/documentation/fuseki2/)

## Supported websites
- [Prima+](https://www.iprima.cz/)

## Project structure
The project consists of 3 main parts:
- **Application**
	- All source files are located in the `app` directory.
	- There are currently 2 Maven modules: `core` and `plugins/iprima`.
	- Docker files are in the `app` and `docker/app` directories.
- **Country proxies**
	- Each country proxy is in its own subdirectory in the `docker/proxy` directory.
	- There are currently 2 countries predefined: `CZ`, `SK`.
	- Using proxies requires further configuration, for more information see [Country proxies](#country-proxies) section.
- **Database**
	- All Docker files are available in the `docker/db` directory.
	- Uses an updated version of [Stain's Jena Docker files](https://github.com/stain/jena-docker) with custom and more performant Jetty configuration file for faster read operations.

## Building the application
Use Maven to build the application files:

	mvn -f app/pom.xml clean package

Files are then available in `app/dist` directory:
- `core.jar` - the application itself (runnable fat JAR)
- `plugins/*.jar` - JAR files of plugins (these are loaded at runtime by the application)

## Running the application
You can use the `run.sh` script to run the application. However, it requires a little bit of setup first.

### Docker network
The Docker containers are all running in the same external Docker network `net-mdi`. First, create it by running:

	docker network create net-mdi

### Environment variables
The script requires a `.envrc` file with the following environment variables:

	export PROXY_CZ_DIRECTORY="./docker/proxy/cz" # Path to CZ proxy files (keep it as-is)
	export PROXY_CZ_PORT=8001                     # CZ proxy local port

	export PROXY_SK_DIRECTORY="./docker/proxy/sk" # Path to SK proxy files (keep it as-is)
	export PROXY_SK_PORT=8002                     # SK proxy local port

	export DB_NAME="mdi"                          # The default Fuseki dataset (will be created automatically)
	export DB_PORT=3030                           # Fuseki port
	export DB_ADMIN_PASSWORD="password"           # Fuseki admin password (to access the admin dashboard)

	export APP_DB_URI="http://mdi-db:3030/mdi"    # DB URI used by the application (use localhost:$DB_PORT when running locally, mdi-db:3030 when running in Docker)
	export APP_DB_USER="admin"                    # Fuseki user
	export APP_DB_PASS="password"                 # Fuseki password (the same as the $DB_ADMIN_PASSWORD)
	export APP_STORAGE_DIR="/storage"             # Path to a storage directory used by the application (for temporary files, etc.)
	export APP_SCHEDULER_CRON="0 30 */1 * * *"    # CRON expression for Spring Boot scheduler
	export APP_SCHEDULER_ZONE="UTC"               # Time zone for Spring Boot scheduler
	export APP_PROXY_CZ="http://proxy-cz:3128"    # CZ proxy URI used by the application (use localhost:$PROXY_CZ_PORT when running locally, proxy-cz:3128 when running in Docker)
	export APP_PROXY_SK="http://proxy-sk:3128"    # SK proxy URI used by the application (use localhost:$PROXY_SK_PORT when running locally, proxy-cz:3128 when running in Docker)

### Country proxies
Running country proxies requires additional setup in the form of placing a `wg0.conf` WireGuard configuration file in the `docker/proxy/COUNTRY/wireguard` directory (where `COUNTRY` is e.g. `CZ` or `SK`).

You can obtain this file by downloading it from a VPN provider or by constructing it yourself when running your own WireGuard server. See [WireGuard official website](https://www.wireguard.com/) for more information.

Each plugin may, or may not use a proxy. Proxies are used for hiding the IP address of the actual server making the requests.

If you want to disable the usage of proxies, you have to disable it in the code of each plugin. For example, for Prima+ you can disable it by changing the following line:
```diff
-       private static final Web.Proxy PROXY = Proxies.CZ;
+       private static final Web.Proxy PROXY = Proxies.NONE;
```
(in `sune.app.mediadown.index.plugin.iprima.Requests:14`)

### Running the script
You can run the script as follows:

	./run.sh GROUPS COMMAND [ARGS]

where:
- `GROUPS` is at least one of: `app`, `db`, `proxy-cz`, `proxy-sk`. If specifying more than one, separate them by comma, e.g. `proxy-cz,proxy-sk`.
- `COMMAND` is one of: `start`, `stop`.
- `ARGS` are optional args passed to the `docker compose` command, e.g. `--build`.
