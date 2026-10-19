# TripMate Order Service

> TripMate의 상품 주문 생성부터 결제 결과 반영, 주문 취소까지 관리하는 주문 마이크로서비스입니다.

Order Service는 여행 일정에 연결된 상품 주문을 관리합니다. 상품 및 일정 참여 가능 여부를 확인하고 주문을 생성하며, 다른 서비스에서 발생한 이벤트를 수신해 주문 상태를 변경합니다. **Spring Cloud OpenFeign**을 이용한 서비스 간 조회와 **Apache Kafka + Transactional Outbox**를 이용한 비동기 이벤트 처리를 함께 사용합니다.

## 주요 기능

| 기능 | 설명 |
| --- | --- |
| 주문 생성 | 일정 참여 승인 여부, 상품/스케줄 판매 상태, 재고 및 중복 주문 검증 후 생성 |
| 주문 조회 | 로그인 사용자 기준 단건 조회 및 최신순 목록 조회 (`Slice` 페이지네이션) |
| 결제 완료 반영 | 결제 완료 Kafka 이벤트 수신 시 주문을 `COMPLETED`로 변경 |
| 주문 취소 | 일정 탈퇴, 인원 초과, 재고 부족 등의 이벤트에 따라 주문 취소 |
| 결제 타임아웃 | 생성 후 15분이 지난 미완료 주문을 주기적으로 확인하고, 결제 데이터가 없으면 취소 |
| 이벤트 발행 | 주문 생성·취소 관련 이벤트를 Outbox에 저장하고 Kafka로 발행 |
| 내부 API | 결제 대상 주문 조회, 주문의 일정 정보 조회, 회원 탈퇴 관련 주문 확인 |

### 주문 생성 규칙

- 한 번의 요청에서는 주문 항목 **1개**, 수량 **1개**만 허용합니다.
- 일정 참여 상태가 `APPROVED`여야 합니다.
- 상품과 상품 스케줄이 모두 `ACTIVE` 상태여야 합니다.
- 구매 가능한 재고가 있어야 합니다.
- 동일 사용자·단위 일정에 `CREATED` 또는 `COMPLETED` 주문이 있으면 중복 주문을 제한합니다.

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.5.13, Spring Cloud 2025.0.2 |
| Database / ORM | PostgreSQL, Spring Data JPA, Hibernate |
| 서비스 통신 | Spring Cloud OpenFeign, Netflix Eureka Client |
| Messaging | Apache Kafka, Spring Kafka |
| Observability | Spring Boot Actuator, Micrometer, Prometheus, Zipkin / Brave |
| Build / Deployment | Gradle, Docker, Docker Compose, Jenkins |
| Testing | JUnit Platform, Spring Boot Test |
| Shared Module | GitHub Packages의 `com.yeoljeong:common-module` |

## 시스템 구성

<img width="1946" height="928" alt="system-diagram" src="https://github.com/user-attachments/assets/e3ca74c3-11c5-4946-af70-3ffd720c9e62" />


- **동기 통신:** 주문 생성 시 Plan Service에서 참여 여부를 확인하고 Company Service에서 상품/스케줄 정보를 조회합니다. 타임아웃 취소 전에는 Payment Service의 결제 정보를 확인합니다.
- **비동기 통신:** 결제 완료 및 일정 참여 변경 이벤트를 소비하고, 주문 생성·취소 관련 이벤트를 발행합니다.
- **Outbox:** 주문 비즈니스 트랜잭션에서 이벤트를 Outbox 테이블에 함께 저장한 뒤 별도의 Dispatcher가 Kafka로 전송합니다. Kafka 전송 실패 시 Outbox의 실패 기록을 갱신합니다.

### 주문 상태

<img width="738" height="924" alt="state-diagram" src="https://github.com/user-attachments/assets/f8114e74-2273-45d7-9222-c79103a0716d" />


| 상태 | 의미 |
| --- | --- |
| `CREATED` | 주문 생성 완료, 결제 완료 전 |
| `COMPLETED` | 결제 완료 이벤트 반영 |
| `CANCELLED` | 주문 취소 완료 (취소 시각 및 사유 기록) |

**취소 사유:** `PLAN_PARTICIPANT_QUIT`(일정 탈퇴), `PLAN_PARTICIPANT_EXCEEDED`(일정 인원 초과), `PRODUCT_STOCK_SHORTAGE`(상품 재고 부족), `PAYMENT_TIMEOUT`(결제 시간 초과)

### 결제 타임아웃 처리

1. 스케줄러가 **매분** `CREATED` 상태이고 생성 후 **15분**이 지난 주문을 조회합니다.
2. 조회는 한 번에 최대 **100건** 단위로 수행합니다.
3. Payment Service에서 해당 주문의 결제 존재 여부를 확인합니다.
4. 결제 데이터가 없으면 `PAYMENT_TIMEOUT` 사유로 취소하고 취소 이벤트를 Outbox에 기록합니다.
5. 외부 서비스 조회 중 오류가 발생하면 해당 주문은 그 실행에서 건너뛰고 오류를 기록합니다.

### Transactional Outbox

