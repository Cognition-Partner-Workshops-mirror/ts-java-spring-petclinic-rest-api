package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
    private Vet helen;
    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);
        surgery = new Specialty("surgery");
        surgery.setId(2);

        james = new Vet("James", "Carter");
        james.setId(1);

        helen = new Vet("Helen", "Leary");
        helen.setId(2);
        helen.addSpecialty(radiology);
    }

    @Test
    void findAll_returnsAllVets() {
        given(vetRepository.findAll()).willReturn(List.of(james, helen));

        List<Vet> result = vetService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_existingId_returnsVet() {
        given(vetRepository.findById(1)).willReturn(Optional.of(james));

        Vet result = vetService.findById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsNotFound() {
        given(vetRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void create_withoutSpecialties_createsVet() {
        VetRequestDto dto = new VetRequestDto("New", "Vet", new ArrayList<>());
        Vet newVet = new Vet("New", "Vet");
        newVet.setId(3);

        given(vetMapper.toEntity(dto)).willReturn(newVet);
        given(vetRepository.save(any(Vet.class))).willReturn(newVet);

        Vet result = vetService.create(dto);

        assertThat(result.getFirstName()).isEqualTo("New");
    }

    @Test
    void create_withSpecialties_resolvesSpecialtiesByName() {
        List<SpecialtyResponseDto> specDtos = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetRequestDto dto = new VetRequestDto("New", "Vet", specDtos);
        Vet newVet = new Vet("New", "Vet");
        newVet.setId(3);

        given(vetMapper.toEntity(dto)).willReturn(newVet);
        given(specialtyRepository.findByNameIn(anySet())).willReturn(List.of(radiology));
        given(vetRepository.save(any(Vet.class))).willReturn(newVet);

        Vet result = vetService.create(dto);

        assertThat(result).isNotNull();
        verify(specialtyRepository).findByNameIn(anySet());
    }

    @Test
    void update_existingVet_updatesFields() {
        VetRequestDto dto = new VetRequestDto("Updated", "Carter", new ArrayList<>());

        given(vetRepository.findById(1)).willReturn(Optional.of(james));
        given(vetRepository.save(james)).willReturn(james);

        Vet result = vetService.update(1, dto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void update_nonExistingVet_throwsNotFound() {
        VetRequestDto dto = new VetRequestDto("Updated", "Carter", new ArrayList<>());

        given(vetRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, dto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingVet_deletesSuccessfully() {
        given(vetRepository.findById(1)).willReturn(Optional.of(james));

        vetService.delete(1);

        verify(vetRepository).delete(james);
    }

    @Test
    void delete_nonExistingVet_throwsNotFound() {
        given(vetRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsMatchingVets() {
        given(vetRepository.findBySpecialtyName("radiology")).willReturn(List.of(helen));

        List<Vet> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void searchByName_returnsMatchingVets() {
        given(vetRepository.searchByName("James")).willReturn(List.of(james));

        List<Vet> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }

    @Test
    void assignSpecialty_validIds_addsSpecialty() {
        given(vetRepository.findById(1)).willReturn(Optional.of(james));
        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology));
        given(vetRepository.save(james)).willReturn(james);

        Vet result = vetService.assignSpecialty(1, 1);

        assertThat(result.getSpecialties()).contains(radiology);
    }

    @Test
    void assignSpecialty_vetNotFound_throwsNotFound() {
        given(vetRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.assignSpecialty(999, 1))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void assignSpecialty_specialtyNotFound_throwsNotFound() {
        given(vetRepository.findById(1)).willReturn(Optional.of(james));
        given(specialtyRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.assignSpecialty(1, 999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void removeSpecialty_validIds_removesSpecialty() {
        james.addSpecialty(radiology);
        given(vetRepository.findById(1)).willReturn(Optional.of(james));
        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology));
        given(vetRepository.save(james)).willReturn(james);

        Vet result = vetService.removeSpecialty(1, 1);

        assertThat(result.getSpecialties()).doesNotContain(radiology);
    }

    @Test
    void removeSpecialty_vetNotFound_throwsNotFound() {
        given(vetRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.removeSpecialty(999, 1))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removeSpecialty_specialtyNotFound_throwsNotFound() {
        given(vetRepository.findById(1)).willReturn(Optional.of(james));
        given(specialtyRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.removeSpecialty(1, 999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withNullSpecialties_createsVetWithEmptySpecialties() {
        VetRequestDto dto = new VetRequestDto("New", "Vet", null);
        Vet newVet = new Vet("New", "Vet");
        newVet.setId(3);

        given(vetMapper.toEntity(dto)).willReturn(newVet);
        given(vetRepository.save(any(Vet.class))).willReturn(newVet);

        Vet result = vetService.create(dto);

        assertThat(result.getSpecialties()).isEmpty();
    }
}
