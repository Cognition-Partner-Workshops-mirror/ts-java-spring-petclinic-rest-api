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

    private Vet vet;
    private Specialty specialty;
    private VetResponseDto responseDto;
    private VetRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));

        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        responseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        requestDto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
    }

    @Test
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = vetService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.getById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void getById_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void create_validRequest_createsVet() {
        when(vetMapper.toEntity(requestDto)).thenReturn(vet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.create(requestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withNonExistingSpecialty_throwsNotFoundException() {
        SpecialtyResponseDto nonExistingSpecialty = new SpecialtyResponseDto(999, "nonexistent");
        VetRequestDto invalidRequest = new VetRequestDto("John", "Doe", List.of(nonExistingSpecialty));

        when(vetMapper.toEntity(invalidRequest)).thenReturn(new Vet());
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(invalidRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void create_withEmptySpecialties_createsVet() {
        VetRequestDto noSpecialtiesRequest = new VetRequestDto("John", "Doe", List.of());
        Vet newVet = new Vet();
        newVet.setId(2);
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        newVet.setSpecialties(new HashSet<>());

        VetResponseDto newResponse = new VetResponseDto(2, "John", "Doe", List.of());

        when(vetMapper.toEntity(noSpecialtiesRequest)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(newResponse);

        VetResponseDto result = vetService.create(noSpecialtiesRequest);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_createsVet() {
        VetRequestDto nullSpecialtiesRequest = new VetRequestDto("John", "Doe", null);
        Vet newVet = new Vet();
        newVet.setId(2);
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        newVet.setSpecialties(new HashSet<>());

        VetResponseDto newResponse = new VetResponseDto(2, "John", "Doe", List.of());

        when(vetMapper.toEntity(nullSpecialtiesRequest)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(newResponse);

        VetResponseDto result = vetService.create(nullSpecialtiesRequest);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void update_existingId_updatesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.update(1, requestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void update_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.searchByName("James")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }
}
