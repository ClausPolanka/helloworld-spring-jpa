#!/usr/bin/env bash

set -e

CONTAINER_NAME="postgres-local"
POSTGRES_VERSION="16"
HOST_PORT=5433
CONTAINER_PORT=5432
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="mysecret"
POSTGRES_DB="postgres"
TARGET_SCHEMA="helloworld_spring_jpa"

DOCKER_CLI="docker"
if ! command -v docker >/dev/null 2>&1; then
  echo "❌ Docker not found."
  exit 1
fi

wait_for_postgres() {
  echo "⏳ Waiting for PostgreSQL to be ready..."
  until $DOCKER_CLI exec "$CONTAINER_NAME" pg_isready -U "$POSTGRES_USER" >/dev/null 2>&1; do
    sleep 0.5
  done
  echo "✅ PostgreSQL is ready."
}

create_schema() {
  echo "📦 Ensuring schema '$TARGET_SCHEMA' exists..."

  $DOCKER_CLI exec -i "$CONTAINER_NAME" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
CREATE SCHEMA IF NOT EXISTS $TARGET_SCHEMA;
EOF

  echo "✅ Schema '$TARGET_SCHEMA' ensured."
}

start_postgres() {
  echo "🚀 Starting PostgreSQL container '${CONTAINER_NAME}'..."

  if $DOCKER_CLI ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "ℹ️ Container exists, starting..."
    $DOCKER_CLI start "$CONTAINER_NAME"
  else
    echo "ℹ️ Creating new container..."

    $DOCKER_CLI run --name "$CONTAINER_NAME" \
      -e POSTGRES_USER="$POSTGRES_USER" \
      -e POSTGRES_PASSWORD="$POSTGRES_PASSWORD" \
      -e POSTGRES_DB="$POSTGRES_DB" \
      -p ${HOST_PORT}:${CONTAINER_PORT} \
      -d postgres:${POSTGRES_VERSION}
  fi

  echo "🔍 Checking database readiness..."
  wait_for_postgres

  echo "📐 Creating schema if missing..."
  create_schema

  echo "🏁 PostgreSQL is up and schema is ready: $TARGET_SCHEMA"
}

stop_postgres() {
  echo "🛑 Stopping PostgreSQL container '${CONTAINER_NAME}'..."

  if $DOCKER_CLI ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    $DOCKER_CLI stop "$CONTAINER_NAME"
    $DOCKER_CLI rm "$CONTAINER_NAME"
    echo "🗑️ Container removed."
  else
    echo "ℹ️ Container does not exist."
  fi
}

show_logs() {
  echo "📄 Logs for '${CONTAINER_NAME}':"
  $DOCKER_CLI logs -f "$CONTAINER_NAME"
}

status() {
  if $DOCKER_CLI ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "✅ PostgreSQL is running (container '${CONTAINER_NAME}')"
  else
    echo "❌ PostgreSQL is NOT running"
  fi
}

case "$1" in
  start) start_postgres ;;
  stop) stop_postgres ;;
  logs) show_logs ;;
  status) status ;;
  *)
    echo "Usage: $0 {start|stop|status|logs}"
    exit 1
    ;;
esac