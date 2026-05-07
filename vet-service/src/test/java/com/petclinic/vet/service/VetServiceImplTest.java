package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet james;
    private VetResponseDto jamesDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(new HashSet<>());

        jamesDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsList() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findAll_emptyList() {
        when(vetRepository.findAll()).thenReturn(List.of());
        when(vetMapper.toResponseDtos(List.of())).thenReturn(List.of());

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void create_withoutSpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of());
        Vet newVet = new Vet();
        newVet.setFirstName("New");
        newVet.setLastName("Vet");
        newVet.setSpecialties(new HashSet<>());
        Vet saved = new Vet();
        saved.setId(7);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        saved.setSpecialties(new HashSet<>());
        VetResponseDto savedDto = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(7);
        verify(vetRepository).save(newVet);
    }

    @Test
    void create_withSpecialties() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specDto));
        Vet newVet = new Vet();
        newVet.setFirstName("Helen");
        newVet.setLastName("Leary");
        newVet.setSpecialties(new HashSet<>());
        Vet saved = new Vet();
        saved.setId(2);
        saved.setFirstName("Helen");
        saved.setLastName("Leary");
        saved.setSpecialties(Set.of(radiology));
        VetResponseDto savedDto = new VetResponseDto(2, "Helen", "Leary", List.of(specDto));

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findByNameIn(Set.of("radiology"))).thenReturn(List.of(radiology));
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(2);
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void create_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", null);
        Vet newVet = new Vet();
        newVet.setFirstName("New");
        newVet.setLastName("Vet");
        newVet.setSpecialties(new HashSet<>());
        Vet saved = new Vet();
        saved.setId(7);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        saved.setSpecialties(new HashSet<>());
        VetResponseDto savedDto = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(7);
    }

    @Test
    void update_existingVet() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Carter");
        updated.setSpecialties(new HashSet<>());
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Carter", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(james)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
    }

    @Test
    void update_withSpecialties() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(specDto));
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("James");
        updated.setLastName("Carter");
        updated.setSpecialties(Set.of(radiology));
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Carter", List.of(specDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findByNameIn(Set.of("radiology"))).thenReturn(List.of(radiology));
        when(vetRepository.save(james)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void update_notFound_throwsException() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));

        vetService.delete(1);

        verify(vetRepository).delete(james);
    }

    @Test
    void delete_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyName_returnsResults() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsResults() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_noResults() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Unknown")).thenReturn(List.of());
        when(vetMapper.toResponseDtos(List.of())).thenReturn(List.of());

        List<VetResponseDto> result = vetService.searchByLastName("Unknown");

        assertThat(result).isEmpty();
    }
}
