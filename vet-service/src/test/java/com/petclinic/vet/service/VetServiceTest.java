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
    private VetResponseDto jamesDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        james = new Vet(1, "James", "Carter");
        james.setSpecialties(new HashSet<>(Set.of(radiology)));
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
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void getVet_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void addVet_validDto_returnsSaved() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void addVet_noSpecialties_returnsSaved() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet noSpecVet = new Vet(1, "James", "Carter");
        VetResponseDto noSpecDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void addVet_emptySpecialtyList_returnsSaved() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet noSpecVet = new Vet(1, "James", "Carter");
        VetResponseDto noSpecDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void addVet_invalidSpecialtyId_throwsException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1, 999));
        when(specialtyRepository.findAllById(List.of(1, 999))).thenReturn(List.of(radiology));

        assertThatThrownBy(() -> vetService.addVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void updateVet_existingId_returnsUpdated() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(1));
        Vet updatedVet = new Vet(1, "Updated", "Carter");
        updatedVet.setSpecialties(new HashSet<>(Set.of(radiology)));
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(radiology));
        when(vetRepository.save(james)).thenReturn(updatedVet);
        when(vetMapper.toResponseDto(updatedVet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
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
    void findBySpecialtyId_returnsMatches() {
        List<Vet> matches = List.of(james);
        when(vetRepository.findBySpecialtyId(1)).thenReturn(matches);
        when(vetMapper.toResponseDtoList(matches)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatches() {
        List<Vet> matches = List.of(james);
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(matches);
        when(vetMapper.toResponseDtoList(matches)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        List<Vet> matches = List.of(james);
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(matches);
        when(vetMapper.toResponseDtoList(matches)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
