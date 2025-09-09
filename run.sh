#!/usr/bin/env bash

usage() {
	printf 'Usage: %s <groups> <command> [<args>]\n' "$0"
	printf '  groups:  app, db, proxy-cz, proxy-sk\n'
	printf '  command: start, stop\n'
}

IFS=, read -ra RUN_GROUPS <<< "$1"
RUN_FILES=()

for run_group in "${RUN_GROUPS[@]}"; do
	case "$run_group" in
		app)
			FILES+=(-f docker/app/docker-compose.yml)
			;;
		db)
			FILES+=(-f docker/db/docker-compose.yml)
			;;
		proxy-cz)
			FILES+=(-f docker/proxy/cz/docker-compose.yml)
			;;
		proxy-sk)
			FILES+=(-f docker/proxy/sk/docker-compose.yml)
			;;
		*)
			printf 'Invalid group: %s\n' "$run_group"
			usage
			exit 1
			;;
	esac
done

RUN_COMMAND="$2"
RUN_ARGS=()

case "$RUN_COMMAND" in
	start)
		RUN_ARGS+=(up)
		;;
	stop)
		RUN_ARGS+=(down)
		;;
	*)
		printf 'Invalid command: %s\n' "$RUN_COMMAND"
		usage
		exit 1
		;;
esac

printf 'Groups: %s\n' "${RUN_GROUPS[*]}"
printf 'Args:   %s\n' "${RUN_ARGS[*]}"

shift 2

source .envrc
exec docker compose --project-directory . "${FILES[@]}" "${RUN_ARGS[@]}" "$@"
