#!/bin/bash

# logs.sh
# 특정 환경의 Spring Boot 로그 확인

ENVIRONMENT=${1:-dev}

echo "📋 Showing logs for $ENVIRONMENT environment..."
docker-compose -f docker-compose.$ENVIRONMENT.yml logs -f backend