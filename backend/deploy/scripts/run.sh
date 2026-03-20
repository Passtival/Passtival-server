#!/usr/bin/env bash
set -Eeuo pipefail

# =========================
# 로그 유틸
# =========================
readonly SCRIPT_NAME="$(basename "$0")"

# ANSI Color
readonly RED='\033[0;31m'
readonly YELLOW='\033[1;33m'
readonly GREEN='\033[0;32m'
readonly BLUE='\033[0;34m'
readonly CYAN='\033[0;36m'
readonly NC='\033[0m' # No Color

timestamp() {
  date '+%Y-%m-%d %H:%M:%S'
}

log() {
  local level="$1"
  shift

  local color="$NC"

  case "$level" in
    WARN)  color="$YELLOW" ;;
    ERROR) color="$RED" ;;
    DONE)  color="$GREEN" ;;
    *)     color="$NC" ;;
  esac

    printf "%b[%s] [%s] [%s] %s%b\n" \
      "$color" "$(timestamp)" "$level" "$SCRIPT_NAME" "$*" "$NC"
}

log_info()  { log "INFO"  "$@"; }
log_step()  { log "STEP"  "$@"; }
log_warn()  { log "WARN"  "$@"; }
log_error() { log "ERROR" "$@"; }
log_done()  { log "DONE"  "$@"; }

print_divider() {
  echo "============================================================"
}

run_cmd() {
  log_info "실행 명령어: $*"
  "$@"
}

# =========================
# 에러 핸들링
# =========================
on_error() {
  local exit_code=$?
  local line_no=$1
  local command="${2:-unknown}"

  print_divider
  log_error "배포 스크립트 실행 중 오류가 발생했습니다."
  log_error "실패한 라인 번호: ${line_no}"
  log_error "실패한 명령어: ${command}"
  log_error "종료 코드: ${exit_code}"
  print_divider

  exit "$exit_code"
}

trap 'on_error ${LINENO} "$BASH_COMMAND"' ERR

# =========================
# 시작 로그
# =========================
print_divider
log_info "배포 스크립트를 시작합니다."
log_info "실행 예시: ./deploy/scripts/run.sh prod"
log_info "환경변수 예시: ENVIRONMENT=dev ./deploy/scripts/run.sh"
print_divider

# =========================
# 환경 설정
# =========================

# 우선순위: 명령행 인자 > ENVIRONMENT 변수 > 기본값 prod
ENVIRONMENT="${1:-${ENVIRONMENT:-prod}}"

if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "prod" ]]; then
  log_error "ENVIRONMENT 인자가 올바르지 않습니다."
  log_error "허용 값: dev 또는 prod"
  log_error "입력 값: ${ENVIRONMENT}"
  exit 1
fi

log_info "배포 대상 환경: ${ENVIRONMENT}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

BASE_COMPOSE_FILE="$PROJECT_ROOT/deploy/compose/compose.yml"
ENV_COMPOSE_FILE="$PROJECT_ROOT/deploy/compose/compose.${ENVIRONMENT}.yml"
ENV_FILE="$PROJECT_ROOT/deploy/env/.env.${ENVIRONMENT}"

log_info "스크립트 위치: ${SCRIPT_DIR}"
log_info "프로젝트 루트: ${PROJECT_ROOT}"
log_info "기본 Compose 파일: ${BASE_COMPOSE_FILE}"
log_info "환경별 Compose 파일: ${ENV_COMPOSE_FILE}"
log_info "환경변수 파일: ${ENV_FILE}"

# =========================
# Docker Compose 명령어 결정
# =========================
log_step "Docker Compose 실행 방식을 확인합니다."

if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD=(docker compose)
  log_info "Docker Compose v2를 사용합니다. (docker compose)"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD=(docker-compose)
  log_info "Docker Compose v1을 사용합니다. (docker-compose)"
else
  log_error "docker compose(v2) 또는 docker-compose(v1)를 찾을 수 없습니다."
  log_error "Docker 설치 여부 및 PATH 설정을 확인해주세요."
  exit 1
fi

# =========================
# 필수 파일 존재 여부 확인
# =========================
log_step "필수 파일 존재 여부를 확인합니다."

for f in "$BASE_COMPOSE_FILE" "$ENV_COMPOSE_FILE" "$ENV_FILE"; do
  if [[ ! -f "$f" ]]; then
    log_error "필수 파일이 존재하지 않습니다: $f"
    exit 1
  fi
  log_info "확인 완료: $f"
done

cd "$PROJECT_ROOT"
log_info "작업 디렉토리를 프로젝트 루트로 이동했습니다: $(pwd)"

# =========================
# gradlew 확인
# =========================
log_step "Gradle Wrapper 파일을 확인합니다."

if [[ ! -f "./gradlew" ]]; then
  log_error "./gradlew 파일을 찾을 수 없습니다."
  log_error "현재 프로젝트 루트: $PROJECT_ROOT"
  exit 1
fi

if [[ ! -x "./gradlew" ]]; then
  log_warn "./gradlew 실행 권한이 없어 권한을 부여합니다."
  run_cmd chmod +x ./gradlew
else
  log_info "./gradlew 실행 권한이 이미 설정되어 있습니다."
fi

# =========================
# 빌드
# =========================
log_step "Spring Boot JAR 빌드를 시작합니다."
run_cmd ./gradlew clean build -x test
log_done "Spring Boot JAR 빌드가 완료되었습니다."

# =========================
# 기존 컨테이너 중지
# =========================
log_step "기존 컨테이너 및 orphan 컨테이너를 정리합니다."
run_cmd "${COMPOSE_CMD[@]}" --env-file "$ENV_FILE" \
  -f "$BASE_COMPOSE_FILE" -f "$ENV_COMPOSE_FILE" down --remove-orphans
log_done "기존 컨테이너 정리가 완료되었습니다."

# =========================
# 이미지 pull
# =========================
log_step "최신 backend 이미지를 가져옵니다."

if "${COMPOSE_CMD[@]}" --env-file "$ENV_FILE" \
  -f "$BASE_COMPOSE_FILE" -f "$ENV_COMPOSE_FILE" pull backend; then
  log_done "최신 backend 이미지 pull이 완료되었습니다."
else
  log_warn "backend 이미지 pull에 실패했습니다."
  log_warn "로컬 빌드/기존 이미지로 계속 진행합니다."
fi

# =========================
# 서비스 기동
# =========================
log_step "서비스를 백그라운드에서 기동합니다."
run_cmd "${COMPOSE_CMD[@]}" --env-file "$ENV_FILE" \
  -f "$BASE_COMPOSE_FILE" -f "$ENV_COMPOSE_FILE" up -d --build
log_done "서비스 기동이 완료되었습니다."

# =========================
# 상태 확인
# =========================
log_step "현재 컨테이너 상태를 조회합니다."
run_cmd "${COMPOSE_CMD[@]}" --env-file "$ENV_FILE" \
  -f "$BASE_COMPOSE_FILE" -f "$ENV_COMPOSE_FILE" ps

print_divider
log_done "'${ENVIRONMENT}' 환경 배포가 정상적으로 완료되었습니다."
print_divider