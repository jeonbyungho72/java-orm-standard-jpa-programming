# 값 타입

JPA의 데이터 타입을 가장 크게 분류하면 엔티티 타입과 값 타입으로 나눈다.

- **엔티티 타입**: `@Entity`로 정의하는 객체
    - 식별자를 통해 지속해서 추적 할 수 있다.
    - 엔티티의 필드 값을 변경해도 같은 엔티티로 인식할 수 있다. (예시로 회원의 키나 나이의 값이 변경되어도 같은 회원 엔티티로 인식할 수 있다.)
- **값 타입**: 자바의 기본형 또는 객체
    - 식별자가 없고 숫자나 문자열 같은 속성만 있으므로 추적이 불가능하다.
    - 숫자 값 처럼 값이 변경하면 다른 값으로 대체된다.

- **값 타입의 종류**
    - **기본 값 타입**(basic value type): 자바가 제공하는 기본 데이터 타입
        - 자바 기본형(primitive type)
        - 래퍼 클래스(wrapper class)
        - String
    - **임베디드 타입**(embedded type)(복합 값 타입): JPA에서 사용자가 직접 정의한 값 타입
    - **컬렉션 값 타입**(collection value type): 하나 이상 값 타입

# 기본 값 타입

엔티티에 식별자 필드(`@id`)는 식별자 값도 가지고 생명주기도 있지만 기본 값 타입(`String`, `int`로 선언된 속성)은 식별자 값도 없고 생명주기도 엔티티에 의존하고 있다. 따라서 엔티티가 제거되면 기본 값 타입의 속성들도 제거된다.  
그리고 값 타입은 공유하면 안된다.(엔티티의 속성 값을 변경 시 다른 엔티티의 속성 값도 변경되는 문제가 발생함)

> 자바에서 기본형(primitive type)은 절대 공유되지 않는다. `a = b` 일 때 `b` 변수의 값을 복사해서 `a`에 입력한다.  
다만 래퍼 클래스(wrapper class)나 String 같은 특수한 클래스는 객체지만 자바에서 기본 타입처럼 사용할 수 있게끔 지원하므로 기본 값 타입으로 정의했다.

# 임베디드 타입

직접 정의한 새로운 값 타입을 **임베디드 타입**이라고 한다.

- `@Embeddable`: 값 타입을 정의하는 곳에 표시
- `@Embedded`: 값 타입을 사용하는 곳에 표시
    - `@AttributeOverride`: 임베디드 타입에 정의한 매핑 정보를 재정의한다.

임베디드 타입은 기본 생성자가 필수다.
임베디드 타입을 포함한 모든 값 타입은 엔티티의 생명주기에 의존하므로 엔티티와 임베디드 관계는 UML로 표현하면 **컴포지션 관계**가 된다.

## 임베디드 타입과 null

임베디드 타입이 `null`이면 매핑한 컬럼 값 모두 `null`이 된다.

```java
entity.setembeddedType(null); // 해당 임베디드 타입의 모든 컬럼의 값은 null이 됨
em.persist(entity);
```

# 값 타입과 불변 객체

값 타입은 복잡한 객체를 단순화하려고 만든 개념이다. 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다.

## 값 타입 공유 참조

임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험하다.  
여러 엔티티가 동일한 임베디드 타입을 공유 참조할 경우, 이 임베디드 타입의 속성 값을 변경할 때 영속성 컨텍스트에서 해당 임베디드 타입을 공유한 엔티티들이 각각 UPDATE SQL가 실행되는 부작용이 발생한다.

## 값 타입 복사

값 타입을 실제 인스턴스 값을 공유하는 것을 위험하므로 값을 복사하여 사용해야 한다. (복사하려는 값 타입의 `clone()`를 사용하는 등)

객체를 대입할 때마다 인스턴스를 복사해서 대입하면 공유 참조를 피할 수 있으나 원본의 참조 값을 직접 넘기는 것을 막을 방법이 없다.  
자바는 대입하려는 것이 값 타입인 지 아닌 지 신경 쓰지 않아 자바 기본형이면 값을 복사하고 객체면 참조 값를 넘길 뿐이다. 그래서 객체의 공유 참조는 피할 수 없다.  

가장 단순한 근본적인 해결책은 객체의 값을 수정하지 못하도록 막는 것이다. (setter와 같은 수정자 메소드를 모두 제거)

## 불변 객체(Immutable Object)

한 번 만들면 절대 변경할 수 없는 객체를 **불변 객체**라 한다.

불변 객체는 값을 조회할 수 있으나 수정할 수 없다. 하지만 불변 객체도 인스턴스의 참조 값 공유를 피할 수 없다. 대신 참조 값을 공유해도 인스턴스의 값을 수정할 수 없으므로 부작용이 발생하지 않는다.

객체를 불변하게 만들면 값을 수정할 수 없으므로 부작용을 원천 차단할 수 있다. 따라서 값 타입은 될 수 있으면 *불변 객체로 설계*해야 한다.

