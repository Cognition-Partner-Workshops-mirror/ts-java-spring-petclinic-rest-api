package org.springframework.samples.petclinic.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.model.Role;

import org.springframework.samples.petclinic.repository.jdbc.JdbcPet;
import org.springframework.samples.petclinic.repository.jdbc.JdbcPetRowMapper;
import org.springframework.samples.petclinic.repository.jdbc.JdbcPetVisitExtractor;

import org.springframework.samples.petclinic.rest.controller.BindingErrorsResponse;

/**
 * Registers GraalVM native-image hints so that classes requiring reflective
 * access (JPA entities, JDBC row mappers, Jackson-serialised DTOs, etc.) are
 * reachable at run time in the ahead-of-time compiled binary.
 */
@Configuration
@ImportRuntimeHints(NativeHintsRegistrar.class)
public class NativeHintsRegistrar implements RuntimeHintsRegistrar {

	// All member categories needed for JPA entities and Jackson serialization
	private static final MemberCategory[] FULL_REFLECTION = {
		MemberCategory.DECLARED_FIELDS,
		MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
		MemberCategory.INVOKE_DECLARED_METHODS,
		MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
		MemberCategory.INVOKE_PUBLIC_METHODS
	};

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		// --- JPA / Hibernate entity hierarchy ---
		registerReflection(hints,
			BaseEntity.class,
			NamedEntity.class,
			Person.class,
			Owner.class,
			Pet.class,
			PetType.class,
			Specialty.class,
			Vet.class,
			Visit.class,
			User.class,
			Role.class
		);

		// --- JDBC row-mapper and extractor classes used via reflection ---
		registerReflection(hints,
			JdbcPet.class,
			JdbcPetRowMapper.class,
			JdbcPetVisitExtractor.class
		);

		// --- REST controller helper serialised by Jackson ---
		registerReflection(hints,
			BindingErrorsResponse.class
		);

		// --- Resource bundles and classpath resources ---
		hints.resources().registerPattern("db/*/*.sql");
		hints.resources().registerPattern("messages/*");
		hints.resources().registerPattern("openapi.yml");
	}

	private void registerReflection(RuntimeHints hints, Class<?>... types) {
		for (Class<?> type : types) {
			hints.reflection().registerType(type, FULL_REFLECTION);
		}
	}
}
