# ResponseEntity 및 HTTP Status 정합성 개선 작업 정리

## 배경
`BaseException` 발생 시 응답 바디의 `code`는 404/400/500 등으로 내려가지만, 실제 HTTP status는 200으로 반환되고 있었습니다.
이로 인해 Prometheus 지표(`http_server_requests_seconds_count{status="404"}`)에 404가 누적되지 않는 문제가 있었습니다.

## 목표
1. 예외 응답의 실제 HTTP status를 바디 `code`와 일치시키기
2. Controller API 응답을 `ResponseEntity` 기반으로 통일하기
3. 누락 API가 없는지 전수 점검하기

## 변경 내용

### 1) 전역 예외 처리기 수정
- 파일: `src/main/java/com/passtival/backend/global/handler/GlobalExceptionHandler.java`

#### 주요 변경
- `@ExceptionHandler` 메서드 반환 타입을 `BaseResponse<?>` -> `ResponseEntity<BaseResponse<?>>`로 변경
- `BaseResponseStatus.code`를 실제 HTTP status에 반영
  - `ResponseEntity.status(HttpStatusCode.valueOf(...)).body(...)`
- 대상 핸들러
  - `handleBaseException`
  - `handleValidationException`
  - `handleBadRequest`
  - `handle404`
  - `handleInternalServerError`

#### 효과
- 비즈니스 예외(`PERFORMANCE_NOT_FOUND` 등) 발생 시 HTTP status 404로 기록
- Prometheus에서 `status="404"`로 집계 가능

---

### 2) Controller 응답 타입 통일
- `*Controller.java`의 API 메서드 반환을 `ResponseEntity<...>`로 통일
- 기존 `return BaseResponse.success/fail(...)`를 `return ResponseEntity.ok(BaseResponse...)` 형태로 변경

#### 변경된 주요 파일
- `src/main/java/com/passtival/backend/global/security/controller/TokenController.java`
- `src/main/java/com/passtival/backend/global/s3/controller/S3Controller.java`
- `src/main/java/com/passtival/backend/domain/lostfound/controller/LnfController.java`
- `src/main/java/com/passtival/backend/domain/authenticationkey/controller/test/AuthenticationKeyTestController.java`
- `src/main/java/com/passtival/backend/domain/matching/controller/MatchingApplicantController.java`
- `src/main/java/com/passtival/backend/domain/matching/controller/MatchingController.java`
- `src/main/java/com/passtival/backend/domain/member/controller/MemberController.java`
- `src/main/java/com/passtival/backend/domain/admin/controller/AdminLnfController.java`
- `src/main/java/com/passtival/backend/domain/raffle/controller/PrizeController.java`
- `src/main/java/com/passtival/backend/domain/admin/controller/SeedController.java`
- `src/main/java/com/passtival/backend/domain/admin/controller/AdminAuthController.java`
- `src/main/java/com/passtival/backend/domain/raffle/controller/RaffleController.java`
- `src/main/java/com/passtival/backend/domain/admin/controller/AdminRaffleController.java`
- `src/main/java/com/passtival/backend/domain/matching/controller/test/AuthTestController.java`
- `src/main/java/com/passtival/backend/domain/matching/controller/test/MatchingTestController.java`
- `src/main/java/com/passtival/backend/domain/festival/booth/controller/BoothController.java`
- `src/main/java/com/passtival/backend/domain/festival/performance/controller/PerformanceController.java`

---

### 3) Redirect API도 ResponseEntity로 통일
기존 `RedirectView` 반환 메서드도 `ResponseEntity<Void>` + `302 FOUND` + `Location` 헤더 방식으로 변경

- `src/main/java/com/passtival/backend/global/security/controller/BlockController.java`
- `src/main/java/com/passtival/backend/domain/member/controller/MemberController.java`

---

## 검증 결과

### 컴파일 검증
- 실행: `./gradlew compileJava`
- 결과: **BUILD SUCCESSFUL**

### 누락 검증
- `public BaseResponse<...>` 반환 컨트롤러 메서드: **0건**
- `return BaseResponse.success/fail(...)` 직접 반환: **0건**
- 매핑 API(`@Get/@Post/@Patch/@Delete/@Put`) 중 `ResponseEntity` 미사용: **0건**

## 기대 효과
1. 에러 응답의 HTTP status가 실제 의미와 일치
2. Prometheus HTTP status 기반 지표 정확성 향상
3. API 응답 규약이 `ResponseEntity` 중심으로 일관화

## 참고
- 성공 응답은 현재 대부분 `ResponseEntity.ok(...)`로 통일되어 있습니다.
- 필요 시 후속으로 생성 API(`POST`)는 `201 Created`, 삭제 API는 `204 No Content`로 세분화할 수 있습니다.
