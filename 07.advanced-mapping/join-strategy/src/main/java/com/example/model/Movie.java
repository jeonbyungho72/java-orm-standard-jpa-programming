package com.example.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = "M")
@PrimaryKeyJoinColumn(name = "movide_id")
@Getter
@Setter
public class Movie extends Item {
    private String director;
    private String actor;
}
