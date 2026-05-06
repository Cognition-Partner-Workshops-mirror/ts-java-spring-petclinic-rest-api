package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Vet;
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
public class VetMapperImpl implements VetMapper {

    @Override
    public VetDto toDto(Vet entity) {
        if ( entity == null ) {
            return null;
        }

        VetDto vetDto = new VetDto();

        vetDto.setId( entity.getId() );
        vetDto.setFirstName( entity.getFirstName() );
        vetDto.setLastName( entity.getLastName() );

        vetDto.setSpecialties( specialtySetToList(entity.getSpecialties()) );

        return vetDto;
    }

    @Override
    public List<VetDto> toDtoList(List<Vet> entities) {
        if ( entities == null ) {
            return null;
        }

        List<VetDto> list = new ArrayList<VetDto>( entities.size() );
        for ( Vet vet : entities ) {
            list.add( toDto( vet ) );
        }

        return list;
    }

    @Override
    public Vet toEntity(VetDto dto) {
        if ( dto == null ) {
            return null;
        }

        Vet vet = new Vet();

        vet.setFirstName( dto.getFirstName() );
        vet.setLastName( dto.getLastName() );

        return vet;
    }
}
