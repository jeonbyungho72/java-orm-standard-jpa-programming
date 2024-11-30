package com.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Admin;
import com.example.model.Member;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Member newMember = new Member("홍길동", 20, "hong@example.com", "1234");
        Admin admin = new Admin("김관리", 22, "00A");

        em.persist(newMember);
        em.persist(admin);

        tx.commit();

        em.close();
        emf.close();
    }
}