FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

# Recursively get pom.xml files
RUN --mount=type=bind,source=.,target=/workspace,readonly \
	bash -lc 'shopt -s globstar nullglob; for f in /workspace/pom.xml /workspace/**/pom.xml; do rel="${f#/workspace/}"; mkdir -p "$(dirname "$rel")"; cp "$f" "$rel"; done'

# Cache Maven repository between builds
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests dependency:go-offline

# Copy the project contents
COPY . .

# Build the project
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests install package

FROM bellsoft/liberica-openjre-alpine:17-cds

WORKDIR /opt/app

RUN apk add --no-cache ca-certificates tzdata

RUN adduser -D -u 1001 appuser \
 && chown -R appuser:appuser "/opt/app"

USER appuser

COPY --from=build --chown=appuser:appuser /app/dist/core.jar ./core.jar
COPY --from=build --chown=appuser:appuser /app/dist/plugins/ ./plugins/

ENTRYPOINT ["java", "-jar", "core.jar"]
