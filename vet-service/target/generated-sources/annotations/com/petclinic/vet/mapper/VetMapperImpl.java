package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Vet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T13:09:37+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class VetMapperImpl implements VetMapper {

    @Override
    public VetResponse toResponse(Vet entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        String firstName = null;
        String lastName = null;

        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();

        List<SpecialtyResponse> specialties = mapSpecialties(entity.getSpecialties());

        VetResponse vetResponse = new VetResponse( id, firstName, lastName, specialties );

        return vetResponse;
    }

    @Override
    public List<VetResponse> toResponseList(List<Vet> entities) {
        if ( entities == null ) {
            return null;
        }

        List<VetResponse> list = new ArrayList<VetResponse>( entities.size() );
        for ( Vet vet : entities ) {
            list.add( toResponse( vet ) );
        }

        return list;
    }
}
