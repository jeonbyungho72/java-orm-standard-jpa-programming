# 엔티티 매니저 팩토리와 엔티티 매니저

- **엔티티 매니저 팩토리**: 엔티티 매니저를 생성해주는 공장
    - 엔티티 매니저 팩토리 생성해주는 코드
    ```java
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    ```
    - 생성하는데 비용이 상당히 크다. 따라서 한 개만 생성해서 애플리케이션 전체에 공유하도록 설계되어 있다.
    - 여러 스레드가 동시에 접근해도 안정하므로 서로 다른 스레드간 공유가 허용된다.

- **앤티티 매니저 팩토리**: 엔티티를 저장, 수정, 삭제, 조회 등 엔티티와 관련된 모든 일을 처리한다. 개발자 입장에서 엔티니 매니저는 엔티티를 저장하는 가상의 데이터베이스로 생각하면 된다.
    - 앤티티 매니저 팩토리에서 엔티티 매니저를 생성하는 코드
    ```java
    EntityManager em = emf.createEntityManager();
    ```
    - 엔티티 매니저는 생성하는 비용이 거의 들지 않는다.
    - 여러 스레드가 동시에 접근하면 *동시성 문제*가 발생하므로 스레드 간에 공유가 절대 허용되지 않는다.

하나의 엔티티 매니저 팩토리에서 다수의 엔티티 매니저를 생성하고, 엔티티 매니저는 데이터베이스 연결이 꼭 필요한 시점이 아니라면 당장 커넥션을 연결하지 않는다. 예를 들어 트랜잭션을 시작할 때 커넥션을 획득한다.

J2EE 환경(스프링도 포함됨)에서 JPA 구현체들(하이버네이트 포함한)은 엔티티 매니저 팩토리를 생성할 때 커넥션 풀도 생성하는데 해당 환경에서의 해당 컨테이너가 제공하는 데이터소스를 사용한다.

# 영속성 컨텍스트(persistence context)

영속성 컨텍스트는 *엔티티를 영구적 저장하는 환경*이라는 뜻이다.  
엔티티 매니저로 엔티티를 저장하거나 조회하면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관한다.

- 영속성 컨텍스트는 논리적인 개념에 가깝고 눈에 보이지 않는다.
- 영속성 컨텍스트는 엔티티 매니저를 생성할 때 하나 만들어진다. 
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근, 관리할 수 있다.
    - 물론 여러 엔티티 매너지가 같은 영속성 컨텍스트에 접근할 수도 있다.


## 엔티티의 생명주기

- 비영속(`new`, `transient`): 영속성 컨텍스트와 관계가 전혀 없는 상태
- 영속(`managed`): 영속성 컨텍스트에 저장된 상태
- 준영속(`detached`): 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제(`removed`): 삭제된 상태

### 비영속

당장 엔티티 객체를 생성했을 때, 순수한 객체 상태이며 아직 영속성 컨텍스트에 저장되지 않는 상태이다. 영속성 컨텍스트나 데이터베이스와 전혀 관련이 없는 상태이다.

### 영속

엔티티 매니저를 통해 엔티티를 영속성 컨텍스트에 저장하면 이 엔티티는 영속성 컨텍스트가 관리되는 영속 상태가 된다. 결국 영속 상태란 영속성 컨텍스트에 의해 관리된다는 뜻이다.

`em.persist()`을 호출해서 엔티티를 영속성 컨텍스트에 저장하거나 `em.find()`으로 조회한 엔티티도 영속성 컨텍스트에 의해 관리하는 영속 상태가 된다.

### 준영속

영속성 컨텍스트가 관리하던 영속 상태의 엔티티를 영속성 컨텍스트가 관리하지 않으면 준영속 상태가 된다.

`em.detach()`를 호출하여 특정 엔티티 준영속 상태로 만들 수 있다.  
`em.close()`를 호출하여 영속성 컨텍스트를 닫거나 `em.clear()`를 호출해서 영속성 컨텍스트를 초기화해도 영속성 컨텍스트가 관리하던 영속 상태의 엔티티를 준영속 상태가 된다.

### 삭제

엔티티를 영속성 컨텍스트와 데이터베이스에서 삭제한다. `em.remove()`을 호출하면 특정 엔티티가 삭제 상태로 된다.

## 영속성 컨텍스트의 특징

- **영속성 컨텍스트와 식별자 값**: 영속성 컨텍스트는 엔티티 식발자 값(`@Id`)으로 구별하므로 영속 상태인 엔티티는 반드시 식별자 값이 있어야 한다. 만약 식별자 값이 없을 경우 예외가 발생한다.
- **영속성 컨텍스트와 데이터베이스 저장**: 영속성 컨텍스트에 엔티티를 저장할 때, 보통 트랜잭션을 커밋하는 순간 영속성 컨텍스트에 새로 저장된 엔티티를 데이터베이스에 반영한다. 이것을 **플러시(flush)** 라고 한다.
- **영속성 컨텍스트가 엔티티를 관리했을 때 장점**
    - 1차 캐시
    - 동일성 보장
    - 트랜잭션을 지원하는 쓰기 지연
    - 변경 감자
    - 지연 로딩

### 엔티티 조회

영속성 컨텍스트는 내부 캐시(1차 캐시)를 가지고 있다. 영속 상태의 엔티티는 모두 1차 캐시에 저장된다.

**1차 캐시**는 영속성 컨텍스트 내부에 존재하는 키는 `@Id`로 매핑한 식별자 값, 값은 엔티티 인스턴스인 Map 형태인 저장소이다.  
식별자 값은 데이터베이스 기본 키와 매핑되어 있다. 따라서 영속성 컨텍스트에 데이터를 저장하고 조회하는 모든 기준은 데이터베이스 기본 키 값이다.

### 1차 캐시에서 조회

엔티티를 조회할 때 먼저 1차 캐시에 저장된 식별자 값으로 엔티티를 찾는다. 만약 찾는 엔티티가 있을 경우 데이터베이스에 조회하지 않고 1차 캐시에서 엔티티를 조회한다.(데이터베이스를 거치지 않고 메모리에 있는 1차 캐시에서 엔티티를 조회하여 성능상 이점을 누릴 수 있다.)

### 데이터베이스에서 조회

조회하려는 엔티티가 1차 캐시가 없을 경우, 엔티티 매니저는 데이터베이스를 조회해서 엔티티를 생성한다. 그리고 1차 캐시에 생성한 엔티티를 저장한 후 영속 상태의 엔티티를 반환한다.

### 영속 엔티티의 동일성 보장

서로 식별자 같은 엔티티를 조회할 경우, 영속성 컨텍스트는 1차 캐시에 있는 같은 엔티티 인스턴스를 반환한다. 따라서 같은 식별자를 가진 엔티티끼리는 동일성이 보장된다.

> JPA는 1차 캐시를 통해 반복 가능한 읽기(Repeatable read) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공한다는 장점이 있다.