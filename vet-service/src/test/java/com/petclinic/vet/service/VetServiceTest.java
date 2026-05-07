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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    void listVets_returnsAll() {
        Vet helen = new Vet();
        helen.setId(2);
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(new HashSet<>(Set.of(radiology)));
        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findAll()).thenReturn(List.of(james, helen));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);
        when(vetMapper.toResponseDto(helen)).thenReturn(helenDto);

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(2);
        verify(vetRepository).findAll();
    }

    @Test
    void getVet_existingId_returnsDto() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void getVet_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createVet_validInput_returnsCreated() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("John", "Doe", List.of(specDto));
        Vet newVet = new Vet();
        newVet.setId(10);
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        newVet.setSpecialties(new HashSet<>(Set.of(radiology)));
        VetResponseDto responseDto = new VetResponseDto(10, "John", "Doe",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void createVet_emptySpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("John", "Doe", List.of());
        Vet newVet = new Vet();
        newVet.setId(10);
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        newVet.setSpecialties(new HashSet<>());
        VetResponseDto responseDto = new VetResponseDto(10, "John", "Doe", List.of());

        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_nullSpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("John", "Doe", null);
        Vet newVet = new Vet();
        newVet.setId(10);
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        VetResponseDto responseDto = new VetResponseDto(10, "John", "Doe", List.of());

        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(10);
    }

    @Test
    void createVet_specialtyWithNullId_isSkipped() {
        SpecialtyResponseDto specDtoNoId = new SpecialtyResponseDto(null, "radiology");
        VetRequestDto request = new VetRequestDto("John", "Doe", List.of(specDtoNoId));
        Vet newVet = new Vet();
        newVet.setId(10);
        VetResponseDto responseDto = new VetResponseDto(10, "John", "Doe", List.of());

        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);
        assertThat(result.getId()).isEqualTo(10);
    }

    @Test
    void createVet_nonExistingSpecialty_throwsException() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(999, "nonexistent");
        VetRequestDto request = new VetRequestDto("John", "Doe", List.of(specDto));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateVet_existingId_returnsUpdated() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        doNothing().when(vetMapper).updateEntity(eq(james), eq(request), any());
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(new VetResponseDto(1, "Updated", "Carter", List.of()));

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
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
        doNothing().when(vetRepository).delete(james);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void deleteVet_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }
}
