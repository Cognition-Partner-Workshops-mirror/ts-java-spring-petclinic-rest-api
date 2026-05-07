package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
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
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyResponse toResponse(Specialty entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        String name = null;

        id = entity.getId();
        name = entity.getName();

        SpecialtyResponse specialtyResponse = new SpecialtyResponse( id, name );

        return specialtyResponse;
    }

    @Override
    public List<SpecialtyResponse> toResponseList(List<Specialty> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SpecialtyResponse> list = new ArrayList<SpecialtyResponse>( entities.size() );
        for ( Specialty specialty : entities ) {
            list.add( toResponse( specialty ) );
        }

        return list;
    }

    @Override
    public Specialty toEntity(SpecialtyRequest request) {
        if ( request == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setName( request.name() );

        return specialty;
    }

    @Override
    public void updateEntity(SpecialtyRequest request, Specialty entity) {
        if ( request == null ) {
            return;
        }

        entity.setName( request.name() );
    }
}
