package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Appointment entity.
 * Provides custom queries for filtering by vet, date range, and status.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    /** Find all appointments for a specific vet */
    List<Appointment> findByVetId(Integer vetId);

    /** Find appointments by status */
    List<Appointment> findByStatus(Appointment.Status status);

    /** Find appointments for a specific date */
    List<Appointment> findByAppointmentDate(LocalDate date);

    /** Find appointments within a date range (inclusive) */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate ORDER BY a.appointmentDate, a.appointmentTime")
    List<Appointment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /** Find appointments for a vet on a specific date, used for conflict detection */
    List<Appointment> findByVetIdAndAppointmentDate(Integer vetId, LocalDate date);
}
