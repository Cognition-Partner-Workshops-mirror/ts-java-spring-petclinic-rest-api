package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.VetRequest;
import com.petclinic.vet.dto.response.SpecialtyResponse;
import com.petclinic.vet.dto.response.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private VetService vetService;

    private Vet james;
    private VetResponse jamesResponse;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        james = new Vet(1, "James", "Carter");
        james.setSpecialties(Set.of(radiology));

        jamesResponse = new VetResponse(1, "James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
    }

    @Test
    void listVets_returnsAll() {
        Vet helen = new Vet(2, "Helen", "Leary");
        VetResponse helenResponse = new VetResponse(2, "Helen", "Leary", List.of());
        when(vetRepository.findAll()).thenReturn(List.of(james, helen));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);
        when(vetMapper.toResponse(helen)).thenReturn(helenResponse);

        List<VetResponse> result = vetService.listVets();

        assertThat(result).hasSize(2);
    }

    @Test
    void listVets_emptyList() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetResponse> result = vetService.listVets();

        assertThat(result).isEmpty();
    }

    @Test
    void getVet_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponse result = vetService.getVet(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void getVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("99");
    }

    @Test
    void createVet_success() {
        VetRequest request = new VetRequest("James", "Carter", List.of(1));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponse result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        ArgumentCaptor<Vet> captor = ArgumentCaptor.forClass(Vet.class);
        verify(vetRepository).save(captor.capture());
        assertThat(captor.getValue().getSpecialties()).contains(radiology);
    }

    @Test
    void createVet_withEmptySpecialties() {
        VetRequest request = new VetRequest("James", "Carter", List.of());
        Vet noSpecVet = new Vet(1, "James", "Carter");
        VetResponse noSpecResponse = new VetResponse(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponse(noSpecVet)).thenReturn(noSpecResponse);

        VetResponse result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_withNullSpecialties() {
        VetRequest request = new VetRequest("James", "Carter", null);
        Vet noSpecVet = new Vet(1, "James", "Carter");
        VetResponse noSpecResponse = new VetResponse(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponse(noSpecVet)).thenReturn(noSpecResponse);

        VetResponse result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_specialtyNotFound() {
        VetRequest request = new VetRequest("James", "Carter", List.of(1, 99));
        when(specialtyRepository.findAllById(List.of(1, 99))).thenReturn(List.of(radiology));

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }

    @Test
    void updateVet_success() {
        VetRequest request = new VetRequest("James", "Updated", List.of(1));
        Vet updated = new Vet(1, "James", "Updated");
        updated.setSpecialties(Set.of(radiology));
        VetResponse updatedResponse = new VetResponse(1, "James", "Updated",
            List.of(new SpecialtyResponse(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(james)).thenReturn(updated);
        when(vetMapper.toResponse(updated)).thenReturn(updatedResponse);

        VetResponse result = vetService.updateVet(1, request);

        assertThat(result.getLastName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_notFound() {
        VetRequest request = new VetRequest("James", "Carter", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponse result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void deleteVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_success() {
        when(specialtyRepository.existsById(1)).thenReturn(true);
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        List<VetResponse> result = vetService.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialty_specialtyNotFound() {
        when(specialtyRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> vetService.findBySpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_returnsMatches() {
        when(vetRepository.searchByLastName("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        List<VetResponse> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_returnsMatches() {
        when(vetRepository.searchByName("James")).thenReturn(List.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        List<VetResponse> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_noMatches() {
        when(vetRepository.searchByLastName("xyz")).thenReturn(List.of());

        List<VetResponse> result = vetService.searchByLastName("xyz");

        assertThat(result).isEmpty();
    }
}
