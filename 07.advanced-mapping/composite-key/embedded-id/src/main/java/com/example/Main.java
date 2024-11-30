package com.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Child;
import com.example.model.Parent;
import com.example.model.ParentId;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Parent parent = new Parent(new ParentId(1L, 1L), "부모 엔티티");

        em.persist(parent);

        Child child1 = new Child(1L, parent, "자식 1");
        Child child2 = new Child(2L, parent, "자식 2");

        em.persist(child1);
        em.persist(child2);
        tx.commit();

        em.close();
        emf.close();
    }
}