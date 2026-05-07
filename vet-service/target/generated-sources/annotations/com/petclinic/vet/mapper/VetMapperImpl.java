package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T13:33:32+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class VetMapperImpl implements VetMapper {

    @Override
    public VetResponseDto toResponseDto(Vet entity) {
        if ( entity == null ) {
            return null;
        }

        VetResponseDto vetResponseDto = new VetResponseDto();

        vetResponseDto.setId( entity.getId() );
        vetResponseDto.setFirstName( entity.getFirstName() );
        vetResponseDto.setLastName( entity.getLastName() );

        vetResponseDto.setSpecialties( specialtiesToDtoList(entity.getSpecialties()) );

        return vetResponseDto;
    }

    @Override
    public Vet toEntity(VetRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Vet vet = new Vet();

        vet.setFirstName( dto.getFirstName() );
        vet.setLastName( dto.getLastName() );

        return vet;
    }
}
