# Deploy Guide

`backend` 프로젝트의 배포 관련 리소스(Compose, env, scripts)를 `deploy/` 하위로 모은 구조입니다.

## Directory

```text
deploy/
├─ compose/
│  ├─ compose.yml
│  ├─ compose.dev.yml
│  └─ compose.prod.yml
├─ env/
│  ├─ .env.dev
│  ├─ .env.prod
│  ├─ .env.dev.example
│  └─ .env.prod.example
└─ scripts/
   ├─ logs.sh
   ├─ run.sh
   └─ restart-backend.sh
```

## 1) Local Quick Start (dev)

### Prerequisites

- Docker Engine + Docker Compose v2
- JDK 17 (스크립트에서 `gradlew` 사용)

### Step 1. env 파일 준비

```bash
cp deploy/env/.env.dev.example deploy/env/.env.dev
```

필요한 값(`DB_USERNAME`, `DB_PASSWORD`, OAuth/JWT 관련 값)을 수정합니다.

### Step 2. 서비스 기동 (backend + mysql)

```bash
docker compose --env-file deploy/env/.env.dev \
  -f deploy/compose/compose.yml \
  -f deploy/compose/compose.dev.yml \
  up -d --build
```

### Step 3. 상태/로그 확인

```bash
docker compose --env-file deploy/env/.env.dev \
  -f deploy/compose/compose.yml \
  -f deploy/compose/compose.dev.yml ps

./deploy/scripts/logs.sh dev
```

### Step 4. backend만 재시작 (DB 유지)

```bash
./deploy/scripts/restart-backend.sh dev
```

## 2) Production Quick Start (prod)

### Prerequisites

- Docker Engine + Docker Compose v2
- 서버에 `deploy/env/.env.prod` 파일 준비
- Docker Hub pull 권한(이미지 사용 시)

### Step 1. env 파일 준비

```bash
cp deploy/env/.env.prod.example deploy/env/.env.prod
```

프로덕션 값(비밀번호, JWT secret, 외부 API 키, DB URL 등)으로 수정합니다.

### Step 2-A. 권장 운영 (backend only, 외부 DB/RDS)

```bash
docker compose --env-file deploy/env/.env.prod \
  -f deploy/compose/compose.yml \
  -f deploy/compose/compose.prod.yml \
  up -d --build
```

### Step 2-B. 임시 운영 (backend + mysql 컨테이너)

```bash
docker compose --env-file deploy/env/.env.prod \
  -f deploy/compose/compose.yml \
  -f deploy/compose/compose.prod.yml \
  --profile with-mysql \
  up -d --build
```

### Step 3. 배포 스크립트 실행

```bash
./deploy/scripts/run.sh prod
```

스크립트 수행 내용:

- `./gradlew clean build -x test`
- 기존 컨테이너 정리(`down --remove-orphans`)
- backend 이미지 pull 시도
- `up -d --build` 재기동

### Step 4. 로그/상태 확인

```bash
./deploy/scripts/logs.sh prod

docker compose --env-file deploy/env/.env.prod \
  -f deploy/compose/compose.yml \
  -f deploy/compose/compose.prod.yml ps
```

## 3) GitHub Actions 배포 기준

워크플로우(`.github/workflows/deploy.yml`)는 다음 리소스를 서버로 복사합니다.

- `backend/deploy/compose/compose.yml`
- `backend/deploy/compose/compose.prod.yml`
- `backend/deploy/scripts/run.sh`
- `backend/deploy/scripts/logs.sh`

서버에서는 `./deploy/scripts/run.sh prod`를 실행해 배포합니다.

## 4) Troubleshooting

### env 파일 누락 에러

- 에러: `Missing file: .../.env.dev` 또는 `.../.env.prod`
- 조치: `deploy/env/.env.<env>` 파일 생성/권한 확인

### Compose 변수 경고

- 경고: `The "DB_USERNAME" variable is not set`
- 조치: `--env-file deploy/env/.env.<env>` 옵션 사용 여부 확인

### DB 준비 지연

- `depends_on`은 시작 순서 보조입니다.
- 앱 레벨에서 DB 재시도 설정(Hikari timeout/retry)을 함께 권장합니다.
