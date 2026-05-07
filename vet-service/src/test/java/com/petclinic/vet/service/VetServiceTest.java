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
    private VetServiceImpl vetService;

    private Vet james;
    private Specialty radiology;
    private VetResponseDto jamesDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        james = new Vet(1, "James", "Carter");
        james.setSpecialties(Set.of(radiology));
        jamesDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAll() {
        List<Vet> entities = List.of(james);
        when(vetRepository.findAll()).thenReturn(entities);
        when(vetMapper.toResponseDtoList(entities)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_existingId_returnsDto() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getVet_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void createVet_validRequest_returnsCreated() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void createVet_nonExistingSpecialty_throwsException() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(999, "nonexistent")));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void createVet_emptySpecialties_succeeds() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet(null, "James", "Carter");
        Vet savedVet = new Vet(1, "James", "Carter");
        VetResponseDto savedDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void createVet_nullSpecialties_succeeds() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet(null, "James", "Carter");
        Vet savedVet = new Vet(1, "James", "Carter");
        VetResponseDto savedDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void updateVet_existingId_returnsUpdated() {
        VetRequestDto request = new VetRequestDto("Updated", "Name",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.updateVet(1, request);

        verify(vetMapper).updateEntity(request, james);
        assertThat(result).isNotNull();
    }

    @Test
    void updateVet_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void deleteVet_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsMatching() {
        List<Vet> entities = List.of(james);
        when(vetRepository.findBySpecialtyId(1)).thenReturn(entities);
        when(vetMapper.toResponseDtoList(entities)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatching() {
        List<Vet> entities = List.of(james);
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(entities);
        when(vetMapper.toResponseDtoList(entities)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatching() {
        List<Vet> entities = List.of(james);
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(entities);
        when(vetMapper.toResponseDtoList(entities)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