주문 생성·취소 처리 중 발행할 이벤트를 먼저 DB에 기록하여 비즈니스 데이터 변경과 이벤트 기록을 하나의 트랜잭션으로 관리합니다. `OrderOutboxDispatcher`는 **1초 간격**으로 대기 중인 이벤트를 최대 **100건** 조회해 Kafka에 발행합니다. 이 구조는 DB 반영 직후 애플리케이션이 중단될 때 발생할 수 있는 이벤트 유실 위험을 줄입니다. 단, 이벤트 전달이 정확히 한 번만 일어나는 것을 보장하지는 않으므로 소비 측 멱등성도 중요합니다.

## API

### 외부 API

인증된 사용자 정보는 공통 모듈의 `@LoginUser`로 전달받습니다. 응답은 공통 `ApiResponse` 형식을 사용합니다.

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/orders` | 주문 생성 |
| `GET` | `/orders/{orderId}` | 로그인 사용자 본인의 주문 단건 조회 |
| `GET` | `/orders` | 로그인 사용자 주문 목록 조회 (`size`, `page`, `sort` 등 Pageable 지원) |

**주문 생성 요청 예시**

```http
POST /orders
Content-Type: application/json

{
  "orderItems": [
    {
      "planUnitId": "11111111-1111-1111-1111-111111111111",
      "productId": "22222222-2222-2222-2222-222222222222",
      "scheduleId": "33333333-3333-3333-3333-333333333333",
      "quantity": 1
    }
  ]
}
```

위 UUID는 요청 형식을 설명하기 위한 예시입니다. 실제 요청에는 유효한 데이터와 인증된 사용자 컨텍스트가 필요합니다.

### 서비스 내부 API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/internal/orders/{orderId}/payment` | 결제 가능 주문 정보 조회 |
| `GET` | `/internal/orders/{orderId}` | 주문에 연결된 `planUnitId` 조회 |
| `GET` | `/internal/orders/withdrawal-check?userId={userId}` | 사용자의 `CREATED` 주문 존재 여부 확인 |


## 프로젝트 구조

```text
src/main/java/com/yeoljeong/tripmate/
├── OrderServiceApplication.java
└── order/
    ├── application/
    │   ├── client/               # 외부 서비스 호출 포트
    │   ├── dto/                  # 명령 및 조회 결과 DTO
    │   ├── port/                 # Outbox 기록 포트
    │   └── service/
    │       ├── command/          # 주문 생성·완료·취소
    │       └── query/            # 주문 조회
    ├── domain/
    │   ├── enums/                # 주문 상태 및 취소 사유
    │   ├── exception/            # 주문 도메인 오류 코드
    │   ├── model/                # Order, OrderItem, ProductInfo
    │   └── repository/           # 저장소 인터페이스
    ├── infrastructure/
    │   ├── config/               # Kafka 설정
    │   ├── external/             # Feign Client 및 Adapter
    │   ├── messaging/            # Kafka Consumer
    │   ├── outbox/               # Outbox 저장 및 발행
    │   ├── persistence/          # JPA 구현
    │   └── scheduler/            # 결제 타임아웃 처리
    └── presentation/
        ├── controller/           # 외부/내부 REST API
        └── dto/                  # 요청 및 응답 DTO
```

## 로컬 실행

### 준비 사항

- JDK 17
- Docker 및 Docker Compose (PostgreSQL / Kafka)
- GitHub Packages의 [common-module](https://github.com/10jeong/common-module)을 내려받을 수 있는 계정과 토큰
- 연동 대상 서비스(Plan, Company, Payment) 및 Eureka 서버: 해당 기능을 호출하려면 실행 또는 접근 가능해야 합니다.

### 1. 저장소 복제

```bash
git clone https://github.com/10jeong/order-service.git
cd order-service
git switch dev
```

### 2. 환경 변수 설정

```bash
export GITHUB_USERNAME=<github-username>
export GITHUB_TOKEN=<github-token-with-package-read-access>
export DB_USER=tripmate
export DB_PASSWORD=<local-db-password>
export DB_NAME=tripmate_postgres
export DB_PORT=5432
export JWT_INTERNAL_SECRET=<shared-internal-jwt-secret>
export EUREKA_SERVER_URL=http://localhost:8761/eureka
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

Windows에서는 사용하는 셸에 맞는 방식으로 환경 변수를 지정하세요. 비밀 값은 저장소에 커밋하지 마세요.


### 3. 인프라 실행

```bash
docker compose up -d
```

### 4. 애플리케이션 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=dev,kafka'
```

기본 `application.yaml`은 `server.port: 0`으로 설정되어 있어 개발 실행 시 사용 가능한 임의 포트를 선택합니다. Eureka에서 인스턴스 포트를 확인하거나 로컬에서 고정 포트를 지정하려면 다음과 같이 실행하세요.

```bash
./gradlew bootRun --args='--spring.profiles.active=dev,kafka --server.port=8080'
```

### 5. 빌드 및 테스트

```bash
./gradlew clean build
./gradlew test
```


## 배포 및 모니터링

- **Jenkins:** 코드 체크아웃 → Gradle 빌드 → Docker 이미지 빌드/푸시 → EC2에서 컨테이너 재생성
- **Docker:** 애플리케이션 이미지 빌드 및 실행
- **Actuator / Prometheus:** 서비스 상태 및 메트릭 수집을 위한 의존성·운영 프로필 설정
- **Micrometer / Zipkin:** 서비스 간 분산 추적 설정

운영 환경은 `application-prod.yaml`의 설정과 배포 서버의 환경 변수에 따라 동작합니다. 

---

**Repository:** [10jeong/order-service](https://github.com/10jeong/order-service)  
**Project:** TripMate
