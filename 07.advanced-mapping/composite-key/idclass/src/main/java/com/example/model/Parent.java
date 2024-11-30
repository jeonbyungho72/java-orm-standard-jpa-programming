package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(ParentId.class)
@Getter
@Setter
@AllArgsConstructor
public class Parent {
    @Id
    @Column(name = "parent_id1")
    private Long id1;

    @Id
    @GeneratedValue
    @Column(name = "parent_id2")
    private Long id2;

    private String name;
}
