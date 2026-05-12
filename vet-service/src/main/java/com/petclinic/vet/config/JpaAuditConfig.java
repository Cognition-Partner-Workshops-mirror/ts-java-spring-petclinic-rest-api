package com.petclinic.vet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Separate JPA auditing configuration.
 * Extracted from the main application class so that @WebMvcTest slices
 * don't fail trying to initialize JPA auditing infrastructure.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
