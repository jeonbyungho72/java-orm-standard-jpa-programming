package com.example.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "M")
public class Movie extends Item {
    private String director;
    private String actor;
}
