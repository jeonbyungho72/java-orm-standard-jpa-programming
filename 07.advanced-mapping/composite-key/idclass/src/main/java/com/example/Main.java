package com.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Child;
import com.example.model.Parent;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Parent parent = new Parent(1L, 1L, "parentA");

        em.persist(parent);

        Child child1 = new Child(1L, parent, "child1");
        Child child2 = new Child(2L, parent, "child2");

        em.persist(child1);
        em.persist(child2);
        tx.commit();

        em.close();
        emf.close();
    }
}