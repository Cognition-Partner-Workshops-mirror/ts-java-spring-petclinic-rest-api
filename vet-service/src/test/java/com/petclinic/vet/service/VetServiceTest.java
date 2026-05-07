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

    private Vet vet;
    private VetResponseDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(List.of(radiology));
        vetDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        Vet vet2 = new Vet(2, "Helen", "Leary");
        VetResponseDto vetDto2 = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetRepository.findAll()).thenReturn(List.of(vet, vet2));
        when(vetMapper.toResponseDtoList(List.of(vet, vet2)))
            .thenReturn(List.of(vetDto, vetDto2));

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
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void create_withExistingSpecialty_returnsCreated() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto("radiology")));

        Vet unsaved = new Vet();
        unsaved.setFirstName("James");
        unsaved.setLastName("Carter");

        when(vetMapper.toEntity(request)).thenReturn(unsaved);
        when(specialtyRepository.findByNameIgnoreCase("radiology"))
            .thenReturn(Optional.of(radiology));
        when(vetRepository.save(unsaved)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
        verify(vetRepository).save(unsaved);
    }

    @Test
    void create_withNewSpecialty_createsSpecialtyAndVet() {
        SpecialtyRequestDto newSpecReq = new SpecialtyRequestDto("oncology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(newSpecReq));

        Vet unsaved = new Vet();
        unsaved.setFirstName("James");
        unsaved.setLastName("Carter");

        Specialty oncology = new Specialty(2, "oncology");

        when(vetMapper.toEntity(request)).thenReturn(unsaved);
        when(specialtyRepository.findByNameIgnoreCase("oncology"))
            .thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(oncology);
        when(vetRepository.save(unsaved)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result).isNotNull();
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_withEmptySpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("James", "Carter", new ArrayList<>());
        Vet unsaved = new Vet();
        unsaved.setFirstName("James");
        unsaved.setLastName("Carter");

        VetResponseDto noSpecDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(unsaved);
        when(vetRepository.save(unsaved)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet unsaved = new Vet();
        unsaved.setFirstName("James");
        unsaved.setLastName("Carter");

        VetResponseDto noSpecDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(unsaved);
        when(vetRepository.save(unsaved)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_existingId_returnsUpdated() {
        VetRequestDto request = new VetRequestDto("James", "Updated",
            List.of(new SpecialtyRequestDto("radiology")));
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Updated",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameIgnoreCase("radiology"))
            .thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getLastName()).isEqualTo("Updated");
        verify(vetMapper).updateEntityFromDto(request, vet);
    }

    @Test
    void update_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.searchByName("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.searchByName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }
}
