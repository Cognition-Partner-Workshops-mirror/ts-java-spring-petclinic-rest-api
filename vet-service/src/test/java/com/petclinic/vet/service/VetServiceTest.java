package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
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
    private VetResponse vetResponse;
    private VetRequest vetRequest;
    private Specialty specialtyEntity;

    @BeforeEach
    void setUp() {
        specialtyEntity = new Specialty();
        specialtyEntity.setId(1);
        specialtyEntity.setName("radiology");

        vetEntity = new Vet();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setSpecialties(new HashSet<>(Set.of(specialtyEntity)));

        vetResponse = new VetResponse(1, "James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));

        vetRequest = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(any())).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void create_savesWithExistingSpecialty() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(specialtyEntity));
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.create(vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_savesWithNewSpecialty() {
        VetRequest requestWithNew = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("cardiology")));

        when(specialtyRepository.findByNameContainingIgnoreCase("cardiology"))
            .thenReturn(List.of());
        Specialty newSpec = new Specialty();
        newSpec.setId(10);
        newSpec.setName("cardiology");
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(newSpec);
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.create(requestWithNew);

        assertThat(result).isNotNull();
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_handlesNullSpecialtiesList() {
        VetRequest requestNoSpec = new VetRequest("James", "Carter", null);

        Vet saved = new Vet();
        saved.setId(2);
        saved.setFirstName("James");
        saved.setLastName("Carter");
        VetResponse noSpecResponse = new VetResponse(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponse(saved)).thenReturn(noSpecResponse);

        VetResponse result = service.create(requestNoSpec);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_handlesEmptySpecialtiesList() {
        VetRequest requestEmpty = new VetRequest("James", "Carter", List.of());

        Vet saved = new Vet();
        saved.setId(2);
        saved.setFirstName("James");
        saved.setLastName("Carter");
        VetResponse emptyResponse = new VetResponse(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponse(saved)).thenReturn(emptyResponse);

        VetResponse result = service.create(requestEmpty);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void update_updatesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findByNameContainingIgnoreCase(anyString()))
            .thenReturn(List.of(specialtyEntity));
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.update(1, vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vetEntity);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, vetRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_delegatesToRepository() {
        when(vetRepository.searchByName("James")).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(any())).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.searchByName("James");

        assertThat(result).hasSize(1);
        verify(vetRepository).searchByName("James");
    }

    @Test
    void findBySpecialtyId_delegatesToRepository() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(any())).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
        verify(vetRepository).findBySpecialtyId(1);
    }
}
