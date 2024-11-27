package com.example;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Group;
import com.example.model.Person;

public class TestFind {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Group group1 = new Group("그룹 A");
        em.persist(group1);

        Person hong = new Person("홍길동", 20, group1);
        em.persist(hong);

        // 객체 그래프 탐색
        Person findPerson = em.find(Person.class, 1L);
        System.out.println(findPerson.getGroup().getName());

        // JPQL
        final String jpql = "select p from Person p join p.group g where g.name=:groupName";

        List<Person> resultList = em.createQuery(jpql,
                Person.class).setParameter("groupName", "그룹 A").getResultList();

        resultList.stream().forEach(p -> System.out.println(p.getGroup().getName()));

        tx.commit();

        em.close();
        emf.close();

    }
}
