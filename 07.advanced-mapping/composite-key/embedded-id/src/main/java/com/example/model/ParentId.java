package com.example.model;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ParentId implements Serializable {
    @Column(name = "parent_id1")
    private Long id1;
    @Column(name = "parent_id2")
    private Long id2;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id1 == null) ? 0 : id1.hashCode());
        result = prime * result + ((id2 == null) ? 0 : id2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParentId other = (ParentId) obj;
        if (id1 == null) {
            if (other.id1 != null)
                return false;
        } else if (!id1.equals(other.id1))
            return false;
        if (id2 == null) {
            if (other.id2 != null)
                return false;
        } else if (!id2.equals(other.id2))
            return false;
        return true;
    }

}
