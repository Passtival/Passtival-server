#!/bin/bash

# run.sh flow 요약
# 1. Gradle 빌드
# 2. 기존 Docker 컨테이너 중지 및 삭제
# 3. docker-compose.yml 파일을 사용하여 재빌드 및 실행

## dev 환경에서 실행되는 스크립트
#echo "[dev]📦 Building Spring Boot JAR with Gradle..."
#./gradlew clean build -x test
#echo "[dev]🧹 Stopping and removing existing containers..."
#docker-compose -f docker-compose.dev.yml down
#echo "[dev]🚀 Starting Docker containers with fresh build..."
#docker-compose -f docker-compose.dev.yml up --build -d

 prod 환경에서 실행되는 스크립트
echo "📦 Building Spring Boot JAR with Gradle..."
./gradlew clean build -x test
echo "🧹 Stopping and removing existing containers..."
docker-compose -f docker-compose.prod.yml down
echo "🚀 Starting Docker containers with fresh build..."
docker-compose -f docker-compose.prod.yml up --build -d

