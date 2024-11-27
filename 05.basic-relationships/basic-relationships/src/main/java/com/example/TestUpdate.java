package com.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Group;
import com.example.model.Person;

public class TestUpdate {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Group group1 = new Group("그룹 A");
        em.persist(group1);

        Group group2 = new Group("그룹 B");
        em.persist(group2);

        Person hong = new Person("홍길동", 20, group1);
        em.persist(hong);

        Person findPerson = em.find(Person.class, 1L);
        findPerson.setGroup(group2);

        tx.commit();

        em.close();
        emf.close();

        System.out.println(findPerson.getGroup().getName());
    }
}
