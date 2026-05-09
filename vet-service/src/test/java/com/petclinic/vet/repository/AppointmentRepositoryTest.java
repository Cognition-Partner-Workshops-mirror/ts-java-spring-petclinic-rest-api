package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Appointment;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataJpaTest for AppointmentRepository.
 * Tests custom query methods against an H2 in-memory database.
 * Imports JpaAuditingConfig to auto-populate createdAt/updatedAt fields.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class AppointmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Vet vet;
    private Specialty specialty;

    @BeforeEach
    void setUp() {
        // Create and persist prerequisite entities for appointment FK references
        specialty = new Specialty();
        specialty.setName("Surgery");
        entityManager.persist(specialty);

        vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        entityManager.persist(vet);

        // Create two test appointments on different dates
        Appointment appt1 = new Appointment();
        appt1.setPetName("Buddy");
        appt1.setPetOwnerName("John Smith");
        appt1.setVet(vet);
        appt1.setSpecialty(specialty);
        appt1.setAppointmentDate(LocalDate.of(2026, 6, 15));
        appt1.setAppointmentTime(LocalTime.of(10, 0));
        appt1.setReason("Annual checkup");
        appt1.setStatus(Appointment.Status.SCHEDULED);
        entityManager.persist(appt1);

        Appointment appt2 = new Appointment();
        appt2.setPetName("Max");
        appt2.setPetOwnerName("Jane Doe");
        appt2.setVet(vet);
        appt2.setSpecialty(null);
        appt2.setAppointmentDate(LocalDate.of(2026, 6, 20));
        appt2.setAppointmentTime(LocalTime.of(14, 30));
        appt2.setReason("Vaccination");
        appt2.setStatus(Appointment.Status.CONFIRMED);
        entityManager.persist(appt2);

        entityManager.flush();
    }

    @Test
    void findByVetId_returnsAppointmentsForVet() {
        List<Appointment> result = appointmentRepository.findByVetId(vet.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    void findByStatus_returnsMatchingAppointments() {
        List<Appointment> scheduled = appointmentRepository.findByStatus(Appointment.Status.SCHEDULED);
        assertThat(scheduled).hasSize(1);
        assertThat(scheduled.get(0).getPetName()).isEqualTo("Buddy");
    }

    @Test
    void findByAppointmentDate_returnsMatchingAppointments() {
        List<Appointment> result = appointmentRepository.findByAppointmentDate(LocalDate.of(2026, 6, 15));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetName()).isEqualTo("Buddy");
    }

    @Test
    void findByDateRange_returnsAppointmentsInRange() {
        List<Appointment> result = appointmentRepository.findByDateRange(
            LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
        assertThat(result).hasSize(2);
    }

    @Test
    void findByDateRange_narrowRange_filtersCorrectly() {
        List<Appointment> result = appointmentRepository.findByDateRange(
            LocalDate.of(2026, 6, 16), LocalDate.of(2026, 6, 25));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetName()).isEqualTo("Max");
    }

    @Test
    void findByVetIdAndAppointmentDate_returnsMatchingAppointments() {
        List<Appointment> result = appointmentRepository.findByVetIdAndAppointmentDate(
            vet.getId(), LocalDate.of(2026, 6, 15));
        assertThat(result).hasSize(1);
    }

    @Test
    void findByStatus_noMatch_returnsEmpty() {
        List<Appointment> result = appointmentRepository.findByStatus(Appointment.Status.CANCELLED);
        assertThat(result).isEmpty();
    }
}
