package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T13:10:26+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class VetMapperImpl implements VetMapper {

    @Override
    public VetResponseDto toResponseDto(Vet vet) {
        if ( vet == null ) {
            return null;
        }

        List<SpecialtyResponseDto> specialties = null;
        Integer id = null;
        String firstName = null;
        String lastName = null;

        specialties = mapSpecialties( vet.getSpecialties() );
        id = vet.getId();
        firstName = vet.getFirstName();
        lastName = vet.getLastName();

        VetResponseDto vetResponseDto = new VetResponseDto( id, firstName, lastName, specialties );

        return vetResponseDto;
    }
}
