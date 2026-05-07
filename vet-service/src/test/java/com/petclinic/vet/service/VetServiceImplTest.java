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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
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

    @BeforeEach
    void setUp() {
        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");

        jamesDto = new VetResponseDto(1, "James", "Carter", Collections.emptyList());
    }

    @Test
    void findAll_returnsAllVets() {
        Vet helen = new Vet();
        helen.setId(2);
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary", Collections.emptyList());

        when(vetRepository.findAll()).thenReturn(List.of(james, helen));
        when(vetMapper.toResponseDtos(List.of(james, helen))).thenReturn(List.of(jamesDto, helenDto));

        List<VetResponseDto> result = vetService.findAll();
        assertThat(result).hasSize(2);
        verify(vetRepository).findAll();
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.findById(1);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void create_withoutSpecialties_savesVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", Collections.emptyList());
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);
        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).save(newVet);
    }

    @Test
    void create_withSpecialties_resolvesAndSaves() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specDto));
        Vet newVet = new Vet();
        newVet.setFirstName("Helen");
        newVet.setLastName("Leary");

        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Vet savedVet = new Vet();
        savedVet.setId(2);
        savedVet.setFirstName("Helen");
        savedVet.setLastName("Leary");
        savedVet.setSpecialties(Set.of(radiology));

        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary", List.of(specDto));

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findByNameIn(Set.of("radiology"))).thenReturn(List.of(radiology));
        when(vetRepository.save(newVet)).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(helenDto);

        VetResponseDto result = vetService.create(request);
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void create_withNullSpecialties_savesVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void update_existingId_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", Collections.emptyList());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(new VetResponseDto(1, "Updated", "Carter", Collections.emptyList()));

        VetResponseDto result = vetService.update(1, request);
        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void update_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", Collections.emptyList());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));

        vetService.delete(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");
        assertThat(result).hasSize(1);
    }
}
