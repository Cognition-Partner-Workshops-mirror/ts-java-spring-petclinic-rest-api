package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T13:10:26+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyResponseDto toResponseDto(Specialty specialty) {
        if ( specialty == null ) {
            return null;
        }

        Integer id = null;
        String name = null;

        id = specialty.getId();
        name = specialty.getName();

        SpecialtyResponseDto specialtyResponseDto = new SpecialtyResponseDto( id, name );

        return specialtyResponseDto;
    }

    @Override
    public Specialty toEntity(SpecialtyRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setName( dto.name() );

        return specialty;
    }

    @Override
    public void updateEntity(SpecialtyRequestDto dto, Specialty specialty) {
        if ( dto == null ) {
            return;
        }

        specialty.setName( dto.name() );
    }
}
