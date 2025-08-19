#!/bin/bash
set -euo pipefail

# ------- Settings -------
ENVIRONMENT=${1:-prod}  # 기본값은 prod, 인자로 dev 전달 가능
COMPOSE_FILE="docker-compose.${ENVIRONMENT}.yml"
# ------------------------

# compose 명령어 자동감지 (v2 우선)
if command -v docker &>/dev/null && docker compose version &>/dev/null; then
  COMPOSE_CMD=(docker compose)
elif command -v docker-compose &>/dev/null; then
  COMPOSE_CMD=(docker-compose)
else
  echo "❌ docker compose/docker-compose가 설치되어 있지 않습니다."
  echo "   Ubuntu 권장: sudo apt update && sudo apt install -y docker-compose-plugin"
  exit 1
fi

# 스크립트 디렉토리로 이동
cd "$(dirname "$0")"

# 파일 체크
if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "❌ Compose 파일을 찾을 수 없습니다: $COMPOSE_FILE"
  exit 1
fi

# gradlew 실행권한 보장
if [[ -f "./gradlew" && ! -x "./gradlew" ]]; then
  chmod +x ./gradlew
fi

echo "📦 [${ENVIRONMENT}] Building Spring Boot JAR with Gradle..."
./gradlew clean build -x test

echo "🧹 [${ENVIRONMENT}] Stopping and removing existing containers..."
"${COMPOSE_CMD[@]}" -f "$COMPOSE_FILE" down --remove-orphans

echo "🚀 [${ENVIRONMENT}] Starting Docker containers with fresh build..."
"${COMPOSE_CMD[@]}" -f "$COMPOSE_FILE" up --build -d

echo "✅ Done. (${ENVIRONMENT})"

