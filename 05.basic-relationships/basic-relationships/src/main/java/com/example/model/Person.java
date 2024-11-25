package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Person {
    @Id
    @Column(name = "person_id")
    @Setter(value = AccessLevel.NONE)
    private Long id;
    private String name;
    @Column(nullable = false)
    private int age;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
