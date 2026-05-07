package org.springframework.samples.petclinic.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
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
 *
 * Also registers serialization hints for Logback model classes, which Spring
 * Boot AOT pre-serializes at build time and deserializes at native runtime
 * for faster logging initialization.
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

	// Logback model classes whose instances are Java-serialized by Spring Boot
	// AOT at build time and deserialized at native-image runtime. Without these
	// hints GraalVM 24+ throws MissingReflectionRegistrationError for
	// serialVersionUID field access during ObjectInputStream deserialization.
	private static final String[] LOGBACK_MODEL_CLASSES = {
		// logback-core model hierarchy
		"ch.qos.logback.core.model.Model",
		"ch.qos.logback.core.model.ComponentModel",
		"ch.qos.logback.core.model.NamedComponentModel",
		"ch.qos.logback.core.model.NamedModel",
		"ch.qos.logback.core.model.AppenderModel",
		"ch.qos.logback.core.model.AppenderRefModel",
		"ch.qos.logback.core.model.ImplicitModel",
		"ch.qos.logback.core.model.ImportModel",
		"ch.qos.logback.core.model.IncludeModel",
		"ch.qos.logback.core.model.PropertyModel",
		"ch.qos.logback.core.model.DefineModel",
		"ch.qos.logback.core.model.TimestampModel",
		"ch.qos.logback.core.model.StatusListenerModel",
		"ch.qos.logback.core.model.EventEvaluatorModel",
		"ch.qos.logback.core.model.ConversionRuleModel",
		"ch.qos.logback.core.model.ParamModel",
		"ch.qos.logback.core.model.ResourceModel",
		"ch.qos.logback.core.model.SequenceNumberGeneratorModel",
		"ch.qos.logback.core.model.SerializeModelModel",
		"ch.qos.logback.core.model.ShutdownHookModel",
		"ch.qos.logback.core.model.SiftModel",
		"ch.qos.logback.core.model.InsertFromJNDIModel",
		// logback-core conditional models
		"ch.qos.logback.core.model.conditional.IfModel",
		"ch.qos.logback.core.model.conditional.ThenModel",
		"ch.qos.logback.core.model.conditional.ElseModel",
		// logback-classic model hierarchy
		"ch.qos.logback.classic.model.ConfigurationModel",
		"ch.qos.logback.classic.model.LoggerModel",
		"ch.qos.logback.classic.model.RootLoggerModel",
		"ch.qos.logback.classic.model.LevelModel",
		"ch.qos.logback.classic.model.ContextNameModel",
		"ch.qos.logback.classic.model.LoggerContextListenerModel",
		"ch.qos.logback.classic.model.PropertiesConfiguratorModel",
		"ch.qos.logback.classic.model.ReceiverModel",
		// Spring Boot logback extensions
		"org.springframework.boot.logging.logback.SpringProfileModel",
		"org.springframework.boot.logging.logback.SpringPropertyModel"
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

		// --- Logback model serialization for Spring Boot AOT logging config ---
		// Register each logback model class for both Java serialization and
		// reflection (DECLARED_FIELDS needed for serialVersionUID access).
		for (String className : LOGBACK_MODEL_CLASSES) {
			TypeReference typeRef = TypeReference.of(className);
			hints.serialization().registerType(typeRef);
			hints.reflection().registerType(typeRef, MemberCategory.DECLARED_FIELDS);
		}

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
