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
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetService service;

    private Vet vet;
    private VetResponse vetResponse;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        vetResponse = new VetResponse(1, "James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.lastName()).isEqualTo("Carter");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id 99");
    }

    @Test
    void create_savesAndReturnsVet() {
        VetRequest request = new VetRequest("James", "Carter", List.of(1));

        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withEmptySpecialties() {
        VetRequest request = new VetRequest("James", "Carter", List.of());
        Vet noSpecVet = new Vet();
        noSpecVet.setId(2);
        noSpecVet.setFirstName("James");
        noSpecVet.setLastName("Carter");
        noSpecVet.setSpecialties(new HashSet<>());
        VetResponse noSpecResponse = new VetResponse(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponse(noSpecVet)).thenReturn(noSpecResponse);

        VetResponse result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties() {
        VetRequest request = new VetRequest("James", "Carter", null);
        Vet noSpecVet = new Vet();
        noSpecVet.setId(2);
        noSpecVet.setFirstName("James");
        noSpecVet.setLastName("Carter");
        noSpecVet.setSpecialties(new HashSet<>());
        VetResponse noSpecResponse = new VetResponse(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponse(noSpecVet)).thenReturn(noSpecResponse);

        VetResponse result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_throwsWhenSpecialtyNotFound() {
        VetRequest request = new VetRequest("James", "Carter", List.of(1, 99));

        when(specialtyRepository.findAllById(List.of(1, 99))).thenReturn(List.of(radiology));

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id 99");
    }

    @Test
    void update_updatesAndReturnsVet() {
        VetRequest request = new VetRequest("Helen", "Leary", List.of(1));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        VetResponse updatedResponse = new VetResponse(1, "Helen", "Leary",
            List.of(new SpecialtyResponse(1, "radiology")));
        when(vetMapper.toResponse(vet)).thenReturn(updatedResponse);

        VetResponse result = service.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_throwsWhenNotFound() {
        VetRequest request = new VetRequest("Helen", "Leary", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsFilteredVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.searchByName("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.searchByName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Carter");
    }
}
