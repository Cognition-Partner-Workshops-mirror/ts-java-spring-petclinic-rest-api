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

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponse result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getById_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_validRequest_returnsCreated() {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponse result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void create_withNonExistingSpecialty_throwsNotFound() {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyResponse(999, "nonexistent")));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withEmptySpecialties_succeeds() {
        VetRequest request = new VetRequest("James", "Carter", List.of());
        Vet noSpecVet = new Vet(2, "James", "Carter");
        VetResponse noSpecResponse = new VetResponse(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponse(noSpecVet)).thenReturn(noSpecResponse);

        VetResponse result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_succeeds() {
        VetRequest request = new VetRequest("James", "Carter", null);
        Vet noSpecVet = new Vet(2, "James", "Carter");
        VetResponse noSpecResponse = new VetResponse(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponse(noSpecVet)).thenReturn(noSpecResponse);

        VetResponse result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void update_existingId_updatesAndReturns() {
        VetRequest request = new VetRequest("Updated", "Name",
            List.of(new SpecialtyResponse(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponse(james)).thenReturn(
            new VetResponse(1, "Updated", "Name", List.of(new SpecialtyResponse(1, "radiology"))));

        VetResponse result = service.update(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
    }

    @Test
    void update_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, new VetRequest("A", "B", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponse(james)).thenReturn(jamesResponse);

        VetResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_matchingName_returnsVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponse> result = service.searchByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void filterBySpecialty_matchingSpecialty_returnsVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponse> result = service.filterBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastNameAndSpecialty_matchingBoth_returnsVets() {
        when(vetRepository.findByLastNameContainingIgnoreCaseAndSpecialtyName("Carter", "radiology"))
            .thenReturn(List.of(james));
        when(vetMapper.toResponseList(List.of(james))).thenReturn(List.of(jamesResponse));

        List<VetResponse> result = service.searchByLastNameAndSpecialty("Carter", "radiology");

        assertThat(result).hasSize(1);
    }
}
