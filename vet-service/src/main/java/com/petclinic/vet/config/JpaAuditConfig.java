package com.petclinic.vet.config;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.entity.Specialty;

public class JpaAuditConfig {

    @PrePersist
    public void prePersist(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity instanceof Vet vet) {
            vet.setCreatedAt(now);
            vet.setUpdatedAt(now);
        } else if (entity instanceof Specialty specialty) {
            specialty.setCreatedAt(now);
            specialty.setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity instanceof Vet vet) {
            vet.setUpdatedAt(now);
        } else if (entity instanceof Specialty specialty) {
            specialty.setUpdatedAt(now);
        }
    }
}
