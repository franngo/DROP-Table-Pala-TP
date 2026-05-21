package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {

    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date updatedAt;

    @Column(nullable = false)
    private Boolean deletedAt=false ;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = new Date();
        deletedAt = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }
}