불변 객체를 구현하는 다양한 방법이 있지만 가장 간단한 방법은 생성자로만 값을 설정하고 수정자(setter 등)를 만들지 않으면 된다.  
불변 객체는 값을 수정할 수 없으므로 공유해도 부작용이 발생하지 않는다. 만약 값을 수정해야 한다면 반드시 새로운 객체를 생성해서 사용해야 한다.

> `Integer`와 같은 래퍼 클래스, `String`이 자바가 제공하는 대표적인 불변 객체다.

# 값 타입 비교

- **동일성 비교**(Identity): 인스턴스의 참조 값 비교
    - `==`
- **동등성 비교**(Equivalence): 인스턴스의 값을 비교
    - `equals()`

값 타입은 인스턴스가 달라도 그 안에 값이 서로 같으면 값은 것으로 봐야 한다. 따라서 값 타입을 비교할 때는 *`equals()`를 사용해서 동등성 비교를 해야 한다.*

값 타입의 `equals()`를 재정의할 때는 보통 모든 필드의 값을 비교하도록 구현해야 한다.

> 자바에서 `equals()`를 재정의하면 `hashCode()`도 재정의하는 것이 안전하다. *그렇지 않으면 해시를 사용하는 컬렉션(`HashSet`, `HashMap`)이 정상 동작하지 않는다.* (대부분의 IDE에서 해당 메소드를 자동으로 생성해주는 기능이 있다.)

# 값 타입 컬랙션

값 타입을 하나 이상 저장하려면 컬렉션에 보관하고 `@ElementCollection`, `@CollectionTable`를 사용하면 된다.

## 값 타입 컬렉션의 제약 사항

엔티티는 식별자가 있으므로 엔티티의 값을 변경해도 식별자로 데이터베이스에 저장된 원본 데이터를 쉽게 찾아서 변경할 수 있다.  
반면에 값 타입은 식별자라는 개념이 없고 단순한 값들의 모음이므로 값을 변경해버리면 데이터베이스에 저장된 원본 데이터를 찾기 어렵다.

값 타입 컬렉션에 보관된 값 타입들은 별도의 테이블에 보관된다. 따라서 여기에 보관된 값 타입의 값이 변경되면 데이터베이스에 있는 원본 데이터를 찾기 어렵다는 문제가 있다.  
JPA 구현체들은 값 타입 컬렉션에 변경 사항이 발생하면 값 타입 컬렉션이 매핑된 테이블의 연관된 모든 데이터를 삭제하고, 현재 값 타입 컬렉션 객체에 있는 모든 값을 데이터베이스에 다시 저장한다.

따라서 *값 타입 컬렉션이 매핑된 테이블에 데이터가 많으면 값 타입 컬렉션 대신 1:N 관계를 고려해야 한다.*

추가로 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야 한다. (기본 키 제약 조건으로 인해 커럼에 null을 입력할 수 없고, 동일한 값을 중복해서 저장할 수 없다.)

이러한 문제를 해결하려면 *값 타입 컬렉션 대신 새로운 엔티티를 만들어 1:N 관계로 설정하면 된다.*  
추가로 영속성 전이(CASCADE) + 고아 객체 제거(ORPHAN REMOVE) 기능을 적용하면 값 타입 컬렉션처럼 사용할 수 있다.

> 값 타입 컬렉션을 변경했을 때 데이블의 기본 키를 식별해서 변경된 내용만 반영하려고 노력한다. 하지만 사용하는 컬렉션이나 여러 조건에 따라 기본 키를 식별할 수도 있고 식별하지 못할 수 있다. 따라서 값 타입 컬렉션을 사용할 때는 모두 삭제하고 다시 저장하는 최악의 상황을 고려하면서 사용해야 한다.

# 정리

## 엔티티 타입의 특징

- 식별자(`@Id`)가 있다.
    - 엔티티 타입은 식별자가 있고 식별자로 구별할 수 있다.
- 생명 주기가 있다.
    - 생성하고 영속화하고 소멸하는 생명 주기가 있다.
- 공유할 수 있다.
    - 참조 값을 공유할 수 있다.(공유 참조)

## 값 타입의 특징

- 식별자가 없다.
- 생명주기를 엔티티에 의존한다.
    - 스스로 생명주기를 가지지 않고 엔티티에 의존하므로 의존하는 엔티티가 제거되면 같이 제거된다.
- 공유하지 않는 것이 안전하다.
    - 엔티티 타입과 다르게 공유하지 않는 것이 안전하다. 값을 복사해서 사용해야 한다.
    - 오직 하나의 주인만이 관리해야 한다.
    - 불변 객체로 만드는 것이 안전하다.

값 타입은 정말 값 타입이라 판단될 때만 사용해야 한다. 특히 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만드면 안된다.  
식별자가 필요하고 지속해서 값을 추적하고 구분하고 변경해야 한다면 그것은 값 타입이 아닌 엔티티로 사용되어야 한다.