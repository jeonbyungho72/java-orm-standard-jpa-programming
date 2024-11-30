package com.example.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Child {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "parent_id1"),
            @JoinColumn(name = "parent_id2")
    })
    private Parent parent;

    private String name;
}
