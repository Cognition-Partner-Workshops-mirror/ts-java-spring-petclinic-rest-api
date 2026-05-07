package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
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

import java.util.ArrayList;
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

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet james;
    private VetResponseDto jamesDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        james = new Vet(1, "James", "Carter", new ArrayList<>());
        jamesDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void listVets_returnsAll() {
        List<Vet> entities = List.of(james);
        when(vetRepository.findAll()).thenReturn(entities);
        when(vetMapper.toResponseDtoList(entities)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().firstName()).isEqualTo("James");
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
    void getVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("999");
    }

    @Test
    void createVet_withExistingSpecialty_savesAndReturns() {
        SpecialtyRequestDto specDto = new SpecialtyRequestDto("radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specDto));
        SpecialtyResponseDto specResponse = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto responseDto = new VetResponseDto(2, "Helen", "Leary", List.of(specResponse));

        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(2);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.firstName()).isEqualTo("Helen");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void createVet_withNewSpecialty_createsSpecialtyAndSaves() {
        SpecialtyRequestDto specDto = new SpecialtyRequestDto("oncology");
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(specDto));
        Specialty newSpec = new Specialty(10, "oncology");
        VetResponseDto responseDto = new VetResponseDto(3, "New", "Vet",
            List.of(new SpecialtyResponseDto(10, "oncology")));

        when(specialtyRepository.findByNameContainingIgnoreCase("oncology"))
            .thenReturn(List.of());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(newSpec);
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(3);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.firstName()).isEqualTo("New");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void createVet_withEmptySpecialties_savesWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", List.of());
        VetResponseDto responseDto = new VetResponseDto(4, "Solo", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(4);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void createVet_withNullSpecialties_savesWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", null);
        VetResponseDto responseDto = new VetResponseDto(5, "Solo", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(5);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void updateVet_existingId_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of());
        VetResponseDto responseDto = new VetResponseDto(1, "Updated", "Name", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(responseDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999,
            new VetRequestDto("X", "Y", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_existingId_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void deleteVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_delegatesToRepository() {
        List<Vet> vets = List.of(james);
        when(vetRepository.findBySpecialtyId(1)).thenReturn(vets);
        when(vetMapper.toResponseDtoList(vets)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_delegatesToRepository() {
        List<Vet> vets = List.of(james);
        when(vetRepository.findByNameContainingIgnoreCase("James")).thenReturn(vets);
        when(vetMapper.toResponseDtoList(vets)).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }
}
