package com.example.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("S")
@PrimaryKeyJoinColumn(name = "Song_id")
@Getter
@Setter
public class Song extends Item {
    private String artist;
}
