# 연관관계 매핑

JPA는 객체의 참조와 테이블의 외래 키를 매핑해서 객체에서는 참조를 사용하고 테이블에서는 외래 키를 사용할 수 있도록 한다.

## 연관관계 매핑 핵심 키워드

- 방향(Direction)
    - 단반향: 한 쪽만 참조하는 관계 (`EntityA --> EntityB`)
    - 양방향: 양쪽 모두 서로 참조하는 관계(`EntityA <--> EntityB`)
- 다중성(Multiplicity)
    - N:1
    - 1:N
    - 1:1
    - N:M
- 연관관계의 주인(owner): 객체를 양방향 연관관계로 만들면 연관관계의 주인을 정해줘야 한다.

# 단방향 연관관계

-| 연관관계| 연관된 데이터 사용 시
--|--|--
객체| 단방향| 참조를 사용(getter 메서드 등)
테이블| 양방향| `JOIN`를 사용

> 참조로 양방향 연관관계로 만들고 싶으면 반대쪽 객체에도 필드를 추가해서 참조를 보관해야 한다. 정확히는 양방향 관계가 아니라 서로 다른 단방향 관계가 2개 존재하는 것이다.

## 객체 관계 매핑

### `@ManyToOne`

`@ManyToOne`은 N:1 관계에서 사용한다.

속성| 기능| 타입| 기본값
--|--|--|--
`optional`| `false`로 설정하면 연관된 엔티티가 항상 존재해야 한다.| `boolean`| `true`
`fetch`| 글로벌 패치 전략을 사용한다.| `FetchType`| `FetchType.EAGER`
`cascade`| 영속성 전이 기능을 사용한다.| `CascadeType[]`|

### `@JoinColumn`

`@JoinColumn`은 외래 키를 매핑할 때 사용한다.

속성| 기능| 타입| 기본값
--|--|--|--
`name`| 매핑할 외래 키 이름| `String`| 필드명`_`테이블의 기본 키와 매핑된 컬럼명
`referencedColumnName` 외 래키가 참조하는 대상 테이블의 컬럼명| `String`| 참조하는 테이블의 기본 키 컬럼명
`foreignKey`| 외래키 제약조건을 직접 지정할 수 있다. 이 속성은 테이블을 생성할 때만 사용된다.| `ForeignKey`

> `@JoinColumn` 생략 시 외래 키를 필드명`_`테이블의 기본 키와 매핑된 컬럼명으로 찾는다.

```java
class Group {
    @Id
    private Long id;
} 

class Person {
    @ManyToOne
    private Group group; // 외래 키를 찾을 때 group_id
}
```

## 연관관계 사용

### 저장(INSERT)

JPA는 참조한 엔티티의 식발자를 외래 키로 사용해서 적절한 등록 쿼리를 생선한다.

```java
Group group1 = new Group("그룹 A");
em.persist(group1);

Person hong = new Person("홍길동", 20, group1);
em.persist(hong);

Person kim = new Person("김철수", 23, group1);
em.persist(kim);
```

```bash
Hibernate: insert into Groups (group_id, name) values (default, ?)
Hibernate: insert into Person (person_id, age, group_id, name) values (default, ?, ?, ?)
Hibernate: insert into Person (person_id, age, group_id, name) values (default, ?, ?, ?)
```

### 조회(SELECT)

- **객체 그래프 탐색**: 객체를 통해 연관된 엔티티를 조회하는 것
    ```java
    Person findPerson = em.find(Person.class, 1L);
    System.out.println(findPerson.getGroup().getName());
    ```

    ```
    Hibernate: 
    select
        person0_.person_id as person_i1_1_,
        person0_.age as age2_1_,
        person0_.group_id as group_id4_1_,
        person0_.name as name3_1_
    from
        Person person0_
    inner join
        Groups group1_
            on person0_.group_id=group1_.group_id
    where
        group1_.name=?
    ```
- **객체지향 쿼리 사용**: SQL와 비교를 하면 테이블이 아닌 엔티티를 대상으로 하고 SQL보다 간결하다.
    ```java
    final String jpql = "select p from Person p join p.group g where g.name=:groupName";
    // :groupName 처럼 :로 시작하는 것은 파라미터를 바인딩하는 문법이다.

    List<Person> resultList = em.createQuery(jpql,
                Person.class).setParameter("groupName", "그룹 A").getResultList();

    resultList.stream().forEach(p -> System.out.println(p.getGroup().getName()));
    ```

### 수정(UPDATE)

연관관계를 수정할 때도 참조하는 대상만 변경하면 트랜잭션을 커밋하는 시점에 플러시가 일어나면서 변경 감지 기능이 작동하고 변경사항은 데이터베이스에 자동으로 반영한다.  

```java
Person findPerson = em.find(Person.class, 1L);
findPerson.setGroup(group2);
```

```
Hibernate: update Person set age=?, group_id=?, name=? where person_id=?
```

### 연관관계 삭제

- **연관관계 해제**: 연관된 엔티티의 참조 필드의 값을 null로 설정해주면 된다.
    ```java
    Person findPerson = em.find(Person.class, 1L);
    findPerson.setGroup(null);
    ```

    ```
    Hibernate:
    update
        Person
    set
        age=?,
        group_id=?,
        name=?
    where
        person_id=?
    ```
- **연관관계인 엔티티 제거**: 연관된 엔티티를 삭제하려면 기존에 있던 연관관계를 먼저 제거하고 삭제해야 한다. 그렇지 않으면 외래 키 제약조건으로 인해, 데이터베이스에서 오류가 발생한다.
    ```java
    final String jpql = "select p from Person p join p.group g";

    List<Person> resultList = em.createQuery(jpql,
            Person.class).getResultList();

    resultList.stream().forEach(p -> p.setGroup(null));

    em.remove(group1);
    ```

    ```
    Hibernate: delete from Groups where group_id=?
    ```