package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T13:33:31+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyResponseDto toResponseDto(Specialty entity) {
        if ( entity == null ) {
            return null;
        }

        SpecialtyResponseDto specialtyResponseDto = new SpecialtyResponseDto();

        specialtyResponseDto.setId( entity.getId() );
        specialtyResponseDto.setName( entity.getName() );

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
    public void updateEntityFromDto(SpecialtyRequestDto dto, Specialty entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
    }
}
