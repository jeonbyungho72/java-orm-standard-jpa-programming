package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Groups")
@Getter
@Setter
public class Group {
    @Id
    @Column(name = "group_id")
    @Setter(value = AccessLevel.NONE)
    private Long id;
    private String name;

}
