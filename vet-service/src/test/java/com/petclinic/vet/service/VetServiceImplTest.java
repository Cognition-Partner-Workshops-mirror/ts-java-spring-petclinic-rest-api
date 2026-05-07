package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl service;

    private Vet james;
    private Specialty radiology;
    private VetResponseDto jamesResponse;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        radiology.setCreatedAt(Instant.now());
        radiology.setUpdatedAt(Instant.now());

        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(Set.of(radiology));
        james.setCreatedAt(Instant.now());
        james.setUpdatedAt(Instant.now());

        jamesResponse = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listAll_returnsAll() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponseDto> result = service.listAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponseDto result = service.getById(1);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void getById_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id 99");
    }

    @Test
    void create_withExistingSpecialty() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto("radiology")));

        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponseDto result = service.create(request);
        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withNewSpecialty() {
        Specialty newSpec = new Specialty();
        newSpec.setId(2);
        newSpec.setName("dentistry");

        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto("dentistry")));

        when(specialtyRepository.findByNameContainingIgnoreCase("dentistry"))
            .thenReturn(List.of());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(newSpec);
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponseDto result = service.create(request);
        assertThat(result).isNotNull();
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponseDto result = service.create(request);
        assertThat(result).isNotNull();
    }

    @Test
    void update_success() {
        VetRequestDto request = new VetRequestDto("Updated", "Name",
            List.of(new SpecialtyRequestDto("radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(radiology));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponseDto result = service.update(1, request);
        assertThat(result).isNotNull();
    }

    @Test
    void update_notFound() {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponseDto result = service.delete(1);
        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsResults() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponseDto> result = service.findBySpecialty(1);
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsResults() {
        when(vetRepository.findByNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponseDto> result = service.searchByName("Carter");
        assertThat(result).hasSize(1);
    }
}
