package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    @Setter(value = AccessLevel.NONE)
    private Long id;
    private String name;
    @Column(nullable = false)
    private int age;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public Person(String name, int age, Group group) {
        this.name = name;
        this.age = age;
        this.group = group;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 연관관계 편의 메서드
    public void setGroup(Group group) {
        // 다른 엔티티로 변경할 때 기존 엔티티를 제거하는 코드
        if (this.group != null)
            this.group.getPersons().remove(this);

        this.group = group;
        this.group.getPersons().add(this);
    }
}
