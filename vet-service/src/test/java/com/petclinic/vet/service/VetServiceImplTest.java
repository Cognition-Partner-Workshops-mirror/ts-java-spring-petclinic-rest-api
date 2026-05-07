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

import java.util.List;
import java.util.Optional;

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
    private VetServiceImpl service;

    private Vet vet;
    private VetResponseDto responseDto;
    private VetRequestDto requestDto;
    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        responseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto("radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().firstName()).isEqualTo("James");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id 999");
    }

    @Test
    void create_validDto_returnsCreated() {
        when(vetMapper.toEntity(requestDto)).thenReturn(vet);
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.create(requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vet);
    }

    @Test
    void create_withNewSpecialty_createsSpecialtyAndVet() {
        when(vetMapper.toEntity(requestDto)).thenReturn(vet);
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.create(requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_withNullSpecialties_createsVetWithEmptySpecialties() {
        VetRequestDto dtoWithNull = new VetRequestDto("James", "Carter", null);
        Vet vetNoSpec = new Vet(1, "James", "Carter");
        VetResponseDto respNoSpec = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(dtoWithNull)).thenReturn(vetNoSpec);
        when(vetRepository.save(vetNoSpec)).thenReturn(vetNoSpec);
        when(vetMapper.toResponseDto(vetNoSpec)).thenReturn(respNoSpec);

        VetResponseDto result = service.create(dtoWithNull);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void update_existingId_returnsUpdated() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.update(1, requestDto);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_returnsMatching() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialty_returnsMatching() {
        when(vetRepository.findBySpecialtyNameContainingIgnoreCase("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = service.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }
}
