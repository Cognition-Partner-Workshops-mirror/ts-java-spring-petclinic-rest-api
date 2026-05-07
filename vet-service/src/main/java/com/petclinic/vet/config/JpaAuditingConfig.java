package com.petclinic.vet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Enables Spring Data JPA auditing so @CreatedDate / @LastModifiedDate are auto-populated. */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
