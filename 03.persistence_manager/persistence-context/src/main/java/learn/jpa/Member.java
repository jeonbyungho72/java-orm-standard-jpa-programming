package learn.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    private Long id;
    @Column(name = "name")
    private String username;
    private Integer age;
}
