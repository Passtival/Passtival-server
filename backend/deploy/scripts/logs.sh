#!/usr/bin/env bash
set -euo pipefail

# logs.sh
# 특정 환경의 Spring Boot 로그 확인
ENVIRONMENT="${1:-prod}" # 기본값은 prod, 인자로 dev 전달 가능

if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "prod" ]]; then
  echo "[ERROR] ENVIRONMENT must be 'dev' or 'prod' (got: $ENVIRONMENT)"
  exit 1
fi

PROJECT_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
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

echo "[INFO] Showing backend logs for '$ENVIRONMENT'"
"${COMPOSE_CMD[@]}" --env-file "$ENV_FILE" -f "$BASE_COMPOSE_FILE" -f "$ENV_COMPOSE_FILE" logs -f backend
