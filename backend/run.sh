#!/bin/bash
set -euo pipefail

COMPOSE_FILE="docker-compose.yml"
ENV_FILE_PATH="${ENV_FILE_PATH:-.env}"
DEPLOY_MODE="${DEPLOY_MODE:-local}"
COMPOSE_CMD=(docker compose --env-file "$ENV_FILE_PATH" -f "$COMPOSE_FILE")

# 스크립트 디렉토리로 이동
cd "$(dirname "$0")"

# 파일 체크
if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "❌ [Compose 파일을 찾을 수 없습니다: $COMPOSE_FILE]"
  exit 1
fi

if [[ ! -f "$ENV_FILE_PATH" ]]; then
  echo "❌ [환경변수 파일을 찾을 수 없습니다: $ENV_FILE_PATH]"
  exit 1
fi

if [[ "$DEPLOY_MODE" != "local" && "$DEPLOY_MODE" != "prod" ]]; then
  echo "❌ [DEPLOY_MODE 값이 올바르지 않습니다: $DEPLOY_MODE (local|prod)]"
  exit 1
fi

if [[ "$DEPLOY_MODE" == "prod" && -z "${DOCKER_USERNAME:-}" ]]; then
  echo "❌ [운영 모드에서는 DOCKER_USERNAME 환경변수가 필요합니다.]"
  echo "   예시: DOCKER_USERNAME=<dockerhub-user> DEPLOY_MODE=prod ./run.sh"
  exit 1
fi

echo "📄 [사용중인 env 파일: $ENV_FILE_PATH]"
echo "🧭 [실행 모드: $DEPLOY_MODE]"
echo "🧹 [존재하는 컨테이너 중지 및 삭제]"
"${COMPOSE_CMD[@]}" down --remove-orphans

if [[ "$DEPLOY_MODE" == "prod" ]]; then
  echo "🗑️ [사용하지 않는 Docker 이미지 정리]"
  docker image prune -a -f
  echo "📥 [백엔드 이미지 Pull]"
  "${COMPOSE_CMD[@]}" pull backend
  echo "🚀 [컨테이너 시작 (pull 이미지)]"
  "${COMPOSE_CMD[@]}" up -d backend
else
  if [[ ! -x "./gradlew" ]]; then
    chmod +x ./gradlew
  fi
  echo "📦 [로컬 JAR 빌드: ./gradlew bootJar]"
  ./gradlew bootJar
  echo "🏗️ [로컬 코드로 이미지 빌드 후 시작]"
  "${COMPOSE_CMD[@]}" up --build -d backend
fi

echo "✅ Done."
