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
    private VetServiceImpl vetService;

    private Vet carter;
    private VetResponseDto carterDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        carter = new Vet(1, "James", "Carter");
        carter.setSpecialties(new HashSet<>());
        carterDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsAllVets() {
        Vet leary = new Vet(2, "Helen", "Leary");
        leary.setSpecialties(Set.of(radiology));
        VetResponseDto learyDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findAll()).thenReturn(List.of(carter, leary));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);
        when(vetMapper.toResponseDto(leary)).thenReturn(learyDto);

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_validDto_returnsCreated() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(1));
        Vet saved = new Vet(7, "New", "Vet");
        saved.setSpecialties(Set.of(radiology));
        VetResponseDto response = new VetResponseDto(7, "New", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(response);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(7);
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void create_emptySpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of());
        Vet saved = new Vet(7, "New", "Vet");
        saved.setSpecialties(new HashSet<>());
        VetResponseDto response = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(response);

        VetResponseDto result = vetService.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_invalidSpecialtyId_throwsException() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(1, 999));
        when(specialtyRepository.findAllById(List.of(1, 999))).thenReturn(List.of(radiology));

        assertThatThrownBy(() -> vetService.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_existingId_returnsUpdated() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(1));
        Vet updated = new Vet(1, "Updated", "Carter");
        updated.setSpecialties(Set.of(radiology));
        VetResponseDto response = new VetResponseDto(1, "Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(carter)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(response);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
    }

    @Test
    void update_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(carter);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsResults() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsResults() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter"))
            .thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsResults() {
        when(vetRepository.findBySpecialtyName("radiology"))
            .thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_nullSpecialtyIds_returnsCreated() {
        VetRequestDto request = new VetRequestDto("New", "Vet", null);
        Vet saved = new Vet(7, "New", "Vet");
        saved.setSpecialties(new HashSet<>());
        VetResponseDto response = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(response);

        VetResponseDto result = vetService.create(request);

        assertThat(result.specialties()).isEmpty();
    }
}
