package com.petclinic.vet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing for automatic createdAt/updatedAt timestamps.
 * Separated from the main application class to avoid conflicts with @WebMvcTest slices.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
