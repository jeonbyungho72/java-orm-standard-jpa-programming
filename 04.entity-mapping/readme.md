# 객체와 테이블 매핑 어노테이션

## `@Entity`

클래스를 테이블과 매핑한다고 JPA에게 알려준다. JPA를 사용해서 테이블과 매핑할 클래스는 해당 어노테이션이 필수로 붙어야 한다.  
`@Entity`가 붙은 클래스는 JPA가 관리하는 것으로 **엔티티**라고 부른다.

`@Entity` 적용 시 주의사항,

- 기본 생성자 필수(파라미터가 없는 `public`, `protected`)
- `final` 클래스, `enum`, `interface`, `inner` 클래스에는 사용 불가
- 저장할 필드에는 `final` 금지

속성| 기능
--|--
`name`| JPA에서 사용할 엔티티 이름을 지정한다. 다른 패키지에 같은 이름의 엔티티 클래스와 충돌하지 않도록 주의가 필요(기본값 엔티티 클래스의 이름)

## `@Table`

엔티티와 매핑할 테이블을 지정한다. 생략 시 매핑한 엔티티의 이름을 테이블 이름으로 사용한다.

속성| 기능
--|--
`name`| 매핑할 테이블 이름(엔티티 이름을 사용)
`catalog`| catalog 기능이 있는 데이터베이스에서 catalog를 매핑한다.
`schema`| schema 기능이 있는 데이터베이스에서 schema를 매핑한다.
`uniqueConstraints`| DDL 생성 시 유니크 제약조건을 만든다. (2개 이상 복합 유니크 제약조건 가능) 스키마 자동 생성 기능을 사용해서 DDL을 만들 때만 사용한다.

# 데이터베이스 스키마 자동 생성

JPA는 데이터베이스 스키마를 자동으로 생성하는 기능을 지원한다. 매핑 정보와 데이터베이스 방언을 사용해서 데이터베이스 스키마를 생성한다.

```xml
<!-- persistence.xml -->
<property name="hibernate.hbm2ddl.auto" value="create" />
```

`persistence.xml` 파일에 해당 속성을 추가하면 애플리케이션 실행 시점(`EntityManagerFactory`가 생성되는 시점)에 데이터베이스 테이블을 자동으로 생성한다.  
스키마 자동 생성 기능이 만든 DDL은 운영 환경에서 사용할 만큼 완벽하지 않아 개발환경에서 사용하거나 참고하는 정도로만 사용하는 것이 좋다.

## HBM2DDL 주의사항

운영서버에서는 DDL을 수정하는 옵션은 운영 중인 데이터베이스의 테이블이나 컬럼을 삭제할 수 있기 때문에 사용해서는 안된다.  

- **개발 초기**: `create`, `update`
- **초기 상태로 테스트하는 개발 환경또는 CI 서버**: `create`, `create-drop`
- **테스트 서버**: `update`, `validate`
- **운영 서버**: `validate`, `none`

# DDL 생성 기능

- `@Column`

제약 조건 속성| 기능| 값 타입| 기본값
--|--|--|--
`nullable`| `false`로 지정 시 `not null` 제약조건 추가| `boolean`| `false`
`length`| 문자의 크기 지정| `int`| 255

- 유니크 제약 조건 예시

```java
@Entity
@Table(name = "TableName", uniqueConstraints = {
        @UniqueConstraint(name = "unique_constraint_name", columnNames = { "column_name", ... })
})
public class EntityClass {
    // ...
}
// alter table TableName
//        add constraint unique_constraint_name unique (column_name, ...)
```

DDL 제약 조건을 추가하는 기능들은 단지 DDL을 자동으로 생성할 떄만 사용되고 애플리케이션 실행 동작에는 영향을 주지 않는다. 따라서 개발자가 직접 DDL을 만든다면 사용할 이유가 없다.

# 기본 키 매핑

## 데이터베이스 기본 키 생성 전략

- **직접 할당**: 기본 키를 애플리케이션에서 직접 할당  
기본 키를 직접 할당하려면, 기본 키와 매핑할 필드에 `@id`만 지정
    - 엔티티를 영속성 컨텍스트에 저장하기 전 식별자 값을 직접 할당해야 한다. 만약 식별자 값이 없으면 예외가 발생한다.
