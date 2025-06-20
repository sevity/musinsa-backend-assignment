
# MUSINSA Backend Assignment API

무신사 백엔드 엔지니어 과제용 **카테고리‑별/브랜드‑별 가격 조회 서비스** 구현체입니다.

---

## 📦 프로젝트 구조

```
com.musinsa
 ├─ controller   – REST API (Price·Brand·Product)
 ├─ service      – 도메인 로직 (PriceService etc.)
 ├─ repository   – Spring Data JPA
 ├─ domain       – JPA 엔티티 & Enum
 ├─ dto          – API 전용 DTO(record)
 └─ common       – 공통 예외·응답·헬퍼
```

## 🚀 빌드 & 실행

```bash
# 1) clone
git clone https://github.com/sevity/musinsa-backend-assignment
cd musinsa-backend-assignment

# 2) test
./gradlew clean test

# 3) run (in‑memory H2)
./gradlew bootRun
```

> H2 콘솔: `http://localhost:8080/h2-console`
> JDBC URL: `jdbc:h2:mem:testdb`

초기 데이터는 `data.sql`(없으면 직접 Brand API 호출)로 주입 됩니다.

---

## 🔗 API 명세

> 모든 응답은 `200 OK` / 실패 시 `ErrorResponse {status, message}`
> 
> 공통 Prefix : `/api/v1`

### 구현1) 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API

```
GET /categories/cheapest-brands
```

```json
{
  "items":[
    {"category":"상의","brand":"C","price":10000},
    {"category":"아우터","brand":"E","price":5000},
    ...
  ],
  "total":34100
}
```

### 구현2) 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API

```
GET /brands/cheapest
```

```json
{
    "brand": "D",
    "categories": [
        {"category": "바지", "price": 3000},
        {"category": "모자", "price": 1500},
        ...
    ],
    "total": 36100
}
```
### 구현3) 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API

```
GET /categories/{category}/price-stats
```
| Path variable | 설명           |
|---------------|----------------|
| `category`    | 카테고리명 |

#### 응답예시 – `category = 상의`


```json
{
    "category": "상의",
    "lowest": [
        {"brand": "C", "price": 10000}
    ],
    "highest": [
        {"brand": "I", "price": 11400}
    ]
}
```



### 구현4) 브랜드 및 상품을 추가 / 업데이트 / 삭제하는 API

| Method | Path | 설명 |
|--------|------|------|
| `POST` | `/brands` | 브랜드 등록 |
| `PUT` | `/brands/{name}` | 브랜드 수정 |
| `DELETE` | `/brands/{name}` | 브랜드 삭제 |
| `POST` | `/products` | 개별 상품 등록 |
| `PUT` | `/products/{id}` | 상품 수정 |
| `DELETE` | `/products/{id}` | 상품 삭제 |

#### 요청 예시 – 브랜드 등록

```json
POST /brands
{
  "brand": "Z",
  "prices": {
    "상의": 10000,
    "아우터": 5000,
    "바지": 3000,
    "스니커즈": 9000,
    "가방": 2000,
    "모자": 1500,
    "양말": 1700,
    "액세서리": 1900
  }
}
```

---

## 🧪 테스트

* **단위 테스트** : 서비스 로직‑단위
* **통합 테스트** : `@SpringBootTest` + H2 DB
* `./gradlew test` 하나로 실행  
---

## ⚙️ 설계 포인트

* **`PriceService` 알고리즘**
    * 카테고리별 최저가는 스트림 최소값 + 누적 합산
    * 단일 브랜드 최저가는 _full‑coverage_ 브랜드만 후보로 두고 O(N) 순회(브랜드 수 N)으로 계산 – 성능·가독성 균형
* **무결성**
    * `Product` 테이블 복합 유니크(`brand_id`,`category`) 로 중복 방지
    * 요청 단 Bean Validation + 서비스 단 중복 체크(MVCC 환경 레벨)
* **예외 처리**
    * `ApiException(status, message)` + `GlobalExceptionHandler` → 일관된 에러 JSON
* **트랜잭션** : 서비스 계층 `@Transactional` 로 CRUD 안정성 확보
* **확장성** : Enum `Category` 한글 ↔︎ 영문 매핑 + `fromKr()`으로 다국어 확장 가능

---

## 🛣️ 향후 개선 아이디어

1. **배치 데이터 초기화** : CSV 업로드 → 서비스 호출.
2. **Query 최적화** : Price 통계에 카테고리‑별 인덱스 추가, JPQL 쿼리.
3. **캐싱 & 모니터링** : Redis 캐싱 + Micrometer/Prometheus.
4. **Swagger (OpenAPI 3)** : `/swagger-ui.html` 자동 문서.
5. **Frontend** : React + Tailwind 로 결과표 UI (Optional).


