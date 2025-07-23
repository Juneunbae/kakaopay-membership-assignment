# 카카오페이 서버 개발과제 - 멤버십 서비스

## 개발환경
|SKILL|VERSION|
|--|--|
|Java|21|
|SpringBoot|3.2.5|
|MapStruct|1.5.5|
|MySQL|8.4.4|
|Swagger|2.2.0|
|Redis||
|Redisson|3.23.2|
|Flyway||


## ERD
<img width="1349" height="400" alt="image" src="https://github.com/user-attachments/assets/43448e17-d21b-443e-9564-4dcd31a24b65" />


## 정보
### 📌 API 문서 (Swagger)

애플리케이션 실행 후 아래 URL로 접속 가능 :

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/docs`

---

### ✨ DTO 계층 분리 설계

#### 패키지 구조 예시
```
presentation
│
├── controller
├── dto
│   ├── request
│   │   └── PresentationRequestDto.java
│   └── response
│       └── PresentationResponseDto.java
│
application
│
├── service
├── dto
│   ├── request
│   │   └── RewardPointRequestServiceDto.java
│   └── response
│       └── RewardPointResponseServiceDto.java
```
#### 설계 목적
|구분|목적|
|--|--|
|Presentation DTO|Controller <-> Client 데이터 전달용|
|Application DTO|Controller <-> Service 데이터 전달용|

#### 흐름 예시
- Controller는 클라이언트 요청을 Presentation DTO로 받고, 이를 내부 로직에 맞는 Application DTO로 변환하여 서비스에 전달합니다.
- 반대로 서비스 로직 결과도 Application DTO → Presentation DTO로 변환하여 클라이언트에 반환합니다.
- 변환은 MapStruct를 이용하여 변환했습니다.

#### 분리의 이점
|항목|설명|
|--|--|
|관심사 분리|클라이언트 요청 포맷과 내부 비즈니스 모델의 결합을 방지합니다.|
|유지보수성 향상|클라이언트 요구사항 변경이 있을 때, 내부 로직을 수정할 필요가 없습니다.|
|유연성 증가|다양한 외부 API 포맷을 하나의 Application DTO로 매핑 가능해 확장성이 뛰어납니다.|
|테스트 용이성|내부 로직 테스트 시 외부 포맷에 영향을 받지 않으므로 단위 테스트가 간단합니다.|
|도메인 모델 보호|외부에 도메인 구조가 노출되지 않아 보안성과 안정성이 증가합니다.|

---

### ❗예외 처리 구조

#### 구성 요소
1. BaseErrorCode 인터페이스
```java
public interface BaseErrorCode {
    String getErrorCode();   // 시스템 내부 고유 에러 코드
    String getMessage();     // 사용자에게 전달할 메시지
    HttpStatus getStatus();  // HTTP 응답 상태 코드
}
```

2. enum (예시. MemberErrorCode)
```java
@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    ALREADY_EXISTS("M-001", "이미 존재하는 아이디입니다.", HttpStatus.ALREADY_REPORTED),
    NO_SUCH_SHA_256("M-002", "SHA-256 알고리즘을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND("M-003", "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND);
    
    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
