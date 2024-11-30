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

# 양방향 연관관계

양방향 객체 연관관계에서 1:N 관계는 여러 건과 연관관계를 맺을 수 있으므로 컬랙션(`List`, `Set`, `Map`, `Collection`)을 사용해야 한다.

데이터베이스 테이블은 외래 키 하나로 양방향을 조회할 수 있다. 이미 두 테이블간 연관관계는 외래 키 하나만으로 양방향 조회가 가능하므로 따로 추가할 내용은 없다.

## 양방향 연관관계 매핑

```java
@Entity
class Person {
    @Id
    @Column(name = "person_id")
    private Long id;

    // 연관관계의 주인 필드
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}

@Entity
class Group {
    @Id
    @Column(name = "group_id")
    private Long id;

    // 연관관계에서 주인이 아닌 필드
    @OneToMany(mappedBy = "group")
    private List<Person> persons = new ArrayList<>();
}
```

`@OneToMany`은 1:N 관계에서 사용한다. `mappedBy`속성은 어떤 연관관계를 주인으로 정할 지 설정하는데 사용된다. 반대쪽에 매핑의 필드 이름 값으로 주면 된다.

엔티티를 양방향으로 연관관계로 매핑[^1]하면 각각 서로를 참조하게 된다. 객체의 참조는 둘인데 외래 키는 하나라 둘 사이의 차이가 발생한다.  
이런 차이로 인해 JPA에서는 두 객체 연관관계 중 하나를 정해서 테이블의 외래 키를 관리해야 하는데 이것을 **연관관계의 주인**이라 한다.(연관관계의 주인을 정하는 것 외래 키 관리자를 선택하는 것이다.)

[^1]: 엄밀히 말하면 객체에는 양방향 연관관계는 없다. 서로 다른 단방향 연관관계를 2개로 로직으로 잘 묶어서 양방향인 것처럼 보이도록 할 뿐이다.

- **양방향 매핑의 규칙(연관관계의 주인)**
    - 두 연관관계 중 하나를 연관관계 주인으로 정해야한다.
    - 연관관계의 주인만이 *데이터베이스 연관관계와 매핑*되고 *외래 키를 관리(등록, 수정, 삭제)* 할 수 있다.
    - 주인이 아닌 쪽은 *읽기*만 가능하다.

- 어떤 연관관계를 주인으로 정할 지는 `mappedBy` 속성을 사용하면 된다.
    - 주인은 `mappedBy` 속성을 사용하지 않는다.
    - 주인이 아니면 `mappedBy` 속성을 사용해서 속성의 값으로 연관관계의 주인으로 지정해야 한다.

- 연관관계의 주인은 외래 키의 위치와 관련해서 정해야지 비즈니스 중요도로 접근하면 안된다.
    - 비즈니스 중요도를 배제하고 단순한 외래 키 관리자 정도의 의미만 부여해야 한다.

## 양방향 연관관계의 주의사항

양방향 연관관계를 설정하고 가장 흔히 하는 실수는 연관관계의 주인에는 값을 입력하지 않고, 주인이 아닌 곳에만 값을 입력하는 것이다.

- 주인이 아닌 곳에서만 연관관계를 설정한 코드
    ```java
    // 연관관계의 주인인 Person.group에는 값이 입력되지 않고
    // Group.persons에만 값을 입력한 상태
    Group groupA = new Group("그룹 A");

    Person persons[] = {
            new Person("홍길동", 20),
            new Person("김철수", 23),
            new Person("이영희", 19)
    };

    em.persist(groupA);

    for (Person p : persons) {
        groupA.getPersons().add(p);
        em.persist(p);
    }
    ```

- `Person` 테이블을 조회한 결과
    PERSON_ID|AGE|NAME|GROUP_ID
    --|--|--|--
    1| 20|홍길동 | `null`|
    2| 23|김철수 | `null`|
    3| 19|이영희 | `null`|

외래 키 컬럼에 `null`로 저장되었는데, *연관관계의 주인이 아닌 필드에만 값을 저장했기 떄문이다.*  
연관관계의 주인만이 외래 키의 값을 변경할 수 있다.

