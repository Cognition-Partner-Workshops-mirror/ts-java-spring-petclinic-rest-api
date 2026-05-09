package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
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
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetService vetService;

    private Vet vet;
    private VetResponseDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));
        vetDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        Vet vet2 = new Vet(2, "Helen", "Leary");
        VetResponseDto vetDto2 = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetRepository.findAll()).thenReturn(List.of(vet, vet2));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);
        when(vetMapper.toResponseDto(vet2)).thenReturn(vetDto2);

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
        assertThat(result.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_withSpecialties_returnsCreatedVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void create_withoutSpecialties_returnsCreatedVet() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", null);
        Vet savedVet = new Vet(2, "Helen", "Leary");
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(response);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getFirstName()).isEqualTo("Helen");
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_withInvalidSpecialtyId_throwsResourceNotFoundException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1, 99));
        when(specialtyRepository.findAllById(List.of(1, 99))).thenReturn(List.of(radiology));

        assertThatThrownBy(() -> vetService.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found");
    }

    @Test
    void update_existingVet_returnsUpdatedVet() {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of(1));
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Updated",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getLastName()).isEqualTo("Updated");
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());

        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeletedVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Cart")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.searchByLastName("Cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void assignSpecialties_validIds_returnsUpdatedVet() {
        Specialty surgery = new Specialty(2, "surgery");
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology"), new SpecialtyResponseDto(2, "surgery")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findAllById(List.of(2))).thenReturn(List.of(surgery));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.assignSpecialties(1, List.of(2));

        assertThat(result.getSpecialties()).hasSize(2);
    }

    @Test
    void assignSpecialties_nonExistingVet_throwsResourceNotFoundException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.assignSpecialties(99, List.of(1)))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withEmptySpecialtyList_returnsCreatedVet() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        Vet savedVet = new Vet(2, "Helen", "Leary");
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(response);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getSpecialties()).isEmpty();
    }
}
