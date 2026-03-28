# 🐬 Docker 가이드
`추가할 내용, 정정 사항이 있으면 PR을 통해 알려주세요!`

## 1. Docker 환경 사용법
Passtival 백엔드의 운영 배포는 `backend/docker-compose.yml` 단일 파일을 기준으로 수행합니다.

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
- 마운트 경로: 로컬 기본 `./deploy`, 운영 `/home/ubuntu/deploy/backend` (실행 시 `DEPLOY_MOUNT_PATH` 지정)
- 이미지 태그: `IMAGE_TAG` (운영은 GitHub Actions에서 커밋 SHA 전달)
- 필수 예시: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SEED_AUTH_KEYS_PATH`, JWT/OAuth/AWS 관련 키들
- `DOCKER_USERNAME`은 `.env`에 두지 않고 배포 실행 시 외부 주입합니다(GitHub Actions/향후 SSM).
- `DOCKER_PASSWORD`는 런타임 env가 아니라 GitHub Actions Docker 로그인 secret 용도로만 사용합니다.
- 권장값: `SEED_AUTH_KEYS_PATH=file:/app/deploy/authentication-keys.xlsx` (로컬은 `classpath:static/authentication-keys.xlsx` 가능)

## 5. 배포 준비
- GitHub Actions에서 애플리케이션 이미지를 빌드/푸시합니다.
- 서버에는 `docker-compose.yml`, `run.sh`, `~/deploy/.env` 파일이 준비되어 있어야 합니다.

## 6. Docker Compose 실행
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

## 7. Docker Compose 종료
```bash
docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml down
```

## 8. Docker 기타 명령어
| 명령어 | 설명 |
|--------|------|
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml up -d` | 백그라운드(detached)로 컨테이너 실행 (`DEPLOY_MOUNT_PATH` 미지정 시 `./deploy`, `IMAGE_TAG` 미지정 시 `latest`) |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml down` | 컨테이너 중지 및 제거 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml restart` | 컨테이너 재시작 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml ps` | 현재 실행 중인 컨테이너 목록 확인 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml logs -f backend` | backend 로그 실시간 출력 |
| `docker compose --env-file "${ENV_FILE_PATH:-.env}" -f docker-compose.yml build` | `Dockerfile`을 기반으로 이미지 빌드 |
| `docker images` | 현재 로컬 이미지 목록 확인 |
| `docker ps -a` | 전체 컨테이너 목록 확인(중지 포함) |
| `docker stop <컨테이너ID>` | 특정 컨테이너 중지 |
| `docker rm <컨테이너ID>` | 특정 컨테이너 삭제 |
| `docker exec -it <컨테이너명> bash` | 컨테이너 내부 bash 접속 |
| `docker network ls` | 도커 네트워크 목록 확인 |
| `docker system prune` | 미사용 리소스 정리(주의) |