- **자동 할당**: 대리 키 사용 방식  
자동 생성 전략을 사용하려면, `@id`을 지정한 필드에 `@GeneratedValue`룰 추가하고 `strategy` 속성에 원하는 키 생성 전략을 선택
    - `GenerationType.IDENTITY`: 기본 키 생성을 데이터베이스에게 위임
        - 데이터베이스에 엔티티를 저장해서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
        - MySQL, PostgreSQL, SQL Server, DB2 등
    - `GenerationType.SEQUENCE`: 데이터베이스 시쿼스를 사용해서 기본 키 할당
        - 데이터베이스 시퀀스에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
        - 오라클, DB2, H2 등
    - `GenerationType.TABLE`: 키 생성 테이블을 생성하고 마치 시퀀스처럼 사용하는 방법
        - 시퀀스 생성용 테이블에서 식별자 값을 획득한 후 영속성 컨텍스트에 저장한다.
        - 모든 데이터베이스에 적용 가능
    - `GenerationType.AUTO`: `IDENTITY`, `SEQUENCE`, `TABLE` 전략들 중 하나를 자동으로 선택한다.
        - 데이터베이스 방언에 따라 알맞은 전략을 선택해준다.
        - 키 생성 전략이 확정되지 않거나 프로토타입 개발 시 편리하게 이용할 수 있다.

오라클은 `SEQUENCE` 제공하지만 MySQL는 지원하지 않고 대신 `AUTO_INCREMENT` 기능을 제공하는 것처럼, 데이터베이스 벤더마다 지원하는 방식이 다르다.

### 권장하는 식별자 선택 전략

- 기본 키는 다음 3가지 조건을 모두 만족해야 한다.
    - null 값 허용하지 않음
    - 유일해야 함
    - 변하면 안됨

- 테이블의 기본 키를 선택하는 전략
    - **Natural key**: 비즈니스에 의미가 있는 키 (주민등록번호, 이메일, 전화번호)
    - **Surrogate Key**: 비즈니스와 관계 없는 임의로 만들어진 키 (시퀀스, auto_increment, 시퀀스 생성용 테이블)

저장된 엔티티의 식별자 값은 절대 변경해선 안된다. 이 경우 예외가 발생하거나 정상 동작하지 않게 된다. setter 메서드처럼 식별자를 수정하는 것들을 외부에 공개하지 않는 등(`private`) 문제를 예방해야 한다.

## 기본 키 직접 할당 전략

기본 키 직접 할당은 `em.persist()`로 엔티티를 저장하기 전(영속 상태로 전환되기 전)에 애플리케이션에서 기본 키를 직접 할당하는 방식(setter 메서드로 속성 값을 바꾸는 등)이다.

```java
EntityClass entity = new EntityClass();
entity.setId(value);
em.persist(entity);
```

`@Id`와 매핑이 가능한 기본 키 필드의 자바 타입

- 기본형
- 래퍼 클래스

> 기본 키 직접 할당 전략에서는 식별자 값 없이 저장하면 예외가 발생한다.(영속 상태 엔티티는 반드시 식별자 값이 있어야 하므로)

## IDENTITY

IDENTITY 전략은 기본 키 생성을 데이터베이스에게 위임한다. 엔티티가 데이터베이스에 저장한 후 식별자 값을 획득한 후 엔티티의 식별자에 할당한다.  
해당 전략은 테이블에 엔티티를 저장해야 식별자 값을 획득할 수 있다.

```java
@Entity
public class EntityClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

> IDENTITY 전략은 식별자 값을 구하려면 엔티티를 데이터베이스에 저장해야 하므로 이 전략은 트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.

## SEQUENCE

SEQUENCE 전략은 데이터 베이스 시퀀스[^1]를 사용해서 기본 키를 생성한다.

1. `em.persist()`를 호출 할 때(엔티티를 영속 상태로 전환할 때), 먼저 데이터베이스 시퀀스를 조회한다
1. 조회한 결과를 엔티티의 식별자에 할당한 후에 엔티티를 영속성 컨텍스트에 저장한다.
1. 이후 트랜잭션을 커밋해서 플러시가 일어나면 데이터베이스에 저장한다.

```java
@Entity
@SequenceGenerator(name = "sequence_generator_name", sequenceName = "entity_seq", initialValue = 1, allocationSize = 1)
public class EntityClass {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator_name")
    private Long id;
    // ...
}
```

- **`@SequenceGenerator`**: 매핑시킬 시퀀스 생성기를 등록한다.
    - `name`: ID Generator 이름, `@GeneratedValue`에 `generator` 속성 이름과 일치 시켜야 한다.
    - `sequenceName`: 데이터베이스 시퀀스의 이름
    - `initialValue`: DDL을 생성 시 초기 값 (기본값 1)
    - `allocationSize`: 시퀀스를 호출할 때마다 증가하는 수(기본 값 50)
    - `catalog`, `schema`: 데이터베이스 catalog, schema 이름

[^1]: **데이터베이스 시퀀스**: 유니크한 값을 순서대로 생성하는 데이트베이스 오브젝트이다.

<!-- ### SEQUENCE 전략과 최적화

... -->

<!-- ## TABLE

TABLE 전략은 키 생성 전용 테이블을 하나 만들고 해당 테이블에 이름과 값을 사용할 컬럼을 만들어 데이터베이스 시퀀스를 흉내내는 전략이다.

... -->