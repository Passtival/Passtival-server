# 🐬 Docker 가이드
`추가할 내용, 정정 사항이 있으면 PR을 통해 알려주세요!`

## 1. Docker 환경 사용법
Passtival 백엔드 운영 배포는 `backend/docker-compose.yml` + `backend/run.sh`를 기준으로 수행합니다.

- Compose는 애플리케이션(`backend`) 컨테이너만 관리합니다.
- MySQL은 Compose 외부(별도 운영 인프라)에서 관리합니다.
- 애플리케이션은 `DB_URL` 환경변수로 외부 DB에 연결합니다.
- Compose는 `ENV_FILE_PATH`를 통해 env 파일 경로를 외부 주입합니다(기본값 `.env`).
- Compose는 `DEPLOY_MOUNT_PATH`로 `/app/deploy` 마운트 경로를 외부 주입합니다(기본값 `./deploy`).
- Compose 이미지 태그는 `IMAGE_TAG`로 주입합니다(기본값 `latest`).
- 실행 스크립트는 `DEPLOY_MODE`로 동작을 분리합니다(`local` 기본, `prod` 운영).

## 2. Docker 설치
[Docker 공식 웹사이트](https://www.docker.com/get-started)의 가이드를 참고해 설치합니다.

```bash
docker --version
docker compose version
```

## 3. 프로젝트 구조
```text
Passtival-server/
├── backend/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── run.sh
│   └── src/
```

## 4. 환경변수 설정
환경변수 파일은 실행 환경별로 다음 위치를 사용합니다.

- 로컬 기본값: `backend/.env`
- 배포 서버: `~/deploy/.env` (실행 시 `ENV_FILE_PATH=../.env` 지정)
- 마운트 경로: 로컬 기본 `./deploy`, 운영 `/home/ubuntu/deploy/backend`
- 이미지 태그: `IMAGE_TAG` (운영은 GitHub Actions에서 커밋 SHA 전달)
- 필수 예시: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SEED_AUTH_KEYS_PATH`, JWT/OAuth/AWS 관련 키
- `DOCKER_USERNAME`은 `.env`에 두지 않고 배포 실행 시 외부 주입합니다.
- `DOCKER_PASSWORD`는 런타임 env가 아니라 GitHub Actions Docker 로그인 secret 용도로만 사용합니다.

## 5. GitHub Actions → AWS SSM 배포 (2-레플리카)

### 5-1. 배포 방식
- 트리거: `main` push + `workflow_dispatch`
- 인증: GitHub OIDC + `aws-actions/configure-aws-credentials`
- 대상: EC2 태그 `Role=backend`를 가진 **running 인스턴스 2대**
- 절차: 인스턴스 1 배포/헬스체크 성공 후 인스턴스 2 배포(롤링)
- 실패 정책: 인스턴스 1 실패 시 즉시 중단(인스턴스 2 미실행)

### 5-2. 헬스체크 기준(Spring Actuator)
- 경로: `GET /actuator/health/readiness`
- 호출 방식: 인스턴스 내부 `http://127.0.0.1:8080/actuator/health/readiness`
- 타임아웃: 120초

### 5-3. GitHub 입력 계약
- Secrets
  - `DOCKER_USERNAME`
  - `DOCKER_PASSWORD`
  - `AWS_ROLE_TO_ASSUME`
- Variables
  - `AWS_REGION`
  - `DEPLOY_DIR`
  - `DEPLOY_ENV_PATH`
  - `DEPLOY_MOUNT_PATH`
  - `EC2_TARGET_TAG_KEY` (`Role`)
  - `EC2_TARGET_TAG_VALUE` (`backend`)
  - `HEALTHCHECK_PATH` (`/actuator/health/readiness`)
  - `HEALTHCHECK_TIMEOUT_SECONDS` (`120`)

### 5-4. 서버 사전 준비 체크리스트
- [ ] SSM Agent 설치 및 정상 동작
- [ ] EC2 IAM Instance Profile 부여(SSM 명령 수신 가능)
- [ ] `~/deploy/backend`에 `docker-compose.yml`, `run.sh` 고정 배치
- [ ] `~/deploy/.env` 준비
- [ ] Docker/Compose 실행 가능

## 6. Spring Actuator 선행 설정(배포 게이트 필수)
- `build.gradle`
  - `spring-boot-starter-actuator` 의존성 추가
- `application.yml`
  - health probes/readiness 노출 설정
- `SecurityConfig`
  - `/actuator/health/**`는 `permitAll`
  - 그 외 actuator endpoint는 비공개 유지

## 7. Docker Compose 실행
로컬(`.env` 기본값):

```bash
./run.sh
```

배포 서버(`~/deploy/.env` 명시):

```bash
DOCKER_USERNAME=<DOCKERHUB_USERNAME> ENV_FILE_PATH=../.env DEPLOY_MOUNT_PATH=/home/ubuntu/deploy/backend IMAGE_TAG=<GITHUB_SHA> DEPLOY_MODE=prod ./run.sh
```

상태 확인:

```bash
docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml ps
```

## 8. Docker Compose 종료
```bash
docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml down
```

## 9. Docker 기타 명령어
| 명령어 | 설명 |
|--------|------|
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml up -d` | 백그라운드(detached)로 컨테이너 실행 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml down` | 컨테이너 중지 및 제거 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml restart` | 컨테이너 재시작 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml ps` | 실행 중인 컨테이너 확인 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml logs -f backend` | backend 로그 실시간 출력 |
| `docker images` | 로컬 이미지 목록 확인 |
| `docker ps -a` | 전체 컨테이너 목록 확인(중지 포함) |
