# 🐬 Docker 가이드
` 추가할 내용, 정정 사항이 있으면 PR을 통해 알려주세요!`
### 1. **Docker 환경 사용법**

Docker를 사용하여 Passtival 서버를 실행하는 방법을 설명합니다.   
Docker는 컨테이너화된 애플리케이션을 쉽게 배포하고 관리할 수 있는 플랫폼입니다.
---

### 2. **Docker 설치**   
Docker를 설치하려면 [Docker 공식 웹사이트](https://www.docker.com/get-started)에서 설치 가이드를 참고하세요.   
    운영 체제에 맞는 Docker Desktop을 다운로드 하고 설치합니다.
    설치 후 Docker가 정상적으로 작동하는지 확인하려면 다음 명령어를 실행합니다:

    docker --version
    docker compose version

---
### 3. **프로젝트 구조**
프로젝트 구조는 다음과 같습니다.

    Passtival-server/
    ├── backend/
    │   ├── Dockerfile
    │   ├── deploy/
    │   │   ├── compose/
    │   │   │   ├── compose.yml
    │   │   │   ├── compose.dev.yml
    │   │   │   └── compose.prod.yml
    │   │   ├── env/
    │   │   │   ├── .env.dev.example
    │   │   │   └── .env.prod.example
    │   │   └── scripts/
    │   └── src/
    
> 환경 변수는 `deploy/env/.env.dev(.example)` 또는 `deploy/env/.env.prod(.example)`을 사용합니다.
---

### 4. .env 설정
개발용은 `backend/deploy/env/.env.dev.example`을 복사해 `backend/deploy/env/.env.dev`를 생성합니다:

    DB_USERNAME=passtival_user
    DB_PASSWORD=your_password
    DB_ROOT_PASSWORD=your_root_password

    REDIS_HOST=redis
    REDIS_PORT=6379
    REDIS_PASSWORD=your_redis_password

    JWT_SECRET_KEY=your_jwt_secret
    JWT_ACCESS_EXPIRATION=3600000
    JWT_REFRESH_EXPIRATION=1209600000

    SPRING_PROFILES_ACTIVE=dev
---

### 5. 어플리케이션 빌드
Docker는 .jar 파일을 사용하므로 먼저 Spring Boot 프로젝트를 빌드해야 합니다:

    cd backend
    ./gradlew clean build -x test

---

### 6. Docker Compose 실행
로컬 개발 환경 실행 (dev):

    docker compose --env-file deploy/env/.env.dev -f deploy/compose/compose.yml -f deploy/compose/compose.dev.yml up -d

상태 확인:

    docker ps

### 7. Docker Compose 종료
종료하려면 다음 명령어를 실행합니다:

    docker compose --env-file deploy/env/.env.dev -f deploy/compose/compose.yml -f deploy/compose/compose.dev.yml down

### 8. Docker 기타 명령어


| 명령어 | 설명 |
|--------|------|
| `docker compose up -d` | 백그라운드(detached)로 컨테이너 실행 |
| `docker compose down` | 모든 컨테이너, 네트워크, 볼륨 중지 및 제거 |
| `docker compose restart` | 모든 컨테이너 재시작 |
| `docker compose ps` | 현재 실행 중인 컨테이너 목록 확인 |
| `docker compose logs -f` | 모든 컨테이너의 로그 실시간 출력 (follow 모드) |
| `docker compose logs -f <서비스명>` | 특정 서비스의 실시간 로그 출력 |
| `docker compose build` | `Dockerfile`을 기반으로 이미지 수동 빌드 |
| `docker images` | 현재 로컬에 저장된 이미지 목록 확인 |
| `docker ps -a` | 모든 컨테이너 (중지된 것 포함) 목록 확인 |
| `docker stop <컨테이너ID>` | 특정 컨테이너 중지 |
| `docker rm <컨테이너ID>` | 특정 컨테이너 삭제 |
| `docker exec -it <컨테이너명> bash` | 컨테이너 내부로 bash 셸 접속 |
| `docker exec -it <컨테이너명> redis-cli -a <비밀번호>` | Redis CLI 접속 (비밀번호 포함) |
| `docker exec -it <컨테이너명> mysql -u root -p` | MySQL 접속 (비밀번호는 입력 후 직접 입력) |
| `docker volume ls` | 도커 볼륨 목록 확인 |
| `docker volume rm <볼륨명>` | 특정 도커 볼륨 삭제 |
| `docker network ls` | 도커 네트워크 목록 확인 |
| `docker system prune` | 안 쓰는 이미지/컨테이너/네트워크 일괄 정리 (주의!) |
