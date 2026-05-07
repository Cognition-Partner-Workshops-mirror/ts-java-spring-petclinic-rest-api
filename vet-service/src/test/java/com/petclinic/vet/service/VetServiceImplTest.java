package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyReferenceDto;
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
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet carter;
    private VetResponseDto carterDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);

        carter = new Vet("James", "Carter");
        carter.setId(1);
        carter.setSpecialties(new HashSet<>());

        carterDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.lastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_validDto_returnsCreated() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyReferenceDto(1, "radiology")));

        Vet saved = new Vet("Helen", "Leary");
        saved.setId(2);
        saved.setSpecialties(Set.of(radiology));

        VetResponseDto expectedDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(expectedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(2);
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void create_withEmptySpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());

        Vet saved = new Vet("James", "Carter");
        saved.setId(3);
        saved.setSpecialties(new HashSet<>());

        VetResponseDto expectedDto = new VetResponseDto(3, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(expectedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(3);
        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_returnsCreated() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        Vet saved = new Vet("James", "Carter");
        saved.setId(4);
        saved.setSpecialties(new HashSet<>());

        VetResponseDto expectedDto = new VetResponseDto(4, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(expectedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withInvalidSpecialtyId_throwsException() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyReferenceDto(99, "missing")));

        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_existingId_returnsUpdated() {
        VetRequestDto request = new VetRequestDto("James", "Updated",
            List.of(new SpecialtyReferenceDto(1, "radiology")));

        Vet updated = new Vet("James", "Updated");
        updated.setId(1);
        updated.setSpecialties(Set.of(radiology));

        VetResponseDto expectedDto = new VetResponseDto(1, "James", "Updated",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(carter)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(expectedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.lastName()).isEqualTo("Updated");
    }

    @Test
    void update_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.delete(1);

        verify(vetRepository).delete(carter);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter"))
            .thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology"))
            .thenReturn(List.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
