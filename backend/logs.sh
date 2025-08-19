#!/bin/bash

# logs.sh
# 특정 환경의 Spring Boot 로그 확인

ENVIRONMENT=${1:-prod} # 기본값은 prod, 인자로 dev 전달 가능

echo "📋 Showing logs for $ENVIRONMENT environment..."
docker compose -f docker-compose.$ENVIRONMENT.yml logs -f backend