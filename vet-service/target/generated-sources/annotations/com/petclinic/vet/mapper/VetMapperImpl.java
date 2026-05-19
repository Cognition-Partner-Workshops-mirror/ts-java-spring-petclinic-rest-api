package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-19T09:03:32+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class VetMapperImpl implements VetMapper {

    @Override
    public VetResponseDto toResponseDto(Vet vet) {
        if ( vet == null ) {
            return null;
        }

        VetResponseDto vetResponseDto = new VetResponseDto();

        vetResponseDto.setId( vet.getId() );
        vetResponseDto.setFirstName( vet.getFirstName() );
        vetResponseDto.setLastName( vet.getLastName() );

        vetResponseDto.setSpecialties( mapSpecialties(vet.getSpecialties()) );

        return vetResponseDto;
    }

    @Override
    public SpecialtyResponseDto specialtyToDto(Specialty specialty) {
        if ( specialty == null ) {
            return null;
        }

        SpecialtyResponseDto specialtyResponseDto = new SpecialtyResponseDto();

        specialtyResponseDto.setId( specialty.getId() );
        specialtyResponseDto.setName( specialty.getName() );

        return specialtyResponseDto;
    }
}
