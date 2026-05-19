package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-19T09:03:32+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyResponseDto toResponseDto(Specialty specialty) {
        if ( specialty == null ) {
            return null;
        }

        SpecialtyResponseDto specialtyResponseDto = new SpecialtyResponseDto();

        specialtyResponseDto.setId( specialty.getId() );
        specialtyResponseDto.setName( specialty.getName() );

        return specialtyResponseDto;
    }

    @Override
    public Specialty toEntity(SpecialtyRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setName( dto.getName() );

        return specialty;
    }

    @Override
    public void updateEntityFromDto(SpecialtyRequestDto dto, Specialty specialty) {
        if ( dto == null ) {
            return;
        }

        specialty.setName( dto.getName() );
    }
}
