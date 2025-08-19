# restart-spring.sh
# Spring Boot 애플리케이션을 재시작하는 스크립트
# MySQL과 Redis는 유지하면서 Spring Boot 컨테이너만 업데이트

ENVIRONMENT=${1:-dev}  # 기본값은 dev, 인자로 prod 전달 가능

echo "🔧 Environment: $ENVIRONMENT"

# Gradle 빌드
echo "📦 Building Spring Boot JAR with Gradle..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ Gradle build failed!"
    exit 1
fi

# Spring 컨테이너만 중지 및 삭제
echo "🛑 Stopping Spring Boot container..."
docker-compose -f docker-compose.$ENVIRONMENT.yml stop backend
docker-compose -f docker-compose.$ENVIRONMENT.yml rm -f backend

# Spring 컨테이너만 재빌드 및 시작
echo "🚀 Starting Spring Boot container with fresh build..."
docker-compose -f docker-compose.$ENVIRONMENT.yml up --build -d backend

# 컨테이너 상태 확인
echo "📊 Checking container status..."
docker-compose -f docker-compose.$ENVIRONMENT.yml ps

echo "✅ Spring Boot application restart completed!"
echo "📋 Logs: docker-compose -f docker-compose.$ENVIRONMENT.yml logs -f backend"