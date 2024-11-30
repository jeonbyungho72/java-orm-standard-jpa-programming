package com.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Book;
import com.example.model.Movie;
import com.example.model.Song;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Book book = new Book();
        book.setAuthor("김영환");
        book.setIsbn("00012000");
        book.setName("JPA Book");
        book.setPrice(45000);
        em.persist(book);

        Movie movie = new Movie();
        movie.setActor("미상");
        movie.setDirector("미상");
        movie.setName("무제");
        movie.setPrice(15000);
        em.persist(movie);

        Song song = new Song();
        song.setArtist("rose");
        song.setName("art");
        song.setPrice(1000);
        em.persist(song);

        tx.commit();

        em.close();
        emf.close();
    }
}