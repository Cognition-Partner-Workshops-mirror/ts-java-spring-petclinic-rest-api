package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet james;
    private Vet helen;
    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");

        helen = new Vet();
        helen.setId(2);
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.getSpecialties().add(radiology);
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(Arrays.asList(james, helen));

        List<Vet> result = vetService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));

        Vet result = vetService.findById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("99");
    }

    @Test
    void create_withValidSpecialties_returnsCreatedVet() {
        VetRequestDto dto = new VetRequestDto("New", "Vet", Arrays.asList(1, 2));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findById(2)).thenReturn(Optional.of(surgery));
        Vet saved = new Vet();
        saved.setId(3);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        saved.getSpecialties().add(radiology);
        saved.getSpecialties().add(surgery);
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);

        Vet result = vetService.create(dto);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getSpecialties()).hasSize(2);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withEmptySpecialties_returnsCreatedVet() {
        VetRequestDto dto = new VetRequestDto("New", "Vet", Collections.emptyList());
        Vet saved = new Vet();
        saved.setId(3);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);

        Vet result = vetService.create(dto);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_withInvalidSpecialtyId_throwsResourceNotFoundException() {
        VetRequestDto dto = new VetRequestDto("New", "Vet", Arrays.asList(99));
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }

    @Test
    void update_existingId_returnsUpdatedVet() {
        VetRequestDto dto = new VetRequestDto("Updated", "Name", Arrays.asList(1));
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        updated.getSpecialties().add(radiology);
        when(vetRepository.save(any(Vet.class))).thenReturn(updated);

        Vet result = vetService.update(1, dto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("Name");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        VetRequestDto dto = new VetRequestDto("Updated", "Name", Collections.emptyList());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));

        vetService.delete(1);

        verify(vetRepository).delete(james);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("99");
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(Arrays.asList(helen));

        List<Vet> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(Arrays.asList(james));

        List<Vet> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void create_withNullSpecialtyIds_returnsCreatedVet() {
        VetRequestDto dto = new VetRequestDto("New", "Vet", null);
        Vet saved = new Vet();
        saved.setId(3);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);

        Vet result = vetService.create(dto);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getSpecialties()).isEmpty();
    }
}
