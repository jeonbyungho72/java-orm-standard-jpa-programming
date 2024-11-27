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

@Entity
@Getter
@Setter
@NoArgsConstructor
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
}
