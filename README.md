# Shopping Management System

전자상거래 시스템 API – 의류 쇼핑몰(자체 브랜드)

## 프로젝트 개요

## 팀원

| Name | Email                  | GitHub                                   |
|------|------------------------|------------------------------------------|
| 김예일  | qorentks2103@gmail.com | [@yeil](https://github.com/yeilkk)       |
| 김기찬  | repitor15@gmail.com    | [@gichan](https://github.com/gichan222)  |
| 손성전  | sonsungjeon@gmail.com  | [@sungjeon](https://github.com/AkiStory) |
| 유의진  | yuuijin863@gmail.com   | [@ejinn](https://github.com/ejinn1)      |

### 역할 분담

- **김예일 (팀장)**
    - 상품(Product) 도메인
    - 주문(Order) 도메인
    - 로그 및 이벤트 수집 시스템 구축
- **김기찬 (팀원)**
    - 리뷰(Review) 도메인
    - 주문(Order) 도메인
    - 채팅 서비스 기능
- **손성전 (팀원)**
    - 장바구니(Cart) 도메인
    - 배송(Delivery) 도메인
    - RAG AI챗봇 구축
- **유의진 (팀원)**
    - 회원/관리자(User) 도메인
    - 결제(Payment) 도메인
    - 결제 서비스 기능

### 💠 목표

- 역할 분담에 따른 책임 있는 기능 개발 수행
- 적극적인 코드 리뷰(Pull Request) 기반의 협업 진행
- 데드라인 준수 및 일정 관리
- 배치 처리 과정 학습 및 실제 기능에 적용
- Redis 기반 캐싱 및 세션 클러스터링 활용
- 객체지향 프로그래밍 원칙 준수
- 코드 컨벤션 일관성 유지
- 가독성 향상을 위한 적절한 주석 유지
- 안정적인 전자상거래 시스템 구축
- 다양한 사용자 편의 기능 제공
- 확장 가능한 서비스 아키텍처 설계
- 효율적인 운영 시스템 구축

### 💠 예상 성과

- 이론 기반의 심화 기술을 프로젝트 전반에 적용
- 요구사항 분석 후 전체 기능 구현 완료
- API 성능 고려(캐싱·트랜잭션·쿼리 최적화 등)
- 안정적인 서버 아키텍처 구성
- 효율적인 Git 브랜치 및 PR 관리
- README 및 기술 문서 기반의 체계적인 문서화
- 성능 테스트 기반의 객관적 성능 개선
- 테스트 기반의 기능 안정성 검증
- 로그 및 이벤트 수집 시스템 구축
- 쿼리, 인덱스, 캐싱 기반의 성능 최적화
- 기존 서비스 고도화 및 부가 기능 제공 (결제, 채팅, 챗봇)

### 💠 기술 스택

````
- 언어 및 프레임워크 : Java (Spring Boot), Spring AI
- Database : MySQL, Redis
- API 문서화 : Swagger+OpenAPI / Postman
- 버전 관리 : GitHub
- 테스트 도구 : Postman, Swagger UI
- 인프라 : Docker
- 배포 환경 : AWS
- 빌드 도구: Gradle
- 메시지 / 이벤트 : Kafka
- 로그 모니터링
- OpenSearch, ELK Stack (Elasticsearch · Logstash · Kibana)
- Prometheus, Grafana
- 성능 테스트: K6 / JMeter
````

### 💠 사용 도구

    - IDE : IntelliJ
    - DB 설계 도구 : ERDCloud, dbdiagram.io
    - 문서화 : Swagger, Notion
    - 협업 도구 : GitHub, Jira

## 프로젝트 구조

``` 
admin.com.shop.(api).
├── controller/          # REST API 컨트롤러
├── service/             # 비즈니스 로직
├── request/             # 데이터 전송 객체
└── response/            # 데이터 전송 객체

user.com.shop.(api).
├── controller/          # REST API 컨트롤러
├── service/             # 비즈니스 로직
├── request/             # 데이터 전송 객체
└── response/            # 데이터 전송 객체

core.com.shop.
├── config/              # QueryDsl
├── repository/          # 데이터 액세스 계층
└── model/               # 도메인 모델

auth.com.shop.
chat.com.shop.
elk

common.com.shop.
integration.com.shop.
```

## 시작하기

### 필요 요구사항

- Java 21 이상
- Gradle 8.14.3

### docker 설정

````
- local-mysql:
- container_name: techup-mysql
- Username: root
- Password: 1234
- ports:"3306:3306"
````

````
- local-redis:
- container_name: techup-redis
- ports:"6379:6379"
````

### API 문서 (Swagger UI)

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

### admin

- http://localhost:8080/api/v1/swagger-ui.html

### user

- http://localhost:8081/api/v1/swagger-ui.html

## 멀티 모듈 서버

### admin

http://kt-techup-1-admin-env.eba-thmzphdi.ap-northeast-2.elasticbeanstalk.com/api/v1/swagger-ui/index.html

### user

http://kt-techup-1-user-env.eba-thmzphdi.ap-northeast-2.elasticbeanstalk.com/api/v1/swagger-ui/index.html

## 주요 기능 (기능 추가 예정)

````
쇼핑몰 백엔드 – 기능 명세서
- 도메인: 회원/인증, 관리자, 상품(Catalog), 재고(Inventory), 장바구니, 주문, 결제, 배송, 리뷰, 멤버십, 쿠폰
- 주요 플로우:
    - 사용자: 회원가입 → 로그인 → 상품 탐색 → 장바구니 → 주문 생성 → 결제 → 배송/리뷰
    - 관리자: 관리자 로그인 → 상품/재고 운영 → 주문/배송/CS 처리 → 리뷰/멤버십/쿠폰 운영
````

## 1. 공통 (인증/권한)

- 아이디/비밀번호 검증, 액세스 토큰 발급
- 회원가입

---

## 2. 상품 API

### 2.1 사용자/공통

    - 상품 목록 조회 및 검색, 필터/정렬 + 페이지네이션
    - 상품 상세 조회(가격, 상태, 품절 여부, 할인 정보)

### 2.2 관리자 전용

    - 관리자용 상품 목록 조회 및 검색, 필터/정렬 + 페이지네이션
    - 관리자용 상품 상세 조회(가격, 상태, 재고/품절 여부, 할인 정보)
    - 상품 등록(필수 필드 검증, 초기 재고/노출 여부 설정)
    - 상품 상세 정보 수정(상품명, 설명, 가격, 상태, 노출 여부, 할인 등)
    - 상품 상태 품절
    - 상품 상태 활성화
    - 상품 상태 비활성화
    - 상품 상태 삭제
    - 재고 관리 (재고관리 페이지가 존재)
        - 재고 조회
        - 재고 수정

---

## 3. 장바구니 API (로그인 사용자)

    - 내 장바구니 목록 조회(상품 정보 + 수량 + 현재 가격)
    - 장바구니 담기(상품ID, 수량) – 재고/상태 검증
    - 수량 수정 (1이상의 범위에서만 수정 가능)
    - 장바구니 상품(리스트로 받아서) 삭제
    - 장바구니 전체 비우기
    - 장바구니 결제 완료 이후 삭제 , 주문 취소시 복구

> → 비로그인 상태에서 위 API 호출 시 401 + “로그인 필요” 응답.
> 비로그인 장바구니는 요구사항 도출만, 구현 보류
---

## 4. 주문 API

    - 주문생성
    - 결제 완료 이후 등록
    - 주문 생성
    1. 사용자 정보 조회
        - 이름, 주소
    2. 내 주문 목록 조회
        - 주문번호 + 상품 리스트
    3. 주문 상세 조회
        - 상품별 원가, 할인율, 최종금액 
    - 결제 완료 후 배송 시작 전 이름/주소/전화번호 수정
    - 주문 목록
    - 주문 상세 정보
        - 결제 완료 이후
            - 배송/결제 상태 (결제 대기중 → 결제완료(배송 대기중) → 배송중 → 배송완료)
    - 주문 취소(상태 변경, 재고 복원 여부 정책에 맞게 처리)

---

## 5. 결제 API

    - 결제 요청 등록
        - 결제 타입(카드/계좌이체/가상계좌 등) + 결제금액 저장
        - 주문 상태를 PAYMENT_PENDING 등으로 변경
    - 주문별 결제 정보 조회.
    - Redis pub/sub으로 결제 완료 알림 보내주기 → 주문 테이블에 주문 완료 → 결제 완료
    - (배치/내부용) 계좌이체 입금 확인 후:
        - 결제 상태 COMPLETED, 주문 상태 PAID로 변경

---

## 6. 배송 API

    - 관리자 : 배송 상태 변경(주문 접수 → 준비 → 배송 시작 → 배송 완료)
    - 단건, 일괄
    - 14시 Cut-off 로직은 배치/관리자 화면에서 처리

---

## 7. 리뷰 및 평점 API

### 7.1 로그인 사용자

    - 리뷰 작성(내용, 구매자 여부 검증)
    - 내 리뷰 삭제
    - 리뷰 좋아요/싫어요 (토큰으로)
    - 내 리뷰 목록 조회

### 7.2 관리자

    - 리뷰에 대한 관리자 댓글 작성
    - 리뷰를 외래키로 갖는 테이블 필요
    - 정책 위반 리뷰 삭제

### 7.3 비로그인 사용자(로그인 사용자 포함)

    - 리뷰 목록 조회 + 페이지네이션
        - → 최신순 리뷰 목록 조회
        - → 좋아요순 리뷰 목록 조회
    - 유저의 댓글 모두 조회
    - 리뷰 상세 조회
    - 글자수(ex.100자 넘으면 짤림처리해서 전달해주고 상세조회에서 보이게)

---



---

## Branch Strategy

본 프로젝트의 **Git Flow**를 설명합니다.
**브랜치 전략과 협업 규칙을 명확히 정의하여 안정적이고 예측 가능한 개발 흐름을 유지**합니다.

```

main          ← 최종 배포 / 릴리스 브랜치. 태그로 버전 관리.
└── develop   ← 통합 테스트 및 개발 중간 병합 브랜치 
└── feature/*  ← 기능별 단위 작업 브랜치

```

### 브랜치 역할

| 브랜치         | 역할                                      | 머지 대상                         |
|-------------|-----------------------------------------|-------------------------------|
| `main`      | 배포용 브랜치. 항상 안정 상태 유지. 태그로 버전 관리.        | (릴리스 단계에서) `develop` → `main` |
| `develop`   | 통합 테스트 및 스테이징 용도. **직접 커밋 금지**, PR만 머지. | `feature/*` → `develop`       |
| `feature/*` | 기능 단위 개발 브랜치 (ex: `feature/login`)      | `develop`                     |

---

## Workflow Guidelines

### 2️⃣ 커밋 컨벤션

- (깃이모지) [티켓] {내용} -
  ex) ✨ [KAN-01] ~~~~

### 🔄Pull Request

- PR 템플릿
- PR 제목 : ex) ✨ [KAN-01] ~~~~

1. **기능 개발** 완료 후 → `feature/*` 브랜치에서 PR 생성

2. **통합 테스트 완료 후** → `develop → main` PR 생성

---

## 🛡️ Branch Protection Rules

| 브랜치       | 보호 설정                   | 비고 |
|-----------|-------------------------|----|
| `main`    | 직접 push 금지, 1명 이상 리뷰 필수 |    |
| `develop` | 직접 push 금지, PR 통합만 허용   |    |

---


> 본 레포는 모든 변경을 PR 을 통해 관리하며, 작고 명확한 단위로 병합하는 것을 원칙으로 합니다.

