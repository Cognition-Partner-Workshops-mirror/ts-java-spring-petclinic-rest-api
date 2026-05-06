package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T17:49:37+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyDto toDto(Specialty entity) {
        if ( entity == null ) {
            return null;
        }

        SpecialtyDto specialtyDto = new SpecialtyDto();

        specialtyDto.setId( entity.getId() );
        specialtyDto.setName( entity.getName() );

        return specialtyDto;
    }

    @Override
    public List<SpecialtyDto> toDtoList(List<Specialty> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SpecialtyDto> list = new ArrayList<SpecialtyDto>( entities.size() );
        for ( Specialty specialty : entities ) {
            list.add( toDto( specialty ) );
        }

        return list;
    }

    @Override
    public Specialty toEntity(SpecialtyDto dto) {
        if ( dto == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setName( dto.getName() );

        return specialty;
    }

    @Override
    public void updateEntity(SpecialtyDto dto, Specialty entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
    }
}
