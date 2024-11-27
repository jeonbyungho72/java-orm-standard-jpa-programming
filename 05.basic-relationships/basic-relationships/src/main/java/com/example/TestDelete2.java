package com.example;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Group;
import com.example.model.Person;

public class TestDelete2 {
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
        Person kim = new Person("김철수", 23, group1);
        Person lee = new Person("이영희", 19, group1);

        em.persist(hong);
        em.persist(kim);
        em.persist(lee);

        final String jpql = "select p from Person p join p.group g";

        List<Person> resultList = em.createQuery(jpql,
                Person.class).getResultList();

        resultList.stream().forEach(p -> p.setGroup(null));

        em.remove(group1);

        tx.commit();

        em.close();
        emf.close();

    }
}
