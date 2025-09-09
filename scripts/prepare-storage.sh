#!/usr/bin/env bash
set -eu

SEED_DIR=${1:-}

if [ -z "$SEED_DIR" ]; then
	echo "Usage: $0 <seed-dir>"
	exit 1
fi

SEED_DIR=$(realpath "$SEED_DIR")

docker compose --project-directory . -f docker/app/docker-compose.yml run \
	--rm --user root --entrypoint /bin/sh -v "$SEED_DIR:/seed:ro" mdi-app \
	-c "cp -a /seed/. /storage/ && chown -R 1001:1001 /storage"
