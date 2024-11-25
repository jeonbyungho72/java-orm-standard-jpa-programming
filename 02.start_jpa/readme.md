# H2 데이터베이스 설치

[H2 공식 사이트](https://www.h2database.com/)에 들어가서 압축 파일을 받은 다음 풀고 `bin/h2.bat`를 실행하면 H2 데이터베이스가 실행된다.  
서버 모드로 실행 한 후 웹 브라우저에서 `http://localhost:8082/`에 들어가면 H2 데이터베이스에 접속할 수 있다.

> H2 데이터베이스는 JVM 메모리 안에서 실행되는 임베디드 모드와 실제 데이터베이스처럼 별도의 서버를 띄워서 동작하는 서버 모드가 있다.

JPA 구현체로 하이버네이트를 사용하기 위한 핵심 라이브러리는 다음과 같다.

- `hibernate-core`: 하이버네이트 라이브러리
- `hibernate-entitymanager`: 하이버네이트 JPA 구현체로 동작하도록 JPA 표준을 구현한 라이브러리
- `hibernate-jpa-api`: JPA 표준 API를 모아둔 라이브러리

# 메이븐 의존성 추가

```xml
<dependencies>
    <!-- JPA hibernate -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-entitymanager</artifactId>
        <version>5.6.15.Final</version>
    </dependency>
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.3.232</version>
    </dependency>
</dependencies>
```

# 객체 매핑 시작

## MEMBER 테이블

```sql
CREATE TABLE MEMBER (
    id bigint NOT NULL,
    name varchar(255),
    age integer,
    PRIMARY KEY (id)
);
```

## Member 클래스

```java
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    private Long id;
    @Column(name = "name")
    private String username;
    private Integer age;
}
```

### 매핑 정보

매핑 정보| 회원 객체| 회원 테이블
--|--|--
클래스와 테이블| `Member`| `MEMBER`
기본 키| `id` | `id`
필드와 컬럼| `username`| `name`
필드와 컬럼| `age`| `age`

### JPA가 제공하는 매핑 어노테이션

#### `@Entity`

클래스를 테이블과 매핑한다고 JPA에게 알려준다. 이렇게 `@Entity`가 사용된 클래스를 **엔티티 클래스**라고 부른다.

#### `@Table`

엔티티 클래스에 매핑할 테이블 정보를 알려준다. `name` 속성에 테이블의 이름을 적으면 매핑이 된다. 이 어노테이션을 생략하면 엔티티 클래스 이름을 테이블 이름으로 매핑한다.

#### `@id`

엔티티 클래스의 필드를 테이블의 기본 키와 매핑한다. 이렇게 `@id`가 사용된 필드를 **식별자 필드**라고 부른다.

#### `@Column`

필드를 컬럼에 매핑한다. `name` 속성에 테이블의 컬럼명을 적으면 이 어노테이션에 사용된 필드와 매핑된다.  
필드에 매핑 어노테이션을 생략할 경우, 필드명을 사용해서 컬럼명과 매핑된다.

## `persistence.xml` 설정

JPA는 `persistence.xml`을 사용해서 필요한 설정 정보를 관리한다. 이 설정 파일은 `META-INF/persistence.xml` 클래스 패스 경로에 있을 경우 별도의 경로 설정 없이 JPA가 인식한다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
    version="2.1">
    <persistence-unit name="jpabook">
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
            <property name="javax.persistence.jdbc.user" value="sa" />
            <property name="javax.persistence.jdbc.password" value="" />
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/student" />
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect" />
        </properties>
    </persistence-unit>
</persistence>
```

- 영속성 유닛(persistence-unit): 연결할 데이터베이스당 하나의 영속성 유닛을 등록한다. `name` 속성은 영속성 유닛의 고유한 이름을 의미한다.
    ```xml
    <persistence-unit name="jpabook">
        <!-- ... -->
    </persistence-unit>
    ```

- JPA 표준 속성
    - `javax.persistence.jdbc.driver`: JDBC 드라이버
    - `javax.persistence.jdbc.user`: 데이터베이스 접속 사용자 계정
    - `javax.persistence.jdbc.password`: 접속할 사용자 계정 비밀번호
    - `javax.persistence.jdbc.url`: 데이터베이스 접속 URL

- 하이버네이트 속성
    - `hibernate.dialect`: 데이터베이스 방언 설정
    - `hibernate.show_sql`: 하이버네이트가 실행한 SQL을 콘솔창에 출력한다.
    - `hibernate.format_sql`: 하이버네이트가 실해나 SQL을 출력할 때 보기 쉽게 정렬해준다.
    - `hibernate.use_sql_comments`: 쿼리를 출력할 때 주석도 함께 출력한다.
    - `hibernate.id.new_generator_mappings`: JPA 표준에 맞춘 새로운 키 생성 전략을 사용한다.
    - `hibernate.hbm2ddl.auto`: 애플리케이션 실행 시점에서 데이터베이스 테이블을 자동으로 생성한다.
        - `create`: 기존 테이블을 삭제하고 새로 생성한다. (`DROP + CREATE`)
        - `create-drop`: 기존 테이블을 삭제하고 새로 생성한다. 애플리케이션을 종료하면 생성했던 DDL를 제거한다.(`DROP + CREATE + DROP`)
        - `update`: 데이터베이스 테이블과 엔티티 매핑정보를 비교해서 변경 사항만 수정한다.
        - `validate`: 데이터베이스 테이블과 엔티티 매핑정보를 비교해서 차이가 있으면 경고를 남기고 애플리케이션을 실행하지 않는다. (이 설정은 DDL을 수정하지 않는다.)

JPA 표준 속성(`javax.persistence`로 시작하는 속성들)은 특정 구현체에 종속되지 않는다.  
반면 하이버네이트 속성(`hibernate`로 시작하는 속성들)은 하이버네이트 전용 속성이므로 하이버네이트에서만 적용된다.

### 데이터베이스 방언

각 데이터베이스가 제공하는 SQL 문법과 데이터 타입, 함수가 조금씩 다른데, 이처럼 SQL 표준을 지키지 않거나 특정 데이터베이스만의 고유한 기능을 JPA에서 **방언**이라고 한다.  

대부분의 JPA 구현체들은 다양한 방언 클래스를 제공한다. 개발자는 JPA가 제공하는 표준 문법에 맞추어 JPA를 사용하면 되고, 특정 데이터베이스에 의존적인 SQL은 데이터베이스 방언이 처리해준다.  
따라서 데이터베이스가 변경되어도 코드 변경할 필요 없이 데이터베이스 방언만 교체하면 된다. 참고로 데이터베이스 방언을 설정하는 방법은 JPA에 표준화되어 있지 않음.

하이버네이트에서는 h2, mysql, 오라클 등 데이터 베이스마다 각각 방언들이 제공한다.