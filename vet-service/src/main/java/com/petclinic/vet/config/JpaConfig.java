package com.petclinic.vet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration for the vet-service.
 * Explicitly enables JPA repositories in the repository package.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.petclinic.vet.repository")
public class JpaConfig {
}
