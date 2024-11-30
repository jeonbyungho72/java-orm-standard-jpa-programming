package com.example;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.example.model.Group;
import com.example.model.Person;

public class CollectionSelect {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        Group groupA = new Group("그룹 A");

        Person persons[] = {
                new Person("홍길동", 20, groupA),
                new Person("김철수", 23, groupA),
                new Person("이영희", 19, groupA)
        };

        tx.begin();

        em.persist(groupA);

        for (Person p : persons) {
            groupA.getPersons().add(p);
            em.persist(p);
        }

        tx.commit();

        // final String jpql = "select g from Group g where g.name=:groupName";

        // Group findGroup = em.createQuery(jpql, Group.class).setParameter("groupName",
        // "그룹 A")
        // .getSingleResult();

        Group findGroup = em.find(Group.class, 1L);

        List<Person> findPersons = findGroup.getPersons();

        for (Person p : findPersons) {
            System.out.println(p.toString());
        }

        System.out.println(findPersons.size());

        em.close();
        emf.close();

    }
}
