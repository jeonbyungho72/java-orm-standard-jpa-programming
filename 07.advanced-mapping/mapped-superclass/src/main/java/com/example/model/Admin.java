package com.example.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "admin_id")),
        @AttributeOverride(name = "name", column = @Column(name = "admin_name"))
})
@Getter
@Setter
@NoArgsConstructor
public class Admin extends Person {
    @Column(name = "admin_no")
    private String adminNumber;

    public Admin(String name, int age, String adminNumber) {
        super(name, age);
        this.adminNumber = adminNumber;
    }
}
