package com.example.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
@Setter
@Getter
@NoArgsConstructor
public class Member extends Person {

    private String email;
    private String password;

    public Member(String name, int age, String email, String password) {
        super(name, age);
        this.email = email;
        this.password = password;
    }
}