```
- 에러 코드(M-001 등) 를 부여하여 로그, 클라이언트 메시지, API 응답에 공통된 기준을 제공합니다.
- 각 도메인별로 Enum을 따로 만들어 모듈화하고 관리합니다. (예: StoreErrorCode, PointErrorCode 등)

3. 에러 응답 예시
```java
{
  "errorCode": "M-003",
  "status": 404,
  "message": "존재하지 않는 유저입니다."
}
```
- 클라이언트는 errorCode로 예외를 구체적으로 분기 처리할 수 있습니다.
- 메시지는 사용자에게 전달 가능한 형태로 구성합니다.

#### 장점
|항목|설명|
|--|--|
|**일관된 예외 응답 포맷**|다양한 예외 상황에도 동일한 형태로 응답 처리|
| **디버깅 용이**|고유한 `errorCode`로 빠른 원인 추적 가능|
| **유지보수 편리** |도메인별로 `ErrorCode Enum`을 관리하여 변경이 용이|
| **확장성**|새로운 예외 케이스가 생겨도 쉽게 추가 가능|

#### 적용 방식
- 서비스 또는 도메인 계층에서 예외가 발생할 경우, throw new GlobalException(MemberErrorCode.NOT_FOUND) 방식으로 사용합니다.
- 공통 ExceptionHandler(@RestControllerAdvice)에서 BaseErrorCode 기반으로 응답 객체를 가공하여 클라이언트에 반환합니다.

---

### 🆔 사용자 ID 생성기 (UserIdGenerator)

#### 목적
회원 가입 시 고유한 9자리 숫자형 사용자 ID를 생성하기 위해 Redis의 원자 연산을 활용합니다.
UUID나 DB Auto-Increment 대신, 클러스터 환경에서도 안전하게 사용할 수 있는 방식으로 설계했습니다.

#### 구현 방식
```java
public class UserIdGenerator {
    private static final String USER_ID_KEY = "user:id:seq";
    private final RedisTemplate<String, Object> redisTemplate;

    public String generateUserId() {
        Long nextId = redisTemplate.opsForValue().increment(USER_ID_KEY); // Redis INCR
        return String.format("%09d", nextId); // 9자리 문자열 반환 (ex. 000000001)
    }
}
```
- Redis의 INCR 명령을 이용하여 user:id:seq 키의 값을 1씩 증가시킵니다.
- 증가된 값을 String.format("%09d", nextId)로 9자리 숫자 문자열로 포맷합니다.
- 첫 번째 생성된 ID는 000000001부터 시작합니다.

#### 사용 이유
|항목|설명|
|--|--|
|원자성 보장|Redis의 INCR 명령은 원자적으로 처리되어 다중 인스턴스 환경에서도 충돌 없이 ID 생성 가능|
|간단한 관리|DB 테이블 없이 Redis 키 하나로 간단하게 ID 시퀀스 관리|
|가독성 높은 ID|9자리 숫자 형태로 사용자에게도 비교적 읽기 쉬운 ID 제공|
|고성능|Redis를 사용하여 빠른 ID 생성이 가능하며, DB 부하를 줄임|

#### 확장성
- 초당 수천 건의 요청에도 병목 없이 처리 가능하며, 분산 서버 환경에서도 충돌 없는 유저 ID 발급이 가능합니다.
- 향후 서비스가 커져도, 레디스 클러스터 확장으로 쉽게 대응 가능합니다.
  
---

### ✅ 바코드 생성 방식

#### 과정
10자리 고유 숫자 바코드는 다음과 같은 과정을 통해 생성됩니다:

1. UUID 생성
- UUID.randomUUID()를 사용해 무작위 UUID 문자열을 생성합니다.
  
2. SHA-256 해시 적용
- UUID 문자열에 SHA-256 알고리즘을 적용해 고정 길이의 해시 바이트 배열을 얻습니다.

3. 숫자 변환 및 자릿수 제한
- 해시 값을 BigInteger로 변환한 뒤, 10^10으로 모듈러 연산을 수행해 10자리 숫자를 생성합니다.
- String.format("%010d", number)로 앞자리를 0으로 채워 고정된 10자리 숫자로 반환합니다.

예상 결과 : "0119283472"

#### 고유성 및 충돌 가능성
충돌 가능성 
- UUID와 SHA-256 해시의 조합으로 생성된 값은 이론적으로 매우 높은 고유성을 가집니다.
- 10자리 숫자 제한으로 인해 완전한 해시 정보를 사용하지 못해 극단적으로 낮은 확률로 중복 가능성은 존재하지만 사실상 유일한 값으로 간주할 수 있습니다.

#### 예외 처리
SHA-256 알고리즘이 지원되지 않을 경우 GlobalException(MemberErrorCode.NO_SUCH_SHA_256)을 통해 예외가 발생합니다.
