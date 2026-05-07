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
class VetServiceTest {

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
        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));

        responseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void create_savesAndReturnsVet() {
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.create(requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withNonExistingSpecialty_throwsException() {
        Vet newVet = new Vet();
        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_withEmptySpecialties_succeeds() {
        VetRequestDto noSpecialtiesDto = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(noSpecialtiesDto)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of()));

        VetResponseDto result = service.create(noSpecialtiesDto);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void create_withNullSpecialties_succeeds() {
        VetRequestDto nullSpecialtiesDto = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(nullSpecialtiesDto)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of()));

        VetResponseDto result = service.create(nullSpecialtiesDto);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_existingId_updatesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = service.update(1, requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vet);
    }

    @Test
    void update_nonExistingId_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(vetRepository.existsById(1)).thenReturn(true);

        service.delete(1);

        verify(vetRepository).deleteById(1);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = service.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.findByNameContainingIgnoreCase("James")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        List<VetResponseDto> result = service.searchByName("James");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }
}
