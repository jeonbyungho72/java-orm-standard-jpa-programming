package com.example.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "book_id")
@Getter
@Setter
public class Book extends Item {
    private String author;
    private String isbn;
}