### 순수 객체까지 고려한 양방향 연관관계

양방향 연관관계에서 연관관계인 양쪽 모두 값을 입력해주는 것이 가장 안전하다. 양쪽 방향 모두 값을 입력하지 않으면 순수한 객체 상태에서 심각한 문제가 발생한다.

- 연관관계 주인 필드에만 값을 입력하고 반대 방향에는 입력하지 않음.
    ```java
    Group groupA = new Group("그룹 A");

    Person persons[] = {
        new Person("홍길동", 20, groupA),
        new Person("김철수", 23, groupA),
        new Person("이영희", 19, groupA)
    };

    em.persist(groupA);

    for (Person p : persons) {
        em.persist(p);
    }

    tx.commit();

    Group findGroup = em.find(Group.class, 1L);
    List<Person> findPersons = findGroup.getPersons();

    System.out.println(findPersons.size()); // 0
    ```
    - JPA를 사용하지 않는 순수 객체에서는 그룹에 소속된 사람이 몇 명인지 전혀 조회되지 않아 0명으로 출력되는 문제가 발생했다.
- 양방향 연관관계에서는 양쪽 다 관계를 설정해야 한다.
    ```java
    Group groupA = new Group("그룹 A");

    Person persons[] = {
            new Person("홍길동", 20, groupA),
            new Person("김철수", 23, groupA),
            new Person("이영희", 19, groupA)
    };

    tx.begin();

    em.persist(groupA);

    for (Person p : persons) {
        groupA.getPersons().add(p);
        em.persist(p);
    }

    tx.commit();

    Group findGroup = em.find(Group.class, 1L);
    List<Person> findPersons = findGroup.getPersons();

    System.out.println(findPersons.size()); // 3
    ```

- **연관관계 편의 메소드**: setter 메소드를 수정해서 양방향 관계에서 두 코드를 하나로 묶는 것이 안전하다.
    ```java
    @Entity
    class Person {
        @Id
        @Column(name = "person_id")
        private Long id;

        // 연관관계의 주인 필드
        @ManyToOne
        @JoinColumn(name = "group_id")
        private Group group;

        // 연관관계 편의 메서드 : 양방향 관계 모두를 하나의 코드로 묶음
        public void setGroup(Group group) {
            // 다른 엔티티로 변경할 때 기존 엔티티를 제거하는 코드
            // 변경된 연관관계는 관계를 제거해주는 것이 안전하다.
            if (this.group != null)
                this.group.getPersons().remove(this);

            this.group = group;
            this.group.getPersons().add(this);
        }
    }
    ```

    ```java
    Group groupA = new Group("그룹 A");

    Person persons[] = {
            new Person("홍길동", 20),
            new Person("김철수", 23),
            new Person("이영희", 19)
    };

    tx.begin();

    em.persist(groupA);

    for (Person p : persons) {
        p.setGroup(groupA); // 연관관계 편의 메서드
        em.persist(p);
    }
    ```

단방향 매핑은 언제나 연관관계의 주인이며, 양방향은 여기에 연관관계를 하나 추가했을 뿐이다. 단방향과 비교해서 양방향의 장점은 반대 방향으로 객체 그래프 탐색 기능을 추가된 것 뿐이다.

- 단방향 매핑만으로 테이블과 객체의 연관관계 매핑은 이미 완료됨.
- 단방향을 양방향으로 만들면 반대방향으로 객체 그래프 탐색 기능이 추가됨.
- 양방향 연관관계를 매핑하려면 객체에서 양쪽 방향을 모두 관리해야 함.

> 양방향 매핑 시 무한 루프에 빠지지 않게 조심해야 한다. 예를 들어 엔티티에 `toString()`를 사용 시 연관된 엔티티끼리 `toString()`를 호출하면서 무한 루프에 빠질 수 있다.  
이런 문제는 JSON으로 변환할 때도 자주 발생하는데 JSON 라이브러리들은 무한 루프를 빠지지 않도록 하는 기능을 제공한다.  
Lomobok에서 `@ToString()` 사용 시 이러한 무한 루프에 빠지기 쉬운 데, `exclude` 속성을 이용해서 무한 루프를 해결 할 수 있다.  