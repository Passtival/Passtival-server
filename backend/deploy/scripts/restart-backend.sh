#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./deploy/scripts/restart-backend.sh dev
#   ENVIRONMENT=prod ./deploy/scripts/restart-backend.sh
ENVIRONMENT="${1:-${ENVIRONMENT:-dev}}"

if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "prod" ]]; then
  echo "[ERROR] ENVIRONMENT must be 'dev' or 'prod' (got: $ENVIRONMENT)"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

BASE_COMPOSE_FILE="$PROJECT_ROOT/deploy/compose/compose.yml"
ENV_COMPOSE_FILE="$PROJECT_ROOT/deploy/compose/compose.${ENVIRONMENT}.yml"
ENV_FILE="$PROJECT_ROOT/deploy/env/.env.${ENVIRONMENT}"

if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD=(docker-compose)
else
  echo "[ERROR] docker compose(v2) or docker-compose(v1) not found"
  exit 1
fi

if [[ ! -f "$BASE_COMPOSE_FILE" ]]; then
  echo "[ERROR] Missing file: $BASE_COMPOSE_FILE"
  exit 1
fi

if [[ ! -f "$ENV_COMPOSE_FILE" ]]; then
  echo "[ERROR] Missing file: $ENV_COMPOSE_FILE"
  exit 1
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "[ERROR] Missing file: $ENV_FILE"
  exit 1
fi

cd "$PROJECT_ROOT"

if [[ ! -f "./gradlew" ]]; then
  echo "[ERROR] ./gradlew not found in project root: $PROJECT_ROOT"
  exit 1
fi

if [[ ! -x "./gradlew" ]]; then
  chmod +x ./gradlew
fi

echo "[INFO] Environment: $ENVIRONMENT"
echo "[INFO] Compose files:"
echo "       - $BASE_COMPOSE_FILE"
echo "       - $ENV_COMPOSE_FILE"

echo "[STEP] Build Spring Boot jar (bootJar)"
./gradlew bootJar

echo "[STEP] Recreate backend only (DB untouched)"
"${COMPOSE_CMD[@]}" \
  --env-file "$ENV_FILE" \
  -f "$BASE_COMPOSE_FILE" \
  -f "$ENV_COMPOSE_FILE" \
  up --build -d --force-recreate --no-deps backend

echo "[STEP] Service status"
"${COMPOSE_CMD[@]}" \
  --env-file "$ENV_FILE" \
  -f "$BASE_COMPOSE_FILE" \
  -f "$ENV_COMPOSE_FILE" \
  ps

echo "[DONE] backend restarted successfully"
echo "[NEXT] Follow logs:"
echo "${COMPOSE_CMD[*]} --env-file $ENV_FILE -f $BASE_COMPOSE_FILE -f $ENV_COMPOSE_FILE logs -f backend"
