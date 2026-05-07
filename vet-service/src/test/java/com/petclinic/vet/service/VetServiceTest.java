package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetService service;

    private Vet vetEntity;
    private Specialty specialtyEntity;
    private VetResponse vetResponse;
    private VetRequest vetRequest;
    private VetRequest vetRequestNoSpecialties;

    @BeforeEach
    void setUp() {
        specialtyEntity = new Specialty();
        specialtyEntity.setId(1);
        specialtyEntity.setName("radiology");
        specialtyEntity.setCreatedAt(Instant.now());
        specialtyEntity.setUpdatedAt(Instant.now());

        vetEntity = new Vet();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setSpecialties(new HashSet<>(Set.of(specialtyEntity)));
        vetEntity.setCreatedAt(Instant.now());
        vetEntity.setUpdatedAt(Instant.now());

        SpecialtyResponse specResp = new SpecialtyResponse(1, "radiology");
        vetResponse = new VetResponse(1, "James", "Carter", List.of(specResp));
        vetRequest = new VetRequest("James", "Carter", List.of(specResp));
        vetRequestNoSpecialties = new VetRequest("James", "Carter", List.of());
    }

    @Test
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(List.of(vetEntity))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().firstName()).isEqualTo("James");
    }

    @Test
    void getById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getById_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_withSpecialties_returnsCreated() {
        Vet newVet = new Vet();
        when(vetMapper.toEntity(vetRequest)).thenReturn(newVet);
        when(specialtyRepository.findAllByIdIn(List.of(1))).thenReturn(List.of(specialtyEntity));
        when(vetRepository.save(newVet)).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.create(vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
        verify(vetRepository).save(newVet);
    }

    @Test
    void create_withNoSpecialties_returnsCreated() {
        Vet newVet = new Vet();
        VetResponse noSpecResp = new VetResponse(1, "James", "Carter", List.of());
        when(vetMapper.toEntity(vetRequestNoSpecialties)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(noSpecResp);

        VetResponse result = service.create(vetRequestNoSpecialties);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void create_withNonExistingSpecialty_throwsNotFound() {
        SpecialtyResponse fakeSpec = new SpecialtyResponse(999, "fake");
        VetRequest badRequest = new VetRequest("James", "Carter", List.of(fakeSpec));
        Vet newVet = new Vet();
        when(vetMapper.toEntity(badRequest)).thenReturn(newVet);
        when(specialtyRepository.findAllByIdIn(List.of(999))).thenReturn(List.of());

        assertThatThrownBy(() -> service.create(badRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void update_existingId_returnsUpdated() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findAllByIdIn(List.of(1))).thenReturn(List.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.update(1, vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, vetRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(List.of(vetEntity))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(List.of(vetEntity))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_withNullSpecialtiesList_returnsCreated() {
        VetRequest nullSpecRequest = new VetRequest("James", "Carter", null);
        Vet newVet = new Vet();
        VetResponse noSpecResp = new VetResponse(1, "James", "Carter", List.of());
        when(vetMapper.toEntity(nullSpecRequest)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(noSpecResp);

        VetResponse result = service.create(nullSpecRequest);

        assertThat(result.specialties()).isEmpty();
    }
}
