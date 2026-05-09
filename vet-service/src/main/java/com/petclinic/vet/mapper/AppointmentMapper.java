package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.AppointmentResponseDto;
import com.petclinic.vet.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting Appointment entity to response DTO.
 * Maps nested vet/specialty names into flat DTO fields for the API response.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    /** Map Appointment entity to response DTO, flattening vet and specialty references */
    @Mapping(target = "vetId", source = "vet.id")
    @Mapping(target = "vetName", expression = "java(appointment.getVet().getFirstName() + \" \" + appointment.getVet().getLastName())")
    @Mapping(target = "specialtyId", expression = "java(appointment.getSpecialty() != null ? appointment.getSpecialty().getId() : null)")
    @Mapping(target = "specialtyName", expression = "java(appointment.getSpecialty() != null ? appointment.getSpecialty().getName() : null)")
    @Mapping(target = "status", expression = "java(appointment.getStatus().name())")
    AppointmentResponseDto toResponseDto(Appointment appointment);
}
